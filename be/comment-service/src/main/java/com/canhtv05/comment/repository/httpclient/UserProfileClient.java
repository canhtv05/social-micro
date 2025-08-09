package com.canhtv05.comment.repository.httpclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.canhtv05.comment.config.AuthenticationRequestInterceptor;
import com.canhtv05.comment.dto.ApiResponse;
import com.canhtv05.comment.dto.res.UserProfileResponse;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "profile-service", url = "${profile-service.url}", configuration = {
        AuthenticationRequestInterceptor.class
})
public interface UserProfileClient {

    @GetMapping("/{userId}")
    ApiResponse<UserProfileResponse> getUserProfile(@PathVariable(name = "userId") String userId);

    @GetMapping("/internal/users")
    ApiResponse<List<UserProfileResponse>> getUserProfilesByIds(@RequestParam("ids") List<String> userIds);

}
