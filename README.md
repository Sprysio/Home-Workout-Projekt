# Home Workout Projekt

## Opis projektu

Home Workout to projekt typu full-stack oparty o architekturę mikroserwisową.
System umożliwia:
- rejestrację i logowanie użytkowników,
- zarządzanie rolami użytkowników (USER / ADMIN),
- przeglądanie katalogu ćwiczeń,
- tworzenie i edycję własnych planów treningowych.

Frontend komunikuje się z backendem przez API Gateway, a wszystkie publiczne endpointy są wystawione pod prefiksem `/api`.

## Architektura

Projekt składa się z następujących komponentów:

1. `frontend` (React + Vite + Nginx)
- interfejs użytkownika,
- wysyła żądania na ścieżki `/api/...`,
- w środowisku Docker Nginx proxy przekazuje `/api` do `api-gateway`.

2. `api-gateway` (Spring Cloud Gateway)
- pojedynczy punkt wejścia do backendu,
- routing do serwisów wewnętrznych,
- obsługa JWT i filtrowanie ruchu.

3. `auth-service` (Spring Boot)
- rejestracja użytkowników,
- logowanie i wydawanie tokenów JWT,
- endpointy administracyjne do zarządzania rolami.

4. `exercise-service` (Spring Boot)
- katalog ćwiczeń,
- odczyt publiczny,
- modyfikacje dostępne dla administratora.

5. `workout-service` (Spring Boot)
- zarządzanie planami treningowymi użytkownika,
- dostęp tylko do własnych planów,
- dodawanie ćwiczeń do planu z walidacją.

6. `postgres`
- wspólna baza danych dla serwisów backendowych.

7. `redis`
- cache / rate limiting dla gateway.

## Struktura repozytorium

- `frontend/` - aplikacja React
- `api-gateway/` - gateway backendowy
- `auth-service/` - autoryzacja i role
- `exercise-service/` - katalog ćwiczeń
- `workout-service/` - plany treningowe
- `db/init.sql` - inicjalizacja bazy
- `docker-compose.yml` - uruchamianie całości lokalnie
- `ENDPOINT_DOCS.md` - szczegółowa dokumentacja API

## Uruchomienie całego projektu (Docker Compose)

Wymagania:
- Docker
- Docker Compose

### 1. Wygeneruj plik `.env` z sekretami JWT

Projekt nie używa domyślnych sekretów JWT. Przed pierwszym uruchomieniem wygeneruj `.env`:

```bash
./scripts/init-env.sh
```

Skrypt tworzy:
- `JWT_SECRET` (dla `auth-service`, `workout-service`, `exercise-service`)
- `JWT_SECRET_BASE64` (dla `api-gateway`)

### 2. Uruchom stack

```bash
docker compose up -d
```

Podgląd logów:

```bash
docker compose logs -f
```

Zatrzymanie:

```bash
docker compose down
```

## Dostęp

- Frontend: `http://localhost`
- API Gateway: `http://localhost:8080`

Uwaga: endpointy backendowe dostępne z zewnątrz przechodzą przez gateway i mają prefiks `/api`.

## API i autoryzacja

- Logowanie zwraca token JWT.
- Endpointy chronione wymagają nagłówka `Authorization: Bearer <token>`.
- Role:
   - `ROLE_USER`
   - `ROLE_ADMIN`

Pełna lista endpointów i przykładów znajduje się w [ENDPOINT_DOCS.md](ENDPOINT_DOCS.md).
