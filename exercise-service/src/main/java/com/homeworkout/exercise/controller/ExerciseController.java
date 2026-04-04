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

@RestController
@RequestMapping("/exercises")
public class ExerciseController {
    private final ExerciseService service;
    private final JwtUtil jwtUtil;

    public ExerciseController(ExerciseService service, JwtUtil jwtUtil) {
        this.service = service;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping
    public ResponseEntity<List<Exercise>> listAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getOne(@PathVariable Long id) {
        Optional<Exercise> ex = service.findById(id);
        if (ex.isPresent()) {
            return ResponseEntity.ok(ex.get());
        } else {
            return ResponseEntity.status(404).body(Map.of("error", "not_found"));
        }
    }

    private ResponseEntity<?> requireAdmin(HttpServletRequest req) {
        String h = req.getHeader("Authorization");
        if (h == null || !h.startsWith("Bearer "))
            return ResponseEntity.status(401).body(Map.of("error", "missing_token"));
        String token = h.substring(7);
        if (!jwtUtil.validateToken(token)) return ResponseEntity.status(401).body(Map.of("error", "invalid_token"));
        if (!jwtUtil.getRolesFromToken(token).contains("ROLE_ADMIN"))
            return ResponseEntity.status(403).body(Map.of("error", "forbidden"));
        return null; // ok
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Exercise exercise, HttpServletRequest req) {
        ResponseEntity<?> authErr = requireAdmin(req);
        if (authErr != null) return authErr;
        Exercise saved = service.save(exercise);
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Exercise exercise, HttpServletRequest req) {
        ResponseEntity<?> authErr = requireAdmin(req);
        if (authErr != null) return authErr;

        Optional<Exercise> existingOpt = service.findById(id);
        if (existingOpt.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("error", "not_found"));
        }
        Exercise existing = existingOpt.get();
        existing.setName(exercise.getName());
        existing.setDescription(exercise.getDescription());
        existing.setMuscleGroup(exercise.getMuscleGroup());
        Exercise saved = service.save(existing);
        return ResponseEntity.ok(saved);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id, HttpServletRequest req) {
        ResponseEntity<?> authErr = requireAdmin(req);
        if (authErr != null) return authErr;
        service.deleteById(id);
        return ResponseEntity.ok(Map.of("deleted", id));
    }
}