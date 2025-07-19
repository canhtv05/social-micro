package com.canhtv05.file.controller;

import com.canhtv05.file.dto.ApiResponse;
import com.canhtv05.file.dto.res.FileResponse;
import com.canhtv05.file.service.FileService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FileController {

    FileService fileService;

    @GetMapping("/me")
    public ApiResponse<List<FileResponse>> getMyResources() {
        return ApiResponse.<List<FileResponse>>builder()
                .data(fileService.getMyResources())
                .build();
    }

    @GetMapping("/{fileId}")
    public ApiResponse<FileResponse> getFileById(@PathVariable("fileId") String fileId) {
        return ApiResponse.<FileResponse>builder()
                .data(fileService.getFileById(fileId))
                .build();
    }

    @PostMapping(value = "/media/upload", produces = MediaType.APPLICATION_JSON_VALUE, consumes =
            MediaType.MULTIPART_FORM_DATA_VALUE)
    ApiResponse<FileResponse> uploadMedia(@RequestPart("files") MultipartFile[] files) throws IOException {
        return ApiResponse.<FileResponse>builder()
                .data(fileService.upload(files))
                .build();
    }
}
