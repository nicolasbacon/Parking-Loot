# Get Started

## 🛠 Requirements

- **Java 24**
- **Maven 3.9+**
- **Docker** (optional, only for containerized runs)

---

## ⚡ Run in Local Development

1. Clone the repository and move into the project directory.

2. Start the application with Maven (this loads `application.properties` + `application-dev.properties` by default):
   ```bash
   mvn spring-boot:run
3. Alternatively, build and run the JAR:
   ```bash
    mvn clean package -DskipTests
    java -jar target/<your-app-name>-0.0.1-SNAPSHOT.jar

---

## 🐳 Run in Docker (Production)

This project uses Google Jib to build container images directly with Maven.
No `Dockerfile` is needed.

### Build the Docker Image

Run:

   ```bash
   mvn clean compile jib:dockerBuild -DskipTests
   ```    

By default, this builds a linux/amd64 image.

👉 On Apple Silicon (M1/M2/M3), you can build an ARM64 image with:

   ```bash
    mvn clean compile jib:dockerBuild -DskipTests -Djib.from.platforms=linux/arm64
   ```

---

### Run with Docker Compose

The provided `docker-compose.yml runs the app in production mode, where:

- application.properties is loaded first,
- application-prod.properties overrides configuration.

Start services:

```bash
docker compose up --build
```