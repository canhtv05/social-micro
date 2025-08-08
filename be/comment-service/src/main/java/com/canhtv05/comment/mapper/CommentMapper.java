package com.canhtv05.comment.mapper;

import com.canhtv05.comment.entity.Comment;
import org.mapstruct.Mapper;
import com.canhtv05.comment.dto.res.CommentResponse;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    @Mapping(target = "replies", ignore = true)
    CommentResponse toCommentResponse(Comment comment);
}
