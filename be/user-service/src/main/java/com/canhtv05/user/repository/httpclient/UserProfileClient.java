package com.canhtv05.user.repository.httpclient;

import com.canhtv05.user.config.AuthenticationRequestInterceptor;
import com.canhtv05.user.dto.ApiResponse;
import com.canhtv05.user.dto.req.UserProfileCreationRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "profile-service", url = "${profile-service.url}", configuration = {
        AuthenticationRequestInterceptor.class
})
public interface UserProfileClient {

    @PostMapping(value = "/internal/create", produces = MediaType.APPLICATION_JSON_VALUE)
    ApiResponse<Void> createUserProfile(@RequestBody UserProfileCreationRequest request);
}
