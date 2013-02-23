package com.siondream.rescue;

import java.util.Iterator;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.equations.Quad;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
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
import com.badlogic.gdx.sionengine.entity.components.Type;
import com.badlogic.gdx.sionengine.entity.managers.GroupManager;
import com.badlogic.gdx.sionengine.entity.managers.TagManager;
import com.badlogic.gdx.sionengine.maps.CircleMapObject;
import com.badlogic.gdx.sionengine.maps.GleedMapRenderer;
import com.badlogic.gdx.sionengine.maps.Map;
import com.badlogic.gdx.sionengine.maps.MapObject;
import com.badlogic.gdx.sionengine.maps.MapObjects;
import com.badlogic.gdx.sionengine.maps.RectangleMapObject;
import com.badlogic.gdx.sionengine.tweeners.CameraTweener;
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
	
	private RescueCopter m_game;
	private Logger m_logger = new Logger("GameScreen", Logger.INFO);  
	private ScreenState m_state = ScreenState.Loading;
	private Map m_map;
	private GleedMapRenderer m_mapRenderer;
	private MapBodyManager m_mapBodyManager = new MapBodyManager();
	private RectangleMapObject m_cameraBounds = null;
	
	public GameScreen(RescueCopter game) {
		m_game = game;
	}
	
	public ScreenState getState() {
		return m_state;
	}
	
	public void setState(ScreenState state) {
		m_state = state;
	}
	
	private void resetGame() {
		EntityWorld world = SionEngine.getEntityWorld();
		TagManager tagManager = world.getManager(TagManager.class);
		GroupManager groupManager = world.getManager(GroupManager.class);
		
		// Destroy space ship
		Entity ship = tagManager.getEntity(GameGlobals.type_spaceship);
		if (ship != null)
			world.deleteEntity(ship);
		
		// Destroy astronauts
		Array<Entity> astronauts = groupManager.getEntities(GameGlobals.group_astronauts);
		
		if (astronauts != null) { 
			for (Entity astronaut : astronauts) {
				world.deleteEntity(astronaut);
			}
			
			astronauts.clear();
		}
		
		// Reset timers
		
		// Reset game state
		setState(ScreenState.Loading);
		
		// Create level geometry if it's not loaded already
		if (m_map == null) {
			m_map = SionEngine.getAssetManager().get("data/level.xml", Map.class);
			m_mapRenderer = new GleedMapRenderer(m_map, new SpriteBatch(), SionEngine.getUnitsPerPixel());
			m_mapBodyManager.createPhysics(m_map, "Physics");
			m_cameraBounds = (RectangleMapObject)m_map.getLayers().getLayer("Camera").getObjects().getObject("bounds");
		}
		
		// Create spaceship
		createSpaceShip();
		
		// Create astronauts
		createAstronauts();
	}
	
	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void hide() {
		m_mapBodyManager.destroyPhysics();
		m_map = null;
		m_mapRenderer.dispose();
		m_mapRenderer = null;
		SionEngine.getAssetManager().unload("data/level.xml");
		m_cameraBounds = null;
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void render(float delta) {

		if (m_state == ScreenState.Loading) {
			if (SionEngine.getAssetManager().update()) {
				resetGame();
				m_state = ScreenState.Playing;
			}
			else {
				return;
			}
		}
		
		if (m_mapRenderer != null) {
			m_mapRenderer.begin();
			m_mapRenderer.render(SionEngine.getCamera());
			m_mapRenderer.end();
		}
		
		if (m_state == ScreenState.Lose) {
			resetGame();
		}
		
		if (m_state == ScreenState.Win) {
			return;
		}
		
		updateCamera();
		
		SionEngine.getEntityWorld().update();
		SionEngine.getTweenManager().update(Gdx.graphics.getDeltaTime());
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
	
	private void createSpaceShip() {
		EntityWorld world = SionEngine.getEntityWorld();
		Entity entity = world.createEntity();
		
		Transform transform = world.createComponent(Transform.class);
		AnimatedSprite anim = world.createComponent(AnimatedSprite.class);
		Physics physics = world.createComponent(Physics.class);
		State state = world.createComponent(State.class);
		Asset asset = world.createComponent(Asset.class);
		Type type = world.createComponent(Type.class);
		
		anim.setFileName("data/spaceship.xml");
		physics.setUserData(entity);
		physics.setFileName("data/spaceship_physics.xml");
		physics.setWakeUp(true);
		state.set(Globals.state_idle);
		Vector3 position = transform.getPosition();
		CircleMapObject circle = (CircleMapObject)m_map.getLayers().getLayer("Objects").getObjects().getObject("player");
		position.x = circle.getCircle().x * SionEngine.getUnitsPerPixel();
		position.y = circle.getCircle().y * SionEngine.getUnitsPerPixel();
		transform.setPosition(position);
		type.set(GameGlobals.type_spaceship);
		
		entity.addComponent(transform);
		entity.addComponent(anim);
		entity.addComponent(state);
		entity.addComponent(asset);
		entity.addComponent(physics);
		entity.addComponent(new SpaceShip());
		
		entity.addComponent(type);
		world.addEntity(entity);
		
		world.getManager(TagManager.class).register(GameGlobals.type_spaceship, entity);
		m_logger.info("Created caveman in " + position);
	}
	
	private void createAstronauts() {
		EntityWorld world = SionEngine.getEntityWorld();
		GroupManager groupManager = world.getManager(GroupManager.class);
		MapObjects objects = m_map.getLayers().getLayer("astronauts").getObjects();
		Iterator<MapObject> objectIt = objects.iterator();
		
		while(objectIt.hasNext()) {
			CircleMapObject circle = (CircleMapObject)objectIt.next();
			
			Entity entity = world.createEntity();
			Transform transform = world.createComponent(Transform.class);
			AnimatedSprite anim = world.createComponent(AnimatedSprite.class);
			Physics physics = world.createComponent(Physics.class);
			State state = world.createComponent(State.class);
			Asset asset = world.createComponent(Asset.class);
			Type type = world.createComponent(Type.class);
			anim.setFileName("data/astronaut.xml");
			physics.setUserData(entity);
			physics.setFileName("data/astronaut_physics.xml");
			physics.setWakeUp(true);
			state.set(Globals.state_idle);
			Vector3 position = transform.getPosition();
			position.x = circle.getCircle().x * SionEngine.getUnitsPerPixel();
			position.y = circle.getCircle().y * SionEngine.getUnitsPerPixel();
			transform.setPosition(position);
			type.set(GameGlobals.type_astronaut);
			entity.addComponent(transform);
			entity.addComponent(anim);
			entity.addComponent(state);
			entity.addComponent(asset);
			entity.addComponent(physics);
			entity.addComponent(new Abductable());
			entity.addComponent(type);
			world.addEntity(entity);
			groupManager.register(GameGlobals.group_astronauts, entity);
			m_logger.info("Created astronaut in " + position);
		}
	}
	
	private void updateCamera() {
		OrthographicCamera camera = SionEngine.getCamera();
		Entity spaceShip = SionEngine.getEntityWorld().getManager(TagManager.class).getEntity(GameGlobals.type_spaceship);
		Vector3 position = spaceShip.getComponent(Transform.class).getPosition();
		
		Vector3 destPos = new Vector3();
		Rectangle bounds = m_cameraBounds.getRectangle();
		
		float camWidth = SionEngine.getVirtualWidth() * SionEngine.getUnitsPerPixel();
		float camHeight = SionEngine.getVirtualHeight() * SionEngine.getUnitsPerPixel();
		float mapWidth = bounds.getWidth() * SionEngine.getUnitsPerPixel();
		float mapHeight = bounds.getHeight() * SionEngine.getUnitsPerPixel();
		
		float maxX = mapWidth - camWidth * 0.5f;
		float maxY = mapHeight - camHeight * 0.5f;
		float minX = bounds.getX() * SionEngine.getUnitsPerPixel() + camWidth * 0.5f;
		float minY = bounds.getY() * SionEngine.getUnitsPerPixel() + camHeight * 0.5f;
		
		// Check map bounds
		destPos.x = Math.max(Math.min(position.x, maxX), minX);
		destPos.y = Math.max(Math.min(position.y, maxY), minY);
		destPos.z = camera.position.z;
		
		Tween.to(camera, CameraTweener.Position, 0.1f).
				 ease(Quad.IN).
				 target(destPos.x, destPos.y, destPos.z).
				 start(SionEngine.getTweenManager());
	}
}
