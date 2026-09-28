# EventEase — College Event Registration System

**EventEase** is a simple, clean, and robust college event registration web application built with **Java 25**, **Spring Boot**, **Spring Data JPA**, **Maven**, and **MySQL**, featuring a modern **HTML5/CSS3/Vanilla JavaScript** frontend.

---

## 1. Existing Spring Initializr Inspection Findings

Upon inspecting the original Spring Initializr project:
* **Spring Boot Version:** `4.1.1` (starter parent configured in initializr artifact).
* **Java Version:** `25` (`<java.version>25</java.version>`).
* **Existing Package:** `com.example.event`.
* **Files Kept:**
  - `pom.xml` (Updated version & starter dependencies cleanly without replacing file structure).
  - `.mvn/`, `mvnw`, `mvnw.cmd` Maven wrappers.
  - `src/main/java/com/example/event/EventApplication.java` (Preserved main class).
  - `src/main/resources/application.properties` (Configured with MySQL & JPA properties).
* **Files Added:**
  - Entity Layer: `Student.java`, `Organizer.java`, `Event.java`, `Registration.java`
  - Repository Layer: `StudentRepository.java`, `OrganizerRepository.java`, `EventRepository.java`, `RegistrationRepository.java`
  - Service Layer: `LoginService.java`, `StudentService.java`, `OrganizerService.java`, `EventService.java`, `RegistrationService.java`
  - Controller Layer: `LoginController.java`, `StudentController.java`, `OrganizerController.java`, `EventController.java`, `RegistrationController.java`
  - Frontend: `index.html`, `student.html`, `organizer.html`, `event.html`, `css/style.css`, `js/app.js` (both in `frontend/` and `src/main/resources/static/`).
  - Database: `database/eventease.sql`
  - Unit Tests: `RegistrationServiceTest.java`

---

## 2. Final Project Structure

```text
event/
├── pom.xml
├── mvnw
├── mvnw.cmd
├── .mvn/
├── database/
│   └── eventease.sql
├── frontend/
│   ├── index.html
│   ├── student.html
│   ├── organizer.html
│   ├── event.html
│   ├── css/
│   │   └── style.css
│   └── js/
│       └── app.js
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── example/
│   │   │           └── event/
│   │   │               ├── EventApplication.java
│   │   │               ├── controller/
│   │   │               │   ├── EventController.java
│   │   │               │   ├── LoginController.java
│   │   │               │   ├── OrganizerController.java
│   │   │               │   ├── RegistrationController.java
│   │   │               │   └── StudentController.java
│   │   │               ├── entity/
│   │   │               │   ├── Event.java
│   │   │               │   ├── Organizer.java
│   │   │               │   ├── Registration.java
│   │   │               │   └── Student.java
│   │   │               ├── repository/
│   │   │               │   ├── EventRepository.java
│   │   │               │   ├── OrganizerRepository.java
│   │   │               │   ├── RegistrationRepository.java
│   │   │               │   └── StudentRepository.java
│   │   │               └── service/
│   │   │                   ├── EventService.java
│   │   │                   ├── LoginService.java
│   │   │                   ├── OrganizerService.java
│   │   │                   ├── RegistrationService.java
│   │   │                   └── StudentService.java
│   │   └── resources/
│   │       ├── application.properties
│   │       └── static/
│   │           ├── index.html
│   │           ├── student.html
│   │           ├── organizer.html
│   │           ├── event.html
│   │           ├── css/style.css
│   │           └── js/app.js
│   └── test/
│       └── java/
│           └── com/
│               └── example/
│                   └── event/
│                       ├── EventApplicationTests.java
│                       └── service/
│                           └── RegistrationServiceTest.java
└── README.md
```

---

## 3. Implemented Features

### Organizer Capabilities
1. Role-based Login.
2. Create Event (automatically associated with logged-in organizer).
3. View Created Events (with calculated seat count and real-time status).
4. Edit Event details.
5. Delete Event.
6. View Participant List (Student Name, Email, Department, Year, Registration Date, Status).

### Student Capabilities
1. Role-based Login.
2. View Available Events (Cards showing title, date, venue, available seats, status pill).
3. View Event Details.
4. Register for an Event (with real-time seat decrement).
5. View My Registrations.
6. Cancel Registration before the event date (frees seat for others).

### System Rules Enforced by Backend
1. **Capacity Enforcement:** Registration fails when `registeredSeats >= maxSeats`.
2. **Duplicate Registration Rejection:** Prevents active duplicate registration (HTTP 409 Conflict).
3. **Automatic Seat Restoration:** Cancelling a registration frees a seat immediately. Re-registration by previously cancelled student is allowed if seats remain.
4. **Past Event Protection:** Rejects registrations and cancellations for past events.

---

## 4. IntelliJ IDEA & JDK 25 Setup

### 1. Open Project in IntelliJ IDEA
1. Launch IntelliJ IDEA.
2. Click **Open** (or `File -> Open`).
3. Select the extracted project folder (`event`).

### 2. Configure JDK 25
1. Go to **File -> Project Structure...** (or `Ctrl + Alt + Shift + S`).
2. Under **Project Settings -> Project**:
   - **SDK:** Select `25 (Oracle OpenJDK 25)` or `Add SDK -> JDK...` and select your JDK 25 installation folder.
   - **Language level:** Select `25 - Preview / Plain`.
3. Click **Apply** and **OK**.
4. Go to **File -> Settings -> Build, Execution, Deployment -> Build Tools -> Maven -> Importing**:
   - Ensure **JDK for importer** is set to `Use Project JDK (25)`.

### 3. Reload Maven Projects
1. Open the **Maven** tool window on the right side panel.
2. Click the **Reload All Maven Projects** icon (refresh icon).
3. Wait for Maven to index dependencies.

---

## 5. Verification Commands

Verify system installation on terminal:

```bash
java -version
```
*Output should display JDK version (e.g., `java version 25` or `17+`).*

```bash
mvn -version
```
*Output should display Maven 3.9+ with Java runtime info.*

---

## 6. MySQL Setup Instructions

1. Start your local **MySQL Server** (e.g. via MySQL Workbench or Windows Services).
2. Open **MySQL Workbench** or command line `mysql -u root -p`.
3. Open and run the provided SQL script located at:
   `database/eventease.sql`
4. This script creates the database `eventease_db`, all 4 required tables (`students`, `organizers`, `events`, `registrations`), and populates sample demonstration data.

---

## 7. Configuring `application.properties`

Open `src/main/resources/application.properties`:

```properties
spring.application.name=event

# MySQL Datasource Setup
spring.datasource.url=jdbc:mysql://localhost:3306/eventease_db?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
spring.datasource.username=root

# ENTER YOUR LOCAL MYSQL PASSWORD BELOW:
spring.datasource.password=YOUR_MYSQL_PASSWORD

# JPA / Hibernate Setup
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect
```

> [!IMPORTANT]
> Replace `YOUR_MYSQL_PASSWORD` with your actual MySQL `root` account password before running the backend.

---

## 8. Database Verification Queries

Run the following in MySQL Workbench to verify database connectivity:

```sql
USE eventease_db;

SHOW TABLES;

SELECT * FROM students;
SELECT * FROM organizers;
SELECT * FROM events;
SELECT * FROM registrations;
```

---

## 9. Complete Connection Architecture

```text
HTML5 / CSS3 / JavaScript
        ↓
  Fetch API (HTTP)
        ↓
Spring Boot REST Controller  (@RestController)
        ↓
Business Service Layer       (@Service)
        ↓
Spring Data JPA Repository   (JpaRepository)
        ↓
     MySQL
```

### Layer Descriptions
* **Frontend (HTML/CSS/JS):** User interface rendering event cards, forms, and sending asynchronous HTTP requests via Fetch API.
* **REST Controllers:** Endpoints accepting HTTP GET/POST/PUT/DELETE requests, extracting parameters, calling services, and returning JSON.
* **Service Layer:** Houses core business rules (capacity calculation, duplicate check, event date validation, status computation).
* **Repository Layer:** Interfaces extending `JpaRepository` providing SQL abstraction and data access methods.
* **MySQL:** Relational database storing entity rows.

---

## 10. Registration Process Step-by-Step Flow

```text
Student clicks "Register"
        ↓
JavaScript sends POST /api/events/{eventId}/register/{studentId}
        ↓
RegistrationController handles request
        ↓
RegistrationService.registerStudentForEvent()
        ↓
Step 1: Verify Student exists (or throw 404)
        ↓
Step 2: Verify Event exists (or throw 404)
        ↓
Step 3: Check Event date is not in the past
        ↓
Step 4: Check if Student is already active REGISTERED (throw 409 Conflict)
        ↓
Step 5: Count active REGISTERED seats countByEventIdAndStatus(eventId, "REGISTERED")
        ↓
Step 6: Compare registeredSeats >= maxSeats (if full, throw 409 Conflict)
        ↓
Step 7: Create/Update Registration record status = "REGISTERED"
        ↓
RegistrationRepository.save() persists to MySQL
        ↓
HTTP 201 CREATED returned with success JSON
        ↓
Frontend displays success message & updates seat counts
```

---

## 11. Running the Application

### Running Backend

