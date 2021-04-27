package com.saravanan.deserializers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.locationtech.jts.geom.Point;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.saravanan.models.OpenStreetMapResponse;
import com.saravanan.util.GeometryUtil;

public class OpenStreetResponseDeserializer extends StdDeserializer<OpenStreetMapResponse> {
	public OpenStreetResponseDeserializer() {
        this(null);
    }

    public OpenStreetResponseDeserializer(Class<OpenStreetMapResponse> vc) {
        super(vc);
    }

	@Override
	public OpenStreetMapResponse deserialize(JsonParser p, DeserializationContext ctxt)
			throws IOException, JsonProcessingException {
		/* 
		 [
		 {
		  'display_name': ....,
		  key : value,
		  "geojson" : {
		   "type": "polygon",
		   "coordinates" : [
		     [
			     [ lng1,lat1],
			     [ lng2,lat2],
			     [ lng3,lat3]
		     ],
		     [
		       [ lng1,lat1],
			     [ lng2,lat2],
			     [ lng3,lat3]
		     ]
		  ] 
		 }
		 ]
		 */
		//https://nominatim.openstreetmap.org/search.php?q=chennai&polygon_geojson=2&format=json&limit=1
		OpenStreetMapResponse response = new OpenStreetMapResponse();

		System.out.println("Parsing....");
		ArrayNode rootArrNode = p.getCodec().readTree(p);
		JsonNode jsNode = rootArrNode.get(0);
		/* raises :java.lang.IllegalArgumentException: Points of LinearRing do not form a closed linestring
        because sometimes coordinates are not complete polygons
		 */
		JsonNode geoJson =  jsNode.get("geojson");
		String type = geoJson.get("type").asText();
		List<Point> boundaries = new ArrayList<Point>();

		if(type.equals("Polygon")) {
			Iterator<JsonNode> coords =geoJson.withArray("coordinates").elements();
			//I just need the first polygon
			System.out.println("Its a polygon");
			if(coords.hasNext()){
				Iterator<JsonNode> coordinatesArea = coords.next().elements();
				while(coordinatesArea.hasNext()) {
					JsonNode latlng = coordinatesArea.next();
					double lng = latlng.get(0).asDouble();
	                double lat = latlng.get(1).asDouble();
					Point eachPoint= GeometryUtil.parseLocation(lat, lng);
					boundaries.add(eachPoint);
					System.out.println("Points:"+eachPoint);
				}
			}
			//13.1057138 80.1401875
		}else {
			System.out.println("Its not a polygon It is a "+type);

			JsonNode boundingBox = jsNode.withArray("boundingbox");
			double topLeft_lat = boundingBox.get(0).asDouble();  //y1
			double topLeft_lng = boundingBox.get(2).asDouble();  //x1
			double bottomRight_lat = boundingBox.get(1).asDouble(); //y2
			double bottomRight_lng = boundingBox.get(3).asDouble(); //x2
			
			//topright ==> (x2,y1) 
			double topRight_lat =  topLeft_lat;
			double topRight_lng = bottomRight_lng;
			
			//bottomLeft ==> (x1,y2)
			double bottomLeft_lat = bottomRight_lat;
			double bottomLeft_lng = topLeft_lng;
			Point topLeft = GeometryUtil.parseLocation(topLeft_lat, topLeft_lng);
			Point bottomRight = GeometryUtil.parseLocation(bottomRight_lat, bottomRight_lng);
			Point topRight = GeometryUtil.parseLocation(topRight_lat, topRight_lng);
			Point bottomLeft = GeometryUtil.parseLocation(bottomLeft_lat, bottomLeft_lng);

			boundaries.add(topLeft);
			boundaries.add(topRight);
			boundaries.add(bottomRight);
			boundaries.add(bottomLeft);
			boundaries.add(topLeft); //add point1 again to complete the polygonx
		}
		
		/*
		 "boundingbox: [
		        lng1,
		        lng2,
		        lat1,
		        lat2
		    ]
		 */
		
	    response.setId(jsNode.get("place_id").asLong());
		response.setDisplayName(jsNode.get("display_name").asText());
		response.setBounds(boundaries);

		System.out.println("Display name:"+response.getDisplayName());

		return response;
	}
	

}
