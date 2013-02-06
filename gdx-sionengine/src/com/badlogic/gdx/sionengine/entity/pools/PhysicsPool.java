package com.badlogic.gdx.sionengine.entity.pools;

import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.sionengine.SionEngine;
import com.badlogic.gdx.sionengine.entity.components.Physics;

public class PhysicsPool extends Pool<Physics> {

	public PhysicsPool() {
		super(SionEngine.getEntityWorld().getMaxEntities());
	}
	
	public PhysicsPool(int poolSize) {
		super(poolSize);
	}
	
	@Override
	protected Physics newObject() {
		return new Physics();
	}
	
	@Override
	public String toString() {
		return "Physics";
	}
}
