package com.canhtv05.auth.service;

import com.canhtv05.auth.dto.ApiResponse;
import com.canhtv05.auth.dto.req.AuthenticationRequest;
import com.canhtv05.auth.dto.req.VerifyTokenRequest;
import com.canhtv05.auth.dto.res.RefreshTokenResponse;
import com.canhtv05.auth.dto.res.UserResponse;
import com.canhtv05.auth.dto.res.VerifyTokenResponse;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.SignedJWT;
import jakarta.servlet.http.HttpServletResponse;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;

public interface AuthService {

    ApiResponse<UserResponse> login(AuthenticationRequest request, HttpServletResponse response)
            throws UnsupportedEncodingException, JOSEException;

    RefreshTokenResponse refreshToken(String refreshToken, HttpServletResponse response) throws ParseException, JOSEException;

    void logout(String token, HttpServletResponse response) throws ParseException, JOSEException;

    VerifyTokenResponse verifyToken(VerifyTokenRequest request);
}
