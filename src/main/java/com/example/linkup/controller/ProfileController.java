package com.example.linkup.controller;

import com.example.linkup.dto.request.ApiResponse;
import com.example.linkup.dto.request.ProfileRequest;
import com.example.linkup.dto.response.ProfileResponse;
import com.example.linkup.service.ProfileService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/profile")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProfileController {
    ProfileService profileService;

    @GetMapping("")
    ApiResponse<List<ProfileResponse>> getAllProfile() {
        List<ProfileResponse> profilesList = profileService.getAllProfile();

        return ApiResponse.<List<ProfileResponse>>builder()
                .result(profilesList)
                .build();
    }

    @PatchMapping("/update-profile")
    ApiResponse<ProfileResponse> updateProfile(@RequestBody ProfileRequest request) {
        return ApiResponse.<ProfileResponse>builder()
                .result(profileService.updateProfile(request))
                .build();
    }

    @PostMapping(value = "/update-avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ApiResponse<ProfileResponse> updateAvatar(@RequestParam("avatar") MultipartFile avatar) {
        return ApiResponse.<ProfileResponse>builder()
                .result(profileService.updateAvatar(avatar))
                .build();
    }
}
