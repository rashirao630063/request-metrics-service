package com.space.store;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

@Component
public class RequestStore { //Thread-safe in-memory map that stores unique IDs per minute//
	    private final ConcurrentHashMap<String, Set<String>> minuteToIds = new ConcurrentHashMap<>();
	     private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
	      public void recordId(String id, LocalDateTime time) {  //No duplicates are counted within the same minute//
	        String key = minuteKey(time);
	        minuteToIds
	                .computeIfAbsent(key, k -> ConcurrentHashMap.newKeySet())
	                .add(id);
	    }

	    public Set<String> getAndRemoveMinute(String minuteKey) {
	        return minuteToIds.remove(minuteKey); // returns set for that minute, removing it
	    }
	    public String minuteKey(LocalDateTime t) {
	        return t.truncatedTo(ChronoUnit.MINUTES).format(FORMATTER);
	    }
	}
