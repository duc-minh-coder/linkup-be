package com.example.linkup.mapper;

import com.example.linkup.dto.response.ProfileResponse;
import com.example.linkup.entity.Profiles;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProfileMapper {
    ProfileResponse profilesToProfileResponse(Profiles profiles);
}
