package com.nehirozsari.smartpantry.service;

import com.nehirozsari.smartpantry.domain.entity.User;
import com.nehirozsari.smartpantry.domain.repository.UserRepository;
import com.nehirozsari.smartpantry.dto.request.UpdateProfileRequest;
import com.nehirozsari.smartpantry.dto.response.UserResponse;
import com.nehirozsari.smartpantry.exception.ResourceNotFoundException;
import com.nehirozsari.smartpantry.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Transactional(readOnly = true)
    public UserResponse getProfile(UUID userId) {
        return userMapper.toResponse(findUser(userId));
    }

    @Transactional
    public UserResponse updateProfile(UUID userId, UpdateProfileRequest request) {
        User user = findUser(userId);

        if (request.firstName() != null) {
            user.setFirstName(request.firstName().trim());
        }
        if (request.lastName() != null) {
            user.setLastName(request.lastName().trim());
        }
        if (request.avatarUrl() != null) {
            user.setAvatarUrl(request.avatarUrl().trim());
        }

        return userMapper.toResponse(userRepository.save(user));
    }

    private User findUser(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
}
