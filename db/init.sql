-- ─────────────────────────────────────────────────────────────────────────────
-- USERS
-- ─────────────────────────────────────────────────────────────────────────────
CREATE TABLE users (
    id       BIGSERIAL    PRIMARY KEY,
    username VARCHAR(50)  NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,           -- store bcrypt hash, never plaintext
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- ─────────────────────────────────────────────────────────────────────────────
-- EXERCISES
-- ─────────────────────────────────────────────────────────────────────────────
CREATE TYPE exercise_category AS ENUM (
    'CHEST', 'BACK', 'SHOULDERS', 'ARMS', 'LEGS', 'CORE', 'CARDIO', 'OTHER'
);

CREATE TABLE exercises (
    id       BIGSERIAL         PRIMARY KEY,
    name     VARCHAR(100)      NOT NULL UNIQUE,
    category exercise_category NOT NULL,
    created_at TIMESTAMPTZ     NOT NULL DEFAULT NOW()
);

-- ─────────────────────────────────────────────────────────────────────────────
-- WORKOUTS  (many users → one user, i.e. 1:n between user and workouts)
-- ─────────────────────────────────────────────────────────────────────────────
CREATE TABLE workouts (
    id         BIGSERIAL    PRIMARY KEY,
    name       VARCHAR(100) NOT NULL,
    user_id    BIGINT       NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    created_at TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

-- ─────────────────────────────────────────────────────────────────────────────
-- WORKOUT_EXERCISES  (n:n join table between workouts and exercises)
-- ─────────────────────────────────────────────────────────────────────────────
CREATE TABLE workout_exercises (
    workout_id  BIGINT NOT NULL REFERENCES workouts(id)  ON DELETE CASCADE,
    exercise_id BIGINT NOT NULL REFERENCES exercises(id) ON DELETE RESTRICT,
    sets        INT,                          -- optional: track sets/reps per entry
    reps        INT,
    PRIMARY KEY (workout_id, exercise_id)
);

-- ─────────────────────────────────────────────────────────────────────────────
-- INDEXES
-- ─────────────────────────────────────────────────────────────────────────────
CREATE INDEX idx_workouts_user_id        ON workouts(user_id);
CREATE INDEX idx_workout_exercises_wid   ON workout_exercises(workout_id);
CREATE INDEX idx_workout_exercises_eid   ON workout_exercises(exercise_id);
CREATE INDEX idx_exercises_category      ON exercises(category);

-- ─────────────────────────────────────────────────────────────────────────────
-- SAMPLE DATA
-- ─────────────────────────────────────────────────────────────────────────────
-- INSERT INTO users (username, password) VALUES
--     ('john_doe',  '$2a$12$placeholder_bcrypt_hash_1'),
--     ('jane_doe',  '$2a$12$placeholder_bcrypt_hash_2');

-- INSERT INTO exercises (name, category) VALUES
--     ('Bench Press',    'CHEST'),
--     ('Pull Up',        'BACK'),
--     ('Overhead Press', 'SHOULDERS'),
--     ('Bicep Curl',     'ARMS'),
--     ('Squat',          'LEGS'),
--     ('Plank',          'CORE'),
--     ('Treadmill Run',  'CARDIO');

-- INSERT INTO workouts (name, user_id) VALUES
--     ('Morning Push',  1),
--     ('Full Body A',   1),
--     ('Leg Day',       2);

-- INSERT INTO workout_exercises (workout_id, exercise_id, sets, reps) VALUES
--     (1, 1, 4, 10),   -- Morning Push → Bench Press
--     (1, 3, 3, 8),    -- Morning Push → Overhead Press
--     (2, 1, 3, 12),   -- Full Body A  → Bench Press
--     (2, 2, 3, 10),   -- Full Body A  → Pull Up
--     (2, 5, 4, 8),    -- Full Body A  → Squat
--     (3, 5, 5, 5),    -- Leg Day      → Squat
--     (3, 6, 3, 60);   -- Leg Day      → Plank (reps = seconds here)