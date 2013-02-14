package com.badlogic.gdx.sionengine.entity;

public interface EntityObserver {
	public void entityAdded(Entity e);
	public void entityDeleted(Entity e);
}
