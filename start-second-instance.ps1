# Porneste a doua instanta de user-service pe portul 8083
# Eureka va inregistra ambele instante sub numele "user-service"
# API Gateway va face load balancing automat intre ele

$servicePath = Join-Path $PSScriptRoot "user-service"

Write-Host "Pornind instanta 2 de user-service pe portul 8083..." -ForegroundColor Cyan
Write-Host "Dupa pornire, verifica Eureka la: http://localhost:8761" -ForegroundColor Yellow
Write-Host "Ambele instante apar sub USER-SERVICE" -ForegroundColor Yellow
Write-Host ""

Set-Location $servicePath
& mvn spring-boot:run "-Dspring-boot.run.arguments=--server.port=8083"
