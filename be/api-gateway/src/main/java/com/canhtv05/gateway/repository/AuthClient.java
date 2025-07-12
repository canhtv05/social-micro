package com.canhtv05.gateway.repository;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.PostExchange;

import com.canhtv05.gateway.dto.ApiResponse;
import com.canhtv05.gateway.dto.req.VerifyTokenRequest;
import com.canhtv05.gateway.dto.res.VerifyTokenResponse;

import reactor.core.publisher.Mono;

public interface AuthClient {

  @PostExchange(url = "/auth/verify", contentType = MediaType.APPLICATION_JSON_VALUE)
  Mono<ApiResponse<VerifyTokenResponse>> verifyToken(@RequestBody VerifyTokenRequest request);
}
