package com.example.linkup.controller;

import com.example.linkup.dto.request.ApiResponse;
import com.example.linkup.dto.request.ProfileRequest;
import com.example.linkup.dto.request.SearchProfileRequest;
import com.example.linkup.dto.response.ProfileResponse;
import com.example.linkup.dto.response.SearchProfileResponse;
import com.example.linkup.service.ProfileService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/profiles")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProfileController {
    ProfileService profileService;

    @GetMapping("/user")
    ApiResponse<ProfileResponse> getProfile() {
        ProfileResponse profilesList = profileService.getProfile();

        return ApiResponse.<ProfileResponse>builder()
                .result(profilesList)
                .build();
    }

    @GetMapping("/user/{userId}")
    ApiResponse<ProfileResponse> getProfileWithId(@PathVariable int userId) {
        ProfileResponse profileResponse = profileService.getProfileWithId(userId);

        return ApiResponse.<ProfileResponse>builder()
                .result(profileResponse)
                .build();
    }

    @PostMapping("/search")
    ApiResponse<List<SearchProfileResponse>> searchUser(@RequestBody SearchProfileRequest request) {
        return ApiResponse.<List<SearchProfileResponse>>builder()
                .result(profileService.searchUser(request))
                .build();
    }

    @GetMapping("")
    ApiResponse<List<ProfileResponse>> getAllProfile() {
        List<ProfileResponse> profilesList = profileService.getAllProfile();

        return ApiResponse.<List<ProfileResponse>>builder()
                .result(profilesList)
                .build();
    }

    @PostMapping("/update-profile")
    ApiResponse<ProfileResponse> updateProfile(@RequestBody ProfileRequest request) {
        return ApiResponse.<ProfileResponse>builder()
                .result(profileService.updateProfile(request))
                .build();
    }

    @PostMapping(value = "/update-avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ApiResponse<String> updateAvatar(@RequestParam("avatar") MultipartFile avatar) {
        return ApiResponse.<String>builder()
                .result(profileService.updateAvatar(avatar))
                .build();
    }

    @PostMapping(value = "/update-profile-picture", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ApiResponse<String> updateProfilePicture(@RequestParam("profilePicture") MultipartFile profilePicture) {
        return ApiResponse.<String>builder()
                .result(profileService.updateProfilePicture(profilePicture))
                .build();
    }
}