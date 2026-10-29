# Farmer Marketplace Frontend

This frontend is designed to run with the Java 8 / Spring Boot 2.7 backend in this project.

## Pages
- `index.html` — landing page
- `login.html` and `register.html` — demo browser-based authentication
- `farmer-dashboard.html` — create, read, update and delete crop listings through the backend API
- `buyer-dashboard.html` — browse and filter crops from the backend
- `crop-details.html` — crop details and demo purchase request
- `my-orders.html` — browser-local demo order history
- `admin-dashboard.html` — crop listing overview

## Run
1. Start PostgreSQL and create database `farmer_marketplace`.
2. In the backend project root, configure `src/main/resources/application.properties` credentials.
3. Run the Spring Boot app from Eclipse or `mvn spring-boot:run`.
4. Open `frontend` in VS Code and use Live Server on `index.html`.
5. Backend API base URL is set at the top of `app.js`: `http://localhost:8080`.

## Backend integration
The frontend connects to existing crop endpoints:
- `GET /api/crops`
- `GET /api/crops/{id}`
- `POST /api/crops`
- `PUT /api/crops/{id}`
- `DELETE /api/crops/{id}`

Spring CORS must allow the exact Live Server origin (commonly `http://localhost:5500`).

## Important limitations
The current backend only implements crop CRUD. Login/register, purchase requests/orders, notifications and admin authorization are demo frontend flows stored in browser `localStorage`; they are not secure server-side features. Do not use real passwords or deploy this as-is. The crop listings and CRUD forms call the Spring Boot backend. External Unsplash images and Google Fonts require internet access; crop cards use emoji as reliable local fallbacks.
