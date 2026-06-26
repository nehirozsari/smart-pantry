package com.nehirozsari.smartpantry.security;

import com.nehirozsari.smartpantry.config.JwtProperties;
import com.nehirozsari.smartpantry.exception.UnauthorizedException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Service
public class JwtService {

    private final JwtProperties jwtProperties;
    private final SecretKey secretKey;

    public JwtService(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(encodeSecret(jwtProperties.getSecret())));
    }

    public String generateAccessToken(UserPrincipal principal) {
        Instant now = Instant.now();
        Instant expiry = now.plus(jwtProperties.getAccessTokenExpiration());

        return Jwts.builder()
                .subject(principal.getId().toString())
                .claim("email", principal.getEmail())
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiry))
                .signWith(secretKey)
                .compact();
    }

    public long getAccessTokenExpirationSeconds() {
        return jwtProperties.getAccessTokenExpiration().toSeconds();
    }

    public Instant getRefreshTokenExpiration() {
        return Instant.now().plus(jwtProperties.getRefreshTokenExpiration());
    }

    public UserPrincipal parseAccessToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            UUID userId = UUID.fromString(claims.getSubject());
            String email = claims.get("email", String.class);
            return new UserPrincipal(userId, email, "", true);
        } catch (JwtException | IllegalArgumentException ex) {
            throw new UnauthorizedException("Invalid or expired access token");
        }
    }

    private String encodeSecret(String secret) {
        return java.util.Base64.getEncoder().encodeToString(secret.getBytes(StandardCharsets.UTF_8));
    }
}
