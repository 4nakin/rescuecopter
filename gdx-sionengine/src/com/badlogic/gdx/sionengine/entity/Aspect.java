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
}
