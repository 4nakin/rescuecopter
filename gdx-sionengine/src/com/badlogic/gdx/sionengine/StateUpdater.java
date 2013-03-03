package com.badlogic.gdx.sionengine;

public interface StateUpdater {
	void onEnter(int previousState);
	void update(float deltaT);
	void onExit(int nextState);
}
