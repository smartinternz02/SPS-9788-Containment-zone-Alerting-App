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
	 public static Point parseLocation(double lat,double lang) {
			Geometry geometry = GeometryUtil.wktToGeometry(String.format("POINT (%s %s)",lat,lang));
         return (Point)geometry;
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
		 return (Polygon)jtsShapeFactory.getGeometryFrom(polygonBuilder.build()); 
	 }
}