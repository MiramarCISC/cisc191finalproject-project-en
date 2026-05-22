[![Open in Codespaces](https://classroom.github.com/assets/launch-codespace-2972f46106e565e64193e422d61a12cf1da4916b45550586e14ef0a7c637dd04.svg)](https://classroom.github.com/open-in-codespaces?assignment_repo_id=23928289)
# Spring Boot 3 Web Client + gRPC + JPA/H2 1v1 Match Lab

## Overview

This GitHub Classroom lab uses:

- **Spring Boot 3.5.14** for the game server
- **Spring Boot 3 web client** for the browser interface
- **gRPC + Protobuf** between the web client module and the game server module
- **Spring Data JPA** for persistence
- **File-based H2** database for saved matches

This version does **not** use JavaFX or FXML.

The browser talks to the `game-client` Spring Boot app using normal HTTP/JSON. The `game-client` then calls the `game-server` through gRPC. The `game-server` persists matches with JPA/H2.

## Architecture

```text
Browser
  ↓ HTTP/JSON
game-client Spring Boot web app
  ↓ gRPC/Protobuf
game-server Spring Boot gRPC service
  ↓ Spring Data JPA
file-based H2 database
```

## Modules

| Module | Purpose |
|---|---|
| `game-server` | Spring Boot 3 app hosting the gRPC service and persisting matches |
| `game-client` | Spring Boot 3 web app serving HTML/CSS/JS and proxying calls to gRPC |


---

## Build the Project

From the root folder:

```bash
mvn clean install
```

---

## Run the gRPC Server

Open a terminal:

```bash
cd game-server
mvn exec:java
```

Expected output:

```text
Database initialized.
1v1 gRPC Game Server started on port 50051
```
Leave this terminal running.

---

## Run the JavaFX Client

Open a second terminal:

```bash
cd game-client
mvn javafx:run
```
## Persistence

The server persists matches using Spring Data JPA and H2.

| File | Purpose |
|---|---|
| `MatchEntity.java` | JPA entity stored in the database |
| `MatchRepository.java` | Spring Data repository for saved matches |
| `game-server/src/main/resources/application.properties` | Configures file-based H2 |
| `.gitignore` | Prevents local database files from being committed |

The database URL is:

```properties
spring.datasource.url=jdbc:h2:file:./data/game-server-db
```

Local database files are created under:

```text
game-server/data/
```

These files should not be committed.

## Important Design Choices

| Removed Feature | Replacement |
|---|---|
| JavaFX/FXML desktop UI | Spring Boot web client with HTML/CSS/JS |
| Direct browser-to-server REST API | Browser calls web client; web client calls gRPC server |
| JSON between modules | gRPC/protobuf between modules |
| Player roles | Difficulty only |
| HP tracking | Server returns winner and loser |
| In-memory-only match storage | Spring Data JPA with file-based H2 |

## Required TODOs

1. Verify the browser UI sends requests to the `game-client` REST facade.
2. Verify the `game-client` calls the `game-server` using gRPC.
3. Verify completed matches are saved through Spring Data JPA.
4. Verify match history loads from persisted JPA records.
5. Add one small web UI improvement.
6. Complete a Pull Request peer review.

## Reflection Questions

1. What does the browser client do?
2. What does the `game-client` Spring Boot app do?
3. What does the `game-server` Spring Boot app do?
4. Why does this project still use gRPC if the UI is web-based?
5. What data is sent in `JoinMatchRequest`?
6. What data is returned in `MatchResultResponse`?
7. What is the purpose of `MatchEntity`?
8. What is the purpose of `MatchRepository`?
9. Why are H2 database files ignored by Git?
10. What did your peer reviewer suggest?
11. What did you change after peer review?
