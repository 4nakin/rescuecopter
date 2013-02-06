package com.badlogic.gdx.sionengine.entity.pools;

import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.sionengine.SionEngine;
import com.badlogic.gdx.sionengine.entity.components.Asset;

public class AssetPool extends Pool<Asset> {

	public AssetPool() {
		super(SionEngine.getEntityWorld().getMaxEntities());
	}
	
	public AssetPool(int poolSize) {
		super(poolSize);
	}
	
	@Override
	protected Asset newObject() {
		return new Asset();
	}
	
	@Override
	public String toString() {
		return "Asset";
	}
}
