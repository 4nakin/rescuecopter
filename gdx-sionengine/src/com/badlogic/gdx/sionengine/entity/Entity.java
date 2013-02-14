package com.badlogic.gdx.sionengine.entity;

import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectMap.Values;
import com.badlogic.gdx.utils.Pool.Poolable;

public class Entity implements Poolable {
	EntityWorld m_world;
	int m_id;
	ObjectMap<Class<? extends Component>, Component> m_components;
	
	Entity(EntityWorld world, int id, int numComponents) {
		m_world = world;
		m_id = id;
		m_components = new ObjectMap<Class<? extends Component>, Component>(numComponents);
	}
	
	public int getID() {
		return m_id;
	}
	
	public void addComponent(Component c) {
		if (c == null) return;
		m_components.put(c.getClass(), c);
	}
	
	@SuppressWarnings("unchecked")
	public <T extends Component> T getComponent(Class<T> c) {
		return (T) m_components.get(c);
	}
	
	public boolean hasComponent(Class<? extends Component> c) {
		return m_components.containsKey(c);
	}
	
	public Values<Component> getComponents() {
		return m_components.values();
	}

	@Override
	public void reset() {
		Values<Component> values = getComponents();
		
		while(values.hasNext()) {
			m_world.freeComponent(values.next());
		}
		
		m_components.clear();
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Entity)) return false;
		Entity e = (Entity)o;
		return m_id == e.m_id;
	}
	
	@Override
	public int hashCode() {
		return m_id;
	}
	
	@Override
	public String toString() {
		return "entity(" + m_id + ")";
	}
}
