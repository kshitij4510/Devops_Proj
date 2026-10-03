# TASK 8 REPORT: Pipeline as Code and Automated Server Deployment
**Project Name:** Dockerized Farm Equipment Rental System (AgriRent DevOps)  
**Document Reference:** AR-REP-008  
**Version:** 1.0.0  
**Date:** 2026-10-04  
**Author:** DevOps Engineer & Release Automation Lead  
**Status:** Completed & Deployed (`Pipeline Build #2 SUCCESS - Live on Port 8085`)  

---

## 1. Executive Summary
This milestone marks the successful implementation of **Task 8: Pipeline as Code and Server Deployment** for the **Dockerized Farm Equipment Rental System (AgriRent)**. Using a declarative `Jenkinsfile` versioned directly in the Git repository, the complete delivery lifecycle was codified into automated stages: SCM checkout, Maven compilation, automated test execution, Spring Boot artifact packaging, and persistent server deployment to Spring Boot's embedded Tomcat web server. The deployment pipeline features parameterization for target port (`DEPLOY_PORT`) and Spring runtime profile (`APP_PROFILE`). The deployed service was verified healthy at `http://localhost:8085/api/health` with live REST endpoints operational.

---

## 2. Declarative Jenkinsfile Specification

The complete Pipeline as Code is stored at [`Jenkinsfile`](file:///c:/Users/Administrator/Documents/Devops_Proj/Jenkinsfile):

```groovy
pipeline {
    agent any

    parameters {
        string(name: 'DEPLOY_PORT', defaultValue: '8085', description: 'Application deployment port')
        choice(name: 'APP_PROFILE', choices: ['prod', 'staging', 'dev'], description: 'Active Spring runtime profile')
    }

    environment {
        JAVA_HOME = 'C:\\Program Files\\Java\\jdk-21.0.12'
        M2_HOME = 'C:\\DevTools\\apache-maven-3.9.16'
        PATH = "${M2_HOME}\\bin;${JAVA_HOME}\\bin;${env.PATH}"
        JENKINS_NODE_COOKIE = 'dontKillMe'
        BUILD_ID = 'dontKillMe'
    }

    stages {
        stage('Checkout SCM') {
            steps {
                echo '=== Stage 1: Checkout Source Code from Repository ==='
                checkout scm
                script {
                    echo "Branch: ${env.GIT_BRANCH ?: 'main'}"
                    echo "Commit: ${env.GIT_COMMIT ?: 'HEAD'}"
                }
            }
        }

        stage('Build & Compile') {
            steps {
                echo '=== Stage 2: Compiling Java 21 Source with Maven ==='
                bat 'call "%M2_HOME%\\bin\\mvn.cmd" clean compile'
            }
        }

        stage('Automated Tests & Quality Gate') {
            steps {
                echo '=== Stage 3: Executing Unit & Integration Tests ==='
                bat 'call "%M2_HOME%\\bin\\mvn.cmd" test "-Dtest=EquipmentServiceTest,RentalWorkflowServiceTest"'
            }
            post {
                always {
                    junit testResults: 'target/surefire-reports/*.xml', allowEmptyResults: true
                }
            }
        }

        stage('Package Artifact') {
            steps {
                echo '=== Stage 4: Packaging Executable JAR Artifact ==='
                bat 'call "%M2_HOME%\\bin\\mvn.cmd" package -DskipTests=true'
                archiveArtifacts artifacts: 'target/*.jar', fingerprint: true, allowEmptyArchive: false
            }
        }

        stage('Deploy to Embedded Tomcat Server') {
            steps {
                echo "=== Stage 5: Deploying Farm Equipment Rental App on Port ${params.DEPLOY_PORT} ==="
                powershell '''
                    $port = [int]$env:DEPLOY_PORT
                    $profile = "$env:APP_PROFILE"
                    Write-Host "Verifying existing process on port $port..."
                    $conn = Get-NetTCPConnection -LocalPort $port -ErrorAction SilentlyContinue
                    if ($conn) {
                        $pidToKill = $conn.OwningProcess | Select-Object -Unique
                        Write-Host "Stopping existing process with PID: $pidToKill"
                        Stop-Process -Id $pidToKill -Force -ErrorAction SilentlyContinue
                        Start-Sleep -Seconds 2
                    }

                    Write-Host "Launching Farm Equipment Rental System executable JAR on port $port with profile $profile..."
                    $jarPath = (Get-Item "target\\farm-equipment-rental-1.0.0.jar").FullName
                    $cmd = "`"C:\\Program Files\\Java\\jdk-21.0.12\\bin\\java.exe`" -jar `"$jarPath`" --server.port=$port --spring.profiles.active=$profile"
                    $procRes = Invoke-CimMethod -ClassName Win32_Process -MethodName Create -Arguments @{ CommandLine = $cmd }
                    Write-Host "Launched detached process PID: $($procRes.ProcessId) ReturnValue: $($procRes.ReturnValue)"

                    Write-Host "Validating deployment liveness via /api/health..."
                    $maxRetries = 15
                    $ready = $false
                    for ($i = 1; $i -le $maxRetries; $i++) {
                        Start-Sleep -Seconds 2
                        try {
                            $res = Invoke-RestMethod -Uri "http://localhost:$port/api/health" -TimeoutSec 3 -ErrorAction Stop
                            if ($res.status -eq "UP") {
                                Write-Host "Health Check SUCCESS: $($res.service) is UP and running!"
                                $ready = $true
                                break
                            }
                        } catch {
                            Write-Host "Waiting for service to become healthy... ($i/$maxRetries)"
                        }
                    }

                    if (-not $ready) {
                        Write-Error "Deployment health check timed out on port $port"
                        exit 1
                    }
                '''
            }
        }
    }

    post {
        success {
            echo "=========================================================================="
            echo "SUCCESS: Farm Equipment Rental System deployed successfully!"
            echo "URL: http://localhost:${params.DEPLOY_PORT}"
            echo "Health Diagnostic: http://localhost:${params.DEPLOY_PORT}/api/health"
            echo "=========================================================================="
        }
        failure {
            echo "PIPELINE FAILED: Check stage logs for details."
        }
    }
}
```

---

## 3. Pipeline Run Execution Details

| Pipeline Metric | Value |
| :--- | :--- |
| **Pipeline Job Name** | `Farm-Equipment-Rental-Pipeline` |
| **Build Number** | `#2` |
| **Target Port (`DEPLOY_PORT`)** | `8085` |
| **Target Profile (`APP_PROFILE`)** | `staging` |
| **Result** | `SUCCESS` |
| **Deployed PID** | `4620` (Detached daemon via Win32_Process) |
| **Artifact Fingerprint** | `farm-equipment-rental-1.0.0.jar` (Repackaged Boot Archive) |

### Stage Breakdown & Timings
1. **Stage 1 (Checkout SCM):** Checked out revision `0019f02` from `main` branch.
2. **Stage 2 (Build & Compile):** Java 21 compilation using Apache Maven 3.9.16.
3. **Stage 3 (Automated Tests & Quality Gate):** Ran JUnit test suite; surefire reports published.
4. **Stage 4 (Package Artifact):** Generated executable JAR and recorded fingerprint.
5. **Stage 5 (Deploy to Embedded Tomcat):** Safely retired stale listeners on port 8085, launched JVM with `--server.port=8085 --spring.profiles.active=staging`, and verified health check handshake.

---

## 4. Deployed Application Verification & Live Evidence

### 4.1 Health Check Diagnostic (`GET http://localhost:8085/api/health`)
```json
{
  "status": "UP",
  "service": "farm-equipment-rental",
  "version": "1.0.0-mvp",
  "uptimeMs": 33575,
  "jvmUptimeMs": 36061,
  "availableProcessors": 8,
  "freeMemoryBytes": 30634280,
  "totalMemoryBytes": 67108864,
  "timestamp": "2026-10-04T02:02:24.583990600"
}
```

### 4.2 Live KPI Metrics (`GET http://localhost:8085/api/dashboard/stats`)
```json
{
  "totalEquipment": 6,
  "availableEquipment": 6,
  "rentedEquipment": 0,
  "maintenanceEquipment": 0,
  "activeRentals": 1,
  "completedRentals": 1,
  "pendingRentals": 0,
  "totalRevenue": 24500.0,
  "utilizationRate": 0.0,
  "categoryBreakdown": {
    "TRACTOR": 2,
    "COMBINE_HARVESTER": 1,
    "ROTAVATOR": 1,
    "SEED_DRILL": 1,
    "BOOM_SPRAYER": 1
  }
}
```

### 4.3 Live Inventory Query (`GET http://localhost:8085/api/equipment`)
Verified live responses returning seeded farm machinery:
- `ID 101`: John Deere 5050D Tractor (₹2,500/day, `AVAILABLE`)
- `ID 102`: Mahindra 575 DI Sarpanch (₹2,200/day, `AVAILABLE`)
- `ID 103`: Kubota Harvester DC-68G (₹8,500/day, `AVAILABLE`)

---

## 5. Deliverable Checklist
- [x] **Jenkinsfile:** Parameterized declarative pipeline with 5 core stages.
- [x] **Successful pipeline run:** Recorded in Jenkins as `Farm-Equipment-Rental-Pipeline #2 (SUCCESS)`.
- [x] **Deployed application URL:** Live at `http://localhost:8085`.
- [x] **Configuration evidence:** Complete console log captured at [`jenkins/pipeline-build-2-console.log`](file:///c:/Users/Administrator/Documents/Devops_Proj/jenkins/pipeline-build-2-console.log).
