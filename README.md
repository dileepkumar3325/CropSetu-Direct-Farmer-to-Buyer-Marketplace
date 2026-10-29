# Farmer Marketplace — Java 8 compatible project

A complete starter project with a Java 8-compatible Spring Boot backend, PostgreSQL persistence, REST CRUD APIs for crops, and a plain HTML/CSS/JavaScript frontend.

## Requirements
- JDK 8
- Maven 3.6+
- PostgreSQL
- VS Code with Live Server (for frontend) or any static web server

## 1. Create the database
In pgAdmin Query Tool or `psql`, run:

```sql
CREATE DATABASE farmer_marketplace;
```

## 2. Configure PostgreSQL
Edit `src/main/resources/application.properties` and set your PostgreSQL username/password. Defaults are `postgres` / `postgres` for local practice; change them to your own values.

## 3. Start the backend
Open a terminal in the project root (the folder containing `pom.xml`) and run:

```bash
mvn clean spring-boot:run
```

Backend base URL: `http://localhost:8080`
Swagger UI: `http://localhost:8080/swagger-ui.html`

## 4. Start the frontend
Open the `frontend` folder in VS Code, right-click `index.html`, and choose **Open with Live Server**. The default Live Server address is `http://localhost:5500`.

The frontend calls `http://localhost:8080/api/crops` and sends new listings to `POST /api/crops`. CORS is configured for localhost:5500.

## API examples
- `GET /api/crops` — list all crops
- `GET /api/crops?q=tomato&location=Anantapur` — search crops
- `GET /api/crops/{id}` — get one crop
- `POST /api/crops` — add a crop
- `PUT /api/crops/{id}` — update a crop
- `DELETE /api/crops/{id}` — delete a crop

Example POST JSON:

```json
{
  "name": "Tomato",
  "category": "Vegetables",
  "farmerName": "Ravi",
  "location": "Anantapur",
  "description": "Fresh farm-grown tomatoes",
  "pricePerKg": 30.0,
  "availableKg": 100.0
}
```

## Scope and security note
This Java 8 version is a working crop-listing CRUD starter so that the frontend can save and read records from PostgreSQL. It does not include JWT login, role-based access, purchase orders, or notifications from the newer Java 21 project. Do not deploy this demo publicly without adding authentication, authorization, and production security controls.


## Full frontend pages
See `frontend/README-FRONTEND.md` for pages, run steps, API integration, and demo-only limitations.
