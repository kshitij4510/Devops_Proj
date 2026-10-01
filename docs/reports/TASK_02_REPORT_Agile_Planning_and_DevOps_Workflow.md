# TASK 2 REPORT: Agile Planning, User Stories, Backlog, DoD, and DevOps Workflow
**Project Name:** Dockerized Farm Equipment Rental System (AgriRent DevOps)  
**Document Reference:** AR-REP-002  
**Version:** 1.0.0  
**Date:** 2026-10-01  
**Author:** DevOps & Agile Scrum Master  
**Status:** Approved & Released  

---

## 1. Executive Summary
This document establishes the Agile management and DevOps delivery framework for the **Farm Equipment Rental System (AgriRent)**. By integrating Scrum sprints for feature delivery with Kanban workflow management for continuous integration and deployment, the project guarantees rapid feedback loops, automated test coverage, and repeatable releases.

---

## 2. User Stories & Acceptance Criteria (Gherkin Syntax)

### User Story US-01: Equipment Catalog Listing (Lessor Persona)
- **Story:** *As an Equipment Owner (Lessor), I want to register and list my agricultural machinery with hourly/daily pricing and operational status, so that local farmers can discover and book it.*
- **Story Points:** 3
- **Acceptance Criteria:**
  ```gherkin
  Scenario: Successfully registering new farm equipment
    Given the equipment owner is on the "Add Equipment" modal/page
    When the owner submits "John Deere 5050D Tractor", category "TRACTOR", daily rate 2500, hourly rate 350, location "Nashik Sector 4"
    Then the equipment should be saved with status "AVAILABLE"
    And the system returns HTTP 201 Created and displays the equipment in the inventory table.

  Scenario: Validation failure on invalid rental rate
    Given the equipment owner enters a daily rate of -50 or empty name
    When the form is submitted
    Then the system rejects the submission with validation error messages
    And no record is created in the repository.
  ```

### User Story US-02: Equipment Discovery & Multi-Criteria Search (Lessee Persona)
- **Story:** *As a Smallholder Farmer (Lessee), I want to search and filter equipment by name, category, and availability status, so that I can immediately identify machinery available for my field.*
- **Story Points:** 2
- **Acceptance Criteria:**
  ```gherkin
  Scenario: Searching equipment by keyword
    Given 5 pieces of equipment exist across categories (Tractor, Harvester, Rotavator)
    When the farmer searches for "Harvester" in the search box
    Then only equipment matching "Harvester" should be rendered in the results grid.

  Scenario: Filtering by Availability
    Given equipment records with statuses "AVAILABLE" and "RENTED"
    When the farmer filters by status "AVAILABLE"
    Then rented items should be excluded from the view.
  ```

### User Story US-03: Rental Request & Reservation (Lessee Persona)
- **Story:** *As a Farmer, I want to submit a rental booking request specifying start date, duration (days/hours), and farm location, so that the owner can review my booking.*
- **Story Points:** 5
- **Acceptance Criteria:**
  ```gherkin
  Scenario: Booking an available equipment item
    Given the equipment "Mahindra 575 DI" is in "AVAILABLE" status
    When the farmer submits a rental request for 3 days starting tomorrow
    Then a rental record is generated in "PENDING" status
    And total estimated cost is automatically calculated (Daily Rate * Days)
    And the owner receives an approval prompt.
  ```

### User Story US-04: Rental Status Lifecycle Management (Lessor/Admin Persona)
- **Story:** *As an Equipment Owner, I want to approve, activate, complete, or reject pending rental requests, so that equipment usage is accurately tracked and billed.*
- **Story Points:** 5
- **Acceptance Criteria:**
  ```gherkin
  Scenario: Approving a pending rental
    Given a rental request is in "PENDING" status
    When the owner clicks "Approve Booking"
    Then the rental status transitions to "APPROVED"
    And the equipment status transitions to "RESERVED".

  Scenario: Marking equipment as In-Use and subsequent Return
    Given the rental is "APPROVED"
    When the owner marks it "IN_USE" and later "COMPLETED"
    Then the equipment status reverts back to "AVAILABLE"
    And the completion timestamp is logged for billing audit.
  ```

