# TASK 3 REPORT: Requirements, Architecture, Data Model, API List, and Local Setup
**Project Name:** Dockerized Farm Equipment Rental System (AgriRent DevOps)  
**Document Reference:** AR-REP-003  
**Version:** 1.0.0  
**Date:** 2026-10-01  
**Author:** Software Architect & Tech Lead  
**Status:** Approved Architectural Baseline  

---

## 1. Executive Summary
This document formalizes the Software Requirements Specification (SRS) summary, system architecture, data models, REST API specifications, and local development runtime environment for the **Farm Equipment Rental System (AgriRent)**. The design prioritizes zero-friction containerization, high performance on Java 21 LTS, embedded Tomcat web server execution, and immediate testability via Selenium WebDriver and JUnit 5.

---

## 2. Software Requirements Specification (SRS) Summary

### 2.1 Functional Requirements (FR)
- **FR-01: Equipment Inventory Management (CRUD):** 
  - Ability to register (create) new equipment with attributes: Name, Category (Tractor, Harvester, Seeder, Sprayer, Tillage), Hourly Rate, Daily Rate, Location, Owner Name, and Contact.
  - Ability to view details and list all equipment in tabular and card formats.
  - Ability to update equipment details and availability status.
  - Ability to delete / retire equipment records.
- **FR-02: Search & Filter Engine:**
  - Real-time instant search by keyword across equipment name, category, and location.
  - Multi-attribute filter by Availability Status (`AVAILABLE`, `RENTED`, `MAINTENANCE`) and Category.
- **FR-03: Rental Booking Lifecycle:**
  - Farmers can book available machinery specifying Lessee Name, Contact, Rental Start Date, Duration (Hours / Days), and Field Location.
  - Automated calculation of Total Estimated Cost = `Daily Rate * Days` (or `Hourly Rate * Hours`).
  - Creation of booking record in `PENDING` state.
- **FR-04: Role-Based Status Workflow:**
  - Role switcher supporting **Farmer (Lessee)**, **Equipment Owner (Lessor)**, and **Admin**.
  - **Owner Workflow:** Review pending bookings -> `APPROVE` or `REJECT`.
  - **Dispatch Workflow:** Transition approved booking to `IN_USE` when machinery is delivered to field.
  - **Return & Settlement Workflow:** Transition to `COMPLETED` when equipment is returned; automatically restore equipment status to `AVAILABLE`.
- **FR-05: Real-Time Analytics Dashboard:**
  - Metric Cards: Total Assets, Available Count, Active Rentals, Total Rental Revenue (₹), Equipment Utilization Percentage.
  - Visual breakdown by Equipment Category and Rental Status distribution.
- **FR-06: System Health & Diagnostics:**
  - Dedicated `/api/health` and `/actuator/health` endpoints returning server timestamp, active threads, memory metrics, and status `UP`.

### 2.2 Non-Functional Requirements (NFR)
- **NFR-01 (Performance):** UI interaction and API response latency < 150ms for local queries.
- **NFR-02 (Portability):** 100% executable inside Linux/Windows Docker containers without external library installations.
- **NFR-03 (Observability):** Structured logging via SLF4J / Logback with correlation IDs for all state transitions.
- **NFR-04 (Compatibility):** Compatible with Google Chrome, Microsoft Edge, Brave, and headless Chromium drivers.

---

## 3. Use-Case Model & Diagram

```
                                  AGRIRENT USE-CASE DIAGRAM
                                  
  ┌─────────────────┐                                                 ┌─────────────────┐
  │                 │ ────► [Search Equipment Catalog]                │                 │
  │                 │                                                 │                 │
  │                 │ ────► [View Equipment Details]                  │                 │
  │     FARMER      │                                                 │ EQUIPMENT OWNER │
  │    (Lessee)     │ ────► [Submit Rental Booking] ──┐               │    (Lessor)     │
  │                 │                                 │               │                 │
  │                 │ ────► [View My Booking Status]  │               │ ────► [List New Equipment]
  └─────────────────┘                                 ▼               │ ────► [Update Equipment Info]
                                            [Rental Status Workflow] ◄│ ────► [Approve/Reject Booking]
  ┌─────────────────┐                                 ▲               │ ────► [Mark In-Use / Returned]
  │                 │ ────► [View Dashboard Metrics]  │               └─────────────────┘
  │      ADMIN      │ ────► [Override Status] ────────┘
  │                 │ ────► [System Health Check]
  └─────────────────┘
```

