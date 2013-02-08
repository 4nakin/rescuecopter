package com.badlogic.gdx.sionengine.entity;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool.Poolable;

public class Entity implements Poolable {
	EntityWorld m_world;
	int m_id;
	Component[] m_components;
	Array<Component> m_validComponents;
	
	Entity(EntityWorld world, int id, int numComponents) {
		m_world = world;
		m_id = id;
		m_components = new Component[numComponents];
		m_validComponents = new Array<Component>(numComponents);
	}
	
	public void addComponent(Component c) {
		if (c == null) return;
		m_components[c.getType()] = c;
		m_validComponents.add(c);
	}
	
	public Component getComponent(int type) {
		if (type < 0 || type >= m_components.length) return null;
		return m_components[type];
	}
	
	public boolean hasComponent(int type) {
		if (type < 0 || type >= m_components.length) return false;
		return m_components[type] != null;
	}
	
	public Array<Component> getComponents() {
		return m_validComponents;
	}

	@Override
	public void reset() {
		for (int i = 0; i < m_components.length; ++i) {
			if (m_components[i] != null) {
				m_world.freeComponent(m_components[i]);
				m_components[i] = null;
			}
		}
		
		m_validComponents.clear();
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
