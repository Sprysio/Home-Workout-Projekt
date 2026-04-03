// java
package com.homeworkout.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import com.homeworkout.auth.model.User;
import com.homeworkout.auth.repository.UserRepository;
import com.homeworkout.auth.service.AuthService;
import com.homeworkout.auth.security.JwtUtil;

import java.util.Map;
@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;
    private final UserRepository userRepo;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    public AuthController(AuthService authService, UserRepository userRepo, JwtUtil jwtUtil, PasswordEncoder passwordEncoder) {
        this.authService = authService;
        this.userRepo = userRepo;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        if (userRepo.existsByUsername(user.getUsername()) || userRepo.existsByEmail(user.getEmail())) {
            return ResponseEntity.badRequest().body(Map.of("error", "username_or_email_taken"));
        }
        User saved = authService.register(user);
        return ResponseEntity.ok(Map.of("id", saved.getId(), "username", saved.getUsername()));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String,String> body) {
        String username = body.get("username");
        String password = body.get("password");
        return userRepo.findByUsername(username)
                .map(u -> {
                    if (passwordEncoder.matches(password, u.getPassword())) {
                        String token = jwtUtil.generateToken(u.getUsername());
                        return ResponseEntity.ok(Map.of("token", token));
                    } else {
                        return ResponseEntity.status(401).body(Map.of("error", "invalid_credentials"));
                    }
                }).orElse(ResponseEntity.status(401).body(Map.of("error", "invalid_credentials")));
    }
}