package com.homeworkout.workout.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.homeworkout.workout.model.WorkoutPlan;
import com.homeworkout.workout.model.WorkoutItem;
import com.homeworkout.workout.repository.WorkoutRepository;
import org.springframework.web.client.RestTemplate;
import com.homeworkout.workout.security.JwtUtil;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/plans")
public class WorkoutController {
    private final WorkoutRepository repo;
    private final RestTemplate rest;
    private final JwtUtil jwtUtil;

    public WorkoutController(WorkoutRepository repo, RestTemplate rest, JwtUtil jwtUtil) {
        this.repo = repo;
        this.rest = rest;
        this.jwtUtil = jwtUtil;
    }

    private ResponseEntity<?> requireAuth(HttpServletRequest req) {
        String h = req.getHeader("Authorization");
        if (h == null || !h.startsWith("Bearer "))
            return ResponseEntity.status(401).body(Map.of("error", "missing_token"));
        String token = h.substring(7);
        if (!jwtUtil.validateToken(token)) return ResponseEntity.status(401).body(Map.of("error", "invalid_token"));
        return null;
    }

    private String getUsernameFromReq(HttpServletRequest req) {
        String token = req.getHeader("Authorization").substring(7);
        return jwtUtil.getUsernameFromToken(token);
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody WorkoutPlan p, HttpServletRequest req) {
        ResponseEntity<?> authErr = requireAuth(req);
        if (authErr != null) return authErr;
        String actor = getUsernameFromReq(req);
        p.setOwnerUsername(actor);
        p.setItems(p.getItems() == null ? List.of() : p.getItems());
        WorkoutPlan saved = repo.save(p);
        return ResponseEntity.ok(saved);
    }

    @GetMapping
    public ResponseEntity<?> listOwn(HttpServletRequest req) {
        ResponseEntity<?> authErr = requireAuth(req);
        if (authErr != null) return authErr;
        String actor = getUsernameFromReq(req);
        List<WorkoutPlan> plans = repo.findByOwnerUsername(actor);
        return ResponseEntity.ok(plans);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getOne(@PathVariable Long id, HttpServletRequest req) {
        ResponseEntity<?> authErr = requireAuth(req);
        if (authErr != null) return authErr;
        String actor = getUsernameFromReq(req);
        Optional<WorkoutPlan> opt = repo.findById(id);
        if (opt.isEmpty()) return ResponseEntity.status(404).body(Map.of("error", "not_found"));
        WorkoutPlan plan = opt.get();
        if (!actor.equals(plan.getOwnerUsername()))
            return ResponseEntity.status(403).body(Map.of("error", "forbidden"));
        return ResponseEntity.ok(plan);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody WorkoutPlan p, HttpServletRequest req) {
        ResponseEntity<?> authErr = requireAuth(req);
        if (authErr != null) return authErr;
        String actor = getUsernameFromReq(req);
        Optional<WorkoutPlan> opt = repo.findById(id);
        if (opt.isEmpty()) return ResponseEntity.status(404).body(Map.of("error", "not_found"));
        WorkoutPlan existing = opt.get();
        if (!actor.equals(existing.getOwnerUsername()))
            return ResponseEntity.status(403).body(Map.of("error", "forbidden"));
        existing.setName(p.getName());
        // nie pozwalam na zmianę ownera przez request
        WorkoutPlan saved = repo.save(existing);
        return ResponseEntity.ok(saved);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id, HttpServletRequest req) {
        ResponseEntity<?> authErr = requireAuth(req);
        if (authErr != null) return authErr;
        String actor = getUsernameFromReq(req);
        Optional<WorkoutPlan> opt = repo.findById(id);
        if (opt.isEmpty()) return ResponseEntity.status(404).body(Map.of("error", "not_found"));
        WorkoutPlan existing = opt.get();
        if (!actor.equals(existing.getOwnerUsername()))
            return ResponseEntity.status(403).body(Map.of("error", "forbidden"));
        repo.deleteById(id);
        return ResponseEntity.ok(Map.of("deleted", id));
    }

    @PostMapping("/{planId}/add")
    public ResponseEntity<?> addExercise(@PathVariable Long planId, @RequestBody WorkoutItem item, HttpServletRequest req) {
        ResponseEntity<?> authErr = requireAuth(req);
        if (authErr != null) return authErr;
        String actor = getUsernameFromReq(req);
        Optional<WorkoutPlan> planOpt = repo.findById(planId);
        if (planOpt.isEmpty()) return ResponseEntity.status(404).body(Map.of("error", "plan_not_found"));
        WorkoutPlan plan = planOpt.get();
        if (!actor.equals(plan.getOwnerUsername()))
            return ResponseEntity.status(403).body(Map.of("error", "forbidden"));

        if (item.getExerciseId() == null)
            return ResponseEntity.badRequest().body(Map.of("error", "exercise_id_missing"));
        if (item.getSets() == null || item.getSets() <= 0)
            return ResponseEntity.badRequest().body(Map.of("error", "invalid_sets"));
        if (item.getReps() == null || item.getReps() <= 0)
            return ResponseEntity.badRequest().body(Map.of("error", "invalid_reps"));

        String url = "http://localhost:8082/exercises/" + item.getExerciseId();
        try {
            rest.getForObject(url, Object.class);
        } catch (Exception e) {
            return ResponseEntity.status(404).body(Map.of("error", "exercise_not_found"));
        }

        plan.getItems().add(item);
        WorkoutPlan saved = repo.save(plan);
        return ResponseEntity.ok(saved);
    }
}