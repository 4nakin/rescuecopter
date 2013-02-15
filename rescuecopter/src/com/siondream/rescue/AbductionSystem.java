package com.siondream.rescue;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.sionengine.Globals;
import com.badlogic.gdx.sionengine.SionEngine;
import com.badlogic.gdx.sionengine.entity.Entity;
import com.badlogic.gdx.sionengine.entity.EntitySystem;
import com.badlogic.gdx.sionengine.entity.EntityWorld;
import com.badlogic.gdx.sionengine.entity.components.Physics;
import com.badlogic.gdx.sionengine.entity.components.State;
import com.badlogic.gdx.sionengine.entity.components.Transform;
import com.badlogic.gdx.sionengine.entity.managers.TagManager;

public class AbductionSystem extends EntitySystem {
	
	private Vector3 m_spaceShipPos = null;
	private Vector3 m_astronautPos = new Vector3();
	private Vector2 m_force = new Vector2();
	private float m_abductionRadius = SionEngine.getSettings().getFloat("g_abductionRadius", 10.0f);
	private float m_abductionForce = SionEngine.getSettings().getFloat("g_abductionForce", 150.0f);
	
	
	public AbductionSystem(EntityWorld world, int priority, int loggingLevel) {
		super(world, priority, loggingLevel);
		m_aspect.addToAll(State.class);
		m_aspect.addToAll(Transform.class);
		m_aspect.addToAll(Abductable.class);
		
	}

	@Override
	public void begin() {
		Entity spaceShip = m_world.getManager(TagManager.class).getEntity("spaceship");
		
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
		
		if (m_astronautPos.dst2(m_spaceShipPos) < m_abductionRadius * m_abductionRadius) {
			abductable.abduct(Gdx.graphics.getDeltaTime());
			m_force.x = m_spaceShipPos.x;
			m_force.y = m_spaceShipPos.y;
			m_force.sub(m_astronautPos.x, m_astronautPos.y);
			m_force.nor();
			m_force.mul(m_abductionForce);
			body.applyForceToCenter(m_force);
			
			if (abductable.getAbductionTimer() < 0.0f) {
				State state = e.getComponent(State.class);
				state.set(Globals.state_erase);
			}
		}
		else {
			abductable.ceaseAbduction();
		}
	}
	
	@Override
	public String toString() {
		return "AbductionSystem";
	}
}
