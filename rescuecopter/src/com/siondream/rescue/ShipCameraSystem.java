package com.siondream.rescue;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.equations.Quad;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.sionengine.Globals;
import com.badlogic.gdx.sionengine.SionEngine;
import com.badlogic.gdx.sionengine.entity.Entity;
import com.badlogic.gdx.sionengine.entity.EntitySystem;
import com.badlogic.gdx.sionengine.entity.components.CameraComponent;
import com.badlogic.gdx.sionengine.entity.components.Transform;
import com.badlogic.gdx.sionengine.tweeners.CameraTweener;

public class ShipCameraSystem extends EntitySystem {

	private Vector3 m_destPos = new Vector3();
	private Rectangle m_bounds = null;
	
	public ShipCameraSystem(int priority) {
		super(priority);
		
	}

	public void setBounds(Rectangle bounds) {
		m_bounds = bounds;
	}
	
	@Override
	public void begin() {
		Entity shipEntity = m_world.getEntityByTag(GameGlobals.type_spaceship);
		Entity cameraEntity = m_world.getEntityByTag(Globals.entity_camera);
		
		if (shipEntity == null || cameraEntity == null) {
			return;
		}
		
		Vector3 shipPos = shipEntity.getComponent(Transform.class).getPosition();
		OrthographicCamera camera = cameraEntity.getComponent(CameraComponent.class).get();
		
		float camWidth = SionEngine.getVirtualWidth() * SionEngine.getUnitsPerPixel();
		float camHeight = SionEngine.getVirtualHeight() * SionEngine.getUnitsPerPixel();
		float mapWidth = m_bounds.getWidth() * SionEngine.getUnitsPerPixel();
		float mapHeight = m_bounds.getHeight() * SionEngine.getUnitsPerPixel();
		
		float maxX = mapWidth - camWidth * 0.5f;
		float maxY = mapHeight - camHeight * 0.5f;
		float minX = m_bounds.getX() * SionEngine.getUnitsPerPixel() + camWidth * 0.5f;
		float minY = m_bounds.getY() * SionEngine.getUnitsPerPixel() + camHeight * 0.5f;
		
		// Check map bounds
		m_destPos.x = Math.max(Math.min(shipPos.x, maxX), minX);
		m_destPos.y = Math.max(Math.min(shipPos.y, maxY), minY);
		m_destPos.z = camera.position.z;
		
		Tween.to(camera, CameraTweener.Position, 0.1f).
				 ease(Quad.IN).
				 target(m_destPos.x, m_destPos.y, m_destPos.z).
				 start(SionEngine.getTweenManager());
	}
	
	@Override
	protected void process(Entity e) {
		
	}

	@Override
	public String toString() {
		return "ShipCameraSystem";
	}
}
