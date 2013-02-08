package com.badlogic.gdx.sionengine.entity;

import java.util.Comparator;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.Logger;
import com.badlogic.gdx.utils.Pool;

@SuppressWarnings("rawtypes")
public class EntityWorld {
	
	private Logger m_logger;
	private EntityPool m_entityPool;
	private IntMap<Entity> m_entities;
	private Pool[] m_componentsPools;
	private Array<EntitySystem> m_systems;
	private EntitySystemSorter m_systemSorter;
	private int m_maxComponents;
	private int m_maxEntities;
	
	public EntityWorld(int entityPoolSize, int maxComponents, int loggingLevel) {
		m_logger = new Logger("EntityWorld", loggingLevel);
		m_logger.info("initializing");
		m_maxComponents = maxComponents;
		m_maxEntities = entityPoolSize;
		m_entityPool = new EntityPool(this, entityPoolSize);
		m_entities = new IntMap<Entity>(entityPoolSize);
		m_componentsPools = new Pool[maxComponents];
		m_systems = new Array<EntitySystem>(true, 10);
		m_systemSorter = new EntitySystemSorter();
	}
	
	public void prepare() {
		m_logger.info("preparing entity systems");
		m_systems.sort(m_systemSorter);
	}
	
	public void update() {
		for(EntitySystem system : m_systems) {
			system.begin();
			system.update();
			system.end();
		}
	}
	
	// ENTITY METHODS
	public int getMaxEntities() {
		return m_maxEntities;
	}
	
	public Entity createEntity() {
		Entity e = m_entityPool.obtain();
		m_entities.put(e.m_id, e);
		m_logger.info("created " + e);
		
		return e;
	}
	
	public void deleteEntity(Entity e) {
		m_logger.info("deleting " + e);
		
		for (EntitySystem system : m_systems) {
			system.entityDeleted(e);
		}
		
		m_entityPool.free(e);
		m_entities.remove(e.m_id);
	}
	
	public void addEntity(Entity e) {
		m_logger.info("adding " + e);
		
		for (EntitySystem system : m_systems) {
			system.entityAdded(e);
		}
	}
	
	public Entity getEntity(int id) {
		if (id < 0 || id >= m_maxEntities) {
			m_logger.error("trying to retrieve entity of invalid id " + id);
			return null;
		}
		
		return m_entities.get(id);
	}
	
	// ENTITY SYSTEMS METHODS
	public void addSystem(EntitySystem system) {
		m_logger.info("adding system " + system);
		m_systems.add(system);
	}
	
	// COMPONENT METHODS
	public void setComponentPool(Pool pool, int type) {
		if (type < 0 || type >= m_maxComponents) {
			m_logger.error("trying to set component pool for invalid type " + type);
			return;
		}
		
		m_logger.info("adding component pool " + pool);
		m_componentsPools[type] = pool;
	}
	
	public Component createComponent(int type) {
		if (type < 0 || type >= m_maxComponents) {
			m_logger.error("trying to create component of invalid type " + type);
			return null;
		}
		
		Pool pool = m_componentsPools[type];
		
		if (pool == null) {
			m_logger.error("there is no pool for component of type " + type);
			return null;
		}
		
		Component c =  (Component)pool.obtain();
		m_logger.info("created " + c);
		return c;
	}

	@SuppressWarnings("unchecked")
	void freeComponent(Component c) {
		int type = c.getType();
		
		if (type < 0 || type >= m_maxComponents) {
			m_logger.error("trying to free component of invalid type " + type);
			return;
		}
		
		m_logger.info("deleting " + c);
		
		Pool pool = m_componentsPools[c.getType()];
		
		if (pool == null) return;
		
		pool.free(c);
	}
	
	private class EntityPool extends Pool<Entity> {

		private EntityWorld m_world;
		int m_nextID;
		
		public EntityPool(EntityWorld world, int poolSize) {
			super(poolSize);
			m_world = world;
			m_nextID = 1;
		}
		
		@Override
		protected Entity newObject() {
			return new Entity(m_world, m_nextID++, m_maxComponents);
		}
	}
	
	private class EntitySystemSorter implements Comparator<EntitySystem> {

		@Override
		public int compare(EntitySystem systemA, EntitySystem systemB) {
			int priorityA = systemA.getPriority();
			int priorityB = systemB.getPriority();
			
			return priorityA - priorityB;
		}
	}
}