---

## 4. Multi-Layer Application Architecture

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                          CLIENT PRESENTATION LAYER                          │
│   • Modern Glassmorphic Dashboard (HTML5 / Vanilla CSS3 / ES6 Javascript)   │
│   • Responsive Grid Layout, Real-Time Filter & Search, Modal Dialogs        │
│   • Role-Based UI Action Controls (Farmer / Owner / Admin views)            │
└──────────────────────────────────────┬──────────────────────────────────────┘
                                       │ HTTP / REST / JSON
                                       ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                     SPRING BOOT REST CONTROLLER LAYER                       │
│   • EquipmentController.java      • RentalController.java                   │
│   • DashboardController.java      • HealthController.java                   │
└──────────────────────────────────────┬──────────────────────────────────────┘
                                       │ Business DTOs & Validation
                                       ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                         SERVICE / DOMAIN LOGIC LAYER                        │
│   • EquipmentService.java (CRUD, State Transitions, Search filters)         │
│   • RentalWorkflowService.java (Atomic Approval, Completion, Cost rules)    │
│   • DashboardMetricsService.java (Aggregates, Revenue, Utilization KPIs)    │
└──────────────────────────────────────┬──────────────────────────────────────┘
                                       │ Java Collections / In-Memory Store
                                       ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                       DATA REPOSITORY LAYER (THREAD-SAFE)                   │
│   • ConcurrentHashMap / AtomicLong Storage with Pre-seeded Farm Data        │
│   • EquipmentRepository.java       • RentalRepository.java                  │
└──────────────────────────────────────┬──────────────────────────────────────┘
                                       │
                                       ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                         DEPLOYMENT RUNTIME TARGET                           │
│   • Embedded Apache Tomcat 10.1 on Port 8085                                │
│   • Multi-Stage Docker Container (Eclipse Temurin JRE 21 Alpine)            │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## 5. Domain Data Models

### 5.1 Enumerations
```java
public enum EquipmentCategory {
    TRACTOR, COMBINE_HARVESTER, ROTAVATOR, SEED_DRILL, BOOM_SPRAYER, TILLER, BALER
}

public enum EquipmentStatus {
    AVAILABLE, RESERVED, RENTED, MAINTENANCE
}

public enum RentalStatus {
    PENDING, APPROVED, REJECTED, IN_USE, COMPLETED, CANCELLED
}

public enum UserRole {
    FARMER, OWNER, ADMIN
}
```

### 5.2 Equipment Entity Model
| Field Name | Data Type | Constraints | Description |
| :--- | :--- | :--- | :--- |
| `id` | `Long` | Primary Key, Auto-generated | Unique Equipment Identifier |
| `name` | `String` | Not Null, Size(2, 100) | Equipment brand and model |
| `category` | `EquipmentCategory`| Not Null | Classification of equipment |
| `dailyRate` | `Double` | Min 100.0, Not Null | Daily rental charge in INR (₹) |
| `hourlyRate`| `Double` | Min 20.0, Not Null | Hourly rental charge in INR (₹) |
| `location` | `String` | Not Null | Village, District or Depot |
| `ownerName`| `String` | Not Null | Name of Lessor |
| `ownerContact`| `String` | Phone regex | Contact phone number |
| `status` | `EquipmentStatus` | Default: `AVAILABLE` | Operational readiness state |
| `imageUrl` | `String` | URL format | Visual reference image |

