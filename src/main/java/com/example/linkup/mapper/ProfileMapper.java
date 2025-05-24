package com.example.linkup.mapper;

import com.example.linkup.dto.response.ProfileWithUserResponse;
import com.example.linkup.entity.Profiles;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProfileMapper {
    ProfileWithUserResponse toProfileWithUserResponse(Profiles profiles);
}
