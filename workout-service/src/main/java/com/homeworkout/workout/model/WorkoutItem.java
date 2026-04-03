// java
package com.homeworkout.workout.model;

import jakarta.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class WorkoutItem implements Serializable {
    private Long exerciseId;
    private Integer sets;
    private Integer reps;

    public WorkoutItem() {}

    public WorkoutItem(Long exerciseId, Integer sets, Integer reps) {
        this.exerciseId = exerciseId;
        this.sets = sets;
        this.reps = reps;
    }

    public Long getExerciseId() { return exerciseId; }
    public void setExerciseId(Long exerciseId) { this.exerciseId = exerciseId; }

    public Integer getSets() { return sets; }
    public void setSets(Integer sets) { this.sets = sets; }

    public Integer getReps() { return reps; }
    public void setReps(Integer reps) { this.reps = reps; }
}