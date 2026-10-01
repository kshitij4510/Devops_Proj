# TASK 1 REPORT: Problem Definition, Stakeholders, Objectives, and MVP Scope
**Project Name:** Dockerized Farm Equipment Rental System (AgriRent DevOps)  
**Document Reference:** AR-REP-001  
**Version:** 1.0.0  
**Date:** 2026-10-01  
**Author:** DevOps & Engineering Team  
**Status:** Approved & Frozen Baseline  

---

## 1. Executive Summary
Agriculture remains the backbone of rural economies; however, small and marginal farmers (holding < 2 hectares) face severe capital barriers when attempting to access modern farm mechanization (tractors, harvesters, seed drills, boom sprayers, rotavators). Purchasing such heavy machinery involves steep capital expenditures, high depreciation, and seasonal underutilization. Conversely, medium-to-large farm owners and equipment rental agencies face prolonged equipment idle periods between sowing and harvest cycles, coupled with manual, opaque booking records and payment tracking issues.

The **Dockerized Farm Equipment Rental System (AgriRent)** bridges this critical gap. It is a cloud-native, containerized, role-based platform designed to democratize access to agricultural equipment via an automated, transparent, and auditable rental lifecycle. This document formalizes the real-world problem statement, stakeholder ecosystem, technical and operational constraints, measurable key performance indicators (KPIs), and freezes the **15-Task MVP Scope**.

---

## 2. Real-Time Domain Need & Problem Statement

### 2.1 The Real-World Domain Problem
Modern precision farming tools improve crop yields by 15–30% and reduce labor hours by up to 50%. Despite these advantages:
- **Capital Cost Barrier:** More than 80% of smallholder farmers cannot afford upfront investments in modern tractors (₹6–15 Lakhs / $8,000–$20,000) or combine harvesters.
- **Underutilization of Assets:** Machinery owners utilize their equipment for only 30–45 days per year, leaving expensive capital idle for over 300 days.
- **Manual, Fragmented Rental Market:** Existing rental arrangements rely on middlemen, word-of-mouth, unverified oral agreements, arbitrary pricing, and zero schedule guarantees.
- **Lack of Tracking & Auditing:** Owners lack visibility into equipment availability, maintenance logs, rental statuses, and accumulated rental revenues.

### 2.2 Formal Problem Statement
> *"Small and marginal farmers lack an affordable, on-demand, transparent channel to discover and rent heavy agricultural equipment, while equipment owners lack a structured, reliable system to list assets, manage booking requests, track rental workflows, and monitor earnings. The AgriRent platform delivers an enterprise-grade, DevOps-automated, containerized solution to streamline equipment discovery, reservation lifecycle management, status auditing, and operational metrics with continuous testing and automated deployment."*

---

## 3. Target User Personas & Pain Points Matrix

| Persona ID | User Persona | Role & Demographics | Existing Pain Points | Value Proposition of AgriRent |
| :--- | :--- | :--- | :--- | :--- |
| **P-01** | **Ramesh Patel (Smallholder Farmer)** | Lessee; 3.5 acres; seasonal crops; mobile-first user. | • Unpredictable equipment availability during critical monsoon windows.<br>• Exorbitant rates charged by local brokers.<br>• No confirmation or digital proof of booking. | • Real-time search of nearby equipment.<br>• Transparent hourly/daily rates.<br>• Instant booking submission & status tracking. |
| **P-02** | **Sardar Balwinder Singh (Equipment Owner)** | Lessor; owns 2 John Deere tractors, 1 combine harvester, 1 rotavator. | • High equipment idle time post-harvest.<br>• Uncollected rental dues and disputed operating hours.<br>• Double-booking conflicts during peak season. | • Centralized inventory management.<br>• One-click Approval / Rejection workflow.<br>• Automated revenue and utilization dashboard. |
| **P-03** | **Kavita Sharma (FPO / Cooperative Coordinator)** | Operations Manager; coordinates machinery pooling for 250 farmers. | • Spreadsheet chaos managing shared community machinery.<br>• Lack of audit trails for equipment condition and maintenance status. | • Role-based access control.<br>• Consolidated dashboard of total assets and active rentals. |
| **P-04** | **DevOps & Platform Admin** | Site Reliability Engineer / System Administrator. | • Fragile deployment environments and manual release errors.<br>• Lack of automated test validation and zero-downtime container rollbacks. | • End-to-end CI/CD via Jenkins.<br>• Dockerized portable runtime.<br>• Ansible/Puppet automated configuration & rollback. |

