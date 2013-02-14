package com.badlogic.gdx.sionengine.entity;

import java.util.Comparator;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.Logger;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Pool;

@SuppressWarnings("rawtypes")
public class EntityWorld {
	
	private Logger m_logger;
	private EntityPool m_entityPool;
	private IntMap<Entity> m_entities;
	private ObjectMap<Class<? extends Component>, Pool<? extends Component>> m_componentsPools;
	private Array<EntitySystem> m_orderedSystems;
	private ObjectMap<Class<? extends EntitySystem>, EntitySystem> m_systems;
	private EntitySystemSorter m_systemSorter;
	private int m_maxEntities;
	private int m_numComponents;
	
	public EntityWorld(int entityPoolSize, int numComponents, int loggingLevel) {
		m_logger = new Logger("EntityWorld", loggingLevel);
		m_logger.info("initializing");
		m_maxEntities = entityPoolSize;
		m_numComponents = numComponents;
		m_entityPool = new EntityPool(this, entityPoolSize);
		m_entities = new IntMap<Entity>(entityPoolSize);
		m_componentsPools = new ObjectMap<Class<? extends Component>, Pool<? extends Component>>(numComponents);
		m_orderedSystems = new Array<EntitySystem>(true, 10);
		m_systems = new ObjectMap<Class<? extends EntitySystem>, EntitySystem>();
		m_systemSorter = new EntitySystemSorter();
	}
	
	public void prepare() {
		m_logger.info("preparing entity systems");
		m_orderedSystems.sort(m_systemSorter);
	}
	
	public void update() {
		for(EntitySystem system : m_orderedSystems) {
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
		
		for (EntitySystem system : m_orderedSystems) {
			system.entityDeleted(e);
		}
		
		m_entityPool.free(e);
		m_entities.remove(e.m_id);
	}
	
	public void addEntity(Entity e) {
		m_logger.info("adding " + e);
		
		for (EntitySystem system : m_orderedSystems) {
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
		m_systems.put(system.getClass(), system);
		m_orderedSystems.add(system);
	}
	
	@SuppressWarnings("unchecked")
	public <T extends EntitySystem> T getSystem(Class<T> system) {
		return (T) m_systems.get(system);
	}
	
	// COMPONENT METHODS
	public void setComponentPool(Class<? extends Component> c, Pool<? extends Component> pool) {
		m_logger.info("adding component pool " + pool);
		m_componentsPools.put(c, pool);
	}
	
	@SuppressWarnings("unchecked")
	public <T extends Component> T createComponent(Class<? extends Component> c) {
		Pool<? extends Component> pool = m_componentsPools.get(c);
		
		if (pool == null) {
			m_logger.error("there is no pool for component of class " + c);
			return null;
		}
		
		T component =  (T)pool.obtain();
		m_logger.info("created " + component);
		return component;
	}

	@SuppressWarnings("unchecked")
	public void freeComponent(Component component) {
		m_logger.info("deleting " + component);
		
		Pool pool = m_componentsPools.get(component.getClass());
		
		if (pool == null) return;
		
		pool.free((Component)component);
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
			return new Entity(m_world, m_nextID++, m_numComponents);
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
