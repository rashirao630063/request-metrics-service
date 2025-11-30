package com.space.scheduler;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class AggregationScheduler {

    private static final Logger log = LoggerFactory.getLogger(AggregationScheduler.class);

    @Autowired
    private RedisTemplate<String, Object> redis;

    // runs every minute
    @Scheduled(cron = "0 * * * * *")
    public void aggregateCounts() {

        String lockKey = "aggregation-lock";
        Boolean locked = redis.opsForValue().setIfAbsent(lockKey, "LOCKED",
                Duration.ofSeconds(55)); // expire auto → avoids deadlock

        if (Boolean.FALSE.equals(locked)) {
            log.info("⏳ Another instance already processing — SKIPPING this minute");
            return; // <-- prevents double aggregation
        }
        
        try {
            String minuteKey = "minute:" + LocalDateTime.now().minusMinutes(1)
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"));

            // Fetch all unique IDs
            Set<Object> ids = redis.opsForSet().members(minuteKey);
            int count = (ids == null) ? 0 : ids.size();
            log.info("⏱ {} → {} unique ids", minuteKey, count);

            // ENDPOINT Webhooks received during the minute
            List<Object> endpoints = redis.opsForList().range(minuteKey + ":endpoints", 0, -1);

            if (endpoints != null) {
                for (Object endpoint : endpoints) {
                    try {
                        sendWebhook((String) endpoint, minuteKey, count);
                    } catch (Exception e) {
                        log.error("⚠ Failed to POST to {}", endpoint);
                    }
                }
            }

        } finally {
            redis.delete(lockKey); // always unlock
        }
    }


    //-----------------------------------------------------------
    // POST Aggregated Results to Endpoint (Extension-1)
    //-----------------------------------------------------------
    private void sendWebhook(String endpoint, String minuteKey, int count) {
        RestTemplate rest = new RestTemplate();

        Map<String, Object> body = new HashMap<>();
        body.put("minuteStart", minuteKey);
        body.put("uniqueIdCount", count);

        rest.postForObject(endpoint, body, String.class);
        log.info("📩 Posted summary → {} = {}", endpoint, body);
    }
}
