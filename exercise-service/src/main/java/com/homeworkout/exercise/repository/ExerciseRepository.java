// java
package com.homeworkout.exercise.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.homeworkout.exercise.model.Exercise;
import java.util.List;

public interface ExerciseRepository extends JpaRepository<Exercise, Long> {
    List<Exercise> findByNameContainingIgnoreCase(String name);
    List<Exercise> findByMuscleGroupIgnoreCase(String muscleGroup);
}