package com.nehirozsari.smartpantry.service;

import com.nehirozsari.smartpantry.domain.entity.User;
import com.nehirozsari.smartpantry.domain.repository.UserRepository;
import com.nehirozsari.smartpantry.dto.request.ChangePasswordRequest;
import com.nehirozsari.smartpantry.dto.request.LoginRequest;
import com.nehirozsari.smartpantry.dto.request.RefreshTokenRequest;
import com.nehirozsari.smartpantry.dto.request.RegisterRequest;
import com.nehirozsari.smartpantry.dto.response.AuthResponse;
import com.nehirozsari.smartpantry.exception.ConflictException;
import com.nehirozsari.smartpantry.exception.UnauthorizedException;
import com.nehirozsari.smartpantry.mapper.UserMapper;
import com.nehirozsari.smartpantry.security.JwtService;
import com.nehirozsari.smartpantry.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final UserMapper userMapper;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmailIgnoreCase(request.email())) {
            throw new ConflictException("Email is already registered");
        }

        User user = new User();
        user.setEmail(request.email().trim().toLowerCase());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setFirstName(request.firstName().trim());
        user.setLastName(request.lastName().trim());
        user.setEnabled(true);

        User savedUser = userRepository.save(user);
        return buildAuthResponse(savedUser);
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmailIgnoreCase(request.email())
                .orElseThrow(() -> new UnauthorizedException("Invalid email or password"));

        if (!user.isEnabled()) {
            throw new UnauthorizedException("Account is disabled");
        }

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new UnauthorizedException("Invalid email or password");
        }

        return buildAuthResponse(user);
    }

    @Transactional
    public AuthResponse refresh(RefreshTokenRequest request) {
        User user = refreshTokenService.validateAndGetUser(request.refreshToken());
        refreshTokenService.revoke(request.refreshToken());
        return buildAuthResponse(user);
    }

    @Transactional
    public void logout(RefreshTokenRequest request) {
        refreshTokenService.revoke(request.refreshToken());
    }

    @Transactional
    public void changePassword(UUID userId, ChangePasswordRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UnauthorizedException("User not found"));

        if (!passwordEncoder.matches(request.currentPassword(), user.getPasswordHash())) {
            throw new UnauthorizedException("Current password is incorrect");
        }

        user.setPasswordHash(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
        refreshTokenService.revokeAllForUser(userId);
    }

    private AuthResponse buildAuthResponse(User user) {
        UserPrincipal principal = new UserPrincipal(user.getId(), user.getEmail(), user.getPasswordHash(), user.isEnabled());
        String accessToken = jwtService.generateAccessToken(principal);
        String refreshToken = refreshTokenService.createRefreshToken(user);

        return new AuthResponse(
                accessToken,
                refreshToken,
                jwtService.getAccessTokenExpirationSeconds(),
                userMapper.toResponse(user)
        );
    }
}
