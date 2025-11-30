package com.space;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class RequestMetricsServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(RequestMetricsServiceApplication.class, args);
	}

}
