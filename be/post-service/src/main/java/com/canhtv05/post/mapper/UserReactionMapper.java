package com.canhtv05.post.mapper;

import com.canhtv05.post.dto.res.UserReactionResponse;
import com.canhtv05.post.entity.UserReaction;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserReactionMapper {

    UserReactionResponse toReactionResponse(UserReaction reaction);
}
