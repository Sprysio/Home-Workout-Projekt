# Dokumentacja endpointów — Home Workout Projekt

## Ważne: globalny prefiks `/api`

Wszystkie endpointy w tym dokumencie są dostępne przez API Gateway z prefiksem `/api`.

Przykłady:
- `/auth/login` w praktyce: `/api/auth/login`
- `/exercises` w praktyce: `/api/exercises`
- `/plans/{id}` w praktyce: `/api/plans/{id}`

Uwaga: wszystkie endpointy chronione wymagają nagłówka Authorization: `Bearer <token>` — token otrzymany z `POST /auth/login`.

Format dokumentacji dla każdego endpointu:
* METHOD
* path: `/ścieżka`
* Required role: `"ROLE_..."` (jeśli dotyczy)
* Example body: (jeżeli dotyczy)

---

## AUTH SERVICE — `/auth`

### 1. Rejestracja użytkownika
* POST
* path: `/auth/register`
* Required role: brak (publiczne)
* Example body:
```json
{
  "username": "jan",
  "email": "jan@example.com",
  "password": "sekret"
}
```
* Przykładowa odpowiedź 200:
```json
{
  "id": 1,
  "username": "jan",
  "roles": ["ROLE_USER"]
}
```

---

### 2. Logowanie
* POST
* path: `/auth/login`
* Required role: brak (publiczne)
* Example body:
```json
{
  "username": "jan",
  "password": "sekret"
}
```
* Przykładowa odpowiedź 200:
```json
{ "token": "<jwt-token>" }
```

---

### 3. Zmiana roli użytkownika
* PUT
* path: `/auth/users/{id}/role`
* Required role: `"ROLE_ADMIN"`
* Example body:
```json
{ "role": "ROLE_ADMIN" }
```
* Uwagi: akceptowane jest też krótkie oznaczenie np. `"admin"` (zostanie znormalizowane do `ROLE_ADMIN`).
* Przykładowa odpowiedź 200:
```json
{
  "id": 2,
  "username": "anna",
  "roles": ["ROLE_ADMIN"]
}
```

---

### 4. Lista użytkowników
* GET
* path: `/auth/users`
* Required role: `"ROLE_ADMIN"`
* Example response 200:
```json
[
  { "id": 1, "username": "jan", "email": "jan@example.com", "roles": ["ROLE_USER"] },
  { "id": 2, "username": "admin", "email": "admin@example.com", "roles": ["ROLE_ADMIN"] }
]
```

---

## EXERCISE SERVICE — `/exercises`

Model Exercise (ważne pola): id, name, description, muscleGroup

### 5. Lista wszystkich ćwiczeń
* GET
* path: `/exercises`
* Required role: brak (publiczne)
* Example response 200:
```json
[
  { "id": 1, "name": "Bench Press", "description": "...", "muscleGroup": "CHEST" },
  { "id": 2, "name": "Pull Up", "description": "...", "muscleGroup": "BACK" }
]
```

---

### 6. Pobierz jedno ćwiczenie
* GET
* path: `/exercises/{id}`
* Required role: brak (publiczne)
* Przykładowa odpowiedź 200:
```json
{ "id": 1, "name": "Bench Press", "description": "...", "muscleGroup": "CHEST" }
```
* Błędy: 404 → `{ "error": "not_found" }`

---

### 7. Utwórz ćwiczenie
* POST
* path: `/exercises`
* Required role: `"ROLE_ADMIN"`
* Example body:
```json
{
  "name": "Squat",
  "description": "Back squat description",
  "muscleGroup": "LEGS"
}
```
* Przykładowa odpowiedź 200: zapisany obiekt Exercise

---

### 8. Aktualizuj ćwiczenie (częściowo)
* PUT
* path: `/exercises/{id}`
* Required role: `"ROLE_ADMIN"`
* Example body (przykład częściowej aktualizacji):
```json
{
  "description": "Updated description",
  "muscleGroup": "LEGS"
}
```
* Przykładowa odpowiedź 200: zaktualizowany Exercise
* Błędy: 404 → `{ "error": "not_found" }`, 400 → `{ "error": "invalid_payload" }`

