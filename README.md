
# Request Metrics Service

This project collects incoming request IDs, stores them temporarily in Redis,
and aggregates them every minute, finally logging total unique requests.

##  Tech Stack

- Java 21  Core application
- Spring Boot 4.x  REST API + scheduler
- Redis (Memurai on Windows) Fast ID storage + deduplication
- H2 Memory DB  Local testing
- Spring Scheduler  1-minute aggregation loop 
- JPA + Hibernate   Persistence layer

##  How to Run
1.Start Spring Boot application
mvn spring-boot:run
Runs on: http://localhost:8082

2. Install & start Redis
   redis-server
   Start Redis Server (Memurai)
   
3.opem cmd as run as admin
  cd "C:\Program Files\Memurai"
  memurai.exe
Runs at localhost:6379
## To run 2nd instance (if wanted for load balancer testing):

 mvn spring-boot:run -Dspring.profiles.active=2
Runs on http://localhost:8082

## 3 API Usage
GET /api/space/accept?id=10
GET /api/space/accept?id=10&endpoint=http://example.com   for external

Output:
processed               # first time http://localhost:8081/api/space/accept?id=500
duplicate ignored       # second time same minute http://localhost:8081/api/space/accept?id=500


4 
 1 Process incoming GET requests
 2 Deduplicate IDs per minute using Redis SET
 3 Send external HTTP GET call when endpoint provided
 4 Store endpoint list and summary per minute
 5 Scheduler runs every minute to calculate count
 6 If endpoints exist → POST minute summary (Extension-1)
 7 Redis distributed lock used → multi-instance safe (Extension-2)

How concurrency is handled:
  Redis distributed lock + SET ensures only one node aggregates.

How per-minute deduplication works:
  I store IDs in Redis set "minute:timestamp".
  Same ID added twice is ignored because Set keeps only unique values.

How multi-instance works:
  Second instance tries SET lock but fails → so only one executes aggregation.
  This prevents double counting globally.

HTTP Error Handling:
 If GET fails, I log the failure and continue.
 POST webhook inside try-catch so it never stops scheduler.

## Per-Minute Unique ID Aggregation

A scheduler runs every minute and:
Fetches unique IDs stored in Redis → minute:yyyy-MM-dd'T'HH:mm

Counts them

Logs result like:

2025-11-30T08:10 → 152 unique IDs

Saves + POST summary if callbacks exist (Extension-1)

## Aggregation (Extension-1)
Every minute scheduler executes:
{
  "minuteStart": "2025-11-30T12:35",
  "uniqueIdCount": 154
}
If webhook was provided → a POST request is sent.
Retries are safe-handled (no infinite retry).
 ## Extension-2: Multi-Instance Support
Redis SET + setIfAbsent(lock) ensures:
1. No duplicate aggregation
2.Only one instance acts as leader
3. Horizontal scaling supported
## Repository Link
 https://github.com/rashirao630063/request-metrics-service
