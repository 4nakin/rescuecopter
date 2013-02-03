package com.siondream.rescue;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.sionengine.SionEngine;
import com.badlogic.gdx.sionengine.animation.AnimationData;
import com.badlogic.gdx.sionengine.physics.PhysicsData;

public class GameScreen implements Screen, InputProcessor {
	
	private SionEngine m_engine;
	
	public GameScreen(SionEngine engine) {
		m_engine = engine;
		m_engine.getAssetManager().load("data/caveman.xml", AnimationData.class);
		m_engine.getAssetManager().load("data/caveman_physics.xml", PhysicsData.class);
	}
	
	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void render(float delta) {
		m_engine.getAssetManager().update();
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void show() {
		// TODO Auto-generated method stub
	}

	public boolean keyDown(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}
	
	public boolean keyTyped(char character) {
		// TODO Auto-generated method stub
		return false;
	}
	
	public boolean keyUp(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}
	 
	public boolean mouseMoved(int screenX, int screenY) {
		// TODO Auto-generated method stub
		return false;
	}
	 
	public boolean scrolled(int amount) {
		// TODO Auto-generated method stub
		return false;
	}
	 
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}
	 
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		// TODO Auto-generated method stub
		return false;
	}
	 
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}
}
