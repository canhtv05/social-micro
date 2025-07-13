package com.canhtv05.auth.controller;

import com.canhtv05.auth.dto.ApiResponse;
import com.canhtv05.auth.dto.req.AuthenticationRequest;
import com.canhtv05.auth.dto.req.RefreshTokenRequest;
import com.canhtv05.auth.dto.req.VerifyTokenRequest;
import com.canhtv05.auth.dto.res.RefreshTokenResponse;
import com.canhtv05.auth.dto.res.UserResponse;
import com.canhtv05.auth.dto.res.VerifyTokenResponse;
import com.canhtv05.auth.service.AuthService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.nimbusds.jose.JOSEException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthController {

    AuthService authService;

    @PostMapping("/login")
    public ApiResponse<UserResponse> login(@RequestBody AuthenticationRequest request, HttpServletResponse response)
            throws UnsupportedEncodingException, JOSEException {

        return authService.login(request, response);
    }

    @PostMapping("/verify")
    public ApiResponse<VerifyTokenResponse> verifyToken(@RequestBody VerifyTokenRequest request) {
        return ApiResponse.<VerifyTokenResponse>builder()
                .data(authService.verifyToken(request))
                .build();
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout(@RequestHeader(HttpHeaders.AUTHORIZATION) String token,
                                    HttpServletResponse response) throws ParseException, JOSEException {
        authService.logout(token, response);
        return ApiResponse.<Void>builder()
                .data(null)
                .build();
    }

    @PostMapping("/refresh-token")
    public ApiResponse<RefreshTokenResponse> refreshToken(@CookieValue(name = "auth") String cookieValue,
                                                          HttpServletResponse response) throws ParseException,
            JOSEException, JsonProcessingException {

        return ApiResponse.<RefreshTokenResponse>builder()
                .data(authService.refreshToken(cookieValue, response))
                .build();
    }
}
