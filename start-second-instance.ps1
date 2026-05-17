# Porneste a doua instanta de user-service pe portul 8083
# Eureka va inregistra ambele instante sub numele "user-service"
# API Gateway va face load balancing automat intre ele

$JAVA_HOME_LOCAL = "$env:LOCALAPPDATA\Programs\Eclipse Adoptium\jdk-21.0.7.6-hotspot"
$MAVEN_VERSION   = "3.9.6"
$MAVEN_DIR       = "$PSScriptRoot\.tools\apache-maven-$MAVEN_VERSION"
$MAVEN_CMD       = "$MAVEN_DIR\bin\mvn.cmd"
$MAVEN_ZIP       = "$PSScriptRoot\.tools\maven.zip"
$MAVEN_URL       = "https://archive.apache.org/dist/maven/maven-3/$MAVEN_VERSION/binaries/apache-maven-$MAVEN_VERSION-bin.zip"

# --- Java ---
$javaExe = Get-Command java -ErrorAction SilentlyContinue
if (-not $javaExe) {
    if (Test-Path "$JAVA_HOME_LOCAL\bin\java.exe") {
        $env:JAVA_HOME = $JAVA_HOME_LOCAL
        $env:PATH = "$JAVA_HOME_LOCAL\bin;$env:PATH"
        Write-Host "Java gasit la: $JAVA_HOME_LOCAL" -ForegroundColor Green
    } else {
        Write-Host "EROARE: Java nu a fost gasit. Instaleaza JDK 21." -ForegroundColor Red
        exit 1
    }
}

# --- Maven ---
if (-not (Test-Path $MAVEN_CMD)) {
    Write-Host "Maven nu e in PATH. Se descarca Maven $MAVEN_VERSION (o singura data)..." -ForegroundColor Yellow
    New-Item -ItemType Directory -Force -Path "$PSScriptRoot\.tools" | Out-Null
    try {
        Invoke-WebRequest -Uri $MAVEN_URL -OutFile $MAVEN_ZIP -UseBasicParsing
        Expand-Archive -Path $MAVEN_ZIP -DestinationPath "$PSScriptRoot\.tools" -Force
        Remove-Item $MAVEN_ZIP
        Write-Host "Maven descarcat cu succes." -ForegroundColor Green
    } catch {
        Write-Host "EROARE la descarcarea Maven: $_" -ForegroundColor Red
        exit 1
    }
}

$env:PATH = "$MAVEN_DIR\bin;$env:PATH"

# --- Pornire instanta 2 ---
Write-Host ""
Write-Host "Pornind instanta 2 de user-service pe portul 8083..." -ForegroundColor Cyan
Write-Host "Dupa pornire, verifica Eureka la: http://localhost:8761" -ForegroundColor Yellow
Write-Host "Ambele instante apar sub USER-SERVICE" -ForegroundColor Yellow
Write-Host ""

$env:SPRING_PROFILES_ACTIVE = "local"

$servicePath = Join-Path $PSScriptRoot "user-service"
Set-Location $servicePath
& $MAVEN_CMD spring-boot:run "-Dspring-boot.run.arguments=--server.port=8083"
