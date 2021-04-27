package com.saravanan.models;
import java.util.List;

import org.locationtech.jts.geom.Point;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.saravanan.deserializers.OpenStreetResponseDeserializer;

@JsonDeserialize(using = OpenStreetResponseDeserializer.class)
public class OpenStreetMapResponse {
   private Long id;
   private String displayName;
   private List<Point> bounds;
   public OpenStreetMapResponse() {}
public OpenStreetMapResponse(Long id, String displayName, List<Point> bounds) {
	super();
	this.id = id;
	this.displayName = displayName;
	this.bounds = bounds;
}
public Long getId() {
	return id;
}
public void setId(Long id) {
	this.id = id;
}
public String getDisplayName() {
	return displayName;
}
public void setDisplayName(String displayName) {
	this.displayName = displayName;
}
public List<Point> getBounds() {
	return bounds;
}
public void setBounds(List<Point> bounds) {
	this.bounds = bounds;
}
   

   
}
