package com.badlogic.gdx.sionengine.maps;

import java.util.Iterator;

import com.badlogic.gdx.utils.Array;

public class MapLayers implements Iterable<MapLayer> {
	
	private Array<MapLayer> layers = new Array<MapLayer>();

	public MapLayers() {
	
	}
	
	public MapLayer getLayer(int index) {
		return layers.get(index);
	}
	
	public MapLayer getLayer(String name) {
		for (MapLayer layer : layers) {
			if (name.equals(layer.getName())) {
				return layer;
			}
		}
		return null;
	}
	
	public void addLayer(MapLayer layer) {
		this.layers.add(layer);
	}
	
	public void removeLayer(int index) {
		layers.removeIndex(index);
	}
	
	public void removeLayer(MapLayer layer) {
		layers.removeValue(layer, true);
	}

	@Override
	public Iterator<MapLayer> iterator() {
		return layers.iterator();
	}
	
}
