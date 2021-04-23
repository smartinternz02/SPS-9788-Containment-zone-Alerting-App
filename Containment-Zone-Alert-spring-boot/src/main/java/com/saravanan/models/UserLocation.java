package com.saravanan.models;


import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;

import com.vividsolutions.jts.geom.Point;


@Entity
public class UserLocation {
   
	@Id
	private Long id;

	@OneToOne
	@JoinColumn(name="id")
	@MapsId
	private User user;
	

	private Point location;
	
	public UserLocation(User user, Point location) {
		super();
		this.user = user;
		this.location = location;
	}

	public UserLocation() {}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Point getLocation() {
		return location;
	}

	public void setLocation(Point location) {
		this.location = location;
	}
	
	
	
	
	
}
