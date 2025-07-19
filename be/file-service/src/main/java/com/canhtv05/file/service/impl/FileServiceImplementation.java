package com.canhtv05.file.service.impl;

import com.canhtv05.file.dto.res.FileResponse;
import com.canhtv05.file.dto.res.ImageResponse;
import com.canhtv05.file.dto.res.VideoResponse;
import com.canhtv05.file.entity.Image;
import com.canhtv05.file.entity.Video;
import com.canhtv05.file.exception.AppException;
import com.canhtv05.file.exception.ErrorCode;
import com.canhtv05.file.mapper.FileMapper;
import com.canhtv05.file.repository.httpclient.FileRepository;
import com.canhtv05.file.service.FileService;
import com.cloudinary.Cloudinary;
import com.cloudinary.EagerTransformation;
import com.cloudinary.utils.ObjectUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.mp4parser.IsoFile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FileServiceImplementation implements FileService {

    Cloudinary cloudinary;
    FileRepository fileRepository;
    FileMapper fileMapper;

    @Override
    public FileResponse upload(MultipartFile[] files) throws IOException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userId = auth.getName();

        String imageString = "image";
        String videoString = "video";
        String secureUrl = "secure_url";

        List<ImageResponse> imageResponses = new ArrayList<>();
        List<VideoResponse> videoResponses = new ArrayList<>();
        long totalSize = 0L;

        for (MultipartFile file : files) {
            String contentType = file.getContentType();
            if (contentType == null) continue;

            totalSize += file.getSize();

            if (contentType.startsWith(imageString)) {
                var imageResult = cloudinary.uploader().upload(
                        file.getBytes(),
                        ObjectUtils.asMap(
                                "resource_type", imageString,
                                "upload_preset", "social-media",
                                "folder", imageString
                        )
                );
                String imageUrl = imageResult.get(secureUrl).toString();

                imageResponses.add(ImageResponse.builder()
                        .contentType(file.getContentType())
                        .imageUrl(imageUrl)
                        .fileSize(file.getSize())
                        .originFileName(file.getOriginalFilename())
                        .build());

            } else if (contentType.startsWith(videoString)) {
                File tmpFile = File.createTempFile("upload-", ".mp4");
                tmpFile.deleteOnExit();
                try (FileOutputStream os = new FileOutputStream(tmpFile)) {
                    os.write(file.getBytes());
                }

                IsoFile isoFile = new IsoFile(tmpFile.getAbsolutePath());
                long duration = isoFile.getMovieBox().getMovieHeaderBox().getDuration();
                long timescale = isoFile.getMovieBox().getMovieHeaderBox().getTimescale();
                isoFile.close();

                long durationMillis = (duration * 1000) / timescale;
                double durationSec = durationMillis / 1000.0;

                var videoResult = cloudinary.uploader().upload(
                        file.getBytes(),
                        ObjectUtils.asMap(
                                "resource_type", videoString,
                                "upload_preset", "social-media",
                                "folder", videoString,
                                "eager", List.of(
                                        new EagerTransformation()
                                                .width(320)
                                                .height(240)
                                                .crop("thumb")
                                                .fetchFormat("jpg")
                                )
                        )
                );

                String videoUrl = videoResult.get(secureUrl).toString();
                @SuppressWarnings("unchecked")
                String thumbnailUrl = ((List<Map<String, String>>) videoResult.get("eager")).getFirst().get(
                        secureUrl);

                videoResponses.add(VideoResponse.builder()
                        .contentType(file.getContentType())
                        .videoUrl(videoUrl)
                        .thumbnailUrl(thumbnailUrl)
                        .playtimeSeconds(durationSec)
                        .playtimeString(formatDuration(durationMillis))
                        .fileSize(file.getSize())
                        .originFileName(file.getOriginalFilename())
                        .build());
            }
        }

        String id = UUID.randomUUID().toString();

        com.canhtv05.file.entity.File fileEntity = com.canhtv05.file.entity.File.builder()
                .id(id)
                .ownerId(userId)
                .totalSize(totalSize)
                .images(imageResponses.stream().map(img -> Image.builder()
                        .contentType(img.getContentType())
                        .imageUrl(img.getImageUrl())
                        .fileSize(img.getFileSize())
                        .originFileName(img.getOriginFileName())
                        .build()).toList())
                .videos(videoResponses.stream().map(vid -> Video.builder()
                        .contentType(vid.getContentType())
                        .videoUrl(vid.getVideoUrl())
                        .thumbnailUrl(vid.getThumbnailUrl())
                        .playtimeSeconds(vid.getPlaytimeSeconds())
                        .playtimeString(vid.getPlaytimeString())
                        .fileSize(vid.getFileSize())
                        .originFileName(vid.getOriginFileName())
                        .build()).toList())
                .build();
        fileRepository.save(fileEntity);

        return FileResponse.builder()
                .ownerId(userId)
                .id(id)
                .totalSize(totalSize)
                .images(imageResponses)
                .videos(videoResponses)
                .build();
    }

    @Override
    public List<FileResponse> getMyResources() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userId = auth.getName();

        return fileRepository
                .findAllByOwnerId(userId)
                .stream()
                .map(fileMapper::toFileResponse)
                .toList();
    }

    @Override
    public FileResponse getFileById(String id) {
        return fileRepository.getFileById(id)
                .orElseThrow(() -> new AppException(ErrorCode.FILE_NOT_FOUND));
    }

    private String formatDuration(long millis) {
        long seconds = millis / 1000;
        return String.format("%d:%02d", seconds / 60, seconds % 60);
    }
}
