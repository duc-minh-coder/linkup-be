package com.example.linkup.service;

import com.example.linkup.dto.request.LogoutRequest;
import com.example.linkup.dto.request.UserCreationRequest;
import com.example.linkup.dto.request.UserUpdatePasswordRequest;
import com.example.linkup.dto.response.UserResponse;
import com.example.linkup.entity.InvalidatedToken;
import com.example.linkup.entity.Profiles;
import com.example.linkup.entity.Users;
import com.example.linkup.exception.AppException;
import com.example.linkup.exception.ErrorCode;
import com.example.linkup.mapper.UserMapper;
import com.example.linkup.repository.InvalidatedTokenRepository;
import com.example.linkup.repository.ProfileRepository;
import com.example.linkup.repository.UserRepository;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.proc.SecurityContext;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService {
    UserRepository userRepository;
    ProfileRepository profileRepository;
    UserMapper userMapper;
    PasswordEncoder passwordEncoder;
    InvalidatedTokenRepository invalidatedTokenRepository;
    AuthenticationService authenticationService;

    public UserResponse createUser(UserCreationRequest request) {
        if (userRepository.existsByUsername(request.getUsername()))
            throw new AppException(ErrorCode.USER_EXISTED);

        Users users = Users.builder()
                .username(request.getUsername())
                .createdTime(new Date())
                .updatedTime(new Date())
                .build();
        users.setPassword(passwordEncoder.encode(request.getPassword()));

        users = userRepository.save(users);

        Profiles profiles = new Profiles();
        profiles.setFullName(request.getFullName());
        profiles.setAvatarUrl("https://res.cloudinary.com/dcvrdyzo1/image/upload/v1750326357/tj2seoxw5otvcfwnxhdi.png");
        profiles.setUpdatedTime(new Date());

        //gán mqh 2 chiều
        users.setProfile(profiles);
        profiles.setUsers(users);

        profileRepository.save(profiles);

        return UserResponse.builder()
                .username(users.getUsername())
                .createdTime(new Date())
                .build();
    }

    public UserResponse getUser(int userId) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("user not exist"));

        return UserResponse.builder()
                .username(user.getUsername())
                .createdTime(user.getCreatedTime())
                .build();
    }

//    @PostAuthorize("returnObject.username == authentication.name")
    public UserResponse getMyInfo() {
        var context = SecurityContextHolder.getContext();
        String username = context.getAuthentication().getName();

        Users users = userRepository.findByUsername(username).orElseThrow(() ->
                new AppException(ErrorCode.USER_NOT_EXISTED));

        return UserResponse.builder()
                .username(users.getUsername())
                .createdTime(users.getCreatedTime())
                .build();
    }

    public List<UserResponse> getAllUser() {
        List<Users> usersList = userRepository.findAll();

        return usersList.stream().map(users ->
                UserResponse.builder()
                    .username(users.getUsername())
                    .createdTime(users.getCreatedTime())
                    .build()
        ).toList();
    }

    public UserResponse updatePassword(UserUpdatePasswordRequest request, String token) {
        var context = SecurityContextHolder.getContext();
        String username = context.getAuthentication().getName();

        Users users = userRepository.findByUsername(username).orElseThrow(() ->
                new AppException(ErrorCode.USER_NOT_EXISTED));

        if (!passwordEncoder.matches(request.getOldPassword(), users.getPassword()))
            throw new AppException(ErrorCode.WRONG_PASSWORD);

        users.setPassword(passwordEncoder.encode(request.getNewPassword()));
        users.setUpdatedTime(new Date());

        userRepository.save(users);

        try {
            var jwt = authenticationService.verifyToken(token, false); // dùng verifyToken ở AuthService
            String jti = jwt.getJWTClaimsSet().getJWTID();
            Date expiry = jwt.getJWTClaimsSet().getExpirationTime();

            InvalidatedToken invalidatedToken = InvalidatedToken.builder()
                    .id(jti)
                    .expiryTime(expiry)
                    .build();

            invalidatedTokenRepository.save(invalidatedToken);
        } catch (Exception e) {
            throw new AppException(ErrorCode.TOKEN_INVALID); // nếu token gửi lên sai
        }

        return UserResponse.builder()
                .username(users.getUsername())
                .createdTime(users.getCreatedTime())
                .build();
    }
}
