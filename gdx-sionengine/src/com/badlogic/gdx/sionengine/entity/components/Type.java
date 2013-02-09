package com.badlogic.gdx.sionengine.entity.components;

import com.badlogic.gdx.sionengine.Globals;
import com.badlogic.gdx.sionengine.SionEngine;
import com.badlogic.gdx.sionengine.entity.Component;

public class Type extends Component {
	int m_type = Globals.type_invalid;;

	public int get() {
		return m_type;
	}
	
	public void set(int type) {
		m_type = type;
	}
	
	@Override
	public void reset() {
		m_type = Globals.type_invalid;
	}
	
	@Override
	public String toString() {
		return "type (" + SionEngine.getIDGenerator().getString(m_type) + ")";
	}
}
