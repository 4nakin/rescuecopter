package com.badlogic.gdx.sionengine.entity.systems;

import java.util.Comparator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.sionengine.Settings;
import com.badlogic.gdx.sionengine.SionEngine;
import com.badlogic.gdx.sionengine.entity.Entity;
import com.badlogic.gdx.sionengine.entity.EntitySystem;
import com.badlogic.gdx.sionengine.entity.EntityWorld;
import com.badlogic.gdx.sionengine.entity.components.AnimatedSprite;
import com.badlogic.gdx.sionengine.entity.components.State;
import com.badlogic.gdx.sionengine.entity.components.Transform;
import com.badlogic.gdx.utils.Array;

public class RenderingSystem extends EntitySystem {

	private SpriteBatch m_batch;
	private OrthographicCamera m_camera;
	private ZSort m_sorter = new ZSort();
	private Array<Entity> m_sorted;
	
	public RenderingSystem(EntityWorld world,
						   int priority,
						   SpriteBatch batch,
						   OrthographicCamera camera) {
		
		super(world, priority);
		
		Settings settings = SionEngine.getSettings();
		
		m_logger.setLevel(settings.getInt("rendering.log", 1));
		m_logger.info("initializing");
		m_aspect.addToAll(Transform.class);
		m_aspect.addToAll(AnimatedSprite.class);
		m_aspect.addToAll(State.class);
		m_batch = batch;
		m_camera = camera;
		m_sorted = new Array<Entity>(settings.getInt("rendering.queueSize", 500));
	}

	@Override
	public void begin() {
		m_batch.begin();
	}
	
	@Override
	public void end() {
		m_sorted.sort(m_sorter);
		
		for (int i = 0; i < m_sorted.size; ++i) {
			AnimatedSprite anim = (AnimatedSprite)m_sorted.get(i).getComponent(AnimatedSprite.class);
			Texture texture = anim.getTexture();
			
			if (texture != null) {
				m_batch.draw(texture, anim.getVertices(), 0, AnimatedSprite.SpriteSize);
			}
		}
		
		m_sorted.clear();
		
		m_batch.end();
	}
	
	@Override
	protected void process(Entity e) {
		AnimatedSprite anim = (AnimatedSprite)e.getComponent(AnimatedSprite.class);
		State state = (State)e.getComponent(State.class);
		Transform transform = (Transform)e.getComponent(Transform.class);
		
		if (anim.isLoaded()) {
			anim.updateState(state.get());
			anim.updateFrame(Gdx.graphics.getDeltaTime());
			anim.applyTransform(transform.getPosition(), transform.getRotation(), transform.getScale());
			anim.computeVertices();
			
			if (isInFrustum(anim, transform)) {
				m_sorted.add(e);
			}
		}
	}
	
	@Override
	public String toString() {
		return "RenderingSystem";
	}
	
	private boolean isInFrustum(AnimatedSprite anim, Transform transform) {
		Vector3 position = transform.getPosition();
		Vector2 origin = anim.getOrigin();
		Vector2 size = anim.getSize();
		Vector3 cameraPos = m_camera.position;
		float halfWidth = m_camera.viewportWidth * 0.5f;
		float halfHeight = m_camera.viewportHeight * 0.5f;

		if (position.y + size.y - origin.y < cameraPos.x - halfWidth || position.x - origin.x > cameraPos.x + halfWidth) return false;
		if (position.y + size.y - origin.y < cameraPos.y - halfHeight || position.y - origin.y > cameraPos.y + halfHeight) return false;
		
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
