# Enterprise Backend

Spring Boot 4 / Java 21 backend for the C-DAC Angular application. The implemented WMS feature is organized by feature and exposes its REST API under `/api/work`.

## Run locally

1. Start PostgreSQL and ensure the database configured in `src/main/resources/application.properties` exists.
2. From this directory, run `.\mvnw.cmd spring-boot:run`.
3. From `Frontend/CDAC-FRONTEND`, run `npm start`. The Angular development server proxies `/api` requests to `http://localhost:8080`.

Set `CORS_ALLOWED_ORIGINS` to a comma-separated list of trusted frontend origins when the browser calls the API from a different origin. Do not commit database credentials or production secrets.

## Run with Docker Compose

From the repository root, run:

```bash
docker compose up --build
```

Open the frontend at `http://localhost:4200`; the API is also exposed at `http://localhost:8080`. Compose starts PostgreSQL and waits for it to become healthy before starting the backend. The default database credentials are for local development only; set `POSTGRES_USER`, `POSTGRES_PASSWORD`, and optionally `POSTGRES_DB` in the environment before starting the stack when using different credentials. Database data persists in the `postgres-data` volume.

## WMS API

Successful responses use `{ "success": true, "message": "...", "data": ... }`. Errors use the same envelope and provide a structured `ApiError` in `data`.

| Method | Route | Behavior |
| --- | --- | --- |
| `GET` | `/api/work` | List work records; optional `search` matches work details and contractor |
| `GET` | `/api/work/{workRegNo}` | Get one work record |
| `GET` | `/api/work/{workRegNo}/audit` | Get its Envers revision history |
| `POST` | `/api/work` | Create a work record |
| `PUT` | `/api/work/{workRegNo}` | Replace a work record's editable fields |
| `DELETE` | `/api/work/{workRegNo}` | Delete a work record |

The WMS entity continues to map to the existing `works_details` table. Actual start date is optional; stipulated dates are required, and the end date cannot precede the start date.
