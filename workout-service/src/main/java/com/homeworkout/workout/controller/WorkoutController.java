// java
package com.homeworkout.workout.controller;

import org.springframework.web.bind.annotation.*;
import com.homeworkout.workout.model.WorkoutPlan;
import com.homeworkout.workout.model.WorkoutItem;
import com.homeworkout.workout.repository.WorkoutRepository;
import org.springframework.web.client.RestTemplate;
import java.util.List;

@RestController
@RequestMapping("/plans")
public class WorkoutController {
    private final WorkoutRepository repo;
    private final RestTemplate rest;

    public WorkoutController(WorkoutRepository repo, RestTemplate rest) {
        this.repo = repo; this.rest = rest;
    }

    @PostMapping
    public WorkoutPlan create(@RequestBody WorkoutPlan p) { return repo.save(p); }

    @GetMapping("/user/{userId}")
    public List<WorkoutPlan> getByUser(@PathVariable Long userId) { return repo.findByUserId(userId); }

    @PostMapping("/{planId}/add")
    public WorkoutPlan addExercise(@PathVariable Long planId, @RequestBody WorkoutItem item) {
        WorkoutPlan plan = repo.findById(planId).orElseThrow();
        String url = "http://localhost:8082/exercises/" + item.getExerciseId();
        try {
            rest.getForObject(url, Object.class);
        } catch (Exception e) {
            throw new IllegalArgumentException("Exercise not found");
        }
        plan.getItems().add(item);
        return repo.save(plan);
    }
}