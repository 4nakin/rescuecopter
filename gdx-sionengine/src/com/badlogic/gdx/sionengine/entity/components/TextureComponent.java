package com.badlogic.gdx.sionengine.entity.components;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.sionengine.SionEngine;
import com.badlogic.gdx.sionengine.animation.AnimationData;
import com.badlogic.gdx.sionengine.entity.Component;

public class TextureComponent extends Component
							  implements AsynchronousAsset, Cullable {
	
	private String m_file = null;
	private TextureRegion m_region = null;
	
	public String getFileName() {
		return m_file;
	}
	
	public void setFileName(String name) {
		if (m_file != name) {
			reset();
			
			m_file = name;
			
			if (m_file != null) {
				SionEngine.getAssetManager().load(m_file, Texture.class);
			}
		}
	}
	
	public TextureRegion getRegion() {
		return m_region;
	}
	
	@Override
	public void fetchAssets() {
		AssetManager manager = SionEngine.getAssetManager();
		
		if (manager.isLoaded(m_file, Texture.class)) {
			m_region = new TextureRegion(manager.get(m_file, Texture.class));
		}
	}

	@Override
	public boolean isLoaded() {
		return m_region != null;
	}

	@Override
	public void reset() {
		m_region = null;
		
		if (m_file != null) {
			SionEngine.getAssetManager().unload(m_file);
			m_file = null;
		}
	}
	
	@Override
	public String toString() {
		return "texture " + m_file;
	}

	@Override
	public float getWidth() {
		return m_region != null? m_region.getRegionWidth() : 0.0f;
	}

	@Override
	public float getHeight() {
		return m_region != null? m_region.getRegionHeight() : 0.0f;
	}

	@Override
	public float getOriginX() {
		return getWidth() * 0.5f;
	}

	@Override
	public float getOriginY() {
		return getHeight() * 0.5f;
	}
}
