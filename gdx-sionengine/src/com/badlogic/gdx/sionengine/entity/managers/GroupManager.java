package com.badlogic.gdx.sionengine.entity.managers;

import java.util.Iterator;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectMap.Entry;
import com.badlogic.gdx.sionengine.SionEngine;
import com.badlogic.gdx.sionengine.entity.Entity;
import com.badlogic.gdx.sionengine.entity.EntityManager;

public class GroupManager extends EntityManager {
	
	private ObjectMap<Integer, Array<Entity>> m_groups = new ObjectMap<Integer, Array<Entity>>();
	
	public Array<Entity> getEntities(String group) {
		return getEntities(SionEngine.getIDGenerator().getID(group));
	}
	
	public Array<Entity> getEntities(int group) {
		return m_groups.get(group);
	}
	
	public void register(String group, Entity e) {
		register(SionEngine.getIDGenerator().getID(group), e);
	}
	
	public void register(int group, Entity e) {
		Array<Entity> entities = m_groups.get(group);
		
		if (entities == null) {
			entities = new Array<Entity>();
			m_groups.put(group, entities);
		}
		
		if (!entities.contains(e, false)) {
			entities.add(e);
		}
	}
	
	public void unregister(String group, Entity e) {
		unregister(SionEngine.getIDGenerator().getID(group), e);
	}
	
	public void unregister(int group, Entity e) {
		Array<Entity> entities = m_groups.get(group);
		
		if (entities != null) {
			entities.removeValue(e, false);
		}
	}
	
	public boolean isInGroup(String group, Entity e) {
		return isInGroup(SionEngine.getIDGenerator().getID(group), e);
	}
	
	public boolean isInGroup(int group, Entity e) {
		Array<Entity> entities = m_groups.get(group);
		
		return entities != null && entities.contains(e, false);
	}
	
	@Override
	public void entityDeleted(Entity e) {
		Iterator<Entry<Integer, Array<Entity>>> groupsIt = m_groups.entries().iterator();
		
		while(groupsIt.hasNext()) {
			Array<Entity> entities = groupsIt.next().value;
			entities.removeValue(e, false);
			
			if (entities.size == 0) {
				groupsIt.remove();
			}
		}
	}
	
	@Override
	public String toString() {
		return "GroupManager";
	}
}
