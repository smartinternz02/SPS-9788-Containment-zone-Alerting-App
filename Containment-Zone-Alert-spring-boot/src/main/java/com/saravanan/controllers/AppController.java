package com.saravanan.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.saravanan.MyUserDetailService;
import com.saravanan.models.AuthenticationResponse;
import com.saravanan.models.User;
import com.saravanan.repositories.UserRepository;
import com.saravanan.util.JwtUtil;

@Controller
public class AppController {
    
	@Autowired
	private UserRepository userRepo;
	
	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private JwtUtil jwtTokenUtil;

	@Autowired
	private MyUserDetailService userDetailsService;
	
	@GetMapping("/register")
	public String getRegisterPage(Model model) {
		model.addAttribute("user",new User());
		return "register";
	}
	@GetMapping("/hello")
	@ResponseBody
	public String hellWorld() {
		return "Hello World";
	}
	@PostMapping("/register")
	@ResponseBody
	public Boolean register(@RequestBody User user) {
		try {
			System.out.println("register :"+user);
			user.setRole("USER");
			userRepo.save(user);
			  return true;
		}catch(Exception ex) {
			return false;
		}
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
