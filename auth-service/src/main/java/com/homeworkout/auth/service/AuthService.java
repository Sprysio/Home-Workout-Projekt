
package com.homeworkout.auth.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.homeworkout.auth.repository.UserRepository;
import com.homeworkout.auth.model.User;
import com.homeworkout.auth.model.Role;

import java.util.HashSet;
import java.util.Optional;

@Service
public class AuthService {
    private final UserRepository repo;
    private final PasswordEncoder encoder;

    public AuthService(UserRepository repo, PasswordEncoder encoder) {
        this.repo = repo;
        this.encoder = encoder;
    }

    public User register(User u) {
        u.setPassword(encoder.encode(u.getPassword()));
        if (u.getRoles() == null) {
            u.setRoles(new HashSet<>());
        }
        if (u.getRoles().isEmpty()) {
            u.getRoles().add(Role.ROLE_USER);
        }
        return repo.save(u);
    }

    public Optional<User> findByUsername(String username) {
        return repo.findByUsername(username);
    }

    public Optional<User> findById(Long id) {
        return repo.findById(id);
    }

    public User save(User u) {
        return repo.save(u);
    }
}