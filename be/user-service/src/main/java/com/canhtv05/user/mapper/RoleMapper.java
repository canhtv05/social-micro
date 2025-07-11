package com.canhtv05.user.mapper;

import com.canhtv05.user.dto.res.RoleResponse;
import com.canhtv05.user.enity.Role;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RoleMapper {

    RoleResponse toRoleResponse(Role role);
}
