package com.canhtv05.post.mapper;

import com.canhtv05.post.dto.res.ReactionResponse;
import com.canhtv05.post.entity.Reaction;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ReactionMapper {

    ReactionResponse toReactionResponse(Reaction reaction);
}
