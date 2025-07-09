package com.example.linkup.service;

import com.example.linkup.dto.request.ProfileRequest;
import com.example.linkup.dto.request.SearchProfileRequest;
import com.example.linkup.dto.response.FriendshipResponse;
import com.example.linkup.dto.response.PostResponse;
import com.example.linkup.dto.response.ProfileResponse;
import com.example.linkup.dto.response.SearchProfileResponse;
import com.example.linkup.entity.Friendships;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static java.util.stream.Collectors.toList;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProfileService {
    ProfileRepository profileRepository;
    UserRepository userRepository;
    ProfileMapper profileMapper;
    CloudinaryService cloudinaryService;
    FriendshipService friendshipService;
    PostService postService;

    public ProfileResponse getProfile() {
        var context = SecurityContextHolder.getContext();
        String username = context.getAuthentication().getName();

        Users users = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        List<FriendshipResponse> friendshipResponseList = friendshipService.getFriends(users.getId());

        List<PostResponse> postResponseList = postService.getAllUrPost();

        return ProfileResponse.builder()
                .id(users.getProfile().getUserId())
                .fullName(users.getProfile().getFullName())
                .avatarUrl(users.getProfile().getAvatarUrl())
                .location(users.getProfile().getLocation())
                .bio(users.getProfile().getBio())
                .birthday(users.getProfile().getBirthday())
                .countFriend(friendshipResponseList.size())
                .countPost(postResponseList.size())
                .build();
    }

    public ProfileResponse getProfileWithId(int userId) {
        Profiles userProfile = profileRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.PROFILE_NOT_EXISTED));

        List<FriendshipResponse> friendshipResponseList = friendshipService.getFriends(userProfile.getUserId());

        List<PostResponse> postResponseList = postService.getPostsByUserId(userProfile.getUserId());

        return ProfileResponse.builder()
                .id(userProfile.getUserId())
                .fullName(userProfile.getFullName())
                .avatarUrl(userProfile.getAvatarUrl())
                .location(userProfile.getLocation())
                .bio(userProfile.getBio())
                .birthday(userProfile.getBirthday())
                .countFriend(friendshipResponseList.size())
                .countPost(postResponseList.size())
                .build();
    }

    public List<SearchProfileResponse> searchUser(SearchProfileRequest request) {
        List<Profiles> profilesList = profileRepository.searchUserProfileByName(request.getText());

        return profilesList.stream().map(profile ->
                SearchProfileResponse.builder()
                        .id(profile.getUserId())
                        .fullName(profile.getFullName())
                        .avatarUrl(profile.getAvatarUrl())
                        .build()
        ).toList();
    }

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

        if (request.getBirthday() != null)
            profiles.setBirthday(request.getBirthday());

        profiles.setUpdatedTime(new Date());

        profileRepository.save(profiles);

        return profileMapper.profilesToProfileResponse(profiles);
    }

    public String updateAvatar(MultipartFile avatar) {
        if (avatar.isEmpty())
            throw new AppException(ErrorCode.FILE_UPLOAD_ERROR);

        var context = SecurityContextHolder.getContext();
        String username = context.getAuthentication().getName();

        Users users = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        int userId = users.getId();

        Profiles profiles = profileRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.PROFILE_NOT_EXISTED));

        try {
            String avatarUrl = cloudinaryService.uploadFile(avatar);

            profiles.setAvatarUrl(avatarUrl);

            profileRepository.save(profiles);

            return "avatar has been updated!";
        } catch (IOException e) {
            throw new AppException(ErrorCode.FILE_UPLOAD_ERROR);
        }
    }

    public String updateProfilePicture(MultipartFile profilePicture) {
        if (profilePicture.isEmpty())
            throw new AppException(ErrorCode.FILE_UPLOAD_ERROR);

        var context = SecurityContextHolder.getContext();
        String username = context.getAuthentication().getName();

        Users users = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        int userId = users.getId();

        Profiles profiles = profileRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.PROFILE_NOT_EXISTED));

        try {
            String profileUrl = cloudinaryService.uploadFile(profilePicture);

            profiles.setProfilePictureUrl(profileUrl);

            profileRepository.save(profiles);

            return "profile picture has been updated!";
        } catch (IOException e) {
            throw new AppException(ErrorCode.FILE_UPLOAD_ERROR);
        }
    }
}
