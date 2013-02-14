package com.badlogic.gdx.sionengine.entity.components;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.sionengine.SionEngine;
import com.badlogic.gdx.sionengine.entity.Component;
import com.badlogic.gdx.sionengine.physics.PhysicsData;
import com.badlogic.gdx.utils.Array;

public class Physics extends Component implements AsynchronousAsset {

	private String m_file = null;
	private Body m_body = null;
	private Object m_userData = null;
	private boolean m_wakeUp = false;
	
	public void setFileName(String file) {
		if (m_file != file) {
			m_file = file;
			
			destroyBody();
			
			if (m_file != null) {
				SionEngine.getAssetManager().load(m_file, PhysicsData.class);
			}
		}
	}
	
	public Body getBody() {
		return m_body;
	}
	
	public void setUserData(Object object) {
		m_userData = object;
		
		if (m_body != null) {
			m_body.setUserData(object);
		}
	}
	
	public void setWakeUp(boolean wakeUp) {
		m_wakeUp = wakeUp;
	}
	
	public boolean getWakeUp() {
		return m_wakeUp;
	}
	
	@Override
	public void fetchAssets() {
		AssetManager manager = SionEngine.getAssetManager();
		
		if (manager.isLoaded(m_file, PhysicsData.class)) {
			PhysicsData data = manager.get(m_file, PhysicsData.class);
			
			m_body = SionEngine.getWorld().createBody(data.getBodyDef());
			m_body.setMassData(data.getMassData());
			m_body.setUserData(m_userData);
			
			Array<FixtureDef> fixtureDefs = data.getFixtureDefs();
			Array<Integer> fixtureIds = data.getFixtureIds();
			Array<Filter> filters = data.getFilters();
			
			for (int i = 0; i < fixtureDefs.size && i < filters.size; ++i) {
				Fixture fixture = m_body.createFixture(fixtureDefs.get(i));
				fixture.setUserData(fixtureIds.get(i));
				//fixture.setFilterData(filters.get(i));
			}
		}
	}

	@Override
	public boolean isLoaded() {
		return m_body != null;
	}

	@Override
	public void reset() {
		m_userData = null;
		
		destroyBody();
		
		if (m_file != null) {
			SionEngine.getAssetManager().unload(m_file);
			m_file = null;
		}
	}
	
	@Override
	public String toString() {
		return "physics " + m_file;
	}

	private void destroyBody() {
		if (m_body != null) {
			SionEngine.getWorld().destroyBody(m_body);
			m_body = null;
		}
	}
}
