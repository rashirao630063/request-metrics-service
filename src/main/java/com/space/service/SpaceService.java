package com.space.service;

import java.time.Duration;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class SpaceService {

    private final StringRedisTemplate redisTemplate;

    public SpaceService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public boolean processRequest(String id, String endpoint) {

        String key = "lock:" + id;       // Unique key per request ID

        // Try acquiring distributed lock
        Boolean acquired = redisTemplate.opsForValue()
                .setIfAbsent(key, "1", Duration.ofSeconds(20)); 

        if (acquired == null || !acquired) { // Duplicate request condition
            System.out.println(" Duplicate request ignored for ID: " + id);
            return false;
        }
        System.out.println(" Lock acquired | Processing ID: " + id);

        try {
            
            System.out.println("⏳ Working on request ID: " + id 
                                + " on instance: " + endpoint);

            Thread.sleep(3000); 

            System.out.println("✔ Completed ID: " + id + " on instance: " + endpoint);
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;

        } finally {
           
            redisTemplate.delete(key);
            System.out.println("🔓 Lock released for ID: " + id);
        }
    }
}
