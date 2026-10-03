# TASK 10 REPORT: Continuous Testing in Jenkins, Quality Gate Enforcement, and Defect Resolution
**Project Name:** Dockerized Farm Equipment Rental System (AgriRent DevOps)  
**Document Reference:** AR-REP-010  
**Version:** 1.0.0  
**Date:** 2026-10-04  
**Author:** CI/CD Engineer & Quality Gate Specialist  
**Status:** Completed & Validated (`Failed Build #4 Demonstrated -> Fixed & Verified in Build #5 SUCCESS`)  

---

## 1. Executive Summary
This report documents the completion of **Task 10: Continuous Testing in Jenkins** for the **Dockerized Farm Equipment Rental System (AgriRent)**. The automated Selenium WebDriver test suite developed in Task 9 was integrated into the Jenkins Declarative Pipeline (`Jenkinsfile`). Test execution was established as an unbreakable Quality Gate prior to packaging and server deployment. To validate pipeline resilience, a deliberate functional defect was introduced into the core business calculation logic (`RentalWorkflowService.java`), which caused the automated test stage to fail in **Build #4**, aborting deployment and preventing flawed software from reaching runtime. The defect was corrected with a versioned Git commit (`3e4024a`), followed by an automated rerun in **Build #5** that achieved 100% test pass rate (16/16 tests passing) and proceeded to a successful deployment.

---

## 2. Pipeline Quality Gate Architecture

### 2.1 Jenkinsfile Configuration
Stage 3 was updated to execute the entire test portfolio (domain unit tests, integration workflows, and headless Selenium browser tests) while enforcing publishing of JUnit XML and screenshot archives:

```groovy
stage('Automated Tests & Quality Gate') {
    steps {
        echo '=== Stage 3: Executing Unit, Integration & Selenium Tests ==='
        bat 'call "%M2_HOME%\\bin\\mvn.cmd" test'
    }
    post {
        always {
            junit testResults: 'target/surefire-reports/*.xml', allowEmptyResults: false
            archiveArtifacts artifacts: 'target/screenshots/*.png', allowEmptyArchive: true
        }
    }
}
```

### 2.2 Quality Gate Enforcement Mechanics
- **Failure Exit Code:** If any test fails, `mvn test` exits with code `1`.
- **Pipeline Abort:** Jenkins pipeline catches the non-zero exit code and marks Stage 3 as `FAILED`.
- **Deployment Circuit Breaker:** Downstream stages (`Package Artifact` and `Deploy to Embedded Tomcat Server`) are strictly skipped.
- **Diagnostics Preserved:** The `post { always { ... } }` block ensures test reports and failure screenshots are archived even when the stage fails.

---

## 3. Deliberate Defect Injection & Failed Pipeline Run (Build #4)

