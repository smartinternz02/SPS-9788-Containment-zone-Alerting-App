package com.saravanan.controllers;

import java.time.LocalDate;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.reactive.function.client.WebClient;

import com.saravanan.models.ContainmentAreas;
import com.saravanan.models.OpenStreetMapResponse;
import com.saravanan.models.User;
import com.saravanan.models.UserLocation;
import com.saravanan.repositories.ContainmentZoneRepository;
import com.saravanan.repositories.UserlocationRepository;
import com.saravanan.services.LocationService;
import com.saravanan.util.GeometryUtil;

@Controller
public class TestController {
	

	@Autowired
	private ContainmentZoneRepository cZoneRepo;
	@Autowired
	private LocationService locService;
	@Autowired
	WebClient.Builder builder;
	@Autowired
	private UserlocationRepository userLocRepo;
	
	@GetMapping("/cZone/add/test")
	@ResponseBody
	public void addCZOnetext() {
//		String dummyUrl = "https://nominatim.openstreetmap.org/search.php?q=chennai&polygon_geojson=1&format=json&limit=1";
		 String dummyUrl = "https://nominatim.openstreetmap.org/search.php?q=tuticorin&polygon_geojson=1&format=json&limit=1";
         
		 
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
	
	@GetMapping("/cZone/fetch/boundaries")
	@ResponseBody
	public String fetchBoundaries() {
		System.out.println("here");
		ContainmentAreas ca =  cZoneRepo.findById(305429575L).get();
		Coordinate[] cs = ca.getBoundaries().getCoordinates();
		for(int i=0;i<cs.length;i++ ) {
			Coordinate c = cs[i];
			System.out.println(c.getX()+" "+c.getY()+" "+c.getZ());
		}
		UserLocation userLoc = userLocRepo.findById(1L).get();
		Point p = userLoc.getLocation();
		System.out.println("User Location : X:"+p.getX()+" Y:"+p.getY()+" Srid:"+p.getSRID());
		return "Nothing to see";
	}
	
	@GetMapping("/cZone/fetch/count")
	@ResponseBody
	public String fetchcount() {
		long cId=305429575L;
		int count = cZoneRepo.findUsersCountWithinBoundary(cId);
		return String.valueOf(count);
	}
	
	@GetMapping("/cZone/test/within/{id}")
	@ResponseBody
	public String userInsideCZoneName(@RequestParam("id") long id) {
		return cZoneRepo.findCZoneWhereUserIs(id)[0];
	}
	
}
