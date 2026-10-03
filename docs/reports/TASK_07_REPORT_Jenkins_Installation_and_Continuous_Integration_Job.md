# TASK 7 REPORT: Jenkins Installation, Configuration, and Continuous Integration Job
**Project Name:** Dockerized Farm Equipment Rental System (AgriRent DevOps)  
**Document Reference:** AR-REP-007  
**Version:** 1.0.0  
**Date:** 2026-10-04  
**Author:** DevOps Engineer & CI/CD Lead  
**Status:** Completed & Validated (`Build #237 SUCCESS`)  

---

## 1. Executive Summary
This document reports the completion of **Task 7: Jenkins Installation and Continuous Integration Job** for the **Dockerized Farm Equipment Rental System (AgriRent)**. Jenkins was configured as an automated continuous integration controller, integrated with the local Git repository baseline, and configured with a Maven-based build job (`Farm-Equipment-Rental-CI`). The job executes automated compilation, runs unit & integration test suites, archives the resulting executable Spring Boot JAR artifact, and records JUnit quality results. The build pipeline was triggered, executed with 100% test success, and verified against all deliverable criteria.

---

## 2. Jenkins Controller Environment & Installation Details

| Parameter | Configuration Value |
| :--- | :--- |
| **Jenkins Service Name** | `Jenkins` (Windows Service PID: 1396 / Child Java PID: 11440) |
| **Controller URL** | `http://localhost:8080/` |
| **Jenkins Version** | Jenkins 2.x (Jetty 12.1.8 runtime) |
| **Java Runtime** | Oracle JDK 21.0.12 LTS (`C:\Program Files\Java\jdk-21.0.12`) |
| **Maven Toolchain** | Apache Maven 3.9.16 (`C:\DevTools\apache-maven-3.9.16`) |
| **Git Tool** | Git for Windows 2.54.0 (`C:\Program Files\Git\cmd\git.exe`) |
| **JENKINS_HOME** | `C:\ProgramData\Jenkins\.jenkins` |
| **Security / CSRF** | Standard Crumbs enabled (`DefaultCrumbIssuer`) with session authentication |

### Security & System Policy Adjustments
Modern Jenkins Git plugin restricts local filesystem checkouts (`file://`) by default. To enable secure local repository synchronization from `file:///C:/Users/Administrator/Documents/Devops_Proj`, the JVM parameter was injected and persisted in `C:\Program Files\Jenkins\jenkins.xml`:
```xml
-Dhudson.plugins.git.GitSCM.ALLOW_LOCAL_CHECKOUT=true
```
In addition, global node properties were populated with `JAVA_HOME`, `M2_HOME`, and updated toolchain `PATH` to guarantee uniform execution across automated builders.

---

## 3. CI Job Specification (`Farm-Equipment-Rental-CI`)

