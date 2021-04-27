package com.saravanan.services;


import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.saravanan.models.ContainmentAreas;
import com.saravanan.models.User;
import com.saravanan.repositories.ContainmentZoneRepository;
import com.saravanan.repositories.UserRepository;
@Service
public class LocationService {
    @Autowired
    private ContainmentZoneRepository cRepo;
    @Autowired
    private  UserRepository userRepo;
    
	public Page<ContainmentAreas> findContainmentZonePaginated(int pageNo,int pageSize){
		Pageable pageable = PageRequest.of(pageNo-1, pageSize);
		return cRepo.findAll(pageable);
	}
	public boolean isLocationAdded(double latitue,double longitude) {
		Optional<ContainmentAreas> cZone = cRepo.findCZoneWithLatLng(latitue, longitude);
		return cZone.isPresent();
	}
	 public Long getCurrentUserId() {
		 Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
         long id = -1L;
         System.out.println("Principal :"+principal);
		 String email = ((UserDetails)principal).getUsername();
		 System.out.println("User:"+email);
		 Optional<User> user = userRepo.findByEmail(email);
		 user.orElseThrow();
		 return user.get().getId();
	 }
	
} 
