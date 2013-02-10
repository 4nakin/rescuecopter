package com.siondream.rescue;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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
import com.badlogic.gdx.sionengine.maps.GleedMapRenderer;
import com.badlogic.gdx.sionengine.maps.Map;
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
	private Map m_map;
	private GleedMapRenderer m_mapRenderer;
	private MapBodyManager m_mapBodyManager = new MapBodyManager();
	
	public GameScreen(SionEngine engine) {
		
	}
	
	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void hide() {
		m_mapBodyManager.destroyPhysics();
		m_map = null;
		m_mapRenderer = null;
		SionEngine.getAssetManager().unload("data/level.xml");

	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void render(float delta) {
		
		
		if (m_state == ScreenState.Loading && SionEngine.getAssetManager().update()) {
			m_map = SionEngine.getAssetManager().get("data/level.xml", Map.class);
			m_mapRenderer = new GleedMapRenderer(m_map, new SpriteBatch(), SionEngine.getUnitsPerPixel());
			m_mapBodyManager.createPhysics(m_map, "Physics");
			m_state = ScreenState.Playing;
		}
		
		if (m_mapRenderer != null) {
			m_mapRenderer.begin();
			m_mapRenderer.render(SionEngine.getCamera());
			m_mapRenderer.end();
		}
		
		SionEngine.getEntityWorld().update();
		
		Vector3 cameraPos = SionEngine.getCamera().position;
		
		if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
			cameraPos.x -= 1.0f;
		}
		else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
			cameraPos.x += 1.0f;
		}
		
		if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
			cameraPos.y += 1.0f;
		}
		else if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
			cameraPos.y -= 1.0f;
		}
		
		if (Gdx.input.isKeyPressed(Input.Keys.D)) {
			SionEngine.getCamera().zoom += 0.05f;
		}
		else if (Gdx.input.isKeyPressed(Input.Keys.F)) {
			SionEngine.getCamera().zoom -= 0.05f;
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
		
		SionEngine.getAssetManager().load("data/level.xml", Map.class);
		m_state = ScreenState.Loading;
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
		Transform transform = world.createComponent(Transform.class);
		AnimatedSprite anim = world.createComponent(AnimatedSprite.class);
		Physics physics = world.createComponent(Physics.class);
		State state = world.createComponent(State.class);
		Asset asset = world.createComponent(Asset.class);
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