The CI Freestyle project was provisioned using declarative XML configuration located at [`jenkins/job-config.xml`](file:///c:/Users/Administrator/Documents/Devops_Proj/jenkins/job-config.xml):

```xml
<?xml version='1.1' encoding='UTF-8'?>
<project>
  <description>Continuous Integration Job for Dockerized Farm Equipment Rental System (AgriRent). Builds code using Maven, executes automated JUnit tests, and archives the resulting JAR artifact.</description>
  <scm class="hudson.plugins.git.GitSCM" plugin="git">
    <userRemoteConfigs>
      <hudson.plugins.git.UserRemoteConfig>
        <url>file:///C:/Users/Administrator/Documents/Devops_Proj</url>
      </hudson.plugins.git.UserRemoteConfig>
    </userRemoteConfigs>
    <branches>
      <hudson.plugins.git.BranchSpec>
        <name>*/main</name>
      </hudson.plugins.git.BranchSpec>
    </branches>
  </scm>
  <triggers>
    <hudson.triggers.SCMTrigger>
      <spec>H/5 * * * *</spec>
      <ignorePostCommitHooks>false</ignorePostCommitHooks>
    </hudson.triggers.SCMTrigger>
  </triggers>
  <builders>
    <hudson.tasks.BatchFile>
      <command>@echo off
set "JAVA_HOME=C:\Program Files\Java\jdk-21.0.12"
set "M2_HOME=C:\DevTools\apache-maven-3.9.16"
set "PATH=%M2_HOME%\bin;%JAVA_HOME%\bin;%PATH%"
echo === Building Farm Equipment Rental System with Maven ===
echo JAVA_HOME: %JAVA_HOME%
call "%M2_HOME%\bin\mvn.cmd" clean package -DskipTests=false
if %ERRORLEVEL% NEQ 0 (
    echo Build failed with error %ERRORLEVEL%
    exit /b %ERRORLEVEL%
)
dir target
      </command>
    </hudson.tasks.BatchFile>
  </builders>
  <publishers>
    <hudson.tasks.ArtifactArchiver>
      <artifacts>target/*.jar</artifacts>
      <allowEmptyArchive>false</allowEmptyArchive>
      <onlyIfSuccessful>true</onlyIfSuccessful>
    </hudson.tasks.ArtifactArchiver>
    <hudson.tasks.junit.JUnitResultArchiver plugin="junit">
      <testResults>target/surefire-reports/*.xml</testResults>
    </hudson.tasks.junit.JUnitResultArchiver>
  </publishers>
</project>
```

---

## 4. Build Execution & Quality Verification Evidence

### 4.1 Build Metadata
- **Job Name:** `Farm-Equipment-Rental-CI`
- **Build Number:** `#237`
- **Execution Timestamp:** 2026-10-04 01:50:22 - 01:52:24 (+05:30)
- **Duration:** 121.059 seconds (~2 minutes)
- **Result:** `SUCCESS` (Color: Blue / Stable)
- **Git Commit Tested:** `83bd152c1232fb7101dee7ff3070f0419ea1efd1` (`main`)

### 4.2 Automated Test Execution Summary
The surefire test execution completed cleanly with zero regressions:
```text
[INFO] -------------------------------------------------------
[INFO]  T E S T S
[INFO] -------------------------------------------------------
[INFO] Running com.agrirent.EquipmentServiceTest
[INFO] Tests run: 5, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 1.842 s -- in com.agrirent.EquipmentServiceTest
[INFO] Running com.agrirent.RentalWorkflowServiceTest
[INFO] Tests run: 6, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 1.531 s -- in com.agrirent.RentalWorkflowServiceTest
[INFO] 
[INFO] Results:
[INFO] 
[INFO] Tests run: 11, Failures: 0, Errors: 0, Skipped: 0
[INFO] 
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
```

### 4.3 Archived Build Artefacts
The packaging step generated and archived the Spring Boot self-contained fat JAR in the Jenkins build directory:
- **Archive Path:** `target/farm-equipment-rental-1.0.0.jar`
- **Size:** 24,181,391 bytes (~24.2 MB)
- **Repackaged Structure:** Spring Boot 3.3.4 executable with embedded Tomcat and nested dependencies in `BOOT-INF/`.

### 4.4 Trigger Evidence
1. **Scheduled Polling Trigger:** Configured with crontab spec `H/5 * * * *` which actively monitors Git SCM changes every 5 minutes.
2. **SCM Polling Log (`scm-polling.log`):**
   ```text
   Started on Oct 4, 2026, 1:46:51 AM
   Using strategy: Default
   [poll] Last Built Revision: Revision 83bd152 (refs/remotes/origin/main)
   Done. Took 0.12 sec
   No changes
   ```
3. **Webhook / API Trigger:** Verified via authenticated REST endpoint `POST http://localhost:8080/job/Farm-Equipment-Rental-CI/build`.

---

## 5. Deliverable Checklist
- [x] **Configured Jenkins job:** `Farm-Equipment-Rental-CI` active and green.
- [x] **Successful build log:** Stored at [`jenkins/build-237-console.log`](file:///c:/Users/Administrator/Documents/Devops_Proj/jenkins/build-237-console.log).
- [x] **Trigger evidence:** Scheduled SCM polling (`H/5 * * * *`) and API trigger verified.
- [x] **Archived artefact:** `target/farm-equipment-rental-1.0.0.jar` recorded in Jenkins artifact repository.
