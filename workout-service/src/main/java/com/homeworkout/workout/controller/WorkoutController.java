package com.homeworkout.workout.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.homeworkout.workout.model.WorkoutPlan;
import com.homeworkout.workout.model.WorkoutItem;
import com.homeworkout.workout.repository.WorkoutRepository;
import com.homeworkout.workout.security.JwtUtil;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/plans")
public class WorkoutController {

    private final WorkoutRepository repo;
    private final JwtUtil jwtUtil;

    public WorkoutController(WorkoutRepository repo, JwtUtil jwtUtil) {
        this.repo = repo;
        this.jwtUtil = jwtUtil;
    }

    private Optional<String> extractUsername(HttpServletRequest req) {
        String h = req.getHeader("Authorization");

        if (h == null || !h.startsWith("Bearer ")) {
            return Optional.empty();
        }

        String token = h.substring(7);

        if (!jwtUtil.validateToken(token)) {
            return Optional.empty();
        }

        return Optional.of(jwtUtil.getUsernameFromToken(token));
    }

    private ResponseEntity<?> requireAuth(HttpServletRequest req) {
        if (extractUsername(req).isEmpty()) {
            return ResponseEntity.status(401).body(Map.of("error", "unauthorized"));
        }
        return null;
    }

    private ResponseEntity<?> authorizeAndGetPlan(HttpServletRequest req, Long planId) {
        Optional<String> usernameOpt = extractUsername(req);
        if (usernameOpt.isEmpty()) {
            return ResponseEntity.status(401).body(Map.of("error", "unauthorized"));
        }

        Optional<WorkoutPlan> opt = repo.findById(planId);
        if (opt.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("error", "not_found"));
        }

        WorkoutPlan plan = opt.get();

        if (!usernameOpt.get().equals(plan.getOwnerUsername())) {
            return ResponseEntity.status(403).body(Map.of("error", "forbidden"));
        }

        return ResponseEntity.ok(plan);
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody WorkoutPlan p, HttpServletRequest req) {

        Optional<String> usernameOpt = extractUsername(req);
        if (usernameOpt.isEmpty()) {
            return ResponseEntity.status(401).body(Map.of("error", "unauthorized"));
        }

        if (p == null || p.getName() == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "invalid_payload"));
        }

        p.setOwnerUsername(usernameOpt.get());
        p.setItems(p.getItems() == null ? List.of() : p.getItems());

        WorkoutPlan saved = repo.save(p);
        return ResponseEntity.ok(saved);
    }

    @GetMapping
    public ResponseEntity<?> listOwn(HttpServletRequest req) {

        Optional<String> usernameOpt = extractUsername(req);
        if (usernameOpt.isEmpty()) {
            return ResponseEntity.status(401).body(Map.of("error", "unauthorized"));
        }

        List<WorkoutPlan> plans = repo.findByOwnerUsername(usernameOpt.get());
        return ResponseEntity.ok(plans);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getOne(@PathVariable Long id, HttpServletRequest req) {

        ResponseEntity<?> authRes = authorizeAndGetPlan(req, id);
        if (!authRes.getStatusCode().is2xxSuccessful()) return authRes;
        WorkoutPlan plan = (WorkoutPlan) authRes.getBody();

        return ResponseEntity.ok(plan);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id,
                                    @RequestBody WorkoutPlan p,
                                    HttpServletRequest req) {

        ResponseEntity<?> authRes = authorizeAndGetPlan(req, id);
        if (!authRes.getStatusCode().is2xxSuccessful()) return authRes;
        WorkoutPlan existing = (WorkoutPlan) authRes.getBody();

        if (p != null && p.getName() != null) {
            existing.setName(p.getName());
        }

        WorkoutPlan saved = repo.save(existing);
        return ResponseEntity.ok(saved);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id, HttpServletRequest req) {

        ResponseEntity<?> authRes = authorizeAndGetPlan(req, id);
        if (!authRes.getStatusCode().is2xxSuccessful()) return authRes;
        WorkoutPlan existing = (WorkoutPlan) authRes.getBody();

        repo.deleteById(id);
        return ResponseEntity.ok(Map.of("deleted", id));
    }

    @PostMapping("/{planId}/add")
    public ResponseEntity<?> addExercise(@PathVariable Long planId,
                                         @RequestBody WorkoutItem item,
                                         HttpServletRequest req) {

        Optional<String> usernameOpt = extractUsername(req);
        if (usernameOpt.isEmpty()) {
            return ResponseEntity.status(401).body(Map.of("error", "unauthorized"));
        }

        Optional<WorkoutPlan> planOpt = repo.findById(planId);
        if (planOpt.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("error", "plan_not_found"));
        }

        WorkoutPlan plan = planOpt.get();

        if (!usernameOpt.get().equals(plan.getOwnerUsername())) {
            return ResponseEntity.status(403).body(Map.of("error", "forbidden"));
        }

        if (item == null || item.getExerciseId() == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "exercise_id_missing"));
        }
        if (item.getSets() == null || item.getSets() <= 0) {
            return ResponseEntity.badRequest().body(Map.of("error", "invalid_sets"));
        }
        if (item.getReps() == null || item.getReps() <= 0) {
            return ResponseEntity.badRequest().body(Map.of("error", "invalid_reps"));
        }

        if (item.getExerciseId() <= 0) {
            return ResponseEntity.badRequest().body(Map.of("error", "invalid_exercise_id"));
        }

        plan.getItems().add(item);

        WorkoutPlan saved = repo.save(plan);
        return ResponseEntity.ok(saved);
    }
}