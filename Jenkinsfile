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
                bat 'call mvn clean compile'
            }
        }

        stage('Automated Tests & Quality Gate') {
            steps {
                echo '=== Stage 3: Executing Unit & Integration Tests ==='
                bat 'call mvn test "-Dtest=EquipmentServiceTest,RentalWorkflowServiceTest"'
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
                bat 'call mvn package -DskipTests=true'
                archiveArtifacts artifacts: 'target/*.jar', fingerprint: true, allowEmptyArchive: false
            }
        }

        stage('Deploy to Embedded Tomcat Server') {
            steps {
                echo "=== Stage 5: Deploying Farm Equipment Rental App on Port ${params.DEPLOY_PORT} ==="
                powershell '''
                    $port = [int]$env:DEPLOY_PORT
                    Write-Host "Verifying existing process on port $port..."
                    $conn = Get-NetTCPConnection -LocalPort $port -ErrorAction SilentlyContinue
                    if ($conn) {
                        $pidToKill = $conn.OwningProcess | Select-Object -Unique
                        Write-Host "Stopping existing process with PID: $pidToKill"
                        Stop-Process -Id $pidToKill -Force -ErrorAction SilentlyContinue
                        Start-Sleep -Seconds 2
                    }

                    Write-Host "Launching Farm Equipment Rental System executable JAR on port $port..."
                    $jarPath = (Get-Item "target\\farm-equipment-rental-1.0.0.jar").FullName
                    $proc = Start-Process -FilePath "java" -ArgumentList "-jar", "`"$jarPath`"", "--server.port=$port" -PassThru
                    Write-Host "Launched background process PID: $($proc.Id)"

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
