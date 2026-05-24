[![Open in Codespaces](https://classroom.github.com/assets/launch-codespace-2972f46106e565e64193e422d61a12cf1da4916b45550586e14ef0a7c637dd04.svg)](https://classroom.github.com/open-in-codespaces?assignment_repo_id=23928289)
# Project EN - The 1v1 battle

## Project Description

This GitHub Classroom lab uses:

Project EN is a multiplayer-style 1v1 battle game built using a Java backend and a JavaFX desktop client. The JavaFX client communicates with the game server using gRPC, and the server handles the game logic and stores match data with file based JDBC database. 

This project demonstrates layered backend architecture, service communication using gRPC, and persistent data storage. 

---

## Build and Run the Project

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

---

## How to run tests 
From the root folder:

```bash
mvn test
```

---

or run individually:

```bash
cd game-server
mvn test
```

```bash
cd game-client
mvn test
```

---

## Modules and where they appear

| Module Topic                                           | Where it appears in Code                                                                                                                              |
|--------------------------------------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------|
| Module 1: Arrays + OO Refresh                          | `MatchRepository` and `GameServiceImpl`                                                                                                               |
| Module 2: OO Design + Functional Interfaces            | `DamageCalculator`                                                                                                                                    |
| Module 3: Inheritance + Polymorphism                   | `GameServiceImpl` and `GameClientApp`                                                                                                                 |
| Module 4: Exceptions + File I/O + Database Persistence | `GameServerMain`, `PlayerRepository`, `ServerRepository`, `Player/MatchRepository`, `DatabaseConfig/initializer`, `GameServiceImpl`, `GameServerMain` |
| Module 5: Recursion + Algorithms                       | `GameServiceImpl`                                                                                                                                     |
| Module 6: Collections + Generics + Advanced Streams    | `GameServiceImpl`, `MatchHistory`                                                                                                                     |
| Module 7: JavaFX + Events + Lambdas                    | `GameController`                                                                                                                                      |


## Reflection

### What We're most proud of
- We are very proud of how we put all 7 modules together to make it into one cohesive, fully-functioning app that uses every module topic in the proper order to work through the game seamlessly. We are also especially proud of the JDBC database that stores the players and matches. The implementation for the JDBC database was really hard but we managed to find the correct code and figured out how to implement it properly and efficiently.
### What We would improve with more time
- If we had more time, we would improve the JavaFX UI design to have more options like attacking, blocking, and powers so it would feel more like a game rather than just a computer logic fight. We would also increase the ways the different difficulties change the opponents and matches by adding other stats such as defense.