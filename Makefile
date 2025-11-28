.PHONY: help docker-up docker-down docker-up-deps docker-build docker-logs docker-shell

help: ## Show this help message
	@echo 'Usage: make [target]'
	@echo ''
	@echo 'Targets:'
	@awk 'BEGIN {FS = ":.*?## "} /^[a-zA-Z_-]+:.*?## / {printf "  %-15s %s\n", $$1, $$2}' $(MAKEFILE_LIST)

docker-up: ## Start all services with Docker Compose
	docker compose up -d

docker-up-deps: ## Start only external dependencies (Redis + WireMock)
	docker compose up -d external-services-mock redis

docker-down: ## Stop services with Docker Compose
	docker compose down

docker-build: ## Build Docker image
	docker compose build

docker-logs: ## Show Docker logs
	docker compose logs -f

docker-shell: ## Enter the running application container
	docker compose exec delivery-window-service /bin/sh