# 🛠️ Dormitory Maintenance Request System (ระบบแจ้งซ่อมอุปกรณ์ในหอพัก)

## Project Overview

This is a web-based **Dormitory Maintenance Request System** developed as a university project for the course "Software Design Principles" (CP353002).

The system addresses the inefficiencies of traditional methods (such as phone calls, messaging apps, or paper reports) for requesting and managing equipment repairs in student dormitories. It provides a centralized, rapid, and transparent platform for all stakeholders.

This project is a strong demonstration of object-oriented programming (OOP) and modern software architecture skills, with a focus on implementing the **S.O.L.I.D. principles**.

## 🎯 Key Objectives

* **Develop a Web-based System:** Create a functional website for reporting internal dormitory issues.
* **Resident Functionality:** Allow residents to conveniently report problems and track the status of their maintenance requests.
* **Administrator Functionality:** Enable dormitory management to efficiently assign tasks and monitor the progress of all work orders.
* **Technician Functionality:** Provide technicians with a simple way to accept jobs and update the maintenance status.

## 💻 Technology Stack (Inferred & Mentioned)

* **Backend Framework:** Spring Boot (Inferred from the mention of Spring Data JPA and common use in the context of the course)
* **Database Interaction:** Spring Data JPA
* **Language:** Java (Standard for Spring Boot)
* **Design Focus:** S.O.L.I.D. Principles


## 🚀 Getting Started

```bash
./mvnw spring-boot:run
OR
spring-boot:run
```

### Prerequisites

* Java Development Kit (JDK) 22
* Maven or Gradle
* A running SQL database (e.g., PostgreSQL, MySQL)

### Installation

1.  Clone the repository:
    ```bash
    git clone https://github.com/Bookantna/ticketDorm
    cd dormitory-maintenance-system
    ```
2.  Configure your database settings in `src/main/resources/application.properties`.
3.  Build and run the application:
    ```bash
    # Using Maven
    ./mvnw spring-boot:run
    ```

### Access

The application will be accessible at: `http://localhost:8080` (Default Spring Boot port).
