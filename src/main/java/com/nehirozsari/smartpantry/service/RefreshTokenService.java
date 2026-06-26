package com.nehirozsari.smartpantry.service;

import com.nehirozsari.smartpantry.domain.entity.RefreshToken;
import com.nehirozsari.smartpantry.domain.entity.User;
import com.nehirozsari.smartpantry.domain.repository.RefreshTokenRepository;
import com.nehirozsari.smartpantry.exception.UnauthorizedException;
import com.nehirozsari.smartpantry.security.JwtService;
import com.nehirozsari.smartpantry.security.TokenHasher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;

    @Transactional
    public String createRefreshToken(User user) {
        String rawToken = UUID.randomUUID().toString() + UUID.randomUUID();
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setTokenHash(TokenHasher.hash(rawToken));
        refreshToken.setExpiresAt(jwtService.getRefreshTokenExpiration());
        refreshTokenRepository.save(refreshToken);
        return rawToken;
    }

    @Transactional
    public User validateAndGetUser(String rawToken) {
        RefreshToken refreshToken = refreshTokenRepository
                .findByTokenHashAndRevokedFalse(TokenHasher.hash(rawToken))
                .orElseThrow(() -> new UnauthorizedException("Invalid refresh token"));

        if (refreshToken.getExpiresAt().isBefore(Instant.now())) {
            refreshToken.setRevoked(true);
            refreshTokenRepository.save(refreshToken);
            throw new UnauthorizedException("Refresh token has expired");
        }

        return refreshToken.getUser();
    }

    @Transactional
    public void revoke(String rawToken) {
        refreshTokenRepository.findByTokenHashAndRevokedFalse(TokenHasher.hash(rawToken))
                .ifPresent(token -> {
                    token.setRevoked(true);
                    refreshTokenRepository.save(token);
                });
    }

    @Transactional
    public void revokeAllForUser(UUID userId) {
        refreshTokenRepository.revokeAllByUserId(userId);
    }
}
