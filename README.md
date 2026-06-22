# Core — Spring Boot Project

A Spring Boot training project covering JPA, REST CRUD APIs, and a `RestTemplate` REST client.

## Prerequisites

Install these on your local machine before running the project:

| Tool | Version | Notes |
|------|---------|-------|
| **Java JDK** | 21+ | Required. Check with `java -version` |
| **Make** | any | Usually pre-installed on macOS/Linux |

Maven is **not** required. This project includes the Maven Wrapper (`./mvnw`), and the `Makefile` uses it for you.

### Install Java (if needed)

**macOS (Homebrew):**
```bash
brew install openjdk@21
```

**Verify:**
```bash
java -version
```

## Local setup

1. **Open the project folder**
   ```bash
   cd /path/to/JavaTraining/core
   ```

2. **Run tests** (downloads dependencies on first run)
   ```bash
   make test
   ```

3. **Start the application**
   ```bash
   make run
   ```

4. **Open in browser**
   - API: http://localhost:8080/api/products
   - H2 console: http://localhost:8080/h2-console

Stop the server with `Ctrl+C`.

## Spring profiles

The app uses Spring profiles for environment-specific settings (REST URL, port, logging).

| Profile | Port | REST URL (`api.base-url`) | SQL logging | H2 console |
|---------|------|---------------------------|-------------|------------|
| `dev` (default) | 8080 | `http://localhost:8080` | on | enabled |
| `prod` | 9090 | `http://localhost:9090` | off | disabled |

### Activate a profile

**Default** — `dev` is active via `application.properties`:
```properties
spring.profiles.active=dev
```

**Makefile:**
```bash
make run-dev          # dev profile, port 8080
make run-prod         # prod profile, port 9090
make run PROFILE=prod   # same as run-prod
```

**VM argument** (lesson 56):
```bash
./mvnw spring-boot:run -Dspring-boot.run.jvmArguments="-Dspring.profiles.active=prod"
```

**Command-line argument:**
```bash
./mvnw spring-boot:run -Dspring-boot.run.arguments=--spring.profiles.active=prod
```

**Running the JAR:**
```bash
make build
java -jar target/core-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```

When using the `prod` profile, open the API at http://localhost:9090/api/products.

## Makefile commands

| Command | Description |
|---------|-------------|
| `make help` | List available commands |
| `make test` | Run all tests |
| `make run` | Start the Spring Boot app on port 8080 |
| `make build` | Compile and package the app (creates a JAR in `target/`) |
| `make clean` | Remove build artifacts |

## Quick API test

With the app running (`make run`), open a second terminal:

```bash
# Create a product
curl -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -d '{"name":"Laptop","description":"15-inch laptop","price":999.99}'

# Get all products
curl http://localhost:8080/api/products

# Get one product
curl http://localhost:8080/api/products/1

# Update a product
curl -X PUT http://localhost:8080/api/products/1 \
  -H "Content-Type: application/json" \
  -d '{"name":"Laptop Pro","description":"Updated model","price":1099.99}'

# Delete a product
curl -X DELETE http://localhost:8080/api/products/1
```

## H2 database console

1. Start the app with `make run`
2. Go to http://localhost:8080/h2-console
3. Use these settings:
   - **JDBC URL:** `jdbc:h2:mem:studentdb`
   - **Username:** `sa`
   - **Password:** *(leave empty)*
4. Run `SELECT * FROM PRODUCT;` to view data

## Project structure

```
core/
├── Makefile
├── mvnw                 # Maven wrapper (used by Makefile)
├── pom.xml
└── src/
    ├── main/java/com/example/core/
    │   ├── CoreApplication.java
    │   ├── client/      # RestTemplate client
    │   ├── config/
    │   ├── controller/  # REST API
    │   ├── model/       # JPA entities
    │   └── repository/
    └── test/            # JUnit tests
```

## Troubleshooting

### Port 8080 already in use

Another instance may still be running. Stop it first:

```bash
lsof -i :8080
kill <PID>
```

Or start on a different port:

```bash
./mvnw spring-boot:run -Dspring-boot.run.arguments=--server.port=8081
```

### First `make test` is slow

The first run downloads Maven and dependencies. Later runs are much faster.

### Permission denied on `mvnw`

```bash
chmod +x mvnw
```
