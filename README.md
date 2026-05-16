# Portal Admitere — Sistem Microservicii

Platformă web pentru gestionarea admiterii universitare și cazării studențești, construită pe arhitectură microservicii cu Spring Boot 3 și Angular 21.

---

## Arhitectura sistemului

```
┌─────────────────────────────────────────────────────────────────────┐
│                        FRONTEND Angular 21                          │
│                    http://localhost:4300                             │
└───────────────────────────┬─────────────────────────────────────────┘
                            │ HTTP (JWT Bearer + XSRF-TOKEN)
                            ▼
┌─────────────────────────────────────────────────────────────────────┐
│                    API GATEWAY  :8080                               │
│         Spring Cloud Gateway · Rate Limiter · JWT Filter            │
│              Load Balancing (lb://user-service)                     │
└────────┬──────────────────┬──────────────────┬──────────────────────┘
         │                  │                  │
         ▼                  ▼                  ▼
┌────────────────┐ ┌─────────────────┐ ┌──────────────────┐
│  USER SERVICE  │ │ ADMISSIONS SVC  │ │  DORMITORY SVC   │
│   :8081/:8083  │ │      :8082      │ │      :8084       │
│  (2 instante)  │ │                 │ │                  │
└───────┬────────┘ └────────┬────────┘ └────────┬─────────┘
        │                   │                   │
        └───────────────────┼───────────────────┘
                            │ Eureka Client
                            ▼
        ┌──────────────────────────────────────┐
        │   DISCOVERY SERVER (Eureka)  :8761   │
        └──────────────────────────────────────┘
        ┌──────────────────────────────────────┐
        │      CONFIG SERVER           :8888   │
        │   (JWT secret, credențiale DB)       │
        └──────────────────────────────────────┘

INFRASTRUCTURA
┌───────────┐ ┌───────────┐ ┌───────────┐ ┌───────────┐
│  MySQL    │ │  MongoDB  │ │  Redis    │ │ RabbitMQ  │
│  :3306    │ │  :27017   │ │  :6379    │ │  :5672    │
└───────────┘ └───────────┘ └───────────┘ └───────────┘
┌───────────┐ ┌───────────┐
│Prometheus │ │  Grafana  │
│  :9090    │ │  :3000    │
└───────────┘ └───────────┘
```

---

## Diagrama ER — MySQL (portal_admitere_users)

```
┌──────────────────────┐         ┌──────────────────────┐
│        users         │         │     user_profiles     │
├──────────────────────┤         ├──────────────────────┤
│ id          BIGINT PK│◄────────│ id          BIGINT PK│
│ username    VARCHAR  │  1   1  │ user_id     BIGINT FK│
│ email       VARCHAR  │         │ first_name  VARCHAR  │
│ password    VARCHAR  │         │ last_name   VARCHAR  │
│ role        ENUM     │         │ cnp         VARCHAR  │
│ enabled     BOOLEAN  │         │ date_of_birth DATE   │
│ created_at  DATETIME │         │ phone       VARCHAR  │
│ updated_at  DATETIME │         └──────────────────────┘
└──────┬───────────────┘
       │ 1
       │
       │ N
┌──────▼───────────────┐         ┌──────────────────────┐
│   refresh_tokens     │         │    announcements     │
├──────────────────────┤         ├──────────────────────┤
│ id          BIGINT PK│         │ id          BIGINT PK│
│ token       VARCHAR  │         │ title       VARCHAR  │
│ user_id     BIGINT FK│    N  1 │ content     TEXT     │
│ expiry_date DATETIME │◄────────│ created_by_user_id FK│
│ revoked     BOOLEAN  │         │ created_by  BIGINT   │
│ device_info VARCHAR  │         │ created_at  DATETIME │
│ created_at  DATETIME │         │ enabled     BOOLEAN  │
└──────────────────────┘         └──────────┬───────────┘
                                            │ N
                              ┌─────────────┴─────────────┐
                              │    announcement_tags       │
                              ├───────────────────────────┤
                              │ announcement_id   BIGINT  │
                              │ tag_id            BIGINT  │
                              └─────────────┬─────────────┘
                                            │ N
                                            │ 1
                                    ┌───────▼──────┐
                                    │     tags     │
                                    ├──────────────┤
                                    │ id   BIGINT  │
                                    │ name VARCHAR │
                                    └──────────────┘

MongoDB — portal_admitere_announcements
┌─────────────────────────────┐
│         audit_logs          │
├─────────────────────────────┤
│ _id       ObjectId          │
│ userId    Long              │
│ action    String            │
│ details   String            │
│ timestamp LocalDateTime     │
└─────────────────────────────┘
```

