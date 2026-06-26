package com.nehirozsari.smartpantry.mapper;

import com.nehirozsari.smartpantry.domain.entity.User;
import com.nehirozsari.smartpantry.dto.response.UserResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserResponse toResponse(User user);
}
