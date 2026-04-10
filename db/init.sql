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
('Pompki klasyczne', 'Ustaw dłonie na szerokość barków, ciało w linii prostej. Opuszczaj klatkę w dół kontrolowanym ruchem aż do prawie dotknięcia podłoża. Łokcie prowadź pod kątem ok. 45°. Wypychaj ciało w górę z wydechem, nie zapadaj się w biodrach.', 'CHEST'),

('Pompki diamentowe', 'Dłonie ustaw blisko siebie pod klatką, tworząc kształt diamentu. Trzymaj łokcie blisko ciała podczas opuszczania. Schodź powoli, utrzymując napięcie brzucha. W górze prostuj ręce bez blokowania łokci.', 'CHEST'),

('Pompki z nogami na podwyższeniu', 'Stopy oprzyj na podwyższeniu (np. krześle), dłonie na podłodze. Utrzymuj napięty brzuch i proste ciało. Schodź w dół kontrolując ruch, nacisk przenosi się bardziej na górną część klatki.', 'CHEST'),

('Pompki szerokie', 'Dłonie ustaw szerzej niż barki. Opuszczaj ciało, prowadząc łokcie bardziej na boki. Skup się na pracy klatki piersiowej, a nie na szybkości ruchu.', 'CHEST'),

('Pompki z pauzą', 'W dolnej fazie zatrzymaj się na 1–2 sekundy tuż nad podłogą. Utrzymuj napięcie całego ciała. Następnie dynamicznie wypchnij się w górę.', 'CHEST'),

('Pompki na jednej ręce (asystowane)', 'Jedna ręka pracuje, druga lekko wspiera. Utrzymuj szeroką pozycję nóg dla stabilizacji. Schodź powoli, kontrolując rotację tułowia.', 'CHEST'),

-- BACK
('Superman', 'Leżąc na brzuchu unieś jednocześnie ręce i nogi. Napnij dolne plecy i pośladki. Przytrzymaj 1–2 sekundy i powoli opuść. Nie zadzieraj głowy do góry.', 'BACK'),

('Wiosłowanie z gumą', 'Usiądź lub stań stabilnie, chwyć gumę. Przyciągaj łokcie do tyłu blisko ciała, ściągając łopatki. Kontroluj powrót.', 'BACK'),

('Mostek biodrowy', 'Leżąc na plecach ugnij nogi. Wypchnij biodra w górę, napinając pośladki i dolne plecy. Nie wyginaj nadmiernie kręgosłupa.', 'BACK'),

('Ściąganie gumy do klatki', 'Przymocuj gumę wysoko. Ściągaj ją do klatki, prowadząc łokcie w dół. Skup się na pracy pleców, nie rąk.', 'BACK'),

('Reverse snow angels', 'Leżąc na brzuchu przesuwaj ręce po łuku od nad głowy do bioder. Utrzymuj ręce nad podłożem przez cały czas.', 'BACK'),

('Podciąganie australijskie', 'Chwyć stabilny drążek/stół. Ciało w linii prostej. Przyciągaj klatkę do drążka, ściągając łopatki.', 'BACK'),

-- SHOULDERS
('Pike push-ups', 'Ustaw ciało w literę V. Schodź głową w kierunku podłogi między dłonie. Wypychaj się w górę, angażując barki.', 'SHOULDERS'),

('Krążenia ramion', 'Wykonuj powolne, kontrolowane krążenia ramion. Nie szarp ruchem, utrzymuj napięcie mięśni.', 'SHOULDERS'),

('Unoszenie ramion z gumą', 'Stań na gumie, unoś ramiona do poziomu barków. Kontroluj ruch w dół.', 'SHOULDERS'),

('Handstand przy ścianie', 'Stań na rękach przy ścianie. Utrzymuj napięty brzuch i prostą linię ciała.', 'SHOULDERS'),

('Pompki na barki przy ścianie', 'W pozycji przy ścianie opuszczaj głowę w dół i wypychaj się w górę. Ruch kontrolowany.', 'SHOULDERS'),

('Unoszenie ramion w opadzie', 'Pochyl się do przodu, unoś ręce na boki. Skup się na tylnej części barków.', 'SHOULDERS'),

-- LEGS
('Przysiady', 'Stopy na szerokość barków. Schodź biodrami w dół jak do krzesła. Kolana prowadź na zewnątrz, plecy proste.', 'LEGS'),

('Wykroki', 'Zrób krok w przód i ugnij oba kolana. Kolano przedniej nogi nie wychodzi poza palce.', 'LEGS'),

('Przysiady bułgarskie', 'Tylna noga na podwyższeniu. Schodź w dół kontrolując kolano przedniej nogi.', 'LEGS'),

('Przysiady z wyskokiem', 'Z pozycji przysiadu dynamicznie wyskocz w górę. Ląduj miękko i kontrolowanie.', 'LEGS'),

('Wall sit', 'Plecy o ścianę, uda równolegle do podłogi. Utrzymuj napięcie przez cały czas.', 'LEGS'),

('Wspięcia na palce', 'Unieś pięty maksymalnie do góry, powoli opuść. Skup się na łydkach.', 'LEGS'),

-- ARMS
('Dipy na krześle', 'Oprzyj dłonie na krześle. Opuszczaj ciało, zginając łokcie w tył. Wypychaj się w górę.', 'ARMS'),

('Uginanie ramion z gumą', 'Stań na gumie, zginaj ręce w łokciach. Kontroluj powrót.', 'ARMS'),

('Pompki wąskie', 'Łokcie prowadź blisko ciała. Większy nacisk na tricepsy.', 'ARMS'),

('Plank z dotykaniem barków', 'Z pozycji deski dotykaj naprzemiennie barków, utrzymując stabilny tułów.', 'ARMS'),

('Uginanie młotkowe z gumą', 'Chwyt neutralny, zginaj ręce bez rotacji nadgarstka.', 'ARMS'),

('Prostowanie ramion nad głową', 'Trzymaj gumę nad głową i prostuj ręce, izolując tricepsy.', 'ARMS'),

-- CORE
('Plank', 'Ciało w jednej linii, napięty brzuch i pośladki. Nie opuszczaj bioder.', 'CORE'),

('Brzuszki', 'Unoszenie tułowia z kontrolą. Nie ciągnij głowy rękami.', 'CORE'),

('Mountain climbers', 'Naprzemienne przyciąganie kolan do klatki w pozycji plank.', 'CORE'),

('Russian twist', 'Skręty tułowia na siedząco. Utrzymuj napięty brzuch.', 'CORE'),

('Unoszenie nóg leżąc', 'Unoszenie prostych nóg bez odrywania dolnych pleców.', 'CORE'),

('Dead bug', 'Naprzemienne ruchy rąk i nóg, utrzymując stabilny kręgosłup.', 'CORE'),

-- CARDIO
('Bieg w miejscu', 'Biegnij w miejscu, utrzymując rytm i lekkie odbicie.', 'CARDIO'),

('Skakanka (bez skakanki)', 'Symuluj skakanie, lądując miękko na śródstopiu.', 'CARDIO'),

('Burpees', 'Przysiad → plank → pompka → wyskok. Zachowaj płynność ruchu.', 'CARDIO'),

('Jumping jacks', 'Podskoki z rozkrokiem i klaśnięciem nad głową.', 'CARDIO'),

('High knees', 'Bieg w miejscu z wysokim unoszeniem kolan.', 'CARDIO'),

('Butt kicks', 'Bieg w miejscu, uderzając piętami o pośladki.', 'CARDIO');


-- INSERT INTO workout_plans (name, owner_username) VALUES
--     ('Morning Push',  'john_doe'),
--     ('Leg Day',       'john_doe');

-- INSERT INTO workout_items (plan_id, exercise_id, sets, reps) VALUES
--     (1, 1, 4, 10),
--     (1, 3, 3, 8),
--     (2, 2, 5, 5);