# BlogSphere Spring Boot Backend (Starter)

Java 17, Spring Boot 3.4, Spring Web, Spring Data JPA, MySQL and BCrypt.

## Setup
1. Install JDK 17+, Maven and MySQL.
2. Create a database (optional; `createDatabaseIfNotExist=true` creates `blogsphere`):
   `CREATE DATABASE blogsphere;`
3. Edit `src/main/resources/application.properties` and set your MySQL password.
4. Open the folder in IntelliJ IDEA (or import as Maven project).
5. Run `BlogSphereApplication.java` or execute `mvn spring-boot:run`.
6. Backend runs at `http://localhost:8080`.

## Frontend CORS
Configured for Live Server localhost ports 5500 and 5501. Change `AppConfig.java` allowed origins if Live Server uses a different port.

## API endpoints
### Auth
- `POST /api/auth/register` `{name,email,password}`
- `POST /api/auth/login` `{email,password}`
- `POST /api/auth/logout`
- `GET /api/auth/me`
- `POST /api/auth/forgot-password` (placeholder response; no email sent)

### Blogs
- `GET /api/blogs` published blogs
- `GET /api/blogs?category=Technology`
- `GET /api/blogs?search=spring`
- `GET /api/blogs/{id}` get and increment views
- `GET /api/blogs/mine` current user's blogs
- `POST /api/blogs` create draft or published blog (login required)
- `PUT /api/blogs/{id}` update own blog or admin
- `DELETE /api/blogs/{id}` delete own blog or admin
- `POST /api/blogs/{id}/like` toggle like (login required)
- `POST /api/blogs/{id}/bookmark` toggle bookmark (login required)
- `GET /api/blogs/bookmarks` current user's saved blogs

### Comments
- `GET /api/blogs/{blogId}/comments`
- `POST /api/blogs/{blogId}/comments` `{text,parentCommentId?}` (login required)
- `DELETE /api/blogs/{blogId}/comments/{commentId}` (comment owner/admin)

### Users
- `GET /api/users`
- `GET /api/users/{id}`
- `PUT /api/users/me` `{name,bio,socialLink}`
- `POST /api/users/{id}/follow` toggle follow (login required)
- `GET /api/users/following` (login required)

### Admin
- `GET /api/admin/users` (admin only)
- `PUT /api/admin/users/{id}/role` `{role:"AUTHOR"|"READER"|"ADMIN"}`
- `GET /api/admin/blogs` (admin only)
- `DELETE /api/admin/blogs/{id}` (admin only)

## Notes / limitations of this starter
- Session-based login is used. Frontend requests need `credentials: "include"` and fetch requests should include `Content-Type: application/json`.
- Session auth endpoints work for same-browser local development. This starter uses session identity in HttpSession and BCrypt password hashes.
- Admin access: after registering, role defaults to READER. For local development, promote a user in MySQL (`UPDATE users SET role='ADMIN' WHERE email='you@example.com';`) and log out/in.
- Password recovery is only a placeholder; connect an email provider before use.
- Notifications, reading history and detailed analytics are not yet persisted as backend modules. They can be added as follow-up entities/endpoints.
- Rich text content is stored as HTML. Sanitize it before rendering in production to avoid XSS.
- Do not deploy this starter publicly until you harden authentication, session security, validation, and rate limiting.
