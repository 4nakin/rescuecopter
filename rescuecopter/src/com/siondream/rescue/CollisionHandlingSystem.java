package com.siondream.rescue;

import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.sionengine.SionEngine;
import com.badlogic.gdx.sionengine.entity.Entity;
import com.badlogic.gdx.sionengine.entity.EntitySystem;
import com.badlogic.gdx.sionengine.entity.EntityWorld;
import com.badlogic.gdx.sionengine.entity.components.GameGlobals;
import com.badlogic.gdx.sionengine.entity.components.Physics;
import com.badlogic.gdx.sionengine.entity.components.State;
import com.badlogic.gdx.sionengine.entity.components.Type;
import com.badlogic.gdx.sionengine.entity.systems.PhysicsSystem;
import com.badlogic.gdx.sionengine.entity.systems.PhysicsSystem.CollisionHandler;

public class CollisionHandlingSystem extends EntitySystem {

	public CollisionHandlingSystem(EntityWorld world, int priority, int loggingLevel) {
		super(world, priority, loggingLevel);
		
		m_aspect.addToAll(Physics.class);
		m_aspect.addToAll(State.class);
		m_aspect.addToAll(Type.class);
		
		PhysicsSystem physics = world.getSystem(PhysicsSystem.class);
		physics.addCollisionHandler(GameGlobals.type_spaceship,
									0,
									new ShipLevelCollisionHandler());
		
	}

	@Override
	public boolean checkProcessing() {
		return false;
	}
	
	@Override
	protected void process(Entity e) {
		
	}
	
	@Override
	public String toString() {
		return "CollisionHandlingSystem";
	}
	
	private class ShipLevelCollisionHandler implements CollisionHandler {

		@Override
		public void beginContact(Entity entityA, Entity entityB) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void endContact(Entity entityA, Entity entityB) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void preSolve(Entity entityA, Entity entityB, Manifold manifold) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void postSolve(Entity entityA, Entity entityB,
				ContactImpulse contactImpulse) {
			// TODO Auto-generated method stub
			
		}
		
	}
}
