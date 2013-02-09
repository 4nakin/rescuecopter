package com.badlogic.gdx.sionengine.entity.components;

import com.badlogic.gdx.sionengine.Globals;
import com.badlogic.gdx.sionengine.SionEngine;
import com.badlogic.gdx.sionengine.entity.Component;

public class State extends Component {
	int m_state = Globals.state_invalid;;

	public int get() {
		return m_state;
	}
	
	public void set(int state) {
		m_state = state;
	}
	
	@Override
	public void reset() {
		m_state = Globals.state_invalid;
	}
	
	@Override
	public String toString() {
		return "state (" + SionEngine.getIDGenerator().getString(m_state) + ")";
	}
}
