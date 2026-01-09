# Student API with Spring Boot and PostgreSQL

A simple REST API for managing student information using Spring Boot and PostgreSQL database.

## Features

- GET /api/students - Get all students
- GET /api/students/{id} - Get a student by ID
- POST /api/students - Create a new student
- PUT /api/students/{id} - Update an existing student
- DELETE /api/students/{id} - Delete a student

## Prerequisites

- Java 17 or higher
- Maven 3.6 or higher
- PostgreSQL database server

## PostgreSQL Setup

1. Install and start PostgreSQL server
2. Create a database named `studentdb`:

```sql
CREATE DATABASE studentdb;
```

3. Update the database credentials in `src/main/resources/application.properties` if needed:
   - `spring.datasource.username` (default: postgres)
   - `spring.datasource.password` (default: yassin1)

Alternatively, you can run the provided SQL setup script `setup_postgres.sql` to initialize the database:

```bash
psql -U postgres -f setup_postgres.sql
```

## How to Run

1. Clone the repository
2. Set up PostgreSQL database as described above
3. Navigate to the project directory
4. Run the application using Maven:

```bash
mvn spring-boot:run
```

Or build and run the JAR file:

```bash
mvn clean package
java -jar target/student-api-0.0.1-SNAPSHOT.jar
```

The API will be available at `http://localhost:8080`

## Docker Deployment

The application can also be deployed using Docker and Docker Compose:

1. Make sure you have Docker and Docker Compose installed
2. From the project directory, run:

```bash
docker-compose up --build
```

This will start both the application and PostgreSQL database in containers. The database will be available on port 5432 and the API on port 8080.

## Example Requests

### Get all students:
```bash
curl -X GET http://localhost:8080/api/students
```

### Get a student by ID:
```bash
curl -X GET http://localhost:8080/api/students/1
```

### Create a new student:
```bash
curl -X POST http://localhost:8080/api/students \
  -H "Content-Type: application/json" \
  -d '{"firstName":"Alice","lastName":"Johnson","email":"alice.johnson@example.com"}'
```

### Update a student:
```bash
curl -X PUT http://localhost:8080/api/students/1 \
  -H "Content-Type: application/json" \
  -d '{"firstName":"Updated","lastName":"Name","email":"updated.name@example.com"}'
```

### Delete a student:
```bash
curl -X DELETE http://localhost:8080/api/students/1
```