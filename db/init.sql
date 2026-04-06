-- sql
-- USERS
CREATE TABLE IF NOT EXISTS users
(
    id         BIGSERIAL PRIMARY KEY,
    username   VARCHAR(50)  NOT NULL UNIQUE,
    email      VARCHAR(255) NOT NULL UNIQUE,
    password   VARCHAR(255) NOT NULL,
    roles      VARCHAR(255) NOT NULL DEFAULT 'ROLE_USER',
    created_at TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

-- EXERCISES
CREATE TABLE IF NOT EXISTS exercises
(
    id           BIGSERIAL PRIMARY KEY,
    name         VARCHAR(100) NOT NULL UNIQUE,
    description  TEXT,
    muscle_group VARCHAR(50),
    created_at   TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

-- WORKOUT_PLANS
CREATE TABLE IF NOT EXISTS workout_plans
(
    id             BIGSERIAL PRIMARY KEY,
    name           VARCHAR(100) NOT NULL,
    owner_username VARCHAR(50)  NOT NULL,
    created_at     TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

-- WORKOUT_ITEMS
CREATE TABLE IF NOT EXISTS workout_items
(
    plan_id     BIGINT NOT NULL REFERENCES workout_plans (id) ON DELETE CASCADE,
    exercise_id BIGINT NOT NULL REFERENCES exercises (id) ON DELETE RESTRICT,
    sets        INT,
    reps        INT,
    PRIMARY KEY (plan_id, exercise_id)
);

-- INDEXES
CREATE INDEX IF NOT EXISTS idx_workout_plans_owner_username ON workout_plans (owner_username);
CREATE INDEX IF NOT EXISTS idx_workout_items_plan_id ON workout_items (plan_id);
CREATE INDEX IF NOT EXISTS idx_workout_items_exercise_id ON workout_items (exercise_id);
CREATE INDEX IF NOT EXISTS idx_exercises_muscle_group ON exercises (muscle_group);

-- SAMPLE DATA
INSERT INTO users (username, email, password, roles) VALUES
    ('john_doe', 'john_doe@example.com', '$2a$12$placeholder_bcrypt_hash_1', 'ROLE_USER'),
    ('admin',    'admin@example.com',    '$2a$12$placeholder_bcrypt_hash_2', 'ROLE_ADMIN');

INSERT INTO exercises (name, description, muscle_group) VALUES
    ('Bench Press',    'Barbell bench press description', 'CHEST'),
    ('Pull Up',        'Pull up description', 'BACK'),
    ('Overhead Press', 'Overhead press description', 'SHOULDERS');

INSERT INTO workout_plans (name, owner_username) VALUES
    ('Morning Push',  'john_doe'),
    ('Leg Day',       'john_doe');

INSERT INTO workout_items (plan_id, exercise_id, sets, reps) VALUES
    (1, 1, 4, 10),
    (1, 3, 3, 8),
    (2, 2, 5, 5);