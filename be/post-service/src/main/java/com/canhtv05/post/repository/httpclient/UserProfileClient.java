package com.canhtv05.post.repository.httpclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.canhtv05.post.config.AuthenticationRequestInterceptor;
import com.canhtv05.post.dto.ApiResponse;
import com.canhtv05.post.dto.res.UserProfileResponse;

@FeignClient(name = "profile-service", url = "${profile-service.url}", configuration = {
    AuthenticationRequestInterceptor.class
})
public interface UserProfileClient {

  @GetMapping("/internal/users/{userId}")
  public ApiResponse<UserProfileResponse> getUserProfile(@PathVariable(name = "userId") String userId);
}
