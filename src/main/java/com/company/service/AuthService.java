package com.company.service;

import com.company.model.User;
import com.company.model.Authority;
import com.company.repository.UserRepository;
import com.company.repository.AuthorityRepository;
import com.company.util.JwtUtil;
import com.company.dto.RegisterDTO;
import com.company.dto.LoginDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final AuthorityRepository authorityRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public Object register(RegisterDTO request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            return Map.of("error", "Username already exists");
        }
        Authority authority = authorityRepository.findByAuthority(request.getAuthority().name())
                .orElseGet(() -> {
                    Authority newAuth = new Authority();
                    newAuth.setAuthority(request.getAuthority().name());
                    return authorityRepository.save(newAuth);
                });
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setAuthorities(Set.of(authority));
        userRepository.save(user);
        return Map.of("message", "User registered successfully");
    }

    public Object login(LoginDTO request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );
            String token = jwtUtil.generateToken(authentication);
            String refreshToken = jwtUtil.generateRefreshTokenFromUsername(request.getUsername());
            return Map.of(
                    "token", token,
                    "refreshToken", refreshToken
            );
        } catch (AuthenticationException e) {
            return Map.of("error", "Invalid username or password");
        }
    }

    public Object refresh(String refreshToken) {
        try {
            String username = jwtUtil.getUsernameFromToken(refreshToken);
            String newToken = jwtUtil.generateTokenFromUsername(username);
            String newRefreshToken = jwtUtil.generateRefreshTokenFromUsername(username);
            return Map.of(
                    "token", newToken,
                    "refreshToken", newRefreshToken
            );
        } catch (Exception e) {
            return Map.of("error", "Invalid refresh token");
        }
    }
}
