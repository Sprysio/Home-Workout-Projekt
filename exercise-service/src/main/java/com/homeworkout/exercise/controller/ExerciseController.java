// java
package com.homeworkout.exercise.controller;

import org.springframework.web.bind.annotation.*;
import com.homeworkout.exercise.model.Exercise;
import com.homeworkout.exercise.service.ExerciseService;
import java.util.List;

@RestController
@RequestMapping("/exercises")
public class ExerciseController {
    private final ExerciseService service;

    public ExerciseController(ExerciseService service) { this.service = service; }

    @GetMapping
    public List<Exercise> all() { return service.findAll(); }

    @GetMapping("/{id}")
    public Exercise get(@PathVariable Long id) { return service.findById(id).orElseThrow(); }

    @PostMapping
    public Exercise create(@RequestBody Exercise e) { return service.save(e); }

    @PutMapping("/{id}")
    public Exercise update(@PathVariable Long id, @RequestBody Exercise e) {
        e.setId(id);
        return service.save(e);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) { service.deleteById(id); }

    @GetMapping("/search")
    public List<Exercise> search(@RequestParam String q) { return service.searchByName(q); }
}