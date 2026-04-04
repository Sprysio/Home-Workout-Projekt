# Home-Workout-Projekt# Home Workout Projekt

1. Opis
Projekt "Home Workout" to prosty system do zarządzania planami treningowymi i katalogiem ćwiczeń, z autoryzacją opartą na JWT. Składa się z kilku mikroserwisów, które komunikują się ze sobą::
   1. `auth-service` - rejestracja, logowanie, zarządzanie rolami (ROLE_USER, ROLE_ADMIN).
   2. `exercise-service` - katalog ćwiczeń (CRUD: tylko ADMIN może tworzyć/edytować/usunąć, wszyscy mogą przeglądać).
   3. `workout-service` - plany treningowe użytkowników (każdy użytkownik widzi tylko swoje plany).
   4. `api-gateway` - prosty gateway (może przekazywać nagłówki Authorization).
   5. `frontend` - prosta aplikacja React.

Wymagania
1. Java 17+
2. Maven
3. Docker + docker-compose 
4. Baza danych

Przykładowe endpointy
1. `auth-service` (domyślnie `/auth`)
   1. `POST /auth/register` - rejestracja 
   2. `POST /auth/login` - logowanie (body: username, password) → zwraca `{ "token": "..." }`
   3. `GET /auth/users` - lista użytkowników (ADMIN only)
   4. `PUT /auth/users/{id}/role` - zmiana roli (ADMIN only)
2. `exercise-service` (domyślnie `/exercises`)
   1. `GET /exercises` - lista ćwiczeń (publiczne)
   2. `GET /exercises/{id}` - pobierz ćwiczenie
   3. `POST /exercises` - utwórz (ADMIN only)
   4. `PUT /exercises/{id}` - edytuj (ADMIN only)
   5. `DELETE /exercises/{id}` - usuń (ADMIN only)
3. `workout-service` (domyślnie `/plans`)
   1. `POST /plans` - utwórz plan 
   2. `GET /plans` - pobierz wszystkie własne plany 
   3. `GET /plans/{id}` - pobierz plan, tylko właściciel
   4. `PUT /plans/{id}` - zaktualizuj plan, tylko właściciel
   5. `DELETE /plans/{id}` - usuń plan, tylko właściciel
   6. `POST /plans/{planId}/add` - dodaj ćwiczenie do planu