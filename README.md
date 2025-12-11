# Delivery Window Service

## Overview

A microservice that calculates delivery time windows by combining venue opening hours and courier delivery availability. The service finds the intersection between these two schedules and returns formatted delivery windows for each day of the week.

## Business Requirements

### Core Functionality

- Calculate delivery windows by intersecting venue opening hours with courier availability
- Handle complex time scenarios including overnight windows (crossing midnight)
- Implement a **30-minute minimum rule**: delivery periods must be at least 30 minutes long
- Provide graceful error handling when external services are unavailable

### Business Rules

The delivery hours of a venue are calculated by finding the intersection between the venue's opening hours and courier service delivery hours.

**Examples:**

| Venue opening hours   | Delivery hours from Courier Service | Expected delivery hours    |
|-----------------------|--------------------------------------|---------------------------|
| 13-20                 | 14-21                                | 14-20                     |
| 13:30-15, 16-01       | 09-14, 17-00:30                      | 13:30-14, 17-00:30        |
| 13-15                 | 09-13                                | Closed                    |
| 13-15                 | 14:31-16                             | Closed (less than 30 mins)|
| Anything              | Closed                               | Closed                    |
| Closed                | Anything                             | Closed                    |

## API Requirements

### Endpoint

```
GET /delivery-hours?city_slug={city_slug}&venue_id={venue_id}
```

### Parameters

- `city_slug`: City identifier used to retrieve courier availability in that area
- `venue_id`: Unique identifier of the venue to get opening hours for

### Response Format

```json
{
  "delivery_hours": {
    "Monday": "09-12, 13:30-22",
    "Tuesday": "16:45-02",
    "Wednesday": "Closed",
    "Thursday": "Closed",
    "Friday": "Closed",
    "Saturday": "Closed",
    "Sunday": "Closed"
  }
}
```

## Technical Requirements

### Architecture

The service should be well-structured with clear separation of concerns. Consider how you'll organize your code for maintainability and testability.

### External Services

The service must integrate with two external APIs:

#### Venue Service

```
GET /venue-service/venues/{venue_id}/opening-hours
```

Response:

```json
{
  "openingHours": {
    "Monday": "09:00-22:00",
    "Tuesday": "09:00-22:00",
    "Wednesday": "Closed",
    "Thursday": "Closed",
    "Friday": "09:00-23:00",
    "Saturday": "10:00-23:00",
    "Sunday": "10:00-21:00"
  }
}
```

#### Courier Service

```
GET /courier-service/cities/{city_slug}/delivery-hours
```

Response:

```json
{
  "deliveryHours": {
    "Monday": "09:00-14:00, 17:00-00:30",
    "Tuesday": "09:00-14:00, 17:00-00:30",
    "Wednesday": "09:00-14:00, 17:00-00:30",
    "Thursday": "09:00-14:00, 17:00-00:30",
    "Friday": "09:00-14:00, 17:00-01:00",
    "Saturday": "10:00-15:00, 18:00-01:00",
    "Sunday": "11:00-21:00"
  }
}
```

### Performance Requirements

- Make concurrent calls to both external services (don't call them sequentially)
- Implement caching with Redis (5-minute TTL)
- Implement circuit breakers to prevent cascading failures
- Target response time: < 300ms for cached responses, < 500ms for fresh data

### Error Handling

- Return meaningful error responses for various failure scenarios
- Implement graceful degradation when one service is unavailable
- Use appropriate HTTP status codes
- Log errors appropriately for debugging

## Implementation Details

### Technology Stack (Already Configured)

- **Java 17** with Spring Boot 3.2
- **Spring Web** for REST endpoints
- **Spring WebFlux** for reactive HTTP clients
- **Spring Data Redis** for caching
- **Resilience4j** for circuit breakers
- **Jackson** for JSON processing
- **JUnit 5** and **TestContainers** for testing

### Key Spring Boot Features to Use

- `@RestController` for API endpoints
- `@Service` for business logic
- `@Component` for adapters
- `@Cacheable` for Redis caching
- `@CircuitBreaker` for resilience
- Configuration properties for external service URLs

### Implementation

Your implementation should go in the `src/main/java/com/deliverywindow/deliveryservice` package. Organize your code as you see fit for maintainability and testability.

## Getting Started

### Prerequisites

- Java 17+
- Maven 3.6+
- Docker and Docker Compose

### Development Setup

#### Option 1: Full Docker Setup (Recommended for testing)

1. **Start all services (including placeholder app):**

   ```bash
   make docker-up
   ```

2. **Test the placeholder endpoint:**

   ```bash
   curl "http://localhost:8080/delivery-hours?city_slug=berlin&venue_id=123"
   ```

#### Option 2: Local Development with External Dependencies

1. **Start only external services:**

   ```bash
   make docker-up-deps
   ```

2. **Run your implementation locally (develop using Docker):**

   ```bash
   make docker-up-deps  # Start dependencies
   # Then develop and test locally
   ```

#### Available Make Commands

- `make help` - Show all available commands
- `make docker-up` - Start all services (app + dependencies)
- `make docker-up-deps` - Start only Redis and WireMock
- `make docker-down` - Stop all services
- `make docker-logs` - View logs from all services
- `make docker-shell` - Enter the running application container

### Testing Your Implementation

The project includes WireMock mappings for testing. Test data includes:

**Venues:**

- `123`: Regular hours (09:00-22:00 most days)
- `456`: Complex schedule with overnight hours
- `789`: Short hours (13:00-15:00) - good for testing 30-min rule
- `999`: Returns 404 (venue not found)
- `500`: Returns 500 (server error)

**Cities:**

- `berlin`: Full delivery coverage
- `london`: Afternoon/evening delivery only
- `paris`: Morning delivery only (good for testing intersections)
- `madrid`: Very short delivery window (14:31-16:00)
- `unknown`: Returns 404 (city not found)
- `error`: Returns 500 (server error)

### Example Test Scenarios

Test these combinations to verify your business logic:

1. **Normal intersection:** `venue_id=123&city_slug=berlin`
2. **No intersection:** `venue_id=789&city_slug=paris`
3. **Less than 30 minutes:** `venue_id=789&city_slug=madrid`
4. **Overnight windows:** `venue_id=456&city_slug=berlin`
5. **Error scenarios:** `venue_id=999` or `city_slug=unknown`
