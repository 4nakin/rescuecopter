package com.badlogic.gdx.sionengine.entity;

public abstract class EntityManager implements EntityObserver {
	
	@Override
	public final void entityAdded(Entity e) {
		
	}
	
	@Override
	public String toString() {
		return "EntityManager";
	}
}
