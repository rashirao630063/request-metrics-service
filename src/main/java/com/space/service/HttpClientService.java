package com.space.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class HttpClientService {
	private static final Logger log = LoggerFactory.getLogger(HttpClientService.class);
	private final RestTemplate restTemplate;
	public HttpClientService(RestTemplate restTemplate)
	{
		this.restTemplate = restTemplate;
	}
	 public void callEndpoint(String url) {
	        try {
	            log.info("Calling external endpoint: {}", url);

	            ResponseEntity<String> response =
	                    restTemplate.getForEntity(url, String.class);

	            log.info("External call success -> {} (status: {})",
	                    url, response.getStatusCode());

	        } catch (Exception e) {
	            log.error("External call failed -> {} | Error: {}", url, e.getMessage());
	        }
	    }
	}
