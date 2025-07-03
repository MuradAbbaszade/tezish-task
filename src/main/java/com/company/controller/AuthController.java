package com.company.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.stream.Collectors;

import com.company.dto.RegisterDTO;
import com.company.dto.LoginDTO;
import com.company.service.AuthService;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterDTO request) {
        Object result = authService.register(request);
        if (result instanceof Map && ((Map<?, ?>) result).containsKey("error")) {
            return ResponseEntity.badRequest().body(result);
        }
        return ResponseEntity.ok(result);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO request) {
        Object result = authService.login(request);
        if (result instanceof Map && ((Map<?, ?>) result).containsKey("error")) {
            return ResponseEntity.status(401).body(result);
        }
        return ResponseEntity.ok(result);
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestParam String refreshToken) {
        Object result = authService.refresh(refreshToken);
        if (result instanceof Map && ((Map<?, ?>) result).containsKey("error")) {
            return ResponseEntity.status(401).body(result);
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping("/me")
    public ResponseEntity<?> getMe(Authentication authentication) {
        String username = authentication.getName();
        var roles = authentication.getAuthorities().stream()
                .map(a -> a.getAuthority())
                .collect(Collectors.toList());
        return ResponseEntity.ok(java.util.Map.of(
            "username", username,
            "roles", roles
        ));
    }
}
