# Git Branching Strategy & Contribution Policy
**Project:** Dockerized Farm Equipment Rental System (AgriRent DevOps)

## 1. Branching Model (GitFlow / Trunk-Hybrid)

The AgriRent project adheres to a structured, disciplined branching model to ensure stable releases and clean CI/CD integration:

- **`main`**: Production-ready branch. Only tagged releases and tested hotfixes are merged here. Direct pushes are strictly forbidden.
- **`develop`**: Integration branch for features and testing. Jenkins CI automatically runs on every commit/PR to `develop`.
- **`feature/<feature-name>`**: Created from `develop` for specific user stories (e.g., `feature/equipment-inventory`, `feature/rental-workflow`). Merged into `develop` via Pull Request with code review.
- **`bugfix/<issue-id>`**: Used for fixing defects discovered during integration or QA cycles.
- **`hotfix/<version>`**: Cut directly from `main` to address critical production issues; merged back into both `main` and `develop`.
- **`release/<version>`**: Preparation branch for final release validation and tagging (`v1.0.0-mvp`).

## 2. Branch Naming Rules
1. Must use lowercase alphanumeric characters and hyphens (`-`).
2. Must begin with a recognized prefix (`feature/`, `bugfix/`, `hotfix/`, `release/`).
3. Examples:
   - `feature/equipment-inventory`
   - `feature/rental-workflow`
   - `bugfix/issue-102-calc-error`
   - `release/v1.0.0`

## 3. Commit Message Standards (Conventional Commits)
All commit messages must follow the Conventional Commits specification:
`<type>(<scope>): <short description>`

Types:
- `feat`: A new feature for the user or system
- `fix`: A bug fix
- `docs`: Documentation only changes
- `style`: Formatting, missing semi colons, white-space
- `refactor`: A code change that neither fixes a bug nor adds a feature
- `test`: Adding missing tests or correcting existing tests
- `chore`: Changes to build process, dependency updates, tooling
- `ci`: Changes to CI configuration files and scripts (Jenkinsfile, Dockerfile)

Examples:
- `feat(inventory): add equipment registration REST API and repository`
- `test(selenium): add critical journey tests for rental booking flow`
- `ci(jenkins): add declarative pipeline with Docker deployment stage`

## 4. Pull Request (PR) Requirements
Every PR must satisfy:
1. Descriptive title referencing relevant Issue ID (e.g., `feat(inventory): add equipment listing #01`).
2. Clean mergeability with `develop` branch without unresolved conflicts.
3. At least one peer review approval.
4. Passing automated CI build and test execution.
