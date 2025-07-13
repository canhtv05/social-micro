package com.canhtv05.auth.repository.httpclient;

import com.canhtv05.auth.config.AuthenticationRequestInterceptor;
import com.canhtv05.auth.dto.ApiResponse;
import com.canhtv05.auth.dto.req.RefreshTokenRequest;
import com.canhtv05.auth.dto.res.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "user-service", url = "${user-service.url}", configuration = {
        AuthenticationRequestInterceptor.class
})
public interface UserClient {

    @GetMapping(value = "/internal/email/{email}")
    ApiResponse<UserResponse> getUserByEmail(@PathVariable("email") String email);

    @PutMapping(value = "/internal/update/refresh-token", produces = MediaType.APPLICATION_JSON_VALUE)
    ApiResponse<String> refreshToken(@RequestBody RefreshTokenRequest refreshToken);
}
