package com.badlogic.gdx.sionengine.entity.systems;

import com.badlogic.gdx.sionengine.Globals;
import com.badlogic.gdx.sionengine.entity.Entity;
import com.badlogic.gdx.sionengine.entity.EntitySystem;
import com.badlogic.gdx.sionengine.entity.EntityWorld;
import com.badlogic.gdx.sionengine.entity.components.State;
import com.badlogic.gdx.utils.Array;

public class DisposingSystem extends EntitySystem {

	private Array<Entity> m_pendingErase = new Array<Entity>();
	
	public DisposingSystem(EntityWorld world, int priority, int loggingLevel) {
		super(world, priority, loggingLevel);
		
		m_aspect.addToAll(State.class);
	}

	@Override
	public void end() {
		for (Entity e : m_pendingErase) {
			m_logger.info("disposing " + e);
			m_world.deleteEntity(e);
		}
		
		m_pendingErase.clear();
	}
	
	@Override
	protected void process(Entity e) {
		State state = e.getComponent(State.class);
		
		if (state.get() == Globals.state_erase) {
			m_pendingErase.add(e);
		}
	}
	
	@Override
	public String toString() {
		return "RenderingSystem";
	}
}
