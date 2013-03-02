package com.badlogic.gdx.sionengine.entity.components;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.sionengine.SionEngine;
import com.badlogic.gdx.sionengine.entity.Component;

public class CameraComponent extends Component {

	private OrthographicCamera m_camera = new OrthographicCamera(SionEngine.getVirtualWidth(), SionEngine.getVirtualHeight());
	
	public CameraComponent() {
		reset();
	}
	
	public OrthographicCamera get() {
		return m_camera;
	}
	
	@Override
	public void reset() {
		m_camera.position.x = 0.0f;
		m_camera.position.y = 0.0f;
		m_camera.zoom = SionEngine.getUnitsPerPixel();
	}

	@Override
	public String toString() {
		return "camera " + m_camera;
	}
}
