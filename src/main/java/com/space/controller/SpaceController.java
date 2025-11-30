package com.space.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.space.service.SpaceService;

@RestController
@RequestMapping("/api/space")
public class SpaceController {
	private final SpaceService spaceService;
	public SpaceController(SpaceService spaceService) {
		this.spaceService = spaceService;
	}
	@GetMapping("/accept")
	public ResponseEntity<String> accept(
	        @RequestParam("id") String id,
	        @RequestParam(value = "endpoint", required = false) String endpoint) {

	    boolean processed = spaceService.processRequest(id, endpoint);

	    return processed ?
	            ResponseEntity.ok("processed ✔") :
	            ResponseEntity.status(208).body("duplicate ignored 🚫");
	}

		
	}
	


