package com.example.linkup.service;

import com.example.linkup.dto.request.UserCreationRequest;
import com.example.linkup.dto.response.UserResponse;
import com.example.linkup.entity.Users;
import com.example.linkup.mapper.UserMapper;
import com.example.linkup.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService {
    UserRepository userRepository;
    UserMapper userMapper;

    public UserResponse createUser(UserCreationRequest request) throws Exception {
        Users users = userMapper.userCreationRequestToUser(request);

        try {
            userRepository.save(users);
        } catch (Exception e) {
            throw new Exception("can't save user");
        }

        return userMapper.userToUserResponse(users);
    }

    public UserResponse getUser(int userId) {
        return userMapper.userToUserResponse(
                userRepository.findById(userId)
                        .orElseThrow(() -> new RuntimeException("user not exist")));
    }
}
