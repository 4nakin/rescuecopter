package com.badlogic.gdx.sionengine.entity.pools;

import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.sionengine.SionEngine;
import com.badlogic.gdx.sionengine.entity.components.Transform;

public class TransformPool extends Pool<Transform> {

	public TransformPool() {
		super(SionEngine.getEntityWorld().getMaxEntities());
	}
	
	public TransformPool(int poolSize) {
		super(poolSize);
	}
	
	@Override
	protected Transform newObject() {
		return new Transform();
	}
	
	@Override
	public String toString() {
		return "Transform";
	}
}
