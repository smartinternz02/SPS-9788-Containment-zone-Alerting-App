package com.saravanan;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;

import com.saravanan.models.User;
import com.saravanan.models.UserLocation;
import com.saravanan.repositories.UserRepository;
import com.saravanan.repositories.UserlocationRepository;
import com.saravanan.util.GeometryUtil;

@SpringBootApplication
public class ContainmentZoneAlertApp implements CommandLineRunner {
	
	@Bean
	public WebClient.Builder getWebClientBuilder() {
		return  WebClient.builder();
	}
	
    @Autowired
    private UserlocationRepository userLocRepo;
    @Autowired
    private UserRepository userRepo;
	public static void main(String[] args) {
		SpringApplication.run(ContainmentZoneAlertApp.class, args);
				
	}
	@Override
	public void run(String... args) throws Exception {
		
//		UserLocation userLocation = new UserLocation();
//		Geometry geometry = GeometryUtil.wktToGeometry(String.format("POINT (13.0827 80.2707)"));
//
////		User user = new User(null,"user 3","user3@gmail.com","hello123","user");
////	    user = userRepo.save(user);
////		System.out.println("$$$$$$$$$$$id :"+user.getId());
//
//		userLocation.setId(1L);
//		userLocation.setLocation((Point)geometry);
//		userLocRepo.save(userLocation);
	}

}