---

## 4. Stakeholder Analysis & Influence Matrix

```
                      HIGH POWER / HIGH INFLUENCE
                                ▲
                                │   [Equipment Owners / Lessors]
   [DevOps / System Admin]      │   [Farmer Cooperatives (FPOs)]
                                │
────────────────────────────────┼──────────────────────────────► HIGH INTEREST
                                │
   [Local Regulatory Bodies]    │   [Smallholder Farmers / Lessees]
                                │   [Equipment Field Operators]
                                │
                      LOW POWER / LOW INFLUENCE
```

1. **Farmers / Renters (High Interest, Medium Power):** Demand high system availability, intuitive interface, zero hidden charges, and clear rental status transitions.
2. **Machinery Owners (High Interest, High Power):** Demand asset protection, reliable booking approvals, rate control, and transparent billing logs.
3. **FPO / Agricultural Extension Officers (High Interest, High Power):** Demand aggregate metrics, equipment utilization reports, and operational compliance.
4. **DevOps & QA Engineers (High Interest, High Power):** Demand repeatable CI/CD pipelines, container immutability, automated regression test gates, and infrastructure as code.

---

## 5. System Objectives & Measurable Success Criteria

### 5.1 Business Objectives
- **BO-1:** Decrease farm equipment idle time by **at least 35%** within the pilot operational cluster.
- **BO-2:** Reduce time spent booking machinery from **1–2 days (manual)** to **under 2 minutes (automated digital flow)**.
- **BO-3:** Eliminate double-booking conflicts through atomic status verification.

### 5.2 Technical & DevOps Objectives
- **TO-1 (Portability):** Achieve 100% platform portability by packaging the application into lightweight, multi-stage Docker containers.
- **TO-2 (Continuous Integration):** Automate code compile, packaging, unit testing, and artifact archiving on Jenkins upon every Git commit.
- **TO-3 (Automated Quality Gate):** Implement Selenium WebDriver automated end-to-end regression suites that block deployment if critical paths fail.
- **TO-4 (Infrastructure as Code):** Guarantee environment idempotency and single-command rollback through Ansible playbooks and Puppet manifests.

### 5.3 Measurable Success Criteria (KPIs)
| KPI Metric | Baseline (Manual) | Target (AgriRent DevOps MVP) | Measurement Method |
| :--- | :--- | :--- | :--- |
| **Booking Lead Time** | 24 - 48 Hours | < 2 Minutes | System event timestamp (Request to Confirmation) |
| **Build & CI Duration** | Manual build (> 25 mins) | < 3 Minutes | Jenkins CI Pipeline Execution Timer |
| **Test Automation Coverage**| 0% (Manual smoke testing) | 100% of Core User Journeys | Selenium WebDriver execution in Jenkins |
| **Deployment Automation** | Manual FTP / copy (~45 mins) | < 60 Seconds (Zero touch) | Docker container restart & health check validation |
| **Configuration Idempotency**| Manual host tuning | 100% reproducible (0 drift) | Ansible playbook rerun (`changed=0`) |
| **Mean Time to Recovery (MTTR)**| > 2 Hours | < 3 Minutes | Automated Rollback script trigger to previous tag |

---

## 6. Constraints & Assumptions

### 6.1 Technical & Operational Constraints
- **Hardware Footprint:** MVP must execute reliably on standard development and edge server environments (Windows 10/11 or Ubuntu Linux, 4GB–16GB RAM).
- **Network Resilience:** The user interface must be responsive and lightweight (< 2MB initial load) to function across 3G/4G rural mobile connections.
- **Browser Compatibility:** Automated validation must pass against standard Chromium-based browsers (Google Chrome, Microsoft Edge, Brave) using headless and standard modes.
- **Security & RBAC:** Clear demarcation of Lessee (Farmer) vs. Lessor (Owner) vs. Admin privileges to prevent unauthorized booking status alterations.

