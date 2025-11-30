package com.space.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "minute_stats")
public class MinuteStat {
	
	@Id
    @Column(name = "minute_start")
    private String minuteStart; //ex: 2025-11-27T12:35//
	@Column(name = "unique_count")
    private long uniqueCount;

    public MinuteStat() {}

    public MinuteStat(String minuteStart, long uniqueCount) {
        this.minuteStart = minuteStart;
        this.uniqueCount = uniqueCount;
    }

    public String getMinuteStart() { return minuteStart; }
    public long getUniqueCount() { return uniqueCount; }

    public void setMinuteStart(String minuteStart) { this.minuteStart = minuteStart; }
    public void setUniqueCount(long uniqueCount) { this.uniqueCount = uniqueCount; }
	                           
		

}
