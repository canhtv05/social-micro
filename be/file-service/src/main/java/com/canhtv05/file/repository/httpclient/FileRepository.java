package com.canhtv05.file.repository.httpclient;

import com.canhtv05.file.dto.res.FileResponse;
import com.canhtv05.file.entity.File;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FileRepository extends MongoRepository<File, String> {

    List<File> findAllByOwnerId(String ownerId);

    Optional<FileResponse> getFileById(String id);
}
