package com.siondream.rescue;

import com.badlogic.gdx.sionengine.SionEngine;
import com.badlogic.gdx.sionengine.entity.Component;

public class SpaceShip extends Component {
	
	private float m_energy = 0.0f;
	private float m_totalEnergy = 0.0f;
	
	public SpaceShip() {
		super();
		m_totalEnergy = SionEngine.getSettings().getFloat("g_spaceshipEnergy", 20.0f);
		reset();
	}
	
	public float getTotalEnergy() {
		return m_totalEnergy;
	}
	
	public float getEnergy() {
		return m_energy;
	}
	
	public void reduceEnergy(float energy) {
		m_energy -= energy;
	}
	
	@Override
	public void reset() {
		m_energy = m_totalEnergy;
	}
	
	@Override
	public String toString() {
		return "space ship";
	}

}
