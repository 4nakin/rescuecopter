package com.badlogic.gdx.sionengine.entity.managers;

import java.util.Iterator;

import com.badlogic.gdx.sionengine.SionEngine;
import com.badlogic.gdx.sionengine.entity.Entity;
import com.badlogic.gdx.sionengine.entity.EntityManager;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.IntMap.Entries;
import com.badlogic.gdx.utils.IntMap.Entry;

public class TagManager extends EntityManager {
	private IntMap<Entity> m_taggedEntities = new IntMap<Entity>();

	public Entity getEntity(String tag) {
		return getEntity(SionEngine.getIDGenerator().getID(tag));
	}
	
	public Entity getEntity(int tag) {
		return m_taggedEntities.get(tag);
	}
	
	public void register(String tag, Entity e) {
		register(SionEngine.getIDGenerator().getID(tag), e);
	}
	
	public void register(int tag, Entity e) {
		m_taggedEntities.put(tag, e);
	}
	
	public void unregister(String tag) {
		unregister(SionEngine.getIDGenerator().getID(tag));
	}
	
	public void unregister(int tag) {
		m_taggedEntities.remove(tag);
	}
	
	@Override
	public final void entityDeleted(Entity e) {
		Entries<Entity> entries =  m_taggedEntities.entries();
		Iterator<Entry<Entity>> entityIt = entries.iterator();
		
		while (entityIt.hasNext()) {
			Entry<Entity> entry = entityIt.next();
			Entity entity = entry.value;
			
			if (entity.equals(e)) {
				entityIt.remove();
				return;
			}
		}
	}
	
	@Override
	public String toString() {
		return "TagManager";
	}
}
