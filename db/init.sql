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
-- INSERT INTO users (username, email, password, roles) VALUES
--     ('john_doe', 'john_doe@example.com', '$2a$12$placeholder_bcrypt_hash_1', 'ROLE_USER'),
--     ('admin',    'admin@example.com',    '$2a$12$placeholder_bcrypt_hash_2', 'ROLE_ADMIN');

INSERT INTO exercises (name, description, muscle_group) VALUES
    -- CHEST
    ('Bench Press',          'The classic compound chest exercise. Lie on a flat bench, lower a barbell to your chest, then press it back up. Targets the pectoralis major, anterior deltoids, and triceps.', 'CHEST'),
    ('Incline Bench Press',  'Performed on a bench set to 30-45 degrees. Emphasizes the upper chest and anterior deltoids more than the flat variation.', 'CHEST'),
    ('Decline Bench Press',  'Performed on a downward-angled bench. Shifts emphasis to the lower pectoralis major. Often allows heavier loads than flat bench.', 'CHEST'),
    ('Dumbbell Fly',         'Lie flat and arc dumbbells out to the sides and back together. Isolates the chest through a wide range of motion with minimal tricep involvement.', 'CHEST'),
    ('Cable Crossover',      'Using a cable machine, bring handles together in front of your chest. Keeps constant tension throughout the movement unlike free weights.', 'CHEST'),
    ('Push Up',              'Bodyweight staple. Targets chest, shoulders, and triceps. Easily scalable by elevating feet for upper chest or hands for lower chest emphasis.', 'CHEST'),
    ('Dip',                  'Lower yourself between parallel bars until shoulders are below elbows, then press back up. Heavily targets lower chest and triceps.', 'CHEST'),

    -- BACK
    ('Pull Up',              'Hang from a bar with an overhand grip and pull yourself up until your chin clears the bar. One of the best compound movements for the back, targeting lats, rhomboids, and biceps.', 'BACK'),
    ('Chin Up',              'Similar to a pull up but with an underhand grip. Increases bicep involvement and is generally easier for beginners.', 'BACK'),
    ('Barbell Row',          'Hinge at the hips and row a barbell to your lower chest. A foundational compound movement for overall back thickness targeting the lats, rhomboids, and traps.', 'BACK'),
    ('Dumbbell Row',         'Single-arm row with a dumbbell supported on a bench. Allows greater range of motion than barbell rows and helps correct side-to-side imbalances.', 'BACK'),
    ('Seated Cable Row',     'Sit at a cable machine and row the handle to your abdomen. Great for targeting the mid-back and maintaining constant tension throughout.', 'BACK'),
    ('Lat Pulldown',         'Pull a bar down to your upper chest on a cable machine. An accessible alternative to pull ups that effectively targets the latissimus dorsi.', 'BACK'),
    ('Deadlift',             'The king of posterior chain exercises. Lift a loaded barbell from the floor to hip level. Works the entire back, glutes, hamstrings, and traps heavily.', 'BACK'),
    ('Face Pull',            'Pull a rope attachment toward your face at eye level. Excellent for rear deltoids and external rotators, crucial for shoulder health and posture.', 'BACK'),

    -- SHOULDERS
    ('Overhead Press',       'Press a barbell from shoulder height overhead to full arm extension. The primary compound movement for shoulder development targeting all three deltoid heads.', 'SHOULDERS'),
    ('Dumbbell Shoulder Press', 'Same movement pattern as the barbell overhead press but with dumbbells. Allows greater range of motion and corrects muscular imbalances.', 'SHOULDERS'),
    ('Lateral Raise',        'Raise dumbbells out to the sides to shoulder height. Isolates the medial deltoid, critical for shoulder width and the V-taper appearance.', 'SHOULDERS'),
    ('Front Raise',          'Raise dumbbells or a plate directly in front of you to shoulder height. Isolates the anterior deltoid.', 'SHOULDERS'),
    ('Arnold Press',         'Start with palms facing you and rotate outward as you press overhead. Named after Arnold Schwarzenegger, it hits all three deltoid heads through the rotational movement.', 'SHOULDERS'),
    ('Upright Row',          'Pull a barbell or dumbbells vertically up along your body to chin height. Targets the traps and medial deltoids. Use a wider grip to reduce shoulder impingement risk.', 'SHOULDERS'),

    -- LEGS
    ('Squat',                'The foundational lower body movement. Bar on upper back, descend until thighs are parallel to the floor, drive back up. Targets quads, glutes, and hamstrings.', 'LEGS'),
    ('Front Squat',          'Barbell held in front at shoulder height. Shifts emphasis to the quads and requires greater core strength and ankle mobility than back squats.', 'LEGS'),
    ('Romanian Deadlift',    'Hip hinge movement with a slight knee bend, lowering the bar along your legs until you feel a hamstring stretch. Primary exercise for hamstring and glute development.', 'LEGS'),
    ('Leg Press',            'Push a weighted sled away from you on a 45-degree machine. Allows heavy quad loading with less spinal compression than squats.', 'LEGS'),
    ('Leg Curl',             'Lying or seated, curl a pad toward your glutes against resistance. Isolates the hamstrings through knee flexion.', 'LEGS'),
    ('Leg Extension',        'Seated, extend your legs against a pad to full lockout. Isolates the quadriceps through knee extension.', 'LEGS'),
    ('Bulgarian Split Squat','Rear foot elevated on a bench, lower your back knee toward the floor. Excellent unilateral quad and glute developer that also improves balance and hip mobility.', 'LEGS'),
    ('Calf Raise',           'Rise up onto the balls of your feet against resistance. Targets the gastrocnemius and soleus. Can be performed standing or seated.', 'LEGS'),
    ('Hip Thrust',           'Upper back on a bench, drive hips upward against a barbell. The most effective exercise for glute isolation and hypertrophy.', 'LEGS'),
    ('Walking Lunge',        'Step forward into a lunge position alternating legs across a distance. Targets quads, glutes, and hamstrings while adding a balance and coordination challenge.', 'LEGS'),

    -- ARMS
    ('Barbell Curl',         'Curl a barbell from hip height to your shoulders. The classic bicep builder targeting the biceps brachii and brachialis.', 'ARMS'),
    ('Dumbbell Curl',        'Alternating or simultaneous dumbbell curls. Allows supination through the movement for full bicep contraction.', 'ARMS'),
    ('Hammer Curl',          'Curl dumbbells with a neutral grip (palms facing each other). Targets the brachialis and brachioradialis more than standard curls, adding arm thickness.', 'ARMS'),
    ('Preacher Curl',        'Curl performed with upper arms resting on a preacher bench pad. Eliminates momentum and places peak tension on the lower bicep.', 'ARMS'),
    ('Tricep Pushdown',      'Push a cable attachment downward to full elbow extension. Isolates the triceps, particularly the lateral head.', 'ARMS'),
    ('Skull Crusher',        'Lower a barbell or EZ bar to your forehead while lying on a bench, then extend. Excellent for the long head of the triceps.', 'ARMS'),
    ('Close Grip Bench Press','Bench press with hands shoulder-width apart. Shifts emphasis from chest to triceps while still being a compound movement.', 'ARMS'),
    ('Overhead Tricep Extension', 'Extend a dumbbell or cable overhead with both hands. Stretches and works the long head of the tricep through its full range of motion.', 'ARMS'),

    -- CORE
    ('Plank',                'Hold a push-up position with forearms on the ground. Builds isometric core strength and stability in the transverse abdominis and entire midsection.', 'CORE'),
    ('Crunch',               'Lie on your back and flex your spine to lift your shoulders off the floor. Classic abdominal isolation exercise targeting the rectus abdominis.', 'CORE'),
    ('Leg Raise',            'Lying flat, raise straight legs to 90 degrees. Heavily targets the lower rectus abdominis and hip flexors.', 'CORE'),
    ('Russian Twist',        'Seated with feet elevated, rotate a weight side to side. Targets the obliques and rotational core strength.', 'CORE'),
    ('Ab Wheel Rollout',     'Kneel and roll a wheel forward until nearly flat, then pull back. One of the most challenging and effective core stability exercises.', 'CORE'),
    ('Cable Crunch',         'Kneel at a cable machine and crunch down against resistance. Allows progressive overload on the abs unlike bodyweight crunches.', 'CORE'),
    ('Hanging Knee Raise',   'Hang from a bar and raise your knees to your chest. Targets the lower abs and hip flexors while also challenging grip strength.', 'CORE'),

    -- CARDIO
    ('Treadmill Run',        'Sustained running on a treadmill. Improves cardiovascular endurance, burns calories, and can be programmed for steady-state or interval training.', 'CARDIO'),
    ('Rowing Machine',       'Full body cardio on an ergometer. Engages legs, back, and arms simultaneously making it one of the most efficient cardio tools available.', 'CARDIO'),
    ('Cycling',              'Low-impact cardio on a stationary or road bike. Excellent for cardiovascular health with minimal joint stress, particularly good for recovery days.', 'CARDIO'),
    ('Jump Rope',            'High intensity cardio using a skipping rope. Improves coordination, footwork, and cardiovascular fitness. Burns significant calories in short sessions.', 'CARDIO'),
    ('Battle Ropes',         'Slam heavy ropes in alternating or simultaneous waves. Combines cardiovascular conditioning with upper body muscular endurance.', 'CARDIO');


-- INSERT INTO workout_plans (name, owner_username) VALUES
--     ('Morning Push',  'john_doe'),
--     ('Leg Day',       'john_doe');

-- INSERT INTO workout_items (plan_id, exercise_id, sets, reps) VALUES
--     (1, 1, 4, 10),
--     (1, 3, 3, 8),
--     (2, 2, 5, 5);