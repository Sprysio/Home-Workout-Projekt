
package com.homeworkout.workout.model;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Embeddable
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class WorkoutItem implements Serializable {
    private Long exerciseId;
    private Integer sets;
    private Integer reps;
}