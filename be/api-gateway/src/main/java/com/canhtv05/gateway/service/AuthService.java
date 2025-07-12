package com.canhtv05.gateway.service;

import com.canhtv05.gateway.dto.ApiResponse;
import com.canhtv05.gateway.dto.req.VerifyTokenRequest;
import com.canhtv05.gateway.dto.res.VerifyTokenResponse;

import reactor.core.publisher.Mono;

public interface AuthService {

  Mono<ApiResponse<VerifyTokenResponse>> verifyToken(VerifyTokenRequest request);
}
