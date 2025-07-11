package com.canhtv05.auth.controller;

import com.canhtv05.auth.dto.ApiResponse;
import com.canhtv05.auth.dto.req.AuthenticationRequest;
import com.canhtv05.auth.dto.res.UserResponse;
import com.canhtv05.auth.service.AuthService;
import com.nimbusds.jose.JOSEException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;

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
}
