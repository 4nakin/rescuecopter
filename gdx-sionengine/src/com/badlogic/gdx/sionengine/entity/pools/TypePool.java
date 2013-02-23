package com.badlogic.gdx.sionengine.entity.pools;

import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.sionengine.SionEngine;
import com.badlogic.gdx.sionengine.entity.components.Type;

public class TypePool extends Pool<Type> {

	public TypePool() {
		super(SionEngine.getEntityWorld().getMaxEntities());
	}
	
	public TypePool(int poolSize) {
		super(poolSize);
	}
	
	@Override
	protected Type newObject() {
		return new Type();
	}
	
	@Override
	public String toString() {
		return "Type";
	}
}
