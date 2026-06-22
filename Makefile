MVNW := ./mvnw
PROFILE ?= dev

.PHONY: help run run-dev run-prod test build clean package

help:
	@echo "Available targets:"
	@echo "  make run          - Start the app (default profile: dev)"
	@echo "  make run-dev      - Start with dev profile (port 8080)"
	@echo "  make run-prod     - Start with prod profile (port 9090)"
	@echo "  make run PROFILE=prod - Start with a custom profile"
	@echo "  make test         - Run tests"
	@echo "  make build        - Compile and package the application"
	@echo "  make clean        - Remove build artifacts"
	@echo "  make package      - Alias for build"

run:
	$(MVNW) spring-boot:run -Dspring-boot.run.profiles=$(PROFILE)

run-dev:
	$(MVNW) spring-boot:run -Dspring-boot.run.profiles=dev

run-prod:
	$(MVNW) spring-boot:run -Dspring-boot.run.profiles=prod

test:
	$(MVNW) test

build package:
	$(MVNW) package

clean:
	$(MVNW) clean
