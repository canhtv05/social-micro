package com.canhtv05.file.service;

import com.canhtv05.file.dto.res.FileResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface FileService {

    FileResponse upload(MultipartFile[] files) throws IOException;

    List<FileResponse> getMyResources();

    FileResponse getFileById(String id);
}
