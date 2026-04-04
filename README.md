# Home-Workout-Projekt

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

Pełna dokumentacja endpointów znajduje się w pliku [ENDPOINT_DOCS.md](ENDPOINT_DOCS.md).