---

### 9. Usuń ćwiczenie
* DELETE
* path: `/exercises/{id}`
* Required role: `"ROLE_ADMIN"`
* Przykładowa odpowiedź 200:
```json
{ "deleted": 5 }
```
* Błędy: 404 → `{ "error": "not_found" }`

---

## WORKOUT SERVICE — `/plans`

Modele (ważne pola):
- WorkoutPlan: id, name, ownerUsername, items
- WorkoutItem: exerciseId (Long), sets (int), reps (int)

Uwaga: `ownerUsername` jest nadawane automatycznie z tokenu JWT przy tworzeniu planu.

### 10. Utwórz plan treningowy
* POST
* path: `/plans`
* Required role: zalogowany użytkownik (token wymagany)
* Example body:
```json
{
  "name": "Plan na masę",
  "items": []
}
```
* Przykładowa odpowiedź 200: zapisany WorkoutPlan (z polem `ownerUsername` ustawionym na nazwę użytkownika z tokenu)

---

### 11. Pobierz wszystkie własne plany
* GET
* path: `/plans`
* Required role: zalogowany użytkownik (token wymagany)
* Przykładowa odpowiedź 200:
```json
[
  { "id": 1, "name": "Morning Push", "ownerUsername": "jan", "items": [] }
]
```

---

### 12. Pobierz pojedynczy plan (tylko właściciel)
* GET
* path: `/plans/{id}`
* Required role: właściciel planu (token wymagany)
* Przykładowa odpowiedź 200: WorkoutPlan
* Błędy: 401 `{ "error": "unauthorized" }`, 403 `{ "error": "forbidden" }`, 404 `{ "error": "not_found" }`

---

### 13. Aktualizuj plan (tylko właściciel)
* PUT
* path: `/plans/{id}`
* Required role: właściciel planu (token wymagany)
* Example body (aktualizacja nazwy):
```json
{ "name": "Nowa nazwa planu" }
```
* Przykładowa odpowiedź 200: zaktualizowany WorkoutPlan

---

### 14. Usuń plan (tylko właściciel)
* DELETE
* path: `/plans/{id}`
* Required role: właściciel planu (token wymagany)
* Przykładowa odpowiedź 200:
```json
{ "deleted": <id> }
```

---

### 15. Dodaj ćwiczenie do planu (tylko właściciel)
* POST
* path: `/plans/{planId}/add`
* Required role: właściciel planu (token wymagany)
* Example body:
```json
{
  "exerciseId": 5,
  "sets": 3,
  "reps": 8
}
```
* Walidacja:
  - `exerciseId` musi być obecne; serwis sprawdza istnienie ćwiczenia przez wywołanie `exercise-service`
  - `sets` i `reps` muszą być > 0
* Przykładowa odpowiedź 200: zapisany WorkoutPlan z dodanym elementem
* Błędy: 400 (np. `invalid_sets`, `invalid_reps`, `exercise_id_missing`), 404 `exercise_not_found` / `plan_not_found`, 403 `forbidden`, 401 `unauthorized`

---

## Ogólne uwagi
- Token JWT zawiera pola `sub` (username) i `roles` — format roli: `ROLE_USER` / `ROLE_ADMIN`.
- Przy rejestracji nowego użytkownika domyślna rola to `ROLE_USER`.
- W środowisku produkcyjnym warto skonfigurować adres `EXERCISE_SERVICE_URL` w `workout-service` (w kodzie jest tymczasowo `http://localhost:8082/exercises/`).
- Standardowe odpowiedzi błędów w serwisach: `{ "error": "unauthorized" }`, `{ "error": "forbidden" }`, `{ "error": "not_found" }`, `{ "error": "invalid_payload" }`.

---
