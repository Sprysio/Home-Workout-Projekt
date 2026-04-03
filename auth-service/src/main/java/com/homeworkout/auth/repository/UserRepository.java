
package com.homeworkout.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.homeworkout.auth.model.User;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}