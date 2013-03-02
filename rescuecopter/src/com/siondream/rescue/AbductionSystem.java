package com.siondream.rescue;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.sionengine.Globals;
import com.badlogic.gdx.sionengine.Settings;
import com.badlogic.gdx.sionengine.SionEngine;
import com.badlogic.gdx.sionengine.entity.Entity;
import com.badlogic.gdx.sionengine.entity.EntitySystem;
import com.badlogic.gdx.sionengine.entity.components.AnimatedSprite;
import com.badlogic.gdx.sionengine.entity.components.Physics;
import com.badlogic.gdx.sionengine.entity.components.State;
import com.badlogic.gdx.sionengine.entity.components.Transform;
import com.badlogic.gdx.sionengine.entity.managers.TagManager;

public class AbductionSystem extends EntitySystem {
	
	private Vector3 m_spaceShipPos = null;
	private Vector3 m_astronautPos = new Vector3();
	private Vector3 m_shipToAstronaut = new Vector3();
	private Vector3 m_down = new Vector3();
	private Vector2 m_force = new Vector2();
	private float m_abductionRadius;
	private float m_abductionCloseRadius;
	private float m_abductionForce;
	private float m_abductionAngle;
	private float m_maxAstronautSpeed;
	
	public AbductionSystem(int priority) {
		super(priority);
		
		Settings settings = SionEngine.getSettings();
		
		m_logger.setLevel(settings.getInt("abduction.log", 1));
		m_logger.info("initializing");
		
		m_aspect.addToAll(State.class);
		m_aspect.addToAll(Transform.class);
		m_aspect.addToAll(Abductable.class);
		m_aspect.addToAll(AnimatedSprite.class);
		
		m_abductionRadius = settings.getFloat("abduction.radius", 10.0f);
		m_abductionCloseRadius = settings.getFloat("abduction.close", 3.5f);
		m_abductionForce = settings.getFloat("abduction.force", 150.0f);
		m_abductionAngle = MathUtils.cos(MathUtils.degRad * settings.getFloat("abduction.angle", 90.0f) * 0.5f);
		m_maxAstronautSpeed = settings.getFloat("abduction.maxSpeed", 2.0f);
		
		m_down.y = -1.0f;
	}

	@Override
	public void begin() {
		Entity spaceShip = m_world.getManager(TagManager.class).getEntity(GameGlobals.type_spaceship);
		
		if (spaceShip != null) {
			m_spaceShipPos = spaceShip.getComponent(Transform.class).getPosition();
		}
	}
	
	@Override
	protected void process(Entity e) {
		Physics physics = e.getComponent(Physics.class);
		
		if (!physics.isLoaded()) {
			return;
		}
		
		Body body = physics.getBody();
		Abductable abductable = e.getComponent(Abductable.class);
		Vector3 position = e.getComponent(Transform.class).getPosition();
		m_astronautPos.x = position.x;
		m_astronautPos.y = position.y;
		
		// Abduction force
		if (canAbduct()) {
			abductable.abduct(Gdx.graphics.getDeltaTime());
			
			if (abductable.getAbductionTimer() < 0.0f) {
				State state = e.getComponent(State.class);
				state.set(Globals.state_erase);
			}
			
			if (!inCloseRadius()) {
				m_force.x = m_spaceShipPos.x;
				m_force.y = m_spaceShipPos.y;
				m_force.sub(m_astronautPos.x, m_astronautPos.y);
				m_force.nor();
				m_force.mul(m_abductionForce);
				body.applyForceToCenter(m_force);
			}
		}
		else {
			abductable.ceaseAbduction();
		}
		
		// Abduction color
		AnimatedSprite sprite = e.getComponent(AnimatedSprite.class);
		Color tint = sprite.getColor();
		tint.a = Math.max(abductable.getAbductionTimer(), 0.0f) / Abductable.getAbductionTime();
		sprite.setColor(tint);
		
		// Clamp max speed
		Vector2 velocity = body.getLinearVelocity();
		
		if (velocity.len2() > m_maxAstronautSpeed * m_maxAstronautSpeed) {
			velocity.nor();
			velocity.mul(m_maxAstronautSpeed);
			body.setLinearVelocity(velocity);
		}
	}
	
	@Override
	public String toString() {
		return "AbductionSystem";
	}
	
	private boolean canAbduct() {
		m_shipToAstronaut.x = m_astronautPos.x;
		m_shipToAstronaut.y = m_astronautPos.y;
		m_shipToAstronaut.sub(m_spaceShipPos);
		
		if (m_shipToAstronaut.len2() > m_abductionRadius * m_abductionRadius) {
			return false;
		}
		
		m_shipToAstronaut.nor();
		
		float dot = m_shipToAstronaut.dot(m_down);
		
		return dot > m_abductionAngle;
	}
	
	private boolean inCloseRadius() {
		m_shipToAstronaut.x = m_astronautPos.x;
		m_shipToAstronaut.y = m_astronautPos.y;
		m_shipToAstronaut.sub(m_spaceShipPos);
		
		return (m_shipToAstronaut.len2() < m_abductionCloseRadius * m_abductionCloseRadius);
	}
}