#### Method 1: IntelliJ IDEA
1. Navigate to `src/main/java/com/example/event/EventApplication.java`.
2. Right-click and select **Run 'EventApplication.main()'** (or click green Play icon).
3. The server starts at `http://localhost:8080`.

#### Method 2: Maven Command Line
In project directory:

Windows Command Prompt / PowerShell:
```cmd
mvnw.cmd spring-boot:run
```
Or:
```bash
mvn spring-boot:run
```

### Running Frontend (Windows - No Node.js Required)

#### Option 1: Direct Spring Boot Static Serving (Recommended)
Simply open your browser to:
[http://localhost:8080](http://localhost:8080)
The Spring Boot backend serves all frontend files automatically from static resources!

#### Option 2: Standalone File / Local Server
Double-click `frontend/index.html` in Windows File Explorer to open directly in Chrome/Edge, or serve using Python simple HTTP server:
```bash
python -m http.server 3000
```
Then visit `http://localhost:3000`.

---

## 12. Complete Demonstration Flow (Viva & Demo)

Follow this exact sequence to demonstrate all business rules:

1. **Start MySQL** and run `database/eventease.sql`.
2. **Start Spring Boot backend** (`EventApplication.java`).
3. Open `http://localhost:8080` in browser.
4. **Organizer Login:**
   - Click **Organizer Login**.
   - Email: `cse@example.com`, Password: `org123`.
5. **Create Event:**
   - Create an event `Hackathon 2026` with `Maximum Seats = 3`.
   - Click **Logout**.
6. **Student 1 Registration:**
   - Login as Student 1: Email `arun@example.com`, Password `student123`.
   - Click **Register** on `Hackathon 2026`. (Seats left: 2/3). Click Logout.
7. **Student 2 Registration:**
   - Login as Student 2: Email `priya@example.com`, Password `student123`.
   - Click **Register** on `Hackathon 2026`. (Seats left: 1/3). Click Logout.
8. **Student 3 Registration:**
   - Login as Student 3: Email `karthik@example.com`, Password `student123`.
   - Click **Register** on `Hackathon 2026`. (Seats left: 0/3, Status becomes `FULL`). Click Logout.
9. **Capacity Limit Test (Student 4):**
   - Login as Student 4: Email `meena@example.com`, Password `student123`.
   - Attempt to register for `Hackathon 2026`.
   - **Result:** System rejects registration with message `"Registration failed. This event is full."`
10. **Cancellation & Seat Restoration Test:**
    - Login as Student 2 (`priya@example.com`).
    - Go to **My Registrations** and click **Cancel**.
    - Status changes from `REGISTERED` to `CANCELLED`.
    - Seats available for `Hackathon 2026` becomes `1`.
11. **Re-registration Test:**
    - Login as Student 4 (`meena@example.com`).
    - Register for `Hackathon 2026`.
    - **Result:** Registration succeeds now that a seat is available!
12. **Organizer Participant Verification:**
    - Login as Organizer (`cse@example.com`).
    - Click **Participants** on `Hackathon 2026`.
    - View live participant list showing registered and cancelled students with registration timestamps.

---

## 13. Troubleshooting Documentation

| Issue | Cause | Solution |
| :--- | :--- | :--- |
| **MySQL Connection Error** | MySQL not started or incorrect password. | Ensure MySQL service is running on port `3306`. Check `spring.datasource.password` in `application.properties`. |
| **Unknown Database `eventease_db`** | Database not created in MySQL. | Run `CREATE DATABASE eventease_db;` or execute `database/eventease.sql`. |
| **Port 8080 already in use** | Another application is running on 8080. | In `application.properties`, set `server.port=8081` and update `API_BASE_URL` in `app.js` to `http://localhost:8081/api`. |
| **Java Version Mismatch** | System default JDK differs from project JDK 25. | Set Project SDK to JDK 25 in IntelliJ (`File -> Project Structure -> SDK`). |
| **CORS Error in Browser** | Calling API from different origin. | All controllers include `@CrossOrigin(origins = "*")`. Alternatively serve frontend via Spring Boot at `http://localhost:8080`. |
| **HTTP 409 Conflict** | Business rule violation (Event Full / Duplicate Registration). | Expected behavior when student registers twice or event reaches max capacity. |
| **HTTP 404 Not Found** | Invalid entity ID or incorrect API endpoint URL. | Check path variable ID and verify backend server is running. |

---

## 14. Academic & Production Note

> [!NOTE]
> This project uses plain text password comparison suitable for college-level demonstration and viva examination. For enterprise production applications, implement Spring Security with BCrypt password hashing (`PasswordEncoder`) and stateless JWT token authentication.
