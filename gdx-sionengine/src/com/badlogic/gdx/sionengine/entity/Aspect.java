package com.badlogic.gdx.sionengine.entity;

import com.badlogic.gdx.utils.Array;

public class Aspect {
	Array<Class<? extends Component>> m_all = new Array<Class<? extends Component>>();
	Array<Class<? extends Component>> m_any = new Array<Class<? extends Component>>();
	Array<Class<? extends Component>> m_none = new Array<Class<? extends Component>>();
	
	public void addToAll(Class<? extends Component> componentClass) {
		m_all.add(componentClass);
	}
	
	public void addToAny(Class<? extends Component> componentClass) {
		m_any.add(componentClass);
	}
	
	public void addToNone(Class<? extends Component> componentClass) {
		m_none.add(componentClass);
	}
	
	public boolean check(Entity e) {
		if (m_all.size == 0 &&
			m_any.size == 0 &&
			m_none.size == 0) {
			return false;
		}
		
		for (Class<? extends Component> c : m_all) {
			if (!e.hasComponent(c)) return false;
		}
		
		for (Class<? extends Component> c : m_none) {
			if (e.hasComponent(c)) return false;
		}
		
		if (m_any.size == 0) return true;
		
		for (Class<? extends Component> c : m_any) {
			if (e.hasComponent(c)) return true;
		}
		
		return false;
	}
	
	@Override
	public String toString() {
		String toString = "aspect all: (";
		
		for (Class<? extends Component> c : m_all) {
			toString += c.getName() + " ";
		}
		
		toString += ") any: (";
		
		for (Class<? extends Component> c : m_none) {
			toString += c.getName() + " ";
		}
		
		toString += ") none: (";
		
		for (Class<? extends Component> c : m_any) {
			toString += c.getName() + " ";
		}
		
		toString += ")";
		
		return toString;
	}
}
