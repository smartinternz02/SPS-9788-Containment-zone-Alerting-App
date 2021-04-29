package com.saravanan.models;


import javax.annotation.Generated;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;

import org.locationtech.jts.geom.Point;

import com.saravanan.util.GeometryUtil;


@Entity
public class UserLocation {
   
	@Id
	private Long id;

	@OneToOne(cascade = CascadeType.MERGE)
	@JoinColumn(name="id")
	@MapsId
	private User user;


	@Column(nullable = true)
	private Point location;
	
	public UserLocation() {}

	public UserLocation(Long id, User user, Point location) {
		super();
		this.id = id;
		this.location = location;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

//	public User getUser() {
//		return user;
//	}
//
//	public void setUser(User user) {
//		this.user = user;
//	}

	public Point getLocation() {
		return location;
	}

	public void setLocation(Point location) {
		this.location = location;
		location.setSRID(GeometryUtil.SRID);
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	
	
}
