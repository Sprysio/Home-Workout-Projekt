package com.homeworkout.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import com.homeworkout.auth.model.User;
import com.homeworkout.auth.model.Role;
import com.homeworkout.auth.repository.UserRepository;
import com.homeworkout.auth.service.AuthService;
import com.homeworkout.auth.security.JwtUtil;
import com.homeworkout.auth.dto.UserDto;

import jakarta.servlet.http.HttpServletRequest;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepo;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    public AuthController(AuthService authService,
                          UserRepository userRepo,
                          JwtUtil jwtUtil,
                          PasswordEncoder passwordEncoder) {
        this.authService = authService;
        this.userRepo = userRepo;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }

    private Optional<User> getAuthenticatedUser(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return Optional.empty();
        }

        String token = authHeader.substring(7);

        if (!jwtUtil.validateToken(token)) {
            return Optional.empty();
        }

        String username = jwtUtil.getUsernameFromToken(token);
        return authService.findByUsername(username);
    }

    private boolean isAdmin(User user) {
        return user.getRoles().stream().anyMatch(r -> r == Role.ROLE_ADMIN);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {

        if (user == null ||
                user.getUsername() == null ||
                user.getEmail() == null ||
                user.getPassword() == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "invalid_payload"));
        }

        if (userRepo.existsByUsername(user.getUsername()) ||
                userRepo.existsByEmail(user.getEmail())) {
            return ResponseEntity.badRequest().body(Map.of("error", "username_or_email_taken"));
        }

        User saved = authService.register(user);

        return ResponseEntity.ok(Map.of(
                "id", saved.getId(),
                "username", saved.getUsername(),
                "roles", saved.getRoles()
        ));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {

        if (body == null || !body.containsKey("username") || !body.containsKey("password")) {
            return ResponseEntity.badRequest().body(Map.of("error", "invalid_payload"));
        }

        String username = body.get("username");
        String password = body.get("password");

        if (username == null || password == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "invalid_payload"));
        }

        return userRepo.findByUsername(username)
                .map(u -> {
                    if (passwordEncoder.matches(password, u.getPassword())) {
                        String token = jwtUtil.generateToken(u.getUsername(), u.getRoles());
                        return ResponseEntity.ok(Map.of("token", token));
                    } else {
                        return ResponseEntity.status(401).body(Map.of("error", "invalid_credentials"));
                    }
                })
                .orElse(ResponseEntity.status(401).body(Map.of("error", "invalid_credentials")));
    }

    @PutMapping("/users/{id}/role")
    public ResponseEntity<?> changeRole(@PathVariable Long id,
                                        @RequestBody Map<String, String> body,
                                        HttpServletRequest request) {

        Optional<User> actorOpt = getAuthenticatedUser(request);

        if (actorOpt.isEmpty()) {
            return ResponseEntity.status(401).body(Map.of("error", "unauthorized"));
        }

        User actor = actorOpt.get();

        if (!isAdmin(actor)) {
            return ResponseEntity.status(403).body(Map.of("error", "forbidden"));
        }

        String roleStr = body.get("role");
        if (roleStr == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "role_missing"));
        }

        String normalized = roleStr.toUpperCase();
        if (!normalized.startsWith("ROLE_")) {
            normalized = "ROLE_" + normalized;
        }

        Role newRole;
        try {
            newRole = Role.valueOf(normalized);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", "invalid_role"));
        }

        Optional<User> targetOpt = authService.findById(id);
        if (targetOpt.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("error", "user_not_found"));
        }

        User target = targetOpt.get();
        target.getRoles().clear();
        target.getRoles().add(newRole);

        authService.save(target);

        return ResponseEntity.ok(Map.of(
                "id", target.getId(),
                "username", target.getUsername(),
                "roles", target.getRoles()
        ));
    }

    @GetMapping("/users")
    public ResponseEntity<?> listUsers(HttpServletRequest request) {

        Optional<User> actorOpt = getAuthenticatedUser(request);

        if (actorOpt.isEmpty()) {
            return ResponseEntity.status(401).body(Map.of("error", "unauthorized"));
        }

        User actor = actorOpt.get();

        if (!isAdmin(actor)) {
            return ResponseEntity.status(403).body(Map.of("error", "forbidden"));
        }

        List<UserDto> users = userRepo.findAll().stream()
                .map(u -> new UserDto(
                        u.getId(),
                        u.getUsername(),
                        u.getEmail(),
                        u.getRoles()
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(users);
    }
}