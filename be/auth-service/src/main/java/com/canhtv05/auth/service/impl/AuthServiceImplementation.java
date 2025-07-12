package com.canhtv05.auth.service.impl;

import com.canhtv05.auth.dto.ApiResponse;
import com.canhtv05.auth.dto.MetaResponse;
import com.canhtv05.auth.dto.req.AuthenticationRequest;
import com.canhtv05.auth.dto.req.RefreshTokenRequest;
import com.canhtv05.auth.dto.res.IntrospectResponse;
import com.canhtv05.auth.dto.res.LoginResponse;
import com.canhtv05.auth.dto.res.RefreshTokenResponse;
import com.canhtv05.auth.dto.res.UserResponse;
import com.canhtv05.auth.exception.AppException;
import com.canhtv05.auth.exception.ErrorCode;
import com.canhtv05.auth.repository.httpclient.UserClient;
import com.canhtv05.auth.service.AuthService;
import com.canhtv05.auth.service.RedisService;
import com.canhtv05.auth.util.CookieUtil;
import com.canhtv05.auth.util.TokenUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthServiceImplementation implements AuthService {

    @NonFinal
    @Value("${jwt.valid-duration}")
    Long validDuration;

    @NonFinal
    @Value("${jwt.refresh-duration}")
    Long refreshDuration;

    RedisService redisService;
    UserClient userClient;
    TokenUtil tokenUtil;
    CookieUtil cookieUtil;

    @Override
    public ApiResponse<UserResponse> login(AuthenticationRequest request, HttpServletResponse response) throws JOSEException {
        var user = userClient.getUserByEmail(request.getEmail());

        String accessToken = tokenUtil.generateAccessToken(user.getData());
        String refreshToken = tokenUtil.generateRefreshToken(user.getData());

        userClient.refreshToken(RefreshTokenRequest.builder().email(request.getEmail()).refreshToken(refreshToken).build());

        Cookie cookie = cookieUtil.setCookie(accessToken, refreshToken);
        response.addCookie(cookie);

        LoginResponse loginResponse = LoginResponse.builder()
                .accessToken(accessToken)
                .accessTokenTTL(validDuration)
                .refreshTokenTTL(refreshDuration)
                .refreshToken(refreshToken).build();

        user.getData().setRefreshToken(null);

        return ApiResponse.<UserResponse>builder()
                .data(user.getData())
                .meta(MetaResponse.<LoginResponse>builder()
                        .token(loginResponse)
                        .build())
                .message("success")
                .build();
    }

    @Override
    public RefreshTokenResponse refreshToken(String refreshToken, HttpServletResponse response) throws ParseException
            , JOSEException {
        if (StringUtils.isBlank(refreshToken)) {
            throw new AppException(ErrorCode.REFRESH_TOKEN_INVALID);
        }

        String email = tokenUtil.verifyAndExtractEmail(refreshToken);
        var user = userClient.getUserByEmail(email);

        if (!Objects.equals(user.getData().getRefreshToken(), refreshToken) || StringUtils.isBlank(user.getData().getRefreshToken())) {
            throw new AppException(ErrorCode.REFRESH_TOKEN_INVALID);
        }

        var signJWT = tokenUtil.verifyToken(refreshToken);
        if (Objects.isNull(signJWT)) {
            throw new AppException(ErrorCode.REFRESH_TOKEN_INVALID);
        }

        String accessToken = tokenUtil.generateAccessToken(user.getData());
        String generateRefreshToken = tokenUtil.generateRefreshToken(user.getData());

        Cookie cookie = cookieUtil.setCookie(accessToken, generateRefreshToken);
        response.addCookie(cookie);

        userClient.refreshToken(RefreshTokenRequest.builder().email(email).refreshToken(refreshToken).build());

        return RefreshTokenResponse.builder().accessToken(accessToken).refreshToken(generateRefreshToken).build();
    }

    @Override
    public void logout(String accessToken, HttpServletResponse response) throws ParseException, JOSEException {
        String email = tokenUtil.verifyAndExtractEmail(accessToken);

        long accessTokenExpired = tokenUtil.verifyAndExtractTokenExpired(accessToken);
        long currentTime = System.currentTimeMillis();

        // con han
        if (currentTime < accessTokenExpired) {
            try {
                String jwtId = tokenUtil.verifyToken(accessToken).getJWTClaimsSet().getJWTID();

                long ttl = accessTokenExpired - currentTime;
                redisService.save(jwtId, accessToken, ttl, TimeUnit.MILLISECONDS);

                userClient.refreshToken(RefreshTokenRequest.builder().email(email).refreshToken(null).build());

                cookieUtil.deleteCookie(response);

                SecurityContextHolder.clearContext();
            } catch (ParseException _) {
                throw new AppException(ErrorCode.INVALID_TOKEN);
            }
        }
    }

}
