package com.badlogic.gdx.sionengine.entity.components;

import com.badlogic.gdx.sionengine.entity.Component;

public class Asset extends Component {

	static private int s_type = generateType();
	
	@Override
	public void reset() {
		
	}

	@Override
	public int getType() {
		return s_type;
	}
	
	public static int getComponentType() {
		return s_type;
	}
	
	@Override
	public String toString() {
		return "asset";
	}
}
