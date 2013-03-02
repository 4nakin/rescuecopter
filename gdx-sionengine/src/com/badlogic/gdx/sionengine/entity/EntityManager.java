package com.badlogic.gdx.sionengine.entity;

import com.badlogic.gdx.utils.Disposable;

public abstract class EntityManager implements EntityObserver, Disposable {
	
	@Override
	public final void entityAdded(Entity e) {
		
	}
	
	@Override
	public void dispose() {
		
	}
	
	@Override
	public String toString() {
		return "EntityManager";
	}
}
