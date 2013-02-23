package com.siondream.rescue;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.sionengine.Globals;
import com.badlogic.gdx.sionengine.SionEngine;
import com.badlogic.gdx.sionengine.entity.Entity;
import com.badlogic.gdx.sionengine.entity.EntitySystem;
import com.badlogic.gdx.sionengine.entity.EntityWorld;
import com.badlogic.gdx.sionengine.entity.components.Physics;
import com.badlogic.gdx.sionengine.entity.components.State;
import com.badlogic.gdx.sionengine.entity.components.Type;
import com.badlogic.gdx.sionengine.entity.systems.PhysicsSystem;
import com.badlogic.gdx.sionengine.entity.systems.PhysicsSystem.CollisionHandler;
import com.siondream.rescue.GameScreen.ScreenState;

public class CollisionHandlingSystem extends EntitySystem {

	private RescueCopter m_game;
	
	public CollisionHandlingSystem(RescueCopter game, EntityWorld world, int priority, int loggingLevel) {
		super(world, priority, loggingLevel);
		
		m_aspect.addToAll(Physics.class);
		m_aspect.addToAll(State.class);
		m_aspect.addToAll(Type.class);
		
		m_game = game;
		
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
		public void beginContact(Contact contact, Entity entityA, Entity entityB) {
			contact.ResetRestitution();
			Entity entity = entityA != null? entityA : entityB;
			
			State state = entity.getComponent(State.class);
			
			if (state.get() != GameGlobals.state_hit) {
				SpaceShip ship = entity.getComponent(SpaceShip.class);
				ship.reduceEnergy(SionEngine.getSettings().getFloat("g_spaceShipCollisionDamage", 10.0f));
				state.set(GameGlobals.state_hit);
				m_logger.info("ship collision energy " + ship.getEnergy());
				
				if (ship.getEnergy() <= 0.0f) {
					GameScreen gameScreen = (GameScreen)m_game.getScreen("GameScreen");
					gameScreen.setState(ScreenState.Lose);
				}
			}
		}

		@Override
		public void endContact(Contact contact, Entity entityA, Entity entityB) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void preSolve(Contact contact, Manifold manifold, Entity entityA, Entity entityB) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void postSolve(Contact contact, ContactImpulse contactImpulse, Entity entityA, Entity entityB) {
			// TODO Auto-generated method stub
			
		}
		
	}
}