---

## Tehnologii

| Layer | Tehnologie |
|---|---|
| Frontend | Angular 21, TypeScript, SCSS |
| API Gateway | Spring Cloud Gateway 2023.0.1 |
| Backend | Spring Boot 3.2.5, Java 21 Virtual Threads |
| Securitate | Spring Security 6, JWT (jjwt 0.12.5), CSRF |
| Baze de date | MySQL 8.0 (JPA/Hibernate), MongoDB 7 (audit log) |
| Caching | Redis 7 (`@Cacheable`, TTL 10 min) |
| Mesagerie | RabbitMQ 3 (producer evenimente admitere) |
| Service Discovery | Netflix Eureka |
| Config centralizat | Spring Cloud Config Server |
| Reziliență | Resilience4j Circuit Breaker + Retry |
| Load Balancing | Spring Cloud LoadBalancer (round-robin) |
| Monitorizare | Micrometer + Prometheus + Grafana |
| Testare | JUnit 5, Mockito, Spring Security Test, JaCoCo |
| Documentație API | SpringDoc OpenAPI (Swagger UI) |

---

## Rulare locală

### Prerequisite
- Java 21
- Maven 3.9+
- Node.js 20+, Angular CLI 21
- Docker Desktop

### 1. Pornire infrastructură

```bash
docker compose up -d
```

Pornește: MySQL, MongoDB, Redis, RabbitMQ, Prometheus, Grafana.

### 2. Pornire servicii (în ordine)

```bash
# Discovery Server
cd discovery-server && mvn spring-boot:run

# Config Server
cd config-server && mvn spring-boot:run

# User Service
cd user-service && mvn spring-boot:run

# API Gateway
cd api-gateway && mvn spring-boot:run
```

### 3. Pornire frontend

```bash
cd frontend
npm install
ng serve
```

Aplicația rulează la `http://localhost:4300`.

### 4. Demo Load Balancing (opțional)

```powershell
.\start-second-instance.ps1
```

Pornește o a doua instanță de user-service pe portul 8083. Ambele apar în Eureka la `http://localhost:8761`. Header-ul `X-Instance-Port` din răspunsuri arată care instanță a procesat cererea.

---

## URL-uri utile

| Serviciu | URL |
|---|---|
| Aplicație web | http://localhost:4300 |
| Eureka Dashboard | http://localhost:8761 |
| API Gateway | http://localhost:8080 |
| Swagger UI (user-service) | http://localhost:8081/swagger-ui.html |
| Config Server | http://localhost:8888/user-service/default |
| Grafana | http://localhost:3000 (admin/admin) |
| Prometheus | http://localhost:9090 |
| RabbitMQ Management | http://localhost:15672 (guest/guest) |
| JaCoCo Coverage | http://localhost:8081/coverage/index.html |

---

## Rulare teste și raport JaCoCo

```bash
cd user-service
mvn test
```

Raportul de acoperire se găsește la: `user-service/target/site/jacoco/index.html`

Sau, dacă user-service rulează: `http://localhost:8081/coverage/index.html`

---

## Roluri utilizatori

| Rol | Acces |
|---|---|
| `STUDENT` | Profil, anunțuri, depunere cerere admitere, cerere cămin |
| `SECRETARIAT` | Gestionare cereri admitere, generare contracte PDF |
| `ADMIN` | CRUD utilizatori, algoritm admitere, monitorizare, anunțuri |
