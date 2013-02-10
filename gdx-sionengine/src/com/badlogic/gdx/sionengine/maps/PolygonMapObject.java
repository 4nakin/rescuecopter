/**
 * 
 */
package com.badlogic.gdx.sionengine.maps;

import com.badlogic.gdx.math.Polygon;

public class PolygonMapObject extends MapObject {

	private Polygon polygon;
	
	public Polygon getPolygon() {
		return polygon;
	}
	
	public void setPolygon(Polygon polygon) {
		this.polygon = polygon;
	}
	
	public PolygonMapObject() {
		
	}
	
	public PolygonMapObject(float[] vertices) {
		super();
		polygon = new Polygon(vertices);
	}
}
