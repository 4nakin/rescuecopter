package com.badlogic.gdx.sionengine;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;

public class SionScreen extends InputAdapter implements Screen {
	protected StateController m_stateController;
	
	public SionScreen(String name) {
		m_stateController = new StateController(name);
	}
	
	public StateController getStateController() {
		return m_stateController;
	}
	
	@Override
	public void dispose() {
	}

	@Override
	public void hide() {
	}

	@Override
	public void pause() {
	}

	@Override
	public void render(float deltaT) {
		m_stateController.update(deltaT);
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void resume() {
	}

	@Override
	public void show() {
	}
}
