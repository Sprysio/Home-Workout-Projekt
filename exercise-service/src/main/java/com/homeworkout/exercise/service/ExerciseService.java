package com.homeworkout.exercise.service;

import org.springframework.stereotype.Service;
import com.homeworkout.exercise.repository.ExerciseRepository;
import com.homeworkout.exercise.model.Exercise;

import java.util.List;
import java.util.Optional;

@Service
public class ExerciseService {
    private final ExerciseRepository repo;

    public ExerciseService(ExerciseRepository repo) {
        this.repo = repo;
    }

    public List<Exercise> findAll() {
        return repo.findAll();
    }

    public Optional<Exercise> findById(Long id) {
        return repo.findById(id);
    }

    public Exercise save(Exercise e) {
        return repo.save(e);
    }

    public void deleteById(Long id) {
        repo.deleteById(id);
    }
}