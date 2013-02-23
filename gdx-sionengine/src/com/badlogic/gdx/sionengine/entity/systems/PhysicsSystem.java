package com.badlogic.gdx.sionengine.entity.systems;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.sionengine.Settings;
import com.badlogic.gdx.sionengine.SionEngine;
import com.badlogic.gdx.sionengine.entity.Entity;
import com.badlogic.gdx.sionengine.entity.EntitySystem;
import com.badlogic.gdx.sionengine.entity.EntityWorld;
import com.badlogic.gdx.sionengine.entity.components.Physics;
import com.badlogic.gdx.sionengine.entity.components.Transform;
import com.badlogic.gdx.sionengine.entity.components.Type;
import com.badlogic.gdx.utils.ObjectMap;

public class PhysicsSystem extends EntitySystem implements ContactListener {

	public interface CollisionHandler {
		void beginContact(Contact contact, Entity entityA, Entity entityB);
		void endContact(Contact contact, Entity entityA, Entity entityB);
		void preSolve(Contact contact, Manifold manifold, Entity entityA, Entity entityB);
		void postSolve(Contact contact, ContactImpulse contactImpulse, Entity entityA, Entity entityB);
	}	
	
	private World m_box2DWorld;
	private Box2DDebugRenderer m_box2DRenderer;
	private OrthographicCamera m_camera;
	private int m_velocityIterations;
	private int m_positionIterations;
	private ObjectMap<Integer, ObjectMap<Integer, CollisionHandler>> m_handlers;
	
	public PhysicsSystem(EntityWorld world,
						 int priority,
						 int loggingLevel,
						 World box2DWorld,
						 OrthographicCamera camera) {
		
		super(world, priority, loggingLevel);
		m_logger.info("initializing");
		m_aspect.addToAll(Physics.class);
		m_box2DWorld = box2DWorld;
		m_camera = camera;
		m_handlers = new ObjectMap<Integer, ObjectMap<Integer, CollisionHandler>>();
		
		Settings settings = SionEngine.getSettings();
		m_box2DRenderer = new Box2DDebugRenderer(settings.getBoolean("drawBodies", true),
											     settings.getBoolean("drawJoints", true),
												 settings.getBoolean("drawAABBs", true),
												 settings.getBoolean("drawInactiveBodies", true),
												 settings.getBoolean("drawVelocities", true));
		
		m_velocityIterations = settings.getInt("velocityIterations", 6);
		m_positionIterations = settings.getInt("positionIterations", 2);
		
		m_box2DWorld.setContactListener(this);
	}
	
	public void addCollisionHandler(int typeA, int typeB, CollisionHandler handler) {
		addCollisionHandlerAux(typeA, typeB, handler);
		addCollisionHandlerAux(typeB, typeA, handler);
	}

	@Override
	public void begin() {
		m_box2DWorld.step(Gdx.graphics.getDeltaTime(), m_velocityIterations, m_positionIterations);
		m_box2DWorld.clearForces();
	}
	
	@Override
	public void end() {
		m_box2DRenderer.render(m_box2DWorld, m_camera.combined);
	}
	
	@Override
	protected void process(Entity e) {
		Transform transform = (Transform)e.getComponent(Transform.class);
		Physics physics = (Physics)e.getComponent(Physics.class);
		
		if (transform != null && physics.isLoaded()) {
			Body body = physics.getBody();
			Vector2 position2D = body.getPosition();
			Vector3 position = transform.getPosition();
			
			if (body.isActive()) {
				position.x = position2D.x;
				position.y = position2D.y;
				transform.setRotation(body.getAngle());
			}
			else {
				position2D.x = position.x;
				position2D.y = position.y;
				body.setTransform(position2D,
								  transform.getRotation());
				
				if (physics.getWakeUp()) {
					body.setActive(true);
					physics.setWakeUp(false);
				}
			}
		}
	}

	@Override
	public void beginContact(Contact contact) {
		CollisionHandler handler = getCollisionHandler(contact);
		
		if (handler != null) {
			Entity entityA = getEntity(contact, EntityIdx.A);
			Entity entityB = getEntity(contact, EntityIdx.B);
			handler.beginContact(contact, entityA, entityB);
		}
	}

	@Override
	public void endContact(Contact contact) {
		CollisionHandler handler = getCollisionHandler(contact);
		
		if (handler != null) {
			Entity entityA = getEntity(contact, EntityIdx.A);
			Entity entityB = getEntity(contact, EntityIdx.B);
			handler.endContact(contact, entityA, entityB);
		}
	}

	@Override
	public void postSolve(Contact contact, ContactImpulse contactImpulse) {
		CollisionHandler handler = getCollisionHandler(contact);
		
		if (handler != null) {
			Entity entityA = getEntity(contact, EntityIdx.A);
			Entity entityB = getEntity(contact, EntityIdx.B);
			handler.postSolve(contact, contactImpulse, entityA, entityB);
		}
	}

	@Override
	public void preSolve(Contact contact, Manifold manifold) {
		CollisionHandler handler = getCollisionHandler(contact);
		
		if (handler != null) {
			Entity entityA = getEntity(contact, EntityIdx.A);
			Entity entityB = getEntity(contact, EntityIdx.B);
			handler.preSolve(contact, manifold, entityA, entityB);
		}
	}
	
	@Override
	public String toString() {
		return "PhysicsSystem";
	}
	
	private CollisionHandler getCollisionHandler(Contact contact) {
		int typeA = 0;
		int typeB = 0;
		Object dataA = contact.getFixtureA().getBody().getUserData();
		Object dataB = contact.getFixtureB().getBody().getUserData();
		
		if (dataA instanceof Entity) {
			Entity entityA = (Entity)dataA;
			Type typeAComponent = (Type)entityA.getComponent(Type.class);
			typeA = (typeAComponent != null)? typeAComponent.get() : 0;
		}
		
		if (dataB instanceof Entity) {
			Entity entityB = (Entity)dataB;
			Type typeBComponent = (Type)entityB.getComponent(Type.class);
			typeB = (typeBComponent != null)? typeBComponent.get() : 0;
		}
		
		
		ObjectMap<Integer, CollisionHandler> collection = m_handlers.get(typeA);
		
		if (collection == null) {
			return null;
		}
		
		return collection.get(typeB);
	}
	
	private void addCollisionHandlerAux(Integer typeA, Integer typeB, CollisionHandler handler) {
		ObjectMap<Integer, CollisionHandler> handlers = m_handlers.get(typeA);
		
		if (handlers == null) {
			handlers = new ObjectMap<Integer, CollisionHandler>();
			m_handlers.put(typeA, handlers);
		}
		
		handlers.put(typeB, handler);
	}
	
	private Entity getEntity(Contact contact, EntityIdx idx) {
		Fixture fixture = idx == EntityIdx.A? contact.getFixtureA() : contact.getFixtureB();
		Object data = fixture.getBody().getUserData();
		
		if (data instanceof Entity) {
			return (Entity)data;
		}
		
		return null;
	}
	
	private enum EntityIdx {
		A,
		B
	};
}