### 6.2 Assumptions
- Equipment rates are calculated on an hourly and daily rental schedule.
- The platform operates with in-memory persistence / lightweight embedded database during MVP testing to maintain self-contained execution without external DB dependencies.
- Git repository maintains trunk-based branching with `main`, `develop`, and `feature/*` conventions.

---

## 7. Approved 15-Task MVP Scope Breakdown

To ensure end-to-end execution, the scope is frozen into the following structured 15 tasks:

```
[TASK 1: Problem Definition & Scope] ────────► [TASK 2: Agile & DevOps Planning]
                 │                                            │
                 ▼                                            ▼
[TASK 3: Architecture & Tech Setup]   ────────► [TASK 4: Git / GitHub Initialization]
                 │                                            │
                 ▼                                            ▼
[TASK 5: Feature 1 (Core Inventory)]   ────────► [TASK 6: MVP Completion & Git Collab]
                 │                                            │
                 ▼                                            ▼
[TASK 7: Jenkins CI Job & Artifact]    ────────► [TASK 8: Pipeline as Code (Jenkinsfile)]
                 │                                            │
                 ▼                                            ▼
[TASK 9: Selenium Test Suite]          ────────► [TASK 10: Continuous Testing Gate]
                 │                                            │
                 ▼                                            ▼
[TASK 11: Dockerfile & Container Life] ────────► [TASK 12: Jenkins-Docker CD Pipeline]
                 │                                            │
                 ▼                                            ▼
[TASK 13: Config Management (Ansible)] ────────► [TASK 14: Provisioning & Rollback]
                                                              │
                                                              ▼
                                               [TASK 15: Final E2E Release & Viva]
```

### Scope Summary Matrix
1. **Task 1:** Domain Problem Definition, Stakeholders, Constraints, and Approved MVP Scope.
2. **Task 2:** Agile Planning, User Stories, 15-Task Scrum Backlog, Definition of Done, and DevOps Workflow Diagram.
3. **Task 3:** Requirements Specification (SRS), Architecture Diagram, Data Models, REST API specs, and Local Dev Setup.
4. **Task 4:** Git Repository Initialization, Branching Policies, `.gitignore`, README, and Issue Templates.
5. **Task 5:** Feature Branch 1 Development: Equipment Inventory Service, Unit Tests, Git Log, Pull Request & Merge.
6. **Task 6:** Feature Branch 2 Development: Booking & Rental Workflow, Conflict Simulation/Resolution, Tagging `v1.0.0-mvp`.
7. **Task 7:** Jenkins Continuous Integration: Maven Build Job, Polling/Webhook Trigger, Test Execution, and Artifact Archival.
8. **Task 8:** Pipeline as Code: Multi-stage Declarative `Jenkinsfile` with Build, Test, Package, and Deploy with Parameterized Profiles.
9. **Task 9:** Selenium WebDriver Test Suite: 5 Critical User Journeys, Assertions, Test Data Provider, and Failure Screenshot mechanism.
10. **Task 10:** Continuous Testing in Jenkins: JUnit Report Integration, Test Quality Gate, Defect Injection, Pipeline Failure & Fix Verification.
11. **Task 11:** Docker Image & Lifecycle: Multi-stage `Dockerfile`, Image build, port mapping, logs inspection, stop/restart/remove lifecycle.
12. **Task 12:** Jenkins-to-Docker CD: Automated container build, version tagging, local registry publication, and live container reload.
13. **Task 13:** Configuration Management: Ansible Playbook (`playbook.yml`) & Inventory + Puppet Manifest (`site.pp`) for target node provisioning.
14. **Task 14:** Automated Provisioning & Reliability: Clean environment execution, Idempotency proof (`changed=0`), Health Check verification, and Rollback demonstration.
15. **Task 15:** Final Release Package, End-to-End Execution Trace, Architecture Dossier, Maintenance Runbook, and Viva Presentation Guide.

---

## 8. Verification & Sign-Off
- **Scope Status:** FROZEN & APPROVED
- **Project Lead Sign-off:** Kshitij Parab (DevOps Lead)
- **Next Phase:** TASK 2 — Agile Planning and DevOps Workflow Execution.
