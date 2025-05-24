package com.example.linkup.controller;

import com.example.linkup.dto.request.ApiResponse;
import com.example.linkup.dto.response.ProfileWithUserResponse;
import com.example.linkup.entity.Profiles;
import com.example.linkup.service.ProfileService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/profiles")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProfileController {
    ProfileService profileService;

    @GetMapping("")
    ApiResponse<List<ProfileWithUserResponse>> getAllProfile() {
        List<ProfileWithUserResponse> profilesList = profileService.getAllProfile();

        return ApiResponse.<List<ProfileWithUserResponse>>builder()
                .result(profilesList)
                .build();
    }
}