### 5.3 Rental Booking Entity Model
| Field Name | Data Type | Constraints | Description |
| :--- | :--- | :--- | :--- |
| `id` | `Long` | Primary Key, Auto-generated | Unique Booking Identifier |
| `equipmentId`| `Long` | Foreign Key (Equipment) | Target machinery booked |
| `equipmentName`| `String` | Snapshot value | Name of equipment for auditing |
| `renterName`| `String` | Not Null | Farmer / Lessee Name |
| `renterContact`| `String` | Not Null | Farmer Phone Number |
| `farmLocation` | `String` | Not Null | Destination farm field |
| `startDate`| `LocalDate` | >= Current Date | Rental commencement date |
| `durationDays` | `Integer` | Min 1 | Total booked rental days |
| `totalCost`| `Double` | Calculated | Total rental charge (Days * Daily Rate)|
| `rentalStatus` | `RentalStatus` | Default: `PENDING` | Current state of rental request |
| `createdDate` | `LocalDateTime` | Auto-stamped | Audit creation timestamp |

---

## 6. REST API Specifications

| Method | Endpoint | Description | Request Body | Response Code & Output |
| :--- | :--- | :--- | :--- | :--- |
| `GET` | `/api/equipment` | Retrieve all equipment records | None | `200 OK` (JSON Array) |
| `GET` | `/api/equipment/{id}` | Retrieve specific equipment by ID | None | `200 OK` / `404 Not Found` |
| `POST`| `/api/equipment` | Create new equipment listing | Equipment JSON | `201 Created` (Saved Entity) |
| `PUT` | `/api/equipment/{id}` | Update equipment details & status | Equipment JSON | `200 OK` (Updated Entity) |
| `DELETE`| `/api/equipment/{id}`| Remove/retire equipment | None | `204 No Content` |
| `GET` | `/api/equipment/search`| Search by query keyword & category | Query params: `q`, `category` | `200 OK` (Filtered Array) |
| `GET` | `/api/rentals` | Retrieve all rental booking records | Optional param: `status` | `200 OK` (JSON Array) |
| `POST`| `/api/rentals` | Submit new rental booking request | Rental Request JSON | `201 Created` (Rental Entity) |
| `PUT` | `/api/rentals/{id}/status`| Update rental status (Role-based) | `{"status": "APPROVED"}` | `200 OK` (Updated Rental) |
| `GET` | `/api/dashboard/stats` | Retrieve aggregated operational KPIs | None | `200 OK` (Dashboard KPI JSON) |
| `GET` | `/api/health` | Service liveness and health diagnostics | None | `200 OK` (`{"status":"UP", ...}`) |

---

## 7. Technology Stack Selection & Rationale

| Layer | Selected Technology | Version | Architectural Justification |
| :--- | :--- | :---: | :--- |
| **Language** | Java (OpenJDK) | 21 LTS | High performance, virtual threads, robust enterprise ecosystem. |
| **Framework** | Spring Boot | 3.3.4 | Production-ready starters, embedded Tomcat, minimal boilerplate. |
| **Build Tool** | Apache Maven | 3.9.16 | Standard declarative dependency resolution, universal Jenkins integration. |
| **Web Server** | Embedded Apache Tomcat | 10.1 | Self-contained fat JAR packaging; no external servlet container needed. |
| **Frontend UI** | HTML5, Vanilla CSS3, ES6 | Native | Zero client-side compile step, high responsiveness, glassmorphic dark theme. |
| **Testing** | JUnit 5 + Selenium WebDriver | 5.10 / 4.25 | End-to-end automated UI journey verification with headless browser support. |
| **Container** | Docker (Alpine Linux JRE) | Multi-stage | Minimal image footprint (~180MB), fast cold start, immutable deployment. |
| **Orchestration**| Jenkins CI/CD + Docker | 2.540+ | Declarative Jenkinsfile pipeline automation from Git commit to live container. |
| **Config Mgmt** | Ansible & Puppet | 2.15+ / 7+ | Idempotent host configuration, zero configuration drift, one-click rollback. |

---

## 8. Local Development Setup & Verification
- **Application Port:** `8085` (configured in `application.properties` to avoid conflict with Jenkins on port `8080`).
- **Build Command:** `mvn clean package -DskipTests`
- **Execution Command:** `java -jar target/farm-equipment-rental-1.0.0.jar`
- **Browser Access URL:** `http://localhost:8085`
- **Health Diagnostic URL:** `http://localhost:8085/api/health`

---

## 9. Verification & Acceptance Sign-off
- **Architectural Specification:** Approved
- **SRS Scope:** Complete
- **Next Phase:** TASK 4 — Git & GitHub Repository Initialization.
