# Microservices Fundamentals: Java Store MVP

A runnable Java/Spring Boot store application demonstrating service boundaries, database-per-service, synchronous and asynchronous communication, and loose coupling. The three services are independent Maven modules.

[Read the beginner-friendly end-to-end application flow and diagrams](docs/application-flow.md).

## Code organization

Each service keeps its own package tree under `com.example.<service>`:

```text
controller/   HTTP endpoints
service/      application and business logic
repository/   persistence interfaces
entity/       service-owned database models
dto/          HTTP and event-contract data
config/       framework and broker configuration
messaging/    event consumers and publishers
```

Only the folders relevant to a service are present; each application entry point remains at the service package root for Spring component scanning.

## Run it

Requires Docker Compose. From the repository root:

```sh
docker compose up --build
```

The services are available at:

| Service | URL | Responsibility |
| --- | --- | --- |
| Catalog | `http://localhost:8001` | Product names and prices |
| Orders | `http://localhost:8002` | Accepts orders and exposes order status |
| Inventory | `http://localhost:8003` | Stock and asynchronously processed orders |
| RabbitMQ management | `http://localhost:15672` | Local broker UI (`app` / `app`) |

Place an order:

```sh
curl -i -X POST http://localhost:8002/orders \
  -H 'Content-Type: application/json' \
  -d '{"product_id":1,"quantity":2}'
```

The response contains an order ID. Orders are accepted after product validation; Inventory processes the `order.placed` event shortly afterward. Check the order and inventory:

```sh
curl http://localhost:8002/orders/<order-id>
curl http://localhost:8003/inventory
curl http://localhost:8001/products
```

Seed products: Coffee (ID 1), Tea (ID 2), and Mug (ID 3). Inventory starts with 20, 15, and 8 units respectively. Orders for unknown products are rejected; if an accepted order exceeds available stock, Inventory records it as `rejected` without allowing stock to go negative.

## How the concepts fit together

- **Service boundaries:** Catalog owns product data, Orders owns order acceptance and order records, and Inventory owns stock and reservations. Each service exposes its own HTTP API and can be built and run independently.
- **Database per service:** Each service has a private file-based H2 database on its own named Docker volume. No service reads or writes another service's database.
- **Synchronous communication:** Before accepting an order, Orders calls Catalog's HTTP API for the product details. If Catalog is unavailable, the request fails with `503`.
- **Asynchronous communication:** Orders writes the order and an `order.placed` event to its own outbox in one database transaction. A background publisher sends pending events to RabbitMQ; Inventory consumes them and updates its own database. The API does not wait for Inventory. Inventory de-duplicates events by order ID, so a publish retry does not reserve stock twice.
- **Loose coupling:** Inventory depends on the event contract, not on Orders' API or database. Catalog does not need to know that Orders or Inventory exists. The order event is durable and published asynchronously, so a temporarily unavailable broker can be retried from the Orders outbox.

This is a learning MVP, not a production deployment: it uses demo broker credentials, H2, and has no authentication, authorization, or external-facing TLS.

## Build and test locally

Requires Java 21 and Maven:

```sh
mvn test
```