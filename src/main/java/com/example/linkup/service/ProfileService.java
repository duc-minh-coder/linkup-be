package com.example.linkup.service;

import com.example.linkup.dto.response.ProfileWithUserResponse;
import com.example.linkup.entity.Profiles;
import com.example.linkup.mapper.ProfileMapper;
import com.example.linkup.repository.ProfileRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProfileService {
    ProfileRepository profileRepository;
    ProfileMapper profileMapper;

    public List<ProfileWithUserResponse> getAllProfile() {
        List<ProfileWithUserResponse> profilesList = profileRepository.findAllProfilesWithUserResponse();

        return profilesList.stream().toList();
    }
}
