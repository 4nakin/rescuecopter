package com.badlogic.gdx.sionengine;

import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.Logger;

public class StateController {
	private Logger m_logger;
	private IntMap<StateUpdater> m_updaters = new IntMap<StateUpdater>();
	private int m_state = 0;
	private int m_nextState = 0;
	
	public StateController(String name) {
		m_logger = new Logger(name, SionEngine.getSettings().getInt(name + ".log", Logger.INFO));
	}
	
	public void addUpdater(int state, StateUpdater updater) {
		m_updaters.put(state, updater);
	}
	
	public int getState() {
		return m_state;
	}
	
	public void setState(int state) {
		m_nextState = state;
	}
	
	public void update(float deltaT) {
		performStateChange();
		StateUpdater updater = m_updaters.get(m_state);
		
		if (updater != null) {
			updater.update(deltaT);
		}
		else {
			m_logger.error("state " + m_state + " not handled");
		}
	}
	
	private void performStateChange() {
		if (m_nextState == 0 || m_state == m_nextState) {
			m_nextState = 0;
			return;
		}
		
		StateUpdater oldUpdater = m_state != 0? m_updaters.get(m_state) : null;
		
		if (oldUpdater != null) {
			oldUpdater.onExit(m_nextState);
		}
		
		StateUpdater nextUpdater = m_updaters.get(m_nextState);
		
		if (nextUpdater != null) {
			nextUpdater.onEnter(m_state);
		}
		
		m_state = m_nextState;
		m_nextState = 0;
	}
}
