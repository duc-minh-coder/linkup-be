package com.example.linkup.service;

import com.example.linkup.dto.request.UserCreationRequest;
import com.example.linkup.dto.response.UserResponse;
import com.example.linkup.entity.Profiles;
import com.example.linkup.entity.Users;
import com.example.linkup.exception.AppException;
import com.example.linkup.exception.ErrorCode;
import com.example.linkup.mapper.UserMapper;
import com.example.linkup.repository.ProfileRepository;
import com.example.linkup.repository.UserRepository;
import com.nimbusds.jose.proc.SecurityContext;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService {
    UserRepository userRepository;
    ProfileRepository profileRepository;
    UserMapper userMapper;
    PasswordEncoder passwordEncoder;

    public UserResponse createUser(UserCreationRequest request) {
        Users users = userMapper.userCreationRequestToUser(request);
        users.setPassword(passwordEncoder.encode(request.getPassword()));

        try {
            userRepository.save(users);

            Profiles profiles = new Profiles();
            profiles.setUsers(users);

            profileRepository.save(profiles);
        } catch (Exception e) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }

        return userMapper.userToUserResponse(users);
    }

    public UserResponse getUser(int userId) {
        return userMapper.userToUserResponse(
                userRepository.findById(userId)
                        .orElseThrow(() -> new RuntimeException("user not exist")));
    }

//    @PostAuthorize("returnObject.username == authentication.name")
    public UserResponse getMyInfo() {
        var context = SecurityContextHolder.getContext();
        String username = context.getAuthentication().getName();

        Users users = userRepository.findByUsername(username).orElseThrow(() ->
                new AppException(ErrorCode.USER_NOT_EXISTED));

        return userMapper.userToUserResponse(users);
    }

    public List<UserResponse> getAllUser() {
        List<Users> usersList = userRepository.findAll();

        return usersList.stream().map(userMapper::userToUserResponse).toList();
    }
}
