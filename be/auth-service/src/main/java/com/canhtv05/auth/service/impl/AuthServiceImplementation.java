package com.canhtv05.auth.service.impl;

import com.canhtv05.auth.dto.ApiResponse;
import com.canhtv05.auth.dto.MetaResponse;
import com.canhtv05.auth.dto.req.AuthenticationRequest;
import com.canhtv05.auth.dto.req.RefreshTokenRequest;
import com.canhtv05.auth.dto.res.LoginResponse;
import com.canhtv05.auth.dto.res.RefreshTokenResponse;
import com.canhtv05.auth.dto.res.UserResponse;
import com.canhtv05.auth.exception.AppException;
import com.canhtv05.auth.exception.ErrorCode;
import com.canhtv05.auth.repository.httpclient.UserClient;
import com.canhtv05.auth.service.AuthService;
import com.canhtv05.auth.service.RedisService;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthServiceImplementation implements AuthService {

    @NonFinal
    @Value("${jwt.key}")
    String key;

    @NonFinal
    @Value("${jwt.valid-duration}")
    Long validDuration;

    @NonFinal
    @Value("${jwt.refresh-duration}")
    Long refreshDuration;

    @NonFinal
    @Value("${spring.application.name}")
    String KEY;

    String EMAIL_CLAIM = "email";
    String ISSUER = "canhtv05";

    RedisService redisService;
    UserClient userClient;

    @Override
    public SignedJWT verifyToken(String token) throws ParseException, JOSEException {
        try {
            if (token.startsWith("Bearer")) {
                token = token.replace("Bearer ", "");
            }
            JWSVerifier verifier = new MACVerifier(key.getBytes());
            SignedJWT signedJWT = SignedJWT.parse(token);

            Date expiration = signedJWT.getJWTClaimsSet().getExpirationTime();

            boolean verified = signedJWT.verify(verifier);
            if (!(verified && expiration.after(Date.from(Instant.now())))) {
                throw new AppException(ErrorCode.UNAUTHORIZED);
            }

            if (StringUtils.isNotBlank(
                    redisService.get(signedJWT.getJWTClaimsSet().getJWTID()))) {
                throw new AppException(ErrorCode.TOKEN_BLACKLISTED);
            }
            return signedJWT;
        } catch (ParseException | JOSEException _) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
    }

    @Override
    public String generateAccessToken(UserResponse user) throws JOSEException {
        return generateToken(user, validDuration);
    }

    @Override
    public String generateRefreshToken(UserResponse user) throws JOSEException {
        return generateToken(user, refreshDuration);
    }

    @Override
    public String verifyAndExtractEmail(String token) throws ParseException, JOSEException {
        Object object = this.verifyToken(token).getJWTClaimsSet().getClaim(EMAIL_CLAIM);
        if (Objects.isNull(object)) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
        return object.toString();
    }

    @Override
    public ApiResponse<UserResponse> login(AuthenticationRequest request, HttpServletResponse response) throws JOSEException {
        var user = userClient.getUserByEmail(request.getEmail());

        String accessToken = this.generateAccessToken(user.getData());
        String refreshToken = this.generateRefreshToken(user.getData());

        userClient.refreshToken(RefreshTokenRequest.builder()
                .email(request.getEmail())
                .refreshToken(refreshToken)
                .build());

        Cookie cookie = setCookie(accessToken, refreshToken);
        response.addCookie(cookie);

        LoginResponse loginResponse = LoginResponse.builder()
                .accessToken(accessToken)
                .accessTokenTTL(validDuration)
                .refreshTokenTTL(refreshDuration)
                .refreshToken(refreshToken)
                .build();

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

        String email = this.verifyAndExtractEmail(refreshToken);
        var user = userClient.getUserByEmail(email);

        if (!Objects.equals(user.getData().getRefreshToken(), refreshToken) || StringUtils.isBlank(user.getData().getRefreshToken())) {
            throw new AppException(ErrorCode.REFRESH_TOKEN_INVALID);
        }

        var signJWT = this.verifyToken(refreshToken);
        if (Objects.isNull(signJWT)) {
            throw new AppException(ErrorCode.REFRESH_TOKEN_INVALID);
        }

        String accessToken = this.generateAccessToken(user.getData());
        String generateRefreshToken = this.generateRefreshToken(user.getData());

        Cookie cookie = setCookie(accessToken, generateRefreshToken);
        response.addCookie(cookie);

        userClient.refreshToken(RefreshTokenRequest.builder()
                .email(email)
                .refreshToken(refreshToken)
                .build());

        return RefreshTokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(generateRefreshToken)
                .build();
    }

    @Override
    public void logout(String accessToken, HttpServletResponse response) throws ParseException, JOSEException {
        String email = this.verifyAndExtractEmail(accessToken);

        long accessTokenExpired = this.verifyAndExtractTokenExpired(accessToken);
        long currentTime = System.currentTimeMillis();

        // con han
        if (currentTime < accessTokenExpired) {
            try {
                String jwtId = verifyToken(accessToken).getJWTClaimsSet().getJWTID();

                long ttl = accessTokenExpired - currentTime;
                redisService.save(jwtId, accessToken, ttl, TimeUnit.MILLISECONDS);

                userClient.refreshToken(RefreshTokenRequest.builder()
                        .email(email)
                        .refreshToken(null)
                        .build());

                deleteCookie(response);

                SecurityContextHolder.clearContext();
            } catch (ParseException e) {
                throw new AppException(ErrorCode.INVALID_TOKEN);
            }
        }
    }

    private String generateToken(UserResponse user, Long duration) throws JOSEException {
        JWSHeader jwsHeader = new JWSHeader(JWSAlgorithm.HS512);

        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getId())
                .jwtID(UUID.randomUUID().toString())
                .issueTime(Date.from(Instant.now()))
                .issuer(ISSUER)
                .claim(EMAIL_CLAIM, user.getEmail())
                .expirationTime(Date.from(Instant.now().plus(duration, ChronoUnit.SECONDS)))
                .build();

        Payload payload = new Payload(jwtClaimsSet.toJSONObject());
        JWSObject jwsObject = new JWSObject(jwsHeader, payload);

        jwsObject.sign(new MACSigner(key.getBytes()));
        return jwsObject.serialize();
    }

    private Cookie setCookie(String accessToken, String refreshToken) {
        Map<String, String> tokenData = new HashMap<>();
        tokenData.put("accessToken", accessToken);
        tokenData.put("refreshToken", refreshToken);

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonData;

        try {
            jsonData = objectMapper.writeValueAsString(tokenData);
        } catch (JsonProcessingException | AppException e) {
            throw new AppException(ErrorCode.JSON_PROCESSING_ERROR);
        }

        // replace các kí tự sao cho giống với thư viện js-cookie
        // https://www.npmjs.com/package/js-cookie
        String formattedJsonData = jsonData.replace("\"", "%22").replace(",", "%2C");

        Cookie cookie = new Cookie("MY_CHAT_APP", formattedJsonData);
        // cookie.setHttpOnly(true);

        // cho phép lấy cookie từ phía client
        cookie.setHttpOnly(false);
        cookie.setMaxAge(refreshDuration.intValue()); // 2 weeks
        cookie.setPath("/");
        cookie.setSecure(true); // true nếu chỉ cho gửi qua HTTPS
        cookie.setDomain("localhost");
        return cookie;
    }

    public void deleteCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie(KEY, "");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        cookie.setSecure(true);
        cookie.setDomain("localhost");
        response.addCookie(cookie);
    }

    public long verifyAndExtractTokenExpired(String token) throws ParseException, JOSEException {
        Date expiredClaim = this.verifyToken(token).getJWTClaimsSet().getExpirationTime();
        if (Objects.isNull(expiredClaim)) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
        return expiredClaim.getTime();
    }
}
