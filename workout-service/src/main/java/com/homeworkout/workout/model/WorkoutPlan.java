package com.homeworkout.workout.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Entity
@Table(name = "workout_plans")
public class WorkoutPlan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String ownerUsername;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "workout_items", joinColumns = @JoinColumn(name = "plan_id"))
    private List<WorkoutItem> items = new ArrayList<>();

    public WorkoutPlan() {
    }

}