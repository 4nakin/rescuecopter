package com.siondream.rescue;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
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

	private Vector3 m_touchPos = new Vector3();
	private float m_recoveryTime = SionEngine.getSettings().getFloat("playerController.recoveryTime", 1.5f);
	private float m_recoveryTimer = m_recoveryTime;
	private float m_maxSpeed = SionEngine.getSettings().getFloat("playerController.maxSpeed", 10.0f);
	
	public PlayerController(int priority) {
		super(priority);
		
		m_logger.setLevel(SionEngine.getSettings().getInt("playerController.log", 1));
		m_logger.info("initializing");
		
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
			Vector3 shipPos = e.getComponent(Transform.class).getPosition();
			m_touchPos.x = Gdx.input.getX();
			m_touchPos.y = Gdx.input.getY();
			OrthographicCamera camera = SionEngine.getCamera();
			camera.unproject(m_touchPos);
			
			if (m_touchPos.x > shipPos.x) {
				body.applyForceToCenter(55.0f, 0.0f);
			}
			else if (m_touchPos.x < shipPos.x) {
				body.applyForceToCenter(-55.0f, 0.0f);
			}
			
			if (m_touchPos.y > shipPos.y) {
				body.applyForceToCenter(0.0f, 100.0f);
			}
		}
		
		Vector2 velocity = body.getLinearVelocity();
		
		if (velocity.len2() > m_maxSpeed * m_maxSpeed) {
			velocity.nor();
			velocity.mul(m_maxSpeed);
			body.setLinearVelocity(velocity);
		}
		
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
