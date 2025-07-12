package com.canhtv05.gateway.service.impl;

import org.springframework.stereotype.Service;

import com.canhtv05.gateway.dto.ApiResponse;
import com.canhtv05.gateway.dto.req.VerifyTokenRequest;
import com.canhtv05.gateway.dto.res.VerifyTokenResponse;
import com.canhtv05.gateway.repository.AuthClient;
import com.canhtv05.gateway.service.AuthService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthServiceImplementation implements AuthService {

  AuthClient authClient;

  @Override
  public Mono<ApiResponse<VerifyTokenResponse>> verifyToken(VerifyTokenRequest request) {
    return authClient.verifyToken(request);
  }

}
