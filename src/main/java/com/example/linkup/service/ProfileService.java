package com.example.linkup.service;

import com.example.linkup.dto.request.ProfileRequest;
import com.example.linkup.dto.response.ProfileResponse;
import com.example.linkup.entity.Profiles;
import com.example.linkup.entity.Users;
import com.example.linkup.exception.AppException;
import com.example.linkup.exception.ErrorCode;
import com.example.linkup.mapper.ProfileMapper;
import com.example.linkup.repository.ProfileRepository;
import com.example.linkup.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProfileService {
    ProfileRepository profileRepository;
    UserRepository userRepository;
    ProfileMapper profileMapper;

    public List<ProfileResponse> getAllProfile() {
        List<Profiles> profilesList = profileRepository.findAll();

        return profilesList.stream().map(profileMapper::profilesToProfileResponse).toList();
    }

    public ProfileResponse updateProfile(ProfileRequest request) {
        var context = SecurityContextHolder.getContext();
        String username = context.getAuthentication().getName();

        Users users = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        int userId = users.getId();

        Profiles profiles = profileRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.PROFILE_NOT_EXISTED));

        if (request.getFullName() != null)
            profiles.setFullName(request.getFullName());

        if (request.getBio() != null)
            profiles.setBio(request.getBio());

        if (request.getLocation() != null)
            profiles.setLocation(request.getLocation());

        if (request.getAvatarUrl() != null)
            profiles.setAvatarUrl(request.getAvatarUrl());

        if (request.getBirthday() != null)
            profiles.setBirthday(request.getBirthday());

        if (request.getProfilePicture() != null)
            profiles.setProfilePicture(request.getProfilePicture());

        profileRepository.save(profiles);

        return profileMapper.profilesToProfileResponse(profiles);
    }
}
