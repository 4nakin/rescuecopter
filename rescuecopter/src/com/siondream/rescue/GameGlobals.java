package com.siondream.rescue;

import com.badlogic.gdx.sionengine.SionEngine;

public class GameGlobals {
	// Entity types
	static final public int type_astronaut = SionEngine.getIDGenerator().getID("type_astronaut");
	static final public int type_spaceship = SionEngine.getIDGenerator().getID("type_spaceship");
	
	// Groups
	static final public int group_astronauts = SionEngine.getIDGenerator().getID("group_astronauts");
	
	// Entity states
	static final public int state_hit = SionEngine.getIDGenerator().getID("state_hit");
	
	// Game states
	static final public int gamescreen_init = SionEngine.getIDGenerator().getID("gamescreen_init");
	static final public int gamescreen_loading = SionEngine.getIDGenerator().getID("gamescreen_loading");
	static final public int gamescreen_playing = SionEngine.getIDGenerator().getID("gamescreen_playing");
	static final public int gamescreen_lose = SionEngine.getIDGenerator().getID("gamescreen_lose");
	static final public int gamescreen_win = SionEngine.getIDGenerator().getID("gamescreen_win");
}
