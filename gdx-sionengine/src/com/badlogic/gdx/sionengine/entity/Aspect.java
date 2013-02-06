package com.badlogic.gdx.sionengine.entity;

import com.badlogic.gdx.utils.Array;

public class Aspect {
	Array<Integer> m_all = new Array<Integer>();
	Array<Integer> m_any = new Array<Integer>();
	Array<Integer> m_none = new Array<Integer>();
	
	public void addToAll(int componentType) {
		m_all.add(componentType);
	}
	
	public void addToAny(int componentType) {
		m_any.add(componentType);
	}
	
	public void addToNone(int componentType) {
		m_none.add(componentType);
	}
	
	public boolean check(Entity e) {
		for (Integer type : m_all) {
			if (!e.hasComponent(type)) return false;
		}
		
		for (Integer type : m_none) {
			if (e.hasComponent(type)) return false;
		}
		
		if (m_any.size == 0) return true;
		
		for (Integer type : m_any) {
			if (e.hasComponent(type)) return true;
		}
		
		return false;
	}
}
