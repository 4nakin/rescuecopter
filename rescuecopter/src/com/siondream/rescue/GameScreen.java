package com.siondream.rescue;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.sionengine.Globals;
import com.badlogic.gdx.sionengine.SionEngine;
import com.badlogic.gdx.sionengine.entity.Entity;
import com.badlogic.gdx.sionengine.entity.EntityWorld;
import com.badlogic.gdx.sionengine.entity.components.AnimatedSprite;
import com.badlogic.gdx.sionengine.entity.components.Asset;
import com.badlogic.gdx.sionengine.entity.components.Physics;
import com.badlogic.gdx.sionengine.entity.components.State;
import com.badlogic.gdx.sionengine.entity.components.Transform;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Logger;

public class GameScreen implements Screen, InputProcessor {
	
	public enum ScreenState {
		Loading,
		Start,
		Playing,
		Lose,
		Win,
	}
	
	private Logger m_logger = new Logger("GameScreen", Logger.INFO);  
	private ScreenState m_state = ScreenState.Loading;
	private Array<Entity> m_entities = new Array<Entity>();
	
	public GameScreen(SionEngine engine) {
		
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
		Gdx.input.setInputProcessor(this);
	}

	public boolean keyDown(int keycode) {
		if (keycode == Input.Keys.A)
			createCaveman();
		if (keycode == Input.Keys.S)
			deleteCaveman();
			
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
	
	private void createCaveman() {
		EntityWorld world = SionEngine.getEntityWorld();
		Entity entity = world.createEntity();
		Transform transform = (Transform)world.createComponent(Transform.getComponentType());
		AnimatedSprite anim = (AnimatedSprite)world.createComponent(AnimatedSprite.getComponentType());
		Physics physics = (Physics)world.createComponent(Physics.getComponentType());
		State state = (State)world.createComponent(State.getComponentType());
		Asset asset = (Asset)world.createComponent(Asset.getComponentType());
		anim.setFileName("data/caveman.xml");
		physics.setFileName("data/caveman_physics.xml");
		state.set(Globals.state_idle);
		Vector3 position = transform.getPosition();
		position.x = MathUtils.random(-SionEngine.getVirtualWidth() * 0.5f * SionEngine.getUnitsPerPixel(), SionEngine.getVirtualWidth() * 0.5f * SionEngine.getUnitsPerPixel());
		position.y = MathUtils.random(-SionEngine.getVirtualHeight() * 0.5f * SionEngine.getUnitsPerPixel(), SionEngine.getVirtualHeight() * 0.5f * SionEngine.getUnitsPerPixel());
		transform.setPosition(position);
		entity.addComponent(transform);
		entity.addComponent(anim);
		entity.addComponent(state);
		entity.addComponent(asset);
		entity.addComponent(physics);
		world.addEntity(entity);
		m_entities.add(entity);
		m_logger.info("Created caveman in " + position);
	}
	
	private void deleteCaveman() {
		if (m_entities.size > 0) {
			int index = MathUtils.random(m_entities.size - 1);
			SionEngine.getEntityWorld().deleteEntity(m_entities.get(index));
			m_entities.removeIndex(index);
		}
	}
}
