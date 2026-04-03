// java
package com.homeworkout.workout.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.homeworkout.workout.model.WorkoutPlan;
import java.util.List;

public interface WorkoutRepository extends JpaRepository<WorkoutPlan, Long> {
    List<WorkoutPlan> findByUserId(Long userId);
}