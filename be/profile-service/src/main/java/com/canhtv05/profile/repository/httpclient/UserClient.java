package com.canhtv05.profile.repository.httpclient;

import com.canhtv05.profile.config.AuthenticationRequestInterceptor;
import com.canhtv05.profile.dto.ApiResponse;
import com.canhtv05.profile.dto.req.UserUpdateRequest;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "user-service", url = "${user-service.url}", configuration = {
        AuthenticationRequestInterceptor.class
})
public interface UserClient {

    @PostMapping(value = "/internal/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    ApiResponse<Void> updateUser(@PathVariable(name = "userId") String userId, @RequestBody UserUpdateRequest request);
}
