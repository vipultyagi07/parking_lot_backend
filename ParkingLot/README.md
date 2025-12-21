# Parking Lot Backend (Spring Boot)

This project is a Java Spring Boot backend application.

---

## Prerequisites

Make sure the following are installed on your system:

- Java 17
- Maven
- IntelliJ IDEA (Community or Ultimate)

Verify installation:
```bash
  java -version
  mvn -v
```

---

## Running the Application using IntelliJ IDEA

1. Open **IntelliJ IDEA**
2. Click **File → Open**
3. Select the project folder containing `pom.xml`
4. Import as a **Maven project**
5. Set **Project SDK = Java 17**
6. Open:
   ```
   src/main/java/com/vip/ParkingLotApplication.java
   ```
7. Click the **Run (▶)** button

The application will start at:
```
http://localhost:8081
```

---

## Running the Application using Terminal (Without IntelliJ)

Navigate to the project root:
```bash
  cd <where src, pom.xml, etc. are present.>
```

Run the application:
```bash
  mvn spring-boot:run
```

### Run using JAR
```bash
  mvn clean package
  java -jar target/ParkingLot-0.0.1-SNAPSHOT.jar
```

---

## Swagger API Documentation

Swagger UI is available at:
```
   http://localhost:8081/swagger-ui/index.html
```

### JWT Authentication in Swagger

1. Generate JWT using the authentication API
2. Click **Authorize ** in Swagger UI
3. Paste the JWT token (without `Bearer`)
4. All secured APIs will use the token automatically

---

## Default URLs

- Application: http://localhost:8081
- Swagger UI: http://localhost:8081/swagger-ui/index.html
- OpenAPI Docs: http://localhost:8081/v3/api-docs







