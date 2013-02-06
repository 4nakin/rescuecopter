package com.badlogic.gdx.sionengine.entity.systems;

import com.badlogic.gdx.sionengine.SionEngine;
import com.badlogic.gdx.sionengine.entity.Component;
import com.badlogic.gdx.sionengine.entity.Entity;
import com.badlogic.gdx.sionengine.entity.EntitySystem;
import com.badlogic.gdx.sionengine.entity.EntityWorld;
import com.badlogic.gdx.sionengine.entity.components.Asset;
import com.badlogic.gdx.sionengine.entity.components.AsynchronousAsset;
import com.badlogic.gdx.utils.Array;

public class AssetSystem extends EntitySystem {

	private boolean m_wasFullyLoaded = false;
	private boolean m_isFullyLoaded = false;
	
	public AssetSystem(EntityWorld world, int priority, int loggingLevel) {
		super(world, priority, loggingLevel);
		m_logger.info("initializing");
		m_aspect.addToAll(Asset.getComponentType());
	}

	@Override
	public void begin() {		
		m_isFullyLoaded = SionEngine.getAssetManager().update();
	}
	
	@Override
	public void end() {		
		m_wasFullyLoaded = m_isFullyLoaded;
	}
	
	@Override
	protected void process(Entity e) {
		if (m_isFullyLoaded && !m_wasFullyLoaded) {
			m_logger.info("fetching components assets for " + e);
			Array<Component> components = e.getComponents();
			
			for(Component c : components) {
				if (c instanceof AsynchronousAsset) {
					AsynchronousAsset asset = (AsynchronousAsset)c;
					if (!asset.isLoaded()) {
						asset.fetchAssets();
					}
				}
			}
		}
	}
	
	@Override
	public String toString() {
		return "AssetSystem";
	}
}
