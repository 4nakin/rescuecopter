package com.siondream.rescue;

import com.badlogic.gdx.sionengine.SionEngine;

public class RescueCopter extends SionEngine {
	
	@Override
	public void create() {		
		super.create();
		
		m_screens.put("GameScreen", new GameScreen(this));
		setScreen("GameScreen");
	}
}
