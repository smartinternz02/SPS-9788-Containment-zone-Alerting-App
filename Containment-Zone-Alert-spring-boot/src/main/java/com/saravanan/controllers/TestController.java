package com.saravanan.controllers;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.reactive.function.client.WebClient;

import com.saravanan.models.ContainmentAreas;
import com.saravanan.models.OpenStreetMapResponse;
import com.saravanan.models.User;
import com.saravanan.repositories.ContainmentZoneRepository;
import com.saravanan.services.LocationService;
import com.saravanan.util.GeometryUtil;

public class TestController {
	

	@Autowired
	private ContainmentZoneRepository cZoneRepo;
	@Autowired
	private LocationService locService;
	@Autowired
	WebClient.Builder builder;
	
	@GetMapping("/cZone/add/test")
	public void addCZOnetext() {
//		String dummyUrl = "https://nominatim.openstreetmap.org/search.php?q=chennai&polygon_geojson=1&format=json&limit=1";
		 String dummyUrl = "https://nominatim.openstreetmap.org/search.php?q=arumbakkam&polygon_geojson=1&format=json&limit=1";
         
		 
		 OpenStreetMapResponse res = builder.baseUrl(dummyUrl).build().get().retrieve().bodyToMono(OpenStreetMapResponse.class).block();
		
		 ContainmentAreas cZoneArea = new ContainmentAreas();
		 cZoneArea.setAdddedDate(LocalDate.now());
		 User user = new User();
		 user.setId(locService.getCurrentUserId());
		 cZoneArea.setAddedby(user);
		 cZoneArea.setAddress(res.getDisplayName());
		 cZoneArea.setBoundaries(GeometryUtil.getPolygonFromPoints(res.getBounds()));
		 cZoneArea.setcId(res.getId());
		 cZoneRepo.save(cZoneArea);
		 
	}
}
