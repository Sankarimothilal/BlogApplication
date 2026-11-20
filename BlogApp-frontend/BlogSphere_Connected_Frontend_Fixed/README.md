# BlogSphere Connected Frontend

This frontend uses `fetch()` to connect to the Spring Boot backend at `http://localhost:8080/api`.

## Run
1. Start MySQL and the BlogSphere Spring Boot backend.
2. In `config.js`, confirm the API base URL.
3. Open `index.html` using Live Server (recommended).
4. Use Create account to register; login uses the backend session.

## Connected endpoints used
- `POST /auth/register`
- `POST /auth/login`
- `POST /auth/logout`
- `GET /auth/me`
- `GET /blogs`
- `GET /blogs/{id}`
- `GET /blogs/mine`
- `POST /blogs`
- `DELETE /blogs/{id}`
- `POST /blogs/{id}/like`
- `POST /blogs/{id}/bookmark`
- `GET /blogs/bookmarks`
- `GET /blogs/{id}/comments`
- `POST /blogs/{id}/comments`
- `GET /users/following`

Requests use `credentials: "include"` for session cookies. If requests fail due to CORS, the backend must allow the frontend origin and credentials.

## Feature status
This is an integration-ready frontend for the available backend endpoints. Some of the original 22 requirements still need additional backend APIs to be fully persistent (password recovery email, media uploads, notifications, reading history, detailed analytics, author discovery/follow controls, rich-text persistence, editing workflow, and complete admin UI). The frontend does not pretend these are completed when the backend endpoints are not available.