### 3.1 Defect Injection Details
- **Affected File:** [`src/main/java/com/agrirent/service/RentalWorkflowService.java`](file:///c:/Users/Administrator/Documents/Devops_Proj/src/main/java/com/agrirent/service/RentalWorkflowService.java)
- **Deliberate Bug:**
  ```java
  // DELIBERATE DEFECT: Task 10 Quality Gate verification - faulty cost calculation
  double calculatedCost = 0.0;
  request.setTotalCost(calculatedCost);
  ```
- **Commit Reference:** `c87516d` (`test(gate): integrate Selenium suite into Jenkinsfile and introduce deliberate cost defect`)

### 3.2 Build #4 Quality Gate Failure Evidence
- **Jenkins Job:** `Farm-Equipment-Rental-Pipeline`
- **Build Number:** `#4`
- **Outcome:** `FAILURE`
- **Failed Test:** `com.agrirent.RentalWorkflowServiceTest.testCreateRentalRequest`
- **Error Details:** `expected: <10000.0> but was: <0.0>`
- **Deployment Status:** **BLOCKED** (Stages `Package Artifact` and `Deploy to Embedded Tomcat` were skipped)

```text
[Pipeline] stage
[Pipeline] { (Automated Tests & Quality Gate)
[INFO] Running com.agrirent.RentalWorkflowServiceTest
[ERROR] Tests run: 6, Failures: 1, Errors: 0, Skipped: 0 <<< FAILURE!
[ERROR] com.agrirent.RentalWorkflowServiceTest.testCreateRentalRequest -- expected: <10000.0> but was: <0.0>
...
Post stage
[Pipeline] junit
Recording test results
[Pipeline] archiveArtifacts
Archiving artifacts
[Pipeline] }
[Pipeline] // stage
[Pipeline] stage
[Pipeline] { (Package Artifact)
Stage "Package Artifact" skipped due to earlier failure(s)
[Pipeline] stage
[Pipeline] { (Deploy to Embedded Tomcat Server)
Stage "Deploy to Embedded Tomcat Server" skipped due to earlier failure(s)
[Pipeline] echo
PIPELINE FAILED: Check stage logs for details.
Finished: FAILURE
```
Console log archived at [`jenkins/pipeline-build-4-failure.log`](file:///c:/Users/Administrator/Documents/Devops_Proj/jenkins/pipeline-build-4-failure.log).

---

## 4. Defect Correction & Successful Rerun (Build #5)

### 4.1 Defect Resolution Commit
- **Commit Hash:** `3e4024a`
- **Commit Message:** `fix(rental): correct deliberate calculation defect and restore quality gate pass`
- **Diff Applied:**
  ```diff
  -        // DELIBERATE DEFECT: Task 10 Quality Gate verification - faulty cost calculation
  -        double calculatedCost = 0.0;
  +        double calculatedCost = equipment.getDailyRate() * (request.getDurationDays() != null ? request.getDurationDays() : 1);
  ```

### 4.2 Build #5 Execution Summary
- **Jenkins Job:** `Farm-Equipment-Rental-Pipeline`
- **Build Number:** `#5`
- **Result:** `SUCCESS` (All 5 stages completed)
- **Total Tests Executed:** **16**
- **Test Results:** **16 Passed, 0 Failed, 0 Skipped** (Pass rate: 100%)
- **Test Execution Duration:** 17.295 seconds
- **Deployed Process:** PID `27248` on `http://localhost:8085`

```text
[Pipeline] stage
[Pipeline] { (Automated Tests & Quality Gate)
[INFO] Running com.agrirent.EquipmentServiceTest
[INFO] Tests run: 5, Failures: 0, Errors: 0, Skipped: 0
[INFO] Running com.agrirent.RentalWorkflowServiceTest
[INFO] Tests run: 6, Failures: 0, Errors: 0, Skipped: 0
[INFO] Running com.agrirent.selenium.FarmEquipmentRentalSeleniumTest
[INFO] Tests run: 5, Failures: 0, Errors: 0, Skipped: 0
[INFO] Results: Tests run: 16, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
...
[Pipeline] stage
[Pipeline] { (Package Artifact)
[INFO] Building jar: ...\target\farm-equipment-rental-1.0.0.jar
...
[Pipeline] stage
[Pipeline] { (Deploy to Embedded Tomcat Server)
Launching Farm Equipment Rental System executable JAR on port 8085 with profile staging...
Launched detached process PID: 27248 ReturnValue: 0
Health Check SUCCESS: farm-equipment-rental is UP and running!
Finished: SUCCESS
```
Console log archived at [`jenkins/pipeline-build-5-success.log`](file:///c:/Users/Administrator/Documents/Devops_Proj/jenkins/pipeline-build-5-success.log).

---

## 5. Deliverable Checklist
- [x] **Jenkins test report:** 16 tests recorded in JUnit reporter (`0 failures, 16 passes`).
- [x] **Failed pipeline evidence:** Build #4 failure logs captured at [`jenkins/pipeline-build-4-failure.log`](file:///c:/Users/Administrator/Documents/Devops_Proj/jenkins/pipeline-build-4-failure.log).
- [x] **Defect correction commit:** Git commit `3e4024a` recorded in repository.
- [x] **Successful rerun:** Build #5 completed cleanly and deployed to port 8085.
