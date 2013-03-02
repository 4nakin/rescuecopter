package com.badlogic.gdx.sionengine.entity.systems;

import java.util.Comparator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.sionengine.Globals;
import com.badlogic.gdx.sionengine.Settings;
import com.badlogic.gdx.sionengine.SionEngine;
import com.badlogic.gdx.sionengine.entity.Entity;
import com.badlogic.gdx.sionengine.entity.EntitySystem;
import com.badlogic.gdx.sionengine.entity.components.AnimatedSprite;
import com.badlogic.gdx.sionengine.entity.components.CameraComponent;
import com.badlogic.gdx.sionengine.entity.components.Cullable;
import com.badlogic.gdx.sionengine.entity.components.State;
import com.badlogic.gdx.sionengine.entity.components.TextureComponent;
import com.badlogic.gdx.sionengine.entity.components.Transform;
import com.badlogic.gdx.utils.Array;

public class RenderingSystem extends EntitySystem {

	private SpriteBatch m_batch;
	private OrthographicCamera m_camera;
	private ZSort m_sorter = new ZSort();
	private Array<Entity> m_sorted;
	
	public RenderingSystem(int priority) {
		
		super(priority);
		
		Settings settings = SionEngine.getSettings();
		
		m_logger.setLevel(settings.getInt("rendering.log", 1));
		m_logger.info("initializing");
		m_aspect.addToAll(Transform.class);
		m_aspect.addToAny(AnimatedSprite.class);
		m_aspect.addToAny(TextureComponent.class);
		m_batch = new SpriteBatch();
		m_sorted = new Array<Entity>(settings.getInt("rendering.queueSize", 500));
	}

	@Override
	public void begin() {
		Entity cameraEntity = m_world.getEntityByTag(Globals.entity_camera);
		
		if (cameraEntity != null) {
			CameraComponent cameraComponent = cameraEntity.getComponent(CameraComponent.class);
			m_camera = cameraComponent.get();
			m_camera.update();
			m_batch.setProjectionMatrix(m_camera.combined);
		}
		
		m_batch.begin();
	}
	
	@Override
	public void end() {
		m_sorted.sort(m_sorter);
		
		for (Entity entity : m_sorted) {
			AnimatedSprite anim = entity.getComponent(AnimatedSprite.class);
			
			if (anim != null) {
				Texture texture = anim.getTexture();
				
				if (texture != null) {
					m_batch.draw(texture, anim.getVertices(), 0, AnimatedSprite.SpriteSize);
				}
				
				continue;
			}
			
			TextureComponent textComponent = entity.getComponent(TextureComponent.class);
			
			if (textComponent != null) {
				TextureRegion region = textComponent.getRegion();
				Transform transform = entity.getComponent(Transform.class);
				Vector3 position = transform.getPosition();
				float units = SionEngine.getUnitsPerPixel();
				float scale = transform.getScale() * units;
				
				m_batch.draw(region,
							 position.x,
							 position.y,
							 -textComponent.getOriginX() * units,
							 -textComponent.getOriginY() * units,
							 region.getRegionWidth(),
							 region.getRegionHeight(),
							 scale,
							 scale,
							 transform.getRotation());
				
				continue;
			}
			
		}
		
		m_sorted.clear();
		m_batch.end();
	}
	
	@Override
	public void dispose() {
		m_batch.dispose();
	}
	
	@Override
	protected void process(Entity e) {
		AnimatedSprite anim = e.getComponent(AnimatedSprite.class);
		
		if (anim != null && anim.isLoaded()) {
			State state = e.getComponent(State.class);
			Transform transform = e.getComponent(Transform.class);
			
			if (state != null) {
				anim.updateState(state.get());
			}
			
			anim.updateFrame(Gdx.graphics.getDeltaTime());
			anim.applyTransform(transform.getPosition(), transform.getRotation(), transform.getScale());
			anim.computeVertices();
			
			if (isInFustrum(anim, transform)) {
				m_sorted.add(e);
			}
			
			return;
		}
		
		TextureComponent textComponent = e.getComponent(TextureComponent.class);
		
		if (textComponent != null && textComponent.isLoaded()) {
			Transform transform = e.getComponent(Transform.class);
			
			if (isInFustrum(textComponent, transform)) {
				m_sorted.add(e);
			}
			
			return;
		}
	}
	
	@Override
	public String toString() {
		return "RenderingSystem";
	}
	
	private boolean isInFustrum(Cullable img, Transform transform) {
		
		if (m_camera == null) {
			return false;
		}
		
		Vector3 position = transform.getPosition();
		float originX = img.getOriginX();
		float originY = img.getOriginY();
		float width = img.getWidth();
		float height = img.getHeight();
		float scale = transform.getScale();
		Vector3 cameraPos = m_camera.position;
		float halfWidth = m_camera.viewportWidth * 0.5f;
		float halfHeight = m_camera.viewportHeight * 0.5f;

		if (position.x + width * scale - originX < cameraPos.x - halfWidth || position.x - originX > cameraPos.x + halfWidth) return false;
		if (position.y + height * scale - originY < cameraPos.y - halfHeight || position.y - originY > cameraPos.y + halfHeight) return false;
		
		return true;
	}

	private class ZSort implements Comparator<Entity> {

		@Override
		public int compare(Entity e1, Entity e2) {
			Transform t1 = (Transform)e1.getComponent(Transform.class);
			Transform t2 = (Transform)e2.getComponent(Transform.class);
			
			Vector3 p1 = t1.getPosition();
			Vector3 p2 = t2.getPosition();
			
			return (int)(p1.z - p2.z);
		}
	}
}
