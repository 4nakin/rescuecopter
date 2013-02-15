package com.badlogic.gdx.sionengine.entity;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Logger;

public abstract class EntitySystem implements EntityObserver {

	protected Array<Entity> m_entities;
	protected EntityWorld m_world;
	protected Aspect m_aspect;
	protected Logger m_logger;
	
	private int m_priority;
	
	public EntitySystem(EntityWorld world, int priority, int loggingLevel) {
		m_world = world;
		m_entities = new Array<Entity>(m_world.getMaxEntities());
		m_priority = priority;
		m_aspect = new Aspect();
		m_logger = new Logger(toString(), loggingLevel);
	}
	
	final int getPriority() {
		return m_priority;
	}
	
	public void begin() {
		
	}
	
	public void end() {
		
	}
	
	final void update() {
		for(Entity e : m_entities) {
			process(e);
		}
	}
	
	@Override
	final public void entityAdded(Entity e) {
		if (m_aspect.check(e)) {
			m_logger.info("adding entity " + e + " to " + this);
			m_entities.add(e);
		}
	}
	
	@Override
	final public void entityDeleted(Entity e) {
		if (m_entities.removeValue(e, true)) {
			m_logger.info("adding entity " + e + " to " + this);
		}
	}
	
	@Override
	public String toString() {
		return "EntitySystem (" + m_priority + ")";
	}
	
	abstract protected void process(Entity e);
}
