package com.saravanan.util;

import java.util.List;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import org.locationtech.spatial4j.context.jts.JtsSpatialContext;
import org.locationtech.spatial4j.context.jts.JtsSpatialContextFactory;
import org.locationtech.spatial4j.shape.Shape;
import org.locationtech.spatial4j.shape.ShapeFactory;
import org.locationtech.spatial4j.shape.jts.JtsShapeFactory;

public class GeometryUtil {
    
	public static final int SRID = 4326; //LatLng
	private static WKTReader wktReader = new WKTReader();
	
	private static Geometry wktToGeometry(String wellKnownText) {
		Geometry geometry = null;
		
		try {
			geometry = wktReader.read(wellKnownText);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		System.out.println("###geometry :"+geometry);
		return geometry;
	}
	 public static Point parseLocation(double lat,double lng) {
			Geometry geometry = GeometryUtil.wktToGeometry(String.format("POINT (%s %s)",lng,lat));
			Point p =(Point)geometry;
			p.setSRID(4326);
         return p;
	 }
	 
	 public static Polygon getPolygonFromPoints(List<Point> points) {
		 JtsSpatialContextFactory jtsSpatialContextFactory = new JtsSpatialContextFactory();
		 JtsSpatialContext jtsSpatialContext = jtsSpatialContextFactory.newSpatialContext();
		 JtsShapeFactory jtsShapeFactory = jtsSpatialContext.getShapeFactory();
		 ShapeFactory.PolygonBuilder polygonBuilder = jtsShapeFactory.polygon();
		 
		 for(Point p : points) {
			 polygonBuilder.pointXY(p.getX(), p.getY());
			 System.out.println(p.toString());
		 }
		 Polygon boundaries =  (Polygon)jtsShapeFactory.getGeometryFrom(polygonBuilder.build());
		 boundaries.setSRID(SRID);
		 return boundaries;
	 }
}