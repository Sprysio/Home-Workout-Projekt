// java
    package com.homeworkout.workout.model;

    import jakarta.persistence.*;
    import java.util.ArrayList;
    import java.util.List;

    @Entity
    @Table(name = "workout_plans")
    public class WorkoutPlan {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        private Long userId;
        private String name;

        @ElementCollection
        @CollectionTable(name = "workout_items", joinColumns = @JoinColumn(name = "plan_id"))
        private List<WorkoutItem> items = new ArrayList<>();

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public List<WorkoutItem> getItems() { return items; }
        public void setItems(List<WorkoutItem> items) { this.items = items; }
    }