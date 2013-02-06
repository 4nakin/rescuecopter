package com.badlogic.gdx.sionengine.entity.components;

public interface AsynchronousAsset {
	public void fetchAssets();
	public boolean isLoaded();
}
