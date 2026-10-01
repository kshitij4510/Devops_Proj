# Dockerized Farm Equipment Rental System (AgriRent DevOps)

[![Java 21](https://img.shields.io/badge/Java-21%20LTS-orange.svg)](https://openjdk.org/)
[![Maven 3.9](https://img.shields.io/badge/Build-Maven%203.9-blue.svg)](https://maven.apache.org/)
[![Spring Boot](https://img.shields.io/badge/Framework-Spring%20Boot%203.3-green.svg)](https://spring.io/projects/spring-boot)
[![Dockerized](https://img.shields.io/badge/Container-Docker%20Ready-2496ED.svg)](https://www.docker.com/)
[![CI/CD Jenkins](https://img.shields.io/badge/CI%2FCD-Jenkins%20Automated-D33833.svg)](https://www.jenkins.io/)
[![Selenium WebDriver](https://img.shields.io/badge/Testing-Selenium%20Automated-43B02A.svg)](https://www.selenium.dev/)
[![IaC Ansible](https://img.shields.io/badge/IaC-Ansible%20%26%20Puppet-EE0000.svg)](https://www.ansible.com/)

An enterprise-grade, DevOps-automated, containerized agricultural machinery rental system designed to connect smallholder farmers with equipment owners, optimize machinery utilization, and automate the entire rental reservation lifecycle.

---

## 🚜 System Overview
The **AgriRent** platform democratizes access to expensive agricultural equipment (tractors, combine harvesters, rotavators, seed drills, boom sprayers) through:
1. **Catalog & Inventory Management:** Real-time listing of machinery with daily/hourly rates, location, and owner contacts.
2. **Instant Search & Multi-criteria Filtering:** Instant query by keyword, category, and availability.
3. **Role-based Rental Booking Workflow:** 
   - **Farmer (Lessee):** Discovers equipment and requests booking.
   - **Equipment Owner (Lessor):** Reviews, approves/rejects, marks in-use, and confirms return.
   - **Admin:** Monitors global transactions, fleet utilization, and system health.
4. **Interactive Dashboard:** Live KPIs for total fleet, active rentals, total revenue generated, and fleet utilization rate.

---

## 🛠️ Technology Stack
- **Backend:** Java 21 LTS, Spring Boot 3.3.4 (Embedded Apache Tomcat 10.1)
- **Frontend:** Glassmorphic UI (HTML5, Vanilla CSS3, ES6 JavaScript)
- **Database:** Thread-safe In-Memory Repository with Seed Data
- **Build & Dependency Management:** Apache Maven 3.9.16
- **Automated Testing:** JUnit 5, Mockito, Selenium WebDriver 4.25
- **CI/CD Orchestration:** Jenkins (Declarative `Jenkinsfile` Pipeline)
- **Containerization:** Multi-stage Dockerfile (Eclipse Temurin JRE 21 Alpine)
- **Configuration Management:** Ansible Playbooks (`playbook.yml`) & Puppet Manifests (`site.pp`)

---

## 🚀 Quick Start Guide (Local Setup)

### Prerequisites
- JDK 21+
- Apache Maven 3.9+
- Docker Engine / Podman (Optional for container runs)
- Chrome / Edge / Brave (for Selenium tests)

### 1. Build Application
```bash
mvn clean package -DskipTests
```

### 2. Run Locally (Port 8085)
```bash
java -jar target/farm-equipment-rental-1.0.0.jar
```
Open your browser to: [http://localhost:8085](http://localhost:8085)

### 3. Run Automated Tests
```bash
# Unit & integration tests
mvn test

# Selenium automated UI regression suite
mvn test -Dtest=FarmEquipmentSeleniumTest
```

---

## 📂 Project Structure
```text
Devops_Proj/
├── .github/
│   └── ISSUE_TEMPLATE/
│       ├── bug_report.md
│       ├── feature_request.md
│       └── devops_task.md
├── docs/
│   └── reports/            # Dedicated reports for Tasks 1-15
├── src/
│   ├── main/
│   │   ├── java/com/agrirent/
│   │   │   ├── AgriRentApplication.java
│   │   │   ├── controller/
│   │   │   ├── model/
│   │   │   ├── repository/
│   │   │   └── service/
│   │   └── resources/
│   │       ├── static/     # UI HTML/CSS/JS assets
│   │       └── application.properties
│   └── test/
│       └── java/com/agrirent/
│           ├── FarmEquipmentServiceTest.java
│           └── FarmEquipmentSeleniumTest.java
├── ansible/                # Ansible Playbooks & Inventories (Task 13-14)
├── puppet/                 # Puppet Manifests (Task 13-14)
├── Dockerfile              # Multi-stage Docker build (Task 11-12)
├── Jenkinsfile             # Declarative CI/CD pipeline (Task 8, 10, 12)
├── pom.xml                 # Maven build configuration
├── BRANCH_POLICY.md        # Branching & Git conventions
└── README.md
```

---

## 📡 REST API Reference

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `GET` | `/api/equipment` | Retrieve all equipment listings |
| `POST` | `/api/equipment` | Register new equipment |
| `GET` | `/api/equipment/search?q={query}` | Search equipment by keyword/category |
| `GET` | `/api/rentals` | Retrieve all rental records |
| `POST` | `/api/rentals` | Submit rental booking request |
| `PUT` | `/api/rentals/{id}/status` | Update rental workflow status |
| `GET` | `/api/dashboard/stats` | Retrieve aggregated dashboard KPIs |
| `GET` | `/api/health` | System health check endpoint |

---

## 📜 License
Educational DevOps Project — All Rights Reserved.
