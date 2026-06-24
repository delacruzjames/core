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
   - Product UI: http://localhost:8080/products
   - API: http://localhost:8080/api/products
   - Health: http://localhost:8080/actuator/health
   - Info: http://localhost:8080/actuator/info
   - Metrics: http://localhost:8080/actuator/metrics
   - H2 console: http://localhost:8080/h2-console

Stop the server with `Ctrl+C`.

## Spring profiles

The app uses Spring profiles for environment-specific settings (REST URL, port, logging).

| Profile | Port | REST URL (`api.base-url`) | SQL logging | H2 console | App logging |
|---------|------|---------------------------|-------------|------------|-------------|
| `dev` (default) | 8080 | `http://localhost:8080` | on | enabled | DEBUG → console |
| `prod` | 9090 | `http://localhost:9090` | off | disabled | INFO → `logs/product-api.log` |

### Activate a profile

**Default** — `dev` is active via `application.properties`:
```properties
spring.profiles.active=dev
```

**Makefile:**
```bash
make run-dev          # dev profile, port 8080
make run-prod         # prod profile, port 9090
make run-prod-vm      # prod profile via VM argument (lesson 56)
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

## Logging (Section 9)

`ProductController` uses SLF4J logging (`LoggerFactory.getLogger`). Each endpoint logs at `INFO` when a request is handled; `DEBUG` for details; `WARN` when a product is not found.

| Profile | Log level (`com.example.core`) | Output |
|---------|-------------------------------|--------|
| `dev` | `DEBUG` | Console |
| `prod` | `INFO` (controller only) | File: `logs/product-api.log` |

**Try it** — start the app and hit the API:

```bash
make run-dev
curl http://localhost:8080/api/products
```

You should see lines like `Fetching all products` in the console.

**Prod file logging:**

```bash
make run-prod
curl http://localhost:9090/api/products
cat logs/product-api.log
```

**Change log level at runtime** (lesson 60) via command line:

```bash
./mvnw spring-boot:run -Dspring-boot.run.arguments="--logging.level.com.example.core=TRACE"
```

## Health checks and metrics (Section 10)

Spring Boot Actuator exposes operational endpoints for monitoring the app.

| Endpoint | URL (dev) | Purpose |
|----------|-----------|---------|
| Health | `/actuator/health` | App and dependency status |
| Info | `/actuator/info` | App metadata and build info |
| Metrics | `/actuator/metrics` | JVM and app metrics |
| Env | `/actuator/env` | Environment properties (dev only) |

### Profile settings

| Profile | Exposed endpoints | Health details |
|---------|-------------------|----------------|
| `dev` | health, info, metrics, env | always shown |
| `prod` | health, info, metrics | hidden |

### Custom health check

`ProductHealthIndicator` checks the product database and reports how many products exist:

```bash
curl http://localhost:8080/actuator/health
```

Example response (dev):

```json
{
  "status": "UP",
  "components": {
    "product": {
      "status": "UP",
      "details": {
        "products": 2,
        "message": "Product database is reachable"
      }
    }
  }
}
```

### Build info and custom info endpoint

Maven generates build metadata (`build.version`, `build.time`, etc.) via the `build-info` goal.

`CoreInfoContributor` adds custom fields like `activeProfiles` and `api`.

```bash
curl http://localhost:8080/actuator/info
```

**Prod** (port 9090):

```bash
curl http://localhost:9090/actuator/health
curl http://localhost:9090/actuator/info
curl http://localhost:9090/actuator/metrics
```

## Thymeleaf templates (Section 11)

Thymeleaf renders server-side HTML views for the product catalog. The REST API at `/api/products` is unchanged.

| URL | Template | Purpose |
|-----|----------|---------|
| `/products` | `products/list.html` | List all products (`th:each`) |
| `/products/{id}` | `products/detail.html` | Single product object |
| `/products/new` | `products/form.html` | Create form |
| `/products/{id}/edit` | `products/form.html` | Edit form |

**MVC flow:**

```
Browser → ProductViewController (@Controller) → Model data → Thymeleaf template → HTML
```

**Dev cache disabled** (lesson 73) — template changes apply without restart:

```properties
spring.thymeleaf.cache=false
```

**Try it:**

```bash
make run-dev
open http://localhost:8080/products
```

Use **Add Product** to submit the HTML form. The controller saves via JPA and redirects back to the list.

**Key Thymeleaf syntax used:**

| Syntax | Example |
|--------|---------|
| `th:text` | `th:text="${product.name}"` |
| `th:each` | `th:each="product : ${products}"` |
| `th:href` | `th:href="@{/products/{id}(id=${product.id})}"` |
| `th:field` | `th:field="*{name}"` (form binding) |
| `th:object` | `th:object="${product}"` |

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
    │   ├── config/      # RestTemplate, info contributor
    │   ├── controller/  # REST API + Thymeleaf MVC views
    │   ├── health/      # Custom health indicators
    │   ├── model/       # JPA entities
    │   └── repository/
    ├── main/resources/
    │   ├── templates/   # Thymeleaf HTML views
    │   └── static/      # CSS and static assets
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
