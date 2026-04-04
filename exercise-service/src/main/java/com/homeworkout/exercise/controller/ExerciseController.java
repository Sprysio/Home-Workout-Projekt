package com.homeworkout.exercise.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.homeworkout.exercise.model.Exercise;
import com.homeworkout.exercise.service.ExerciseService;
import com.homeworkout.exercise.security.JwtUtil;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/exercises")
public class ExerciseController {

    private static final String ROLE_ADMIN = "ROLE_ADMIN";

    private final ExerciseService service;
    private final JwtUtil jwtUtil;

    public ExerciseController(ExerciseService service, JwtUtil jwtUtil) {
        this.service = service;
        this.jwtUtil = jwtUtil;
    }

    private Optional<Set<String>> extractRoles(HttpServletRequest req) {
        String h = req.getHeader("Authorization");

        if (h == null || !h.startsWith("Bearer ")) {
            return Optional.empty();
        }

        String token = h.substring(7);

        if (!jwtUtil.validateToken(token)) {
            return Optional.empty();
        }

        return Optional.of(jwtUtil.getRolesFromToken(token));
    }

    private ResponseEntity<?> checkAdmin(HttpServletRequest req) {
        Optional<Set<String>> rolesOpt = extractRoles(req);

        if (rolesOpt.isEmpty()) {
            return ResponseEntity.status(401).body(Map.of("error", "unauthorized"));
        }

        if (!rolesOpt.get().contains(ROLE_ADMIN)) {
            return ResponseEntity.status(403).body(Map.of("error", "forbidden"));
        }

        return null;
    }

    @GetMapping
    public ResponseEntity<List<Exercise>> listAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getOne(@PathVariable Long id) {
        return service.findById(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(404).body(Map.of("error", "not_found")));
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Exercise exercise, HttpServletRequest req) {
        ResponseEntity<?> authErr = checkAdmin(req);
        if (authErr != null) return authErr;

        if (exercise == null || exercise.getName() == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "invalid_payload"));
        }

        Exercise saved = service.save(exercise);
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id,
                                    @RequestBody Exercise exercise,
                                    HttpServletRequest req) {

        ResponseEntity<?> authErr = checkAdmin(req);
        if (authErr != null) return authErr;

        Optional<Exercise> existingOpt = service.findById(id);
        if (existingOpt.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("error", "not_found"));
        }

        if (exercise == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "invalid_payload"));
        }

        Exercise existing = existingOpt.get();

        if (exercise.getName() != null) {
            existing.setName(exercise.getName());
        }
        if (exercise.getDescription() != null) {
            existing.setDescription(exercise.getDescription());
        }
        if (exercise.getMuscleGroup() != null) {
            existing.setMuscleGroup(exercise.getMuscleGroup());
        }

        Exercise saved = service.save(existing);
        return ResponseEntity.ok(saved);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id, HttpServletRequest req) {
        ResponseEntity<?> authErr = checkAdmin(req);
        if (authErr != null) return authErr;

        Optional<Exercise> existingOpt = service.findById(id);
        if (existingOpt.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("error", "not_found"));
        }

        service.deleteById(id);
        return ResponseEntity.ok(Map.of("deleted", id));
    }
}