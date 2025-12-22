# 2nd week report

## Entities Created

You have defined multiple **JPA entities** representing the system domain:

### User

- Fields: `id`, `username`, `password`, `email`, `fullName`
- Relationship:
  - `@ManyToMany` with **Role** via join table `user_roles`

### Role

- Represents user roles (e.g., `ADMIN`, `STUDENT`, `INSTRUCTOR`)

![alt text](./pics/image.png)

### Course

- Represents a course offered in the system

![alt text](./pics/image-1.png)

### Classroom

- Represents physical or virtual classrooms

![alt text](./pics/image-2.png)

### ClassSchedule

- Represents scheduling information for courses

![alt text](./pics/image-3.png)

### Enrollment

- Represents student enrollment in courses or schedules

![alt text](./pics/image-4.png)

All entities are annotated with `@Entity`, `@Table`, and proper JPA mappings.

---

## Relationships Implemented

- **User ↔ Role** → `@ManyToMany`
- **Enrollment** connects users to courses and/or class schedules
- **Scheduling entities** link **Course**, **Classroom**, and time-related information

![alt text](./pics/image-5.png)

---

## Repositories

You created **Spring Data JPA repositories** for each main entity:

- `UserRepository`
- `RoleRepository`
- `CourseRepository`
- `ClassScheduleRepository`
- `EnrollmentRepository`

These repositories handle database operations automatically.

![alt text](./pics/image-6.png)
![alt text](./pics/image-7.png)
![alt text](./pics/image-8.png)
![alt text](./pics/image-9.png)
![alt text](./pics/image-10.png)

---

## DTOs & Requests

- `ClassScheduleDTO`
- `EnrollmentRequest`

These are used to separate **API input/output models** from **entity models**, following good design practices.

![alt text](./pics/image-11.png)
![alt text](./pics/image-12.png)

---

## Database & Migration

- Integrated **Flyway** for database version control
- Created migration file:
  - `V1__Create_Tables.sql`
- The database successfully initializes and applies migrations on application startup

![alt text](./pics/image-13.png)
![alt text](./pics/image-14.png)
![alt text](./pics/image-15.png)
