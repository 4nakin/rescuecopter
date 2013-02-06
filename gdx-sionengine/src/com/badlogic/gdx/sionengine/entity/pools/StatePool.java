package com.badlogic.gdx.sionengine.entity.pools;

import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.sionengine.SionEngine;
import com.badlogic.gdx.sionengine.entity.components.State;

public class StatePool extends Pool<State> {

	public StatePool() {
		super(SionEngine.getEntityWorld().getMaxEntities());
	}
	
	public StatePool(int poolSize) {
		super(poolSize);
	}
	
	@Override
	protected State newObject() {
		return new State();
	}
	
	@Override
	public String toString() {
		return "State";
	}
}
