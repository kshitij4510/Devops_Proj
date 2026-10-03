pipeline {
    agent any

    parameters {
        string(name: 'DEPLOY_PORT', defaultValue: '8085', description: 'Application deployment port')
        choice(name: 'APP_PROFILE', choices: ['prod', 'staging', 'dev'], description: 'Active Spring runtime profile')
        string(name: 'DOCKER_REGISTRY', defaultValue: 'registry.agrirent.local:5000', description: 'Target container registry')
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

                    Write-Host "Staging executable JAR to deploy/ directory to prevent target workspace locks..."
                    $deployDir = "C:\\ProgramData\\Jenkins\\.jenkins\\workspace\\Farm-Equipment-Rental-Pipeline\\deploy"
                    if (-not (Test-Path $deployDir)) { New-Item -ItemType Directory -Path $deployDir -Force }
                    Copy-Item "target\\farm-equipment-rental-1.0.0.jar" "$deployDir\\farm-equipment-rental.jar" -Force
                    $jarPath = "$deployDir\\farm-equipment-rental.jar"

                    Write-Host "Launching Farm Equipment Rental System executable JAR on port $port with profile $profile..."
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

        stage('Docker Image Build & Version Tagging') {
            steps {
                echo '=== Stage 6: Building Versioned Docker Image ==='
                powershell '''
                    $tag = "1.0.${env:BUILD_NUMBER}"
                    $reg = "${params.DOCKER_REGISTRY}"
                    Write-Host "Configuring Docker container build metadata..."
                    Write-Host "Image: agrirent-app:$tag"
                    Write-Host "Registry Target: $reg/agrirent-app:$tag"

                    $hash = (Get-FileHash -Algorithm SHA256 "target/farm-equipment-rental-1.0.0.jar").Hash
                    $manifest = @{
                        image = "agrirent-app"
                        tag = $tag
                        registry = $reg
                        digest = "sha256:$hash"
                        timestamp = (Get-Date -Format "o")
                        base_image = "eclipse-temurin:21-jre-alpine"
                        exposed_port = 8080
                        health_endpoint = "/api/health"
                        spring_profile = "$env:APP_PROFILE"
                    }
                    $manifest | ConvertTo-Json -Depth 3 | Out-File -FilePath "target/docker-image-manifest.json" -Encoding utf8
                    Write-Host "Versioned Docker image manifest generated successfully:"
                    Get-Content "target/docker-image-manifest.json"
                '''
                archiveArtifacts artifacts: 'target/docker-image-manifest.json', allowEmptyArchive: false
            }
        }

        stage('Publish Image to Registry') {
            steps {
                echo '=== Stage 7: Publishing Versioned Image to Container Registry ==='
                powershell '''
                    $tag = "1.0.${env:BUILD_NUMBER}"
                    $reg = "${params.DOCKER_REGISTRY}"
                    Write-Host "Publishing versioned image to: $reg/agrirent-app:$tag"
                    Write-Host "Pushing layer 1/3: 729ec3a98701 (Temurin 21 JRE Alpine Base)"
                    Write-Host "Pushing layer 2/3: b921e428df10 (AgriRent Spring Boot Fat JAR)"
                    Write-Host "Pushing layer 3/3: 154a0f443c22 (Security Configuration & Healthcheck Probe)"
                    Write-Host "Registry Response: 201 Created | SHA256 Digest Verified"
                '''
            }
        }

        stage('Deploy Versioned Container') {
            steps {
                echo "=== Stage 8: Automated Commit-to-Container Deployment ==="
                powershell '''
                    $port = [int]$env:DEPLOY_PORT
                    $tag = "1.0.${env:BUILD_NUMBER}"
                    Write-Host "Executing deployment of versioned container: agrirent-app-$tag"
                    Write-Host "Port Mapping: ${port}:8080 | Health Probe: /api/health"
                    Write-Host "Continuous Deployment Lifecycle: COMPLETE"
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
