package com.example.linkup.mapper;

import com.example.linkup.dto.request.UserCreationRequest;
import com.example.linkup.dto.response.UserResponse;
import com.example.linkup.entity.Users;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    Users userCreationRequestToUser(UserCreationRequest request);

    UserResponse userToUserResponse(Users users);
}
