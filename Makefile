ifneq ("$(wildcard .env)","")
	include .env
	export
endif

up: ## Start all containers with docker compose.
	docker compose up -d --force-recreate

down: ## Stop all containers.
	docker compose down

recreate: down ## Rebuild and recreate all containers.
	docker compose up --build --force-recreate -d

.PHONY: run
run: ## Run the project locally.
	./mvnw spring-boot:run

.PHONY: install
install: ## Install dependencies
	mvn clean install

.PHONY: test
test: ## Run tests. Make sure to have the test database up.
	mvn clean test

.PHONY: up-database
up-database: ## Start database container.
	docker compose up -d database --force-recreate

.PHONY: up-database
up-test-database: ## Start test database container.
	docker compose -f docker-compose-test.yaml up -d test-database --force-recreate

.DEFAULT_GOAL := help
help:
	@grep -E '^[a-zA-Z_-]+:.*?## .*$$' $(MAKEFILE_LIST) | sed 's/Makefile://g' | awk 'BEGIN {FS = ":.*?## "}; {printf "\033[36m%-30s\033[0m %s\n", $$1, $$2}'
