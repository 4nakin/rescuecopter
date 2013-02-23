package com.siondream.rescue;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.sionengine.Globals;
import com.badlogic.gdx.sionengine.SionEngine;
import com.badlogic.gdx.sionengine.entity.Entity;
import com.badlogic.gdx.sionengine.entity.EntitySystem;
import com.badlogic.gdx.sionengine.entity.EntityWorld;
import com.badlogic.gdx.sionengine.entity.components.AnimatedSprite;
import com.badlogic.gdx.sionengine.entity.components.Physics;
import com.badlogic.gdx.sionengine.entity.components.State;
import com.badlogic.gdx.sionengine.entity.components.Transform;

public class PlayerController extends EntitySystem {

	private float m_recoveryTime = SionEngine.getSettings().getFloat("g_recoveryTime", 1.5f);
	private float m_recoveryTimer = m_recoveryTime;
	
	public PlayerController(EntityWorld world, int priority, int loggingLevel) {
		super(world, priority, loggingLevel);
		
		m_aspect.addToAll(Transform.class);
		m_aspect.addToAll(AnimatedSprite.class);
		m_aspect.addToAll(Physics.class);
		m_aspect.addToAll(SpaceShip.class);
	}

	@Override
	protected void process(Entity e) {
		
		// Ship movement
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
		
		// Ship state
		State state = e.getComponent(State.class);
		
		if (state.get() == GameGlobals.state_hit) {
			m_recoveryTimer -= Gdx.graphics.getDeltaTime();
			
			if (m_recoveryTimer < 0.0f) {
				state.set(Globals.state_idle);
			}
		}
	}
	
	@Override
	public String toString() {
		return "PlayerController";
	}
}
