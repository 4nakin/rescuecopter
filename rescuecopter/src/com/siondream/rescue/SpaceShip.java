package com.siondream.rescue;

import com.badlogic.gdx.sionengine.SionEngine;
import com.badlogic.gdx.sionengine.entity.Component;

public class SpaceShip extends Component {
	
	private float m_energy = 0.0f;
	
	public SpaceShip() {
		super();
		reset();
	}
	
	public float getEnergy() {
		return m_energy;
	}
	
	public void reduceEnergy(float energy) {
		m_energy -= energy;
	}
	
	@Override
	public void reset() {
		m_energy = SionEngine.getSettings().getFloat("g_spaceshipEnergy", 20.0f);
	}
	
	@Override
	public String toString() {
		return "space ship";
	}

}
