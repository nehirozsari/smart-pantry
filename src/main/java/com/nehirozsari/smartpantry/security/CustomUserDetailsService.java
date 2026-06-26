package com.nehirozsari.smartpantry.security;

import com.nehirozsari.smartpantry.domain.entity.User;
import com.nehirozsari.smartpantry.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) {
        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return toPrincipal(user);
    }

    @Transactional(readOnly = true)
    public UserPrincipal loadById(java.util.UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return toPrincipal(user);
    }

    private UserPrincipal toPrincipal(User user) {
        return new UserPrincipal(user.getId(), user.getEmail(), user.getPasswordHash(), user.isEnabled());
    }
}
