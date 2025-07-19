package com.canhtv05.post.repository.httpclient;

import com.canhtv05.post.config.AuthenticationRequestInterceptor;
import com.canhtv05.post.config.FeignConfig;
import com.canhtv05.post.dto.ApiResponse;
import com.canhtv05.post.dto.res.FileResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@FeignClient(name = "file-service", url = "${file-service.url}", configuration = {
        FeignConfig.class,
        AuthenticationRequestInterceptor.class
})
public interface FileClient {

    @PostMapping(value = "/media/upload", produces = MediaType.APPLICATION_JSON_VALUE, consumes =
            MediaType.MULTIPART_FORM_DATA_VALUE)
    ApiResponse<FileResponse> uploadFile(@RequestPart("files") MultipartFile[] files);

    @GetMapping(value = "/{fileId}", produces = MediaType.APPLICATION_JSON_VALUE)
    ApiResponse<FileResponse> getFileById(@PathVariable("fileId") String fileId);
}
