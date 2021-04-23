package com.saravanan.models;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Fetch;

import com.vividsolutions.jts.geom.Point;
@Entity
public class ContainmentAreas {
  
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long cId;
	
	private Point location;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id")
	private User addedby;
	@Temporal(TemporalType.DATE)
	private Date adddedDate;
	
	public ContainmentAreas() {}

	public ContainmentAreas(Long cId, Point location, User addedby, Date adddedDate) {
		super();
		this.cId = cId;
		this.location = location;
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

	public User getAddedby() {
		return addedby;
	}

	public void setAddedby(User addedby) {
		this.addedby = addedby;
	}

	public Date getAdddedDate() {
		return adddedDate;
	}

	public void setAdddedDate(Date adddedDate) {
		this.adddedDate = adddedDate;
	}
	
	
	
}
