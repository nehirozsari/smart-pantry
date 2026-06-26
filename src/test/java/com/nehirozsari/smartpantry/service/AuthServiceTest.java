package com.nehirozsari.smartpantry.service;

import com.nehirozsari.smartpantry.domain.entity.User;
import com.nehirozsari.smartpantry.domain.repository.UserRepository;
import com.nehirozsari.smartpantry.dto.request.LoginRequest;
import com.nehirozsari.smartpantry.dto.request.RegisterRequest;
import com.nehirozsari.smartpantry.dto.response.AuthResponse;
import com.nehirozsari.smartpantry.dto.response.UserResponse;
import com.nehirozsari.smartpantry.exception.ConflictException;
import com.nehirozsari.smartpantry.exception.UnauthorizedException;
import com.nehirozsari.smartpantry.mapper.UserMapper;
import com.nehirozsari.smartpantry.security.JwtService;
import com.nehirozsari.smartpantry.security.UserPrincipal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private RefreshTokenService refreshTokenService;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private AuthService authService;

    @Test
    void register_createsUserAndReturnsTokens() {
        RegisterRequest request = new RegisterRequest(
                "user@example.com", "Password1", "Nehir", "Ozsari");

        when(userRepository.existsByEmailIgnoreCase("user@example.com")).thenReturn(false);
        when(passwordEncoder.encode("Password1")).thenReturn("hashed-password");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(UUID.randomUUID());
            return user;
        });
        when(jwtService.generateAccessToken(any(UserPrincipal.class))).thenReturn("access-token");
        when(jwtService.getAccessTokenExpirationSeconds()).thenReturn(900L);
        when(refreshTokenService.createRefreshToken(any(User.class))).thenReturn("refresh-token");
        when(userMapper.toResponse(any(User.class))).thenReturn(
                new UserResponse(UUID.randomUUID(), "user@example.com", "Nehir", "Ozsari", null));

        AuthResponse response = authService.register(request);

        assertThat(response.accessToken()).isEqualTo("access-token");
        assertThat(response.refreshToken()).isEqualTo("refresh-token");
        assertThat(response.expiresIn()).isEqualTo(900L);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_throwsConflictWhenEmailExists() {
        RegisterRequest request = new RegisterRequest(
                "user@example.com", "Password1", "Nehir", "Ozsari");

        when(userRepository.existsByEmailIgnoreCase("user@example.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(ConflictException.class)
                .hasMessage("Email is already registered");

        verify(userRepository, never()).save(any());
    }

    @Test
    void login_throwsUnauthorizedForInvalidPassword() {
        User user = buildUser();
        when(userRepository.findByEmailIgnoreCase("user@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("WrongPass1", "hashed-password")).thenReturn(false);

        assertThatThrownBy(() -> authService.login(new LoginRequest("user@example.com", "WrongPass1")))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage("Invalid email or password");
    }

    private User buildUser() {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail("user@example.com");
        user.setPasswordHash("hashed-password");
        user.setFirstName("Nehir");
        user.setLastName("Ozsari");
        user.setEnabled(true);
        return user;
    }
}
