package com.canhtv05.auth.config;

import com.canhtv05.auth.dto.ApiResponse;
import com.canhtv05.auth.exception.AppException;
import com.canhtv05.auth.exception.DetailException;
import com.canhtv05.auth.exception.ErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.util.Map;

@Slf4j
public class FeignErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String s, Response response) {
        try (InputStream inputStream = response.body().asInputStream()) {
            ApiResponse<?> apiResponse = new ObjectMapper().readValue(inputStream, ApiResponse.class);

            int code = apiResponse.getCode();
            String message = apiResponse.getMessage();
            return new DetailException(Map.of(
                    "code", code,
                    "message", message
            ));
        } catch (Exception e) {
            return new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        }
    }
}
