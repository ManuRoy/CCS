# Connected Vehicle Telemetry & Health Platform

A portfolio project demonstrating a cloud-native backend for **simulated** connected-vehicle data.
It is inspired by common connected-car concepts; it is not a Nissan production system and does not
use proprietary Nissan data.

## Architecture

Python simulator -> Spring Boot REST API -> PostgreSQL
                                  |-> diagnostic alerts
                                  |-> fleet summary
Docker Compose runs the API and PostgreSQL locally. Kubernetes and Helm files provide a starter
for later deployment. The first version ingests telemetry over REST; MQTT/RabbitMQ integration is
a planned extension, not yet wired into the API.

## Prerequisites

- Java 21
- Maven 3.9+ (or use the Maven wrapper if you add one)
- Docker Desktop with Docker Compose
- Python 3.12 only if running the simulator outside Docker

## Run with Docker Compose

From the repository root:

```bash
docker compose up --build -d
docker compose ps
docker compose logs -f api
```

Open:
- API health: http://localhost:8080/actuator/health
- Swagger UI: http://localhost:8080/swagger-ui.html
- Fleet summary: http://localhost:8080/api/v1/fleet/summary

Stop:

```bash
docker compose down
```

Remove local database data too (destructive):

```bash
docker compose down -v
```

## Create a vehicle

```bash
curl -X POST http://localhost:8080/api/v1/vehicles \
  -H "Content-Type: application/json" \
  -d '{"vin":"1N4AL3AP0JC123456","make":"Nissan-inspired demo","model":"Telemetry EV","modelYear":2025}'
```

VIN must be 17 characters and omit I, O, and Q. If the example VIN already exists, list vehicles first.

## Send telemetry

Replace `1` with the returned vehicle id:

```bash
curl -X POST http://localhost:8080/api/v1/telemetry \
  -H "Content-Type: application/json" \
  -d '{"vehicleId":1,"speedKph":62.5,"engineTempC":91.0,"batteryVoltage":12.6,"odometerKm":12450.3}'
```

A high engine temperature (>= 105 C) or low battery voltage (< 11.8 V) creates an alert.
Health score is a simple demonstration heuristic, not a certified vehicle diagnostic algorithm.

## Start the synthetic simulator

```bash
docker compose --profile simulator up --build
```

The simulator creates one demo vehicle if it does not exist, then sends telemetry every five seconds.
It occasionally sends abnormal values to demonstrate alerts.

## API endpoints

| Method | Endpoint | Purpose |
|---|---|---|
| POST | `/api/v1/vehicles` | Register a vehicle |
| GET | `/api/v1/vehicles` | List vehicles |
| GET | `/api/v1/vehicles/{id}` | Get vehicle |
| POST | `/api/v1/telemetry` | Ingest a telemetry reading |
| GET | `/api/v1/vehicles/{id}/telemetry` | Latest 100 readings |
| GET | `/api/v1/vehicles/{id}/alerts` | Alerts for a vehicle |
| GET | `/api/v1/fleet/summary` | Fleet counters |
| GET | `/actuator/health` | Health check |
| GET | `/swagger-ui.html` | API docs |

## Kubernetes starter

The Kubernetes/Helm deployment expects an existing reachable PostgreSQL database and a Kubernetes
Secret named `vehicle-db` containing keys `jdbc-url`, `username`, and `password`. The sample image
name is a placeholder; replace it with your registry/image. Do not deploy the local demo password
to a public cluster.

```bash
kubectl apply -f k8s/namespace.yaml
kubectl apply -f k8s/api-deployment.yaml
```

Before applying, edit the image and create the database Secret in the `connected-vehicle` namespace.
For local Kind, build the image and load it into the cluster first.

## Azure DevOps

`azure-pipelines.yml` builds and tests the Maven app. Registry push and AKS deployment are deliberately
not enabled until you configure Azure service connections and ACR/AKS resource names.

## Next engineering milestones

1. Add unit/integration tests and request/response error contracts.
2. Add pagination, authentication/authorization, and idempotent telemetry ingestion.
3. Wire MQTT/RabbitMQ into an asynchronous ingestion path.
4. Add Prometheus/Grafana dashboards and alert rules.
5. Complete Terraform for ACR/AKS, secure state and secrets, then deploy after checking Azure costs.
