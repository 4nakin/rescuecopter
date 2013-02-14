package com.siondream.rescue;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.sionengine.SionEngine;
import com.badlogic.gdx.sionengine.entity.Entity;
import com.badlogic.gdx.sionengine.entity.EntitySystem;
import com.badlogic.gdx.sionengine.entity.EntityWorld;
import com.badlogic.gdx.sionengine.entity.components.AnimatedSprite;
import com.badlogic.gdx.sionengine.entity.components.Physics;
import com.badlogic.gdx.sionengine.entity.components.Transform;

public class PlayerController extends EntitySystem {

	public PlayerController(EntityWorld world, int priority, int loggingLevel) {
		super(world, priority, loggingLevel);
		
		m_aspect.addToAll(Transform.class);
		m_aspect.addToAll(AnimatedSprite.class);
		m_aspect.addToAll(Physics.class);
		m_aspect.addToAll(SpaceShip.class);
	}

	@Override
	protected void process(Entity e) {
		Physics physics = e.getComponent(Physics.class);
		
		if (!physics.isLoaded()) {
			return;
		}
		
		Body body = physics.getBody();
		
		if (Gdx.input.isTouched()) {
			if (Gdx.input.getX() > SionEngine.getVirtualWidth() * 0.85f) {
				body.applyForceToCenter(55.0f, 0.0f);
			}
			else if (Gdx.input.getX() < SionEngine.getVirtualWidth() * 0.15f) {
				body.applyForceToCenter(-55.0f, 0.0f);
			}
			
			if (Gdx.input.getY() < SionEngine.getVirtualHeight() * 0.15f) {
				body.applyForceToCenter(0.0f, 100.0f);
			}
		}
		
		Vector2 velocity = body.getLinearVelocity();
		velocity.x = Math.min(velocity.x, 5.0f);
		velocity.y = Math.min(velocity.y, 5.0f);
	}

}