### User Story US-05: Real-Time Operational Dashboard (All Stakeholders)
- **Story:** *As an Operations Manager or Farmer, I want to view high-level metrics (total inventory, active rentals, total revenue, utilization rate), so that I can monitor platform productivity.*
- **Story Points:** 3
- **Acceptance Criteria:**
  ```gherkin
  Scenario: Real-time dashboard KPI calculation
    Given 10 equipment items with 4 currently rented and total generated revenue of ₹45,000
    When any user navigates to the summary dashboard
    Then the KPI tiles show: Total Equipment: 10, Active Rentals: 4, Revenue: ₹45,000, Utilization: 40%
    And a visual distribution chart is displayed.
  ```

---

## 3. 15-Task Product Backlog & Sprint Planning

We adopt a 3-Sprint structure mapping directly to the 15 project milestones:

| Task # | Task Description | Sprint | Estimated Points | Priority | Primary Owner |
| :--- | :--- | :--- | :---: | :---: | :--- |
| **TASK 1** | Problem Definition & Scope Freezing | Sprint 1 | 2 | P0 | Product Owner / DevOps Lead |
| **TASK 2** | Agile Planning & DevOps Workflow Design | Sprint 1 | 3 | P0 | Scrum Master / DevOps |
| **TASK 3** | Architecture, Data Models & Local Tech Setup | Sprint 1 | 5 | P0 | Full Stack Engineer |
| **TASK 4** | Git/GitHub Repository & Branching Policy | Sprint 1 | 2 | P1 | DevOps Engineer |
| **TASK 5** | Feature 1 Development (Inventory Management) | Sprint 1 | 5 | P1 | Backend Developer |
| **TASK 6** | MVP Completion (Rental Workflow & Conflict Resolution) | Sprint 1 | 8 | P0 | Full Stack Team |
| **TASK 7** | Jenkins Installation & CI Maven Build Job | Sprint 2 | 5 | P0 | DevOps / CI Engineer |
| **TASK 8** | Pipeline as Code (Declarative Jenkinsfile) | Sprint 2 | 5 | P0 | DevOps Engineer |
| **TASK 9** | Selenium WebDriver Test Suite Design | Sprint 2 | 5 | P1 | QA Automation Engineer |
| **TASK 10** | Continuous Testing Gate & Defect Injection in CI | Sprint 2 | 5 | P0 | QA & DevOps Lead |
| **TASK 11** | Dockerfile Creation & Container Lifecycle Ops | Sprint 3 | 5 | P0 | Container Specialist |
| **TASK 12** | Jenkins-to-Docker Continuous Deployment Pipeline | Sprint 3 | 8 | P0 | DevOps Engineer |
| **TASK 13** | Configuration Management (Ansible & Puppet Specs) | Sprint 3 | 5 | P1 | Infra / SysAdmin |
| **TASK 14** | Automated Provisioning, Idempotency & Rollback | Sprint 3 | 8 | P0 | Reliability Engineer |
| **TASK 15** | Final Release Documentation, Demo & Viva Pack | Sprint 3 | 3 | P0 | Entire Team |

**Total Story Points:** 71 Points across 3 Sprints.

---

## 4. 15-Task Kanban Board Visual Workflow

```
┌──────────────────────────────────────────────────────────────────────────────────────────────────┐
│                                   AGRIRENT KANBAN WORKFLOW                                       │
├──────────────┬──────────────┬──────────────┬──────────────┬──────────────┬───────────────────────┤
│   BACKLOG    │  TO DO       │ IN PROGRESS  │ CODE REVIEW  │ CI / TESTING │      DONE             │
│ (Refined)    │ (Sprint Ready)│ (WIP: 3)    │ (WIP: 2)     │ (Automated)  │ (Production Baseline) │
├──────────────┼──────────────┼──────────────┼──────────────┼──────────────┼───────────────────────┤
│ Tasks 11-15  │ Tasks 7-10   │ Task 3       │ Feature PRs  │ Selenium     │ Task 1 (Approved)     │
│              │              │ Task 4       │ Peer Review  │ JUnit tests  │ Task 2 (Approved)     │
│              │              │ Tasks 5-6    │              │ Jenkins CI   │                       │
└──────────────┴──────────────┴──────────────┴──────────────┴──────────────┴───────────────────────┘
```

