MVNW := ./mvnw

.PHONY: help run test build clean package

help:
	@echo "Available targets:"
	@echo "  make run     - Start the Spring Boot application"
	@echo "  make test    - Run tests"
	@echo "  make build   - Compile and package the application"
	@echo "  make clean   - Remove build artifacts"
	@echo "  make package - Alias for build"

run:
	$(MVNW) spring-boot:run

test:
	$(MVNW) test

build package:
	$(MVNW) package

clean:
	$(MVNW) clean
