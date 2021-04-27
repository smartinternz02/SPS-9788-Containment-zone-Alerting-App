package com.saravanan.controllers;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.geolatte.geom.M;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import com.saravanan.MyUserDetailService;
import com.saravanan.models.AuthResponse;
import com.saravanan.models.AuthenticationResponse;
import com.saravanan.models.CZoneForm;
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

import antlr.Utils;

@Controller
public class AdminController {

	@Autowired
	private ContainmentZoneRepository cZoneRepo;

	@Autowired
	private LocationService locService;

	@Autowired
	private WebClient.Builder webClientBuilder;

	@RequestMapping(value = "/home", method = { RequestMethod.GET, RequestMethod.POST })
	public String getHome(Model model) {
		return findPaginatedZone(1, model);
	}

	@GetMapping("/cZone/{pageNo}")
	public String findPaginatedZone(@PathVariable(value = "pageNo") int pageNo, Model model) {

		int pageSize = 10;

		Page<ContainmentAreas> page = locService.findContainmentZonePaginated(pageNo, pageSize);
		List<ContainmentAreas> cAreas = page.getContent();
		model.addAttribute("currentPage", pageNo);
		model.addAttribute("totalPages", page.getTotalPages());
		model.addAttribute("totalItems", page.getTotalElements());
		model.addAttribute("listCAreas", cAreas);
		for(ContainmentAreas c: cAreas) {
			System.out.println(c.getAddress());
		}
		return "index";
	}

	@GetMapping("/cZone/add")
	public String getAddCZone(Model model, @RequestParam(name = "error", required = false) Boolean eror) {

		model.addAttribute("form", new CZoneForm());

		return "addPlace";

	}

	@PostMapping("/cZone/add")
	public String postAddCZone(@ModelAttribute("form") CZoneForm form) {

		System.out.println(form.getLatitude() + " " + form.getLongitude());
		double lat = form.getLatitude();
		double lng = form.getLongitude();
		String address = form.getAddress();

		WebClient webClient = webClientBuilder.build();

		try {
			OpenStreetMapResponse res = webClient.get()
					.uri(uriBuilder -> uriBuilder.scheme("https").host("nominatim.openstreetmap.org").path("search.php")
							.queryParam("q", address).queryParam("polygon_geojson", 1).queryParam("format", "json")
							.queryParam("limit", 1).build())
					.retrieve().bodyToMono(OpenStreetMapResponse.class).block();

			ContainmentAreas cArea = new ContainmentAreas();
			cArea.setLocation(GeometryUtil.parseLocation(lat, lng));
			cArea.setBoundaries(GeometryUtil.getPolygonFromPoints(res.getBounds()));

			cArea.setAdddedDate(LocalDate.now());
			cArea.setAddress(address);
			User currUser = new User();
			currUser.setId(locService.getCurrentUserId());
			cArea.setAddedby(currUser);
			cArea.setcId(res.getId());
			cZoneRepo.save(cArea);
		} catch (Exception e) {
			e.printStackTrace();
			return "redirect:/cZone/add?error=true";
		}

		return "redirect:/cZone/1?success=true";
	}

	@PostMapping("/cZone/delete/{id}")
	public String deleteCZone(@PathVariable("id") long id) {
		try {
			cZoneRepo.deleteById(id);
		} catch (IllegalArgumentException ex) {
			return "redirect:/cZone/1?delsuccess=false";
		}
		return "redirect:/cZone/1?delsuccess=true";
	}

	@GetMapping("/error/403")
	public String error403Page() {
		return "error403page";
	}

}
