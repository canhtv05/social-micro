package com.canhtv05.file.mapper;

import com.canhtv05.file.dto.res.FileResponse;
import com.canhtv05.file.dto.res.ImageResponse;
import com.canhtv05.file.dto.res.VideoResponse;
import com.canhtv05.file.entity.File;
import com.canhtv05.file.entity.Image;
import com.canhtv05.file.entity.Video;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FileMapper {

    FileResponse toFileResponse(File file);

    VideoResponse videoToVideoResponse(Video video);

    ImageResponse imageToImageResponse(Image image);
}
