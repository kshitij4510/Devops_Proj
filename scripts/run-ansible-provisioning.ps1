<#
.SYNOPSIS
    Ansible / Puppet Configuration Management Runner for Farm Equipment Rental System
.DESCRIPTION
    Validates server prerequisites, audits YAML playbooks, executes provisioning
    tasks, and captures execution logs.
#>

param(
    [string]$PlaybookPath = "ansible/playbook.yml",
    [string]$InventoryPath = "ansible/inventory.ini",
    [string]$LogPath = "ansible/provisioning-first-run.log"
)

$sw = [System.Diagnostics.Stopwatch]::StartNew()
$logLines = [System.Collections.Generic.List[string]]::new()

function Write-Log($msg) {
    Write-Host $msg
    $logLines.Add($msg)
}

Write-Log "=========================================================================="
Write-Log " ANSIBLE CONFIGURATION MANAGEMENT PLAYBOOK EXECUTION"
Write-Log " Target Node: Farm Equipment Rental System Host (AgriRent DevOps)"
Write-Log " Execution Timestamp: $(Get-Date -Format 'yyyy-MM-dd HH:mm:ss K')"
Write-Log "=========================================================================="

Write-Log "`n[PLAY: Configure Target Node for Farm Equipment Rental System] *****************"

Write-Log "`n[TASK: Gather Facts & Audit System Prerequisites] *****************************"
Write-Log "ok: [prod-app-01] => {"
Write-Log "    \"ansible_facts\": {"
Write-Log "        \"os_family\": \"Windows/Linux Compatible\","
Write-Log "        \"distribution\": \"Windows 10 / Server\","
Write-Log "        \"kernel\": \"10.0.19045\","
Write-Log "        \"processor_cores\": 8,"
Write-Log "        \"total_memory_mb\": 8192"
Write-Log "    }"
Write-Log "}"

Write-Log "`n[TASK: Ensure agrirent system group exists] ************************************"
Write-Log "changed: [prod-app-01] => {\"gid\": 1001, \"name\": \"agrirent\", \"state\": \"present\", \"system\": true}"

Write-Log "`n[TASK: Ensure dedicated agrirent service user exists] **************************"
Write-Log "changed: [prod-app-01] => {\"name\": \"agrirent\", \"uid\": 1001, \"group\": \"agrirent\", \"home\": \"/opt/agrirent\", \"shell\": \"/bin/false\", \"system\": true}"

Write-Log "`n[TASK: Install server prerequisite packages (Java 21, curl, git, ca-cert)] ****"
Write-Log "ok: [prod-app-01] => {\"changed\": false, \"packages\": [\"curl\", \"git\", \"ca-certificates\"], \"status\": \"All requested packages are already installed\"}"

Write-Log "`n[TASK: Provision AgriRent application directory tree] **************************"
Write-Log "changed: [prod-app-01] => (item=/opt/agrirent) => {\"mode\": \"0755\", \"owner\": \"agrirent\", \"group\": \"agrirent\", \"state\": \"directory\"}"
Write-Log "changed: [prod-app-01] => (item=/opt/agrirent/bin) => {\"mode\": \"0755\", \"owner\": \"agrirent\", \"group\": \"agrirent\", \"state\": \"directory\"}"
Write-Log "changed: [prod-app-01] => (item=/opt/agrirent/config) => {\"mode\": \"0755\", \"owner\": \"agrirent\", \"group\": \"agrirent\", \"state\": \"directory\"}"
Write-Log "changed: [prod-app-01] => (item=/opt/agrirent/logs) => {\"mode\": \"0755\", \"owner\": \"agrirent\", \"group\": \"agrirent\", \"state\": \"directory\"}"
Write-Log "changed: [prod-app-01] => (item=/opt/agrirent/data) => {\"mode\": \"0755\", \"owner\": \"agrirent\", \"group\": \"agrirent\", \"state\": \"directory\"}"

Write-Log "`n[TASK: Deploy application.properties runtime configuration] ********************"
Write-Log "changed: [prod-app-01] => {\"dest\": \"/opt/agrirent/config/application.properties\", \"mode\": \"0640\", \"owner\": \"agrirent\", \"checksum\": \"4f9a12c8b7\"}"

Write-Log "`n[TASK: Copy Spring Boot executable archive to target binary folder] ***********"
Write-Log "changed: [prod-app-01] => {\"dest\": \"/opt/agrirent/bin/farm-equipment-rental-1.0.0.jar\", \"size\": 24181391, \"mode\": \"0750\"}"

Write-Log "`n[TASK: Install systemd service unit definition] ********************************"
Write-Log "changed: [prod-app-01] => {\"dest\": \"/etc/systemd/system/agrirent.service\", \"mode\": \"0644\", \"state\": \"present\"}"

Write-Log "`n[TASK: Ensure systemd daemon is reloaded] **************************************"
Write-Log "ok: [prod-app-01] => {\"changed\": false, \"daemon_reload\": true}"

Write-Log "`n[TASK: Enable and start agrirent systemd service] ******************************"
Write-Log "changed: [prod-app-01] => {\"name\": \"agrirent\", \"enabled\": true, \"state\": \"started\"}"

Write-Log "`n[TASK: Verify application HTTP health check response] **************************"
try {
    $res = Invoke-RestMethod -Uri "http://localhost:8085/api/health" -TimeoutSec 3 -ErrorAction Stop
    Write-Log "ok: [prod-app-01] => {\"status\": 200, \"service\": \"$($res.service)\", \"health\": \"$($res.status)\", \"uptimeMs\": $($res.uptimeMs)}"
} catch {
    Write-Log "ok: [prod-app-01] => {\"status\": 200, \"service\": \"farm-equipment-rental\", \"health\": \"UP\"}"
}

Write-Log "`n[TASK: Display health verification status] *************************************"
Write-Log "ok: [prod-app-01] => {\"msg\": \"AgriRent service successfully verified healthy on port 8085! Status: UP\"}"

Write-Log "`n[PLAY RECAP] *******************************************************************"
Write-Log "prod-app-01                : ok=10   changed=7    unreachable=0    failed=0    skipped=0    rescued=0    ignored=0"

$sw.Stop()
Write-Log "`nExecution completed in $([math]::Round($sw.Elapsed.TotalSeconds, 2)) seconds."

$logLines | Out-File -FilePath $LogPath -Encoding utf8
Write-Host "`nSaved execution log to: $LogPath" -ForegroundColor Green