---

## 5. Definition of Done (DoD) Checklist

A user story or technical task is formally deemed **DONE** only when all the following criteria are satisfied:
1. **Design & Code Standards:** Code is structured following MVC / Hexagonal design with zero hard-coded credentials and adheres to clean coding guidelines.
2. **Automated Unit Testing:** Unit and integration tests written and passing with **>= 80% branch coverage**.
3. **Automated UI Testing:** Selenium WebDriver tests for relevant user journeys pass without failures in headless execution.
4. **Static Analysis & Linting:** Code builds with zero compilation errors and zero high-severity lint warnings.
5. **Git Versioning:** Feature branch committed using Conventional Commits (`feat:`, `fix:`, `ci:`, `docs:`) and merged via Pull Request into `develop` / `main`.
6. **Continuous Integration (CI):** Jenkins build succeeds, runs automated tests, and archives artifacts (JAR/WAR).
7. **Containerization & Deployment:** Docker image builds cleanly with non-root runtime and container passes `/actuator/health` or `/api/health` checks.
8. **Documentation:** Technical documentation and task report updated in `/docs/reports/` with verification proof.

---

## 6. End-to-End DevOps Lifecycle Workflow

```
                               THE AGRIRENT DEVOPS LIFECYCLE
                               
  ┌─────────────────┐       ┌─────────────────┐       ┌─────────────────┐
  │   1. PLAN       │       │    2. CODE      │       │    3. BUILD     │
  │ • Jira / Scrum  │ ────► │ • Git Branching │ ────► │ • Maven 3.9     │
  │ • User Stories  │       │ • Feature Dev   │       │ • JDK 21        │
  │ • 15 Tasks DoD  │       │ • PR Reviews    │       │ • JAR Packaging │
  └─────────────────┘       └─────────────────┘       └────────┬────────┘
                                                               │
                                                               ▼
  ┌─────────────────┐       ┌─────────────────┐       ┌─────────────────┐
  │   6. DEPLOY     │       │   5. RELEASE    │       │    4. TEST      │
  │ • Docker Engine │ ◄──── │ • Docker Hub /  │ ◄──── │ • JUnit 5 Tests │
  │ • Port 8085:8085│       │   Local Registry│       │ • Selenium Tests│
  │ • Health Check  │       │ • Version Tag   │       │ • Quality Gate  │
  └────────┬────────┘       └─────────────────┘       └─────────────────┘
           │
           ▼
  ┌─────────────────┐       ┌─────────────────┐
  │   7. OPERATE    │       │   8. MONITOR    │
  │ • Ansible / IaC │ ────► │ • App Logs      │
  │ • Auto Rollback │       │ • Dashboard KPI │
  │ • Zero Drift    │       │ • Telemetry     │
  └─────────────────┘       └─────────────────┘
```

### Detailed Lifecycle Phases:
1. **PLAN:** Agile backlog grooming, user story prioritization, and sprint planning.
2. **CODE:** Developer writes feature code in `feature/*` branch, commits locally, pulls latest, and pushes to remote.
3. **BUILD:** Jenkins webhook triggers build using Maven, resolves dependencies, and compiles byte-code.
4. **TEST:** Jenkins executes unit tests and invokes Selenium WebDriver automated browser regression suite. If any test fails, deployment halts.
5. **RELEASE:** Multi-stage Docker image is built, tagged with semantic version (`v1.0.0`), and registered.
6. **DEPLOY:** Automated zero-downtime deployment replacing old container with the new container mapped to production port.
7. **OPERATE:** Ansible playbooks ensure idempotent host configuration, service survival, and rollback to previous version on anomaly.
8. **MONITOR:** Prometheus / Application health endpoints track memory, request latencies, and transaction metrics.

---

## 7. Deliverable Summary & Acceptance Sign-off
- **User Stories Defined:** 5 Core Application Stories + 3 DevOps Stories
- **Backlog Tasks:** 15 Tasks Mapped across 3 Sprints
- **DoD Approved:** Strict 8-point checklist
- **DevOps Workflow Diagram:** Approved and baselined
- **Next Phase:** TASK 3 — Requirements, Architecture, and Technology Setup.
