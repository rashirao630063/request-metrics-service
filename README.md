
# Request Metrics Service

This project collects incoming request IDs, stores them temporarily in Redis,
and aggregates them every minute, finally logging total unique requests.

##  Tech Stack

- Java 21
- Spring Boot 4.x
- Redis (Memurai on Windows)
- H2 Memory DB
- Spring Scheduler
- JPA + Hibernate

##  How to Run

1. Install & start Redis
   redis-server
   Start Redis Server (Memurai)
2.opem cmd as run as admin
cd "C:\Program Files\Memurai"
memurai.exe

## To run 2nd instance (if wanted for load balancer testing):

 mvn spring-boot:run -Dspring.profiles.active=2
Runs on http://localhost:8082


3 API Usage
GET /api/space/accept?id=10
GET /api/space/accept?id=10&endpoint=http://example.com   <-- external call

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
