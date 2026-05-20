# Course Enrollment and Classroom Scheduling System

This is the project for the Software Engineering course (GIC). It is a comprehensive web-based application designed to manage university course enrollments and classroom scheduling. Built with **Spring Boot** and **Java 21**, the system provides a robust platform for Administrators, Lecturers, and Students to interact seamlessly.

## 📖 Project Description

The Course Enrollment and Classroom Scheduling System streamlines the academic administration process. It eliminates manual scheduling conflicts and provides an intuitive portal for all university stakeholders. 

### Key Features by Role:

*   **Administrator:**
    *   Manage user accounts (Students, Lecturers, Admins) and assign roles.
    *   Create, update, and manage courses and their details (credits, capacity).
    *   Manage classroom data (room numbers, building, max capacity).
    *   Assign lecturers and classrooms to specific courses.
    *   Schedule classes (assigning days of the week, start times, and end times) avoiding conflicts.
*   **Lecturer:**
    *   Access a personalized dashboard to view assigned courses.
    *   View the list of students enrolled in their respective courses.
    *   Manage their department information and office hours.
*   **Student:**
    *   Browse available courses for the upcoming semester.
    *   Enroll in courses (respecting capacity limits and avoiding duplicate enrollments).
    *   View their personalized class schedule and enrolled courses on their dashboard.

## 🛠️ Technology Stack

*   **Backend:** Java 21, Spring Boot 3.5.7
*   **Web Framework:** Spring Web MVC
*   **Security:** Spring Security (Form Login & Role-Based Access Control), JWT (JSON Web Tokens)
*   **Database Integration:** Spring Data JPA, Hibernate
*   **Database:** MySQL 8.0
*   **Database Migration:** Flyway
*   **Frontend (Views):** Thymeleaf, HTML5, CSS3
*   **Build Tool:** Gradle

## 🗄️ Database Schema Overview

The system uses a relational database structure with the following core entities:
*   `users` & `roles`: Manages authentication and authorization (Admin, Lecturer, Student).
*   `lecturers`: Stores lecturer-specific details (department, office hours).
*   `classrooms`: Stores room details and capacities.
*   `courses`: Holds course metadata and foreign keys linking to the assigned lecturer and classroom.
*   `class_schedules`: Manages the timetables (day, start/end time) for courses.
*   `enrollments`: Tracks which students are enrolled in which courses.

## 🚀 Getting Started

### Prerequisites

*   Java Development Kit (JDK) 21
*   MySQL Server (Version 8.0+)
*   Gradle (optional, as the wrapper `gradlew` is included)

### Setup Instructions

1.  **Clone the Repository:**
    ```bash
    git clone <repository-url>
    cd Course-Enrollment-and-Classroom-Scheduling-System
    ```

2.  **Database Configuration:**
    *   Create a MySQL database named `course_enrollment_and_class_scheduling`.
    *   Update the `src/main/resources/application.properties` file with your MySQL credentials if they differ from the default:
        ```properties
        spring.datasource.username=root
        spring.datasource.password=your_password
        ```

3.  **Run the Application:**
    *   Using Gradle Wrapper (Linux/macOS):
        ```bash
        ./gradlew bootRun
        ```
    *   Using Gradle Wrapper (Windows):
        ```cmd
        gradlew.bat bootRun
        ```

    *Flyway will automatically run the SQL migration scripts located in `src/main/resources/db/migration/` to create the tables and insert initial dummy data.*

4.  **Access the Application:**
    *   Open your web browser and navigate to: `http://localhost:8080`
    *   **Default Admin Account** (seeded by Flyway):
        *   **Username:** admin
        *   **Password:** 987654321

## 📂 Project Structure

*   `src/main/java/com/.../`: Contains all Java source code organized by controllers, services, repositories, entities, and configuration.
*   `src/main/resources/templates/`: Thymeleaf HTML templates for the frontend views, separated by roles (`admin/`, `lecturer/`, `student/`).
*   `src/main/resources/static/`: Static assets like CSS (`style.css`, `dashboard.css`).
*   `src/main/resources/db/migration/`: Flyway SQL scripts for database initialization and versioning.
*   `report/`: Contains project documentation, progress reports, and the final presentation.
