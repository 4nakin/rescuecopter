package com.siondream.rescue;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.sionengine.Globals;
import com.badlogic.gdx.sionengine.SionEngine;
import com.badlogic.gdx.sionengine.entity.Entity;
import com.badlogic.gdx.sionengine.entity.EntityWorld;
import com.badlogic.gdx.sionengine.entity.components.AnimatedSprite;
import com.badlogic.gdx.sionengine.entity.components.Asset;
import com.badlogic.gdx.sionengine.entity.components.State;
import com.badlogic.gdx.sionengine.entity.components.Transform;
import com.badlogic.gdx.utils.Logger;

public class GameScreen implements Screen, InputProcessor {
	
	private Logger m_logger = new Logger("GameScreen", Logger.INFO);
	
	public GameScreen(SionEngine engine) {
		EntityWorld world = SionEngine.getEntityWorld();
		Entity entity = world.createEntity();
		Transform transform = (Transform)world.createComponent(Transform.getComponentType());
		AnimatedSprite anim = (AnimatedSprite)world.createComponent(AnimatedSprite.getComponentType());
		State state = (State)world.createComponent(State.getComponentType());
		Asset asset = (Asset)world.createComponent(Asset.getComponentType());
		anim.setFileName("data/caveman.xml");
		state.set(Globals.state_idle);
		entity.addComponent(transform);
		entity.addComponent(anim);
		entity.addComponent(state);
		entity.addComponent(asset);
		world.addEntity(entity);
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
		//SionEngine.getAssetManager().update();
		SionEngine.getEntityWorld().update();
		Vector3 cameraPos = SionEngine.getCamera().position;
		
		if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
			cameraPos.x -= 5.0f;
		}
		else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
			cameraPos.x += 5.0f;
		}
		
		if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
			cameraPos.y += 5.0f;
		}
		else if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
			cameraPos.y -= 5.0f;
		}
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
