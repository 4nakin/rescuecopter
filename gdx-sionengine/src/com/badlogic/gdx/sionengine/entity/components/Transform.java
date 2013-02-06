package com.badlogic.gdx.sionengine.entity.components;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.sionengine.entity.Component;

public class Transform extends Component {

	static private int s_type = generateType();
	
	private Vector3 m_position = Vector3.Zero.cpy();
	private float m_rotation = 0.0f;
	private float m_scale = 1.0f;
	
	public Vector3 getPosition() {
		return m_position;
	}
	
	public void setPosition(Vector3 position) {
		m_position = position;
	}
	
	public float getRotation() {
		return m_rotation;
	}
	
	public void setRotation(float rotation) {
		m_rotation = rotation;
	}
	
	public float getScale() {
		return m_scale;
	}
	
	public void setScale(float scale) {
		m_scale = scale;
	}
	
	@Override
	public void reset() {
		m_position.x = m_position.y = m_position.z = 0.0f;
		m_rotation = 0.0f;
		m_scale = 0.0f;
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
		return "transform pos: (" + m_position.x + ", " + m_position.y + ", " + m_position.z + ") " +
						 "rot: " + m_rotation + " scale: " + m_scale;
	}
}
