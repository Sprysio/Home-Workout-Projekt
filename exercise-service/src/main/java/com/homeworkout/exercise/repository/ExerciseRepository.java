// java
package com.homeworkout.exercise.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.homeworkout.exercise.model.Exercise;

public interface ExerciseRepository extends JpaRepository<Exercise, Long> {
}