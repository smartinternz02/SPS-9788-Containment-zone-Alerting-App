package com.saravanan.controllers;

import java.time.LocalDate;

import javax.transaction.Transactional;

import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import com.saravanan.MyUserDetailService;
import com.saravanan.models.AuthenticationResponse;
import com.saravanan.models.ContainmentAreas;
import com.saravanan.models.OpenStreetMapResponse;
import com.saravanan.models.User;
import com.saravanan.models.UserLocation;
import com.saravanan.repositories.ContainmentZoneRepository;
import com.saravanan.repositories.UserRepository;
import com.saravanan.repositories.UserlocationRepository;
import com.saravanan.services.LocationService;
import com.saravanan.util.GeometryUtil;
import com.saravanan.util.JwtUtil;
import com.saravanan.util.Util;

@RestController
public class MyRestController {
    @Autowired
    private UserRepository userRepo;
    
    @Autowired
	private UserlocationRepository userLocrepo;
	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private JwtUtil jwtTokenUtil;

	@Autowired
	private MyUserDetailService userDetailsService;
	
	@Autowired
	private ContainmentZoneRepository cZoneRepo;
	@Autowired
	private LocationService locService;
	@Autowired
	WebClient.Builder builder;
	
	@GetMapping("/hello")
	public String hellWorld() {
		return "Hello World";
	}
	@PostMapping("/register")
	@Transactional
	public Boolean register(@RequestBody User user) {
		try {
			System.out.println("register :"+user);
			user.setRole("USER");
			userRepo.save(user);
			UserLocation userLocation = new UserLocation();
			userLocation.setUser(user);
			userLocrepo.save(userLocation);
			  return true;
		}catch(Exception ex) {
			return false;
		}
	}
	
	
	@PostMapping("/update/{id}")
	public void updateLocation(@PathVariable("id") Long id,@RequestParam("lat") double lat,@RequestParam("lng") double lng) {
		Point point = GeometryUtil.parseLocation(lat, lng);
		int i =  userLocrepo.updateLocation(point.toString(),id);
		System.out.println("Is successful :"+i);
	}
	@RequestMapping(value = "/authenticate", method = RequestMethod.POST)
	public ResponseEntity<?> createAuthenticationToken(@RequestBody User authenticationRequest) throws Exception {
        System.out.println("Connection received "+authenticationRequest);

		try {
			authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(authenticationRequest.getEmail(), authenticationRequest.getPassword())
			);
		}
		catch (BadCredentialsException e) {
			throw new Exception("Incorrect username or password", e);
		}


		final UserDetails userDetails = userDetailsService
				.loadUserByUsername(authenticationRequest.getEmail());

		final String jwt = jwtTokenUtil.generateToken(userDetails);

		return ResponseEntity.ok(new AuthenticationResponse(jwt));
	}
	
}
