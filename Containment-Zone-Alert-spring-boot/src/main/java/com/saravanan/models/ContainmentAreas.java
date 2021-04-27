package com.saravanan.models;


import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.util.GeometricShapeFactory;


@Entity
public class ContainmentAreas {
  
	@Id
	private Long cId;
	
	private Point location;
	private String address; //we cant leave this field, if we reverse-geocode the latlng
	
	private Polygon boundaries;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id")
	private User addedby;
	
	private LocalDate adddedDate;
	
	public ContainmentAreas() {}

	public ContainmentAreas(Long cId, Point location, String address, Polygon boundaries, User addedby,
			LocalDate adddedDate) {
		super();
		this.cId = cId;
		this.location = location;
		this.address = address;
		this.boundaries = boundaries;
		this.addedby = addedby;
		this.adddedDate = adddedDate;
	}

	public Long getcId() {
		return cId;
	}

	public void setcId(Long cId) {
		this.cId = cId;
	}

	public Point getLocation() {
		return location;
	}

	public void setLocation(Point location) {
		this.location = location;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Polygon getBoundaries() {
		return boundaries;
	}

	public void setBoundaries(Polygon boundaries) {
		this.boundaries = boundaries;
	}
	

	public User getAddedby() {
		return addedby;
	}

	public void setAddedby(User addedby) {
		this.addedby = addedby;
	}

	public LocalDate getAdddedDate() {
		return adddedDate;
	}

	public void setAdddedDate(LocalDate adddedDate) {
		this.adddedDate = adddedDate;
	}

  
	
	

	
}
