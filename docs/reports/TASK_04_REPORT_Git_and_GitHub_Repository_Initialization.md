# TASK 4 REPORT: Git & GitHub Repository Initialization and Branch Policy
**Project Name:** Dockerized Farm Equipment Rental System (AgriRent DevOps)  
**Document Reference:** AR-REP-004  
**Version:** 1.0.0  
**Date:** 2026-10-01  
**Author:** DevOps & Version Control Lead  
**Status:** Completed & Committed  

---

## 1. Executive Summary
This document records the initialization and configuration of the version control foundation for the **Dockerized Farm Equipment Rental System (AgriRent)**. A Git repository has been established on branch `main`, augmented with industry-standard `.gitignore` rules, GitHub issue templates, a comprehensive `README.md`, and a formal `BRANCH_POLICY.md` enforcing Conventional Commits and GitFlow principles.

---

## 2. Git Repository Initialization

### 2.1 Initialization Commands
```bash
cd c:\Users\Administrator\Documents\Devops_Proj
git init -b main
git config user.name "kshitij"
git config user.email "kshitijparab45@gmail.com"
```

### 2.2 Remote Target Configuration
- **Repository URL:** `https://github.com/kshitij4510/Devops_Proj.git`
- **Default Upstream Branch:** `main`
- **Development Integration Branch:** `develop`

---

## 3. Project Directory Architecture
The repository skeleton follows standard Maven and Spring Boot layout:
```text
Devops_Proj/
├── .github/
│   └── ISSUE_TEMPLATE/
│       ├── bug_report.md          # Structured defect reporting
│       ├── feature_request.md     # Feature enhancement proposals
│       └── devops_task.md         # CI/CD, Container & IaC tracking
├── docs/
│   └── reports/                   # Dedicated milestone task reports (Tasks 1-15)
│       ├── TASK_01_REPORT_Problem_Definition_and_Scope.md
│       ├── TASK_02_REPORT_Agile_Planning_and_DevOps_Workflow.md
│       ├── TASK_03_REPORT_Requirements_Architecture_and_Technology_Setup.md
│       └── TASK_04_REPORT_Git_and_GitHub_Repository_Initialization.md
├── src/
│   ├── main/
│   │   ├── java/com/agrirent/
│   │   │   ├── AgriRentApplication.java
│   │   │   ├── model/             # Domain entities (Equipment, RentalRequest)
│   │   │   └── repository/        # Persistence layer (EquipmentRepository, RentalRepository)
│   │   └── resources/
│   │       ├── static/            # Frontend assets (HTML, CSS, JS)
│   │       └── application.properties # App port 8085, actuator config
│   └── test/
│       └── java/com/agrirent/     # Unit and Selenium UI automation suites
├── .gitignore                     # Build, IDE, and artifact exclusion filters
├── BRANCH_POLICY.md               # Git branching rules & Conventional Commits
├── pom.xml                        # Maven dependency and build configuration
└── README.md                      # Comprehensive developer & DevOps documentation
```

---

## 4. Repository Governance & Templates

### 4.1 `.gitignore` Rules
Excludes compiled `.class` files, `target/` directories, IDE metadata (`.idea/`, `.vscode/`, `.project`), temporary log files, and Selenium failure screenshot directories (`screenshots/*.png` while preserving `.gitkeep`).

### 4.2 GitHub Issue Templates (`.github/ISSUE_TEMPLATE/`)
1. **`bug_report.md`:** Captures steps to reproduce, expected vs. actual behavior, browser environment, and screenshots.
2. **`feature_request.md`:** Enforces user story formatting (*As a [role] I want [feature] so that [benefit]*).
3. **`devops_task.md`:** Tracks CI/CD pipeline, containerization, and configuration management deliverables.

### 4.3 Branching Strategy (`BRANCH_POLICY.md`)
- **Trunk-Hybrid Model:** 
  - `main`: Production baseline, strictly protected.
  - `develop`: Nightly / CI staging branch.
  - `feature/<name>`: Isolated feature branches merged via PR after code review.
  - `bugfix/<id>`: Bug fix branches.
  - `release/<v>`: Release stabilization and tagging.
- **Commit Format:** Conventional Commits (`feat:`, `fix:`, `docs:`, `chore:`, `ci:`, `test:`).

---

## 5. Verification & Initial Commits
Initial baseline commits recorded in Git repository:
- `commit 1`: `chore: initialize repository with .gitignore, issue templates, and branch policy`
- `commit 2`: `docs: add comprehensive README and task reports`
- `commit 3`: `build: configure Maven pom.xml with Java 21, Spring Boot, and Selenium`
- `commit 4`: `feat(core): scaffold domain models, repositories, and application entry point`

---

## 6. Deliverable Acceptance Sign-off
- **Git Repo:** Initialized on `main`
- **Governance:** Templates and Policies in place
- **Skeleton Code:** Committed
- **Next Phase:** TASK 5 — Feature Development with Branching (Core Inventory Feature).
