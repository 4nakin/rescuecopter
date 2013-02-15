package com.siondream.rescue;

import com.badlogic.gdx.sionengine.entity.Component;

public class Abductable extends Component {

	static float s_abductionTime = 3.0f; 
	float m_abductionTimer = s_abductionTime;
	
	static void setAbductionTime(float abductionTime) {
		s_abductionTime = abductionTime;
	}
	
	static float getAbductionTime() {
		return s_abductionTime;
	}
	
	float getAbductionTimer() {
		return m_abductionTimer;
	}
	
	void abduct(float time) {
		m_abductionTimer -= time;
	}
	
	void ceaseAbduction() {
		m_abductionTimer = s_abductionTime;
	}
	
	@Override
	public void reset() {
		m_abductionTimer = s_abductionTime;
	}

}
