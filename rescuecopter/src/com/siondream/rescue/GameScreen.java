package com.siondream.rescue;

import java.util.Iterator;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.sionengine.Chronometer;
import com.badlogic.gdx.sionengine.Chronometer.Format;
import com.badlogic.gdx.sionengine.Chronometer.Time;
import com.badlogic.gdx.sionengine.Globals;
import com.badlogic.gdx.sionengine.SionEngine;
import com.badlogic.gdx.sionengine.entity.Entity;
import com.badlogic.gdx.sionengine.entity.EntityWorld;
import com.badlogic.gdx.sionengine.entity.components.AnimatedSprite;
import com.badlogic.gdx.sionengine.entity.components.Asset;
import com.badlogic.gdx.sionengine.entity.components.CameraComponent;
import com.badlogic.gdx.sionengine.entity.components.Physics;
import com.badlogic.gdx.sionengine.entity.components.State;
import com.badlogic.gdx.sionengine.entity.components.TextureComponent;
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
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Logger;

public class GameScreen implements Screen, InputProcessor {
	
	public enum ScreenState {
		Init,
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
	private Chronometer m_chrono = new Chronometer();
	private Time m_time = new Time(0, 0, 0, 0);
	
	// UI
	private Stage m_stage = new Stage();
	Table m_table = new Table();
	private OrthographicCamera m_HUDCamera = new OrthographicCamera(SionEngine.getVirtualWidth(), SionEngine.getVirtualWidth());
	private Label m_lblTime;
	private Label m_lblAstronauts;
	private Label m_lblEnergy;
	
	public GameScreen(RescueCopter game) {
		m_game = game;
		
		m_stage.setCamera(m_HUDCamera);
		Skin skin = m_game.getSkin();
		m_table.setFillParent(true);
		m_stage.addActor(m_table);
		m_lblTime = new Label("", skin);
		m_lblTime.setColor(1.0f, 1.0f, 1.0f, 1.0f);
		m_lblTime.setWidth(70.0f);
		m_lblTime.setX(SionEngine.getVirtualWidth() - m_lblTime.getWidth());
		m_lblTime.setY(700.0f);
		m_stage.addActor(m_lblTime);
		m_lblAstronauts = new Label("", skin);
		m_lblAstronauts.setColor(1.0f, 1.0f, 1.0f, 1.0f);
		m_lblAstronauts.setWidth(70.0f);
		m_lblAstronauts.setX(20.0f);
		m_lblAstronauts.setY(700.0f);
		m_stage.addActor(m_lblAstronauts);
		m_lblEnergy = new Label("", skin);
		m_lblEnergy.setColor(1.0f, 1.0f, 1.0f, 1.0f);
		m_lblEnergy.setWidth(100.0f);
		m_lblEnergy.setX((SionEngine.getVirtualWidth() - m_lblEnergy.getWidth()) / 2.0f);
		m_lblEnergy.setY(700.0f);
		m_stage.addActor(m_lblEnergy);
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
		Array<Entity> astronauts = new Array<Entity>();
		groupManager.getEntities(GameGlobals.group_astronauts, astronauts);
		
		if (astronauts != null) { 
			for (Entity astronaut : astronauts) {
				world.deleteEntity(astronaut);
			}
			
			astronauts.clear();
		}
		
		// Reset timers
		
		// Reset game state
		setState(ScreenState.Loading);
		
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
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void render(float delta) {

		if (m_state == ScreenState.Init){
			if (SionEngine.getAssetManager().update()) {
				
				if (m_map == null) {
					m_map = SionEngine.getAssetManager().get("data/level.xml", Map.class);
					m_mapRenderer = new GleedMapRenderer(m_map, new SpriteBatch(), SionEngine.getUnitsPerPixel());
					m_mapBodyManager.createPhysics(m_map, "Physics");
					RectangleMapObject boundsObject = (RectangleMapObject)m_map.getLayers().getLayer("Camera").getObjects().getObject("bounds");
					
					if (boundsObject != null) {					
						EntityWorld world = SionEngine.getEntityWorld();
						ShipCameraSystem cameraSystem = world.getSystem(ShipCameraSystem.class);
						cameraSystem.setBounds(boundsObject.getRectangle()); 
					}
				}
				
				resetGame();
				setState(ScreenState.Loading);
			}
			
			return;
		}
		
		if (m_state == ScreenState.Loading) {
			if (SionEngine.getAssetManager().update()) {
				m_state = ScreenState.Playing;
				m_chrono.start();
			}
			else {
				return;
			}
		}
		
		if (m_mapRenderer != null) {
			Entity cameraEntity = SionEngine.getEntityWorld().getEntityByTag(Globals.entity_camera);
			
			if (cameraEntity != null) {
				CameraComponent cameraComponent = cameraEntity.getComponent(CameraComponent.class);
				m_mapRenderer.begin();
				m_mapRenderer.render(cameraComponent.get());
				m_mapRenderer.end();
			}
			
		}
		
		if (m_state == ScreenState.Lose) {
			resetGame();
		}
		
		if (m_state == ScreenState.Win) {
			return;
		}
				
		updateHUD();
	}

	private void updateHUD() {
		m_HUDCamera.update();
		
		// Update info
		EntityWorld world = SionEngine.getEntityWorld();
		
		m_chrono.getTime(m_time, Format.Minutes);
		m_lblTime.setText("" + m_time.getMinutes() + " : " + m_time.getSeconds());
		
		Array<Entity> astronauts = world.getManager(GroupManager.class).getEntities(GameGlobals.group_astronauts);
		m_lblAstronauts.setText((astronauts != null)? "" + astronauts.size : "0");
		
		Entity shipEntity = world.getManager(TagManager.class).getEntity(GameGlobals.type_spaceship);
		SpaceShip ship = shipEntity.getComponent(SpaceShip.class);
		m_lblEnergy.setText("Energy: " + ship.getEnergy() / ship.getTotalEnergy() * 100.0f + "%");
		
		// Draw
		m_stage.act();
		m_stage.draw();
	}
	
	@Override
	public void resize(int width, int height) {
		m_stage.setViewport(width, height, true);
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void show() {
		Gdx.input.setInputProcessor(this);
		
		SionEngine.getAssetManager().load("data/level.xml", Map.class);
		setState(ScreenState.Init);
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
		//AnimatedSprite anim = world.createComponent(AnimatedSprite.class);
		Physics physics = world.createComponent(Physics.class);
		State state = world.createComponent(State.class);
		Asset asset = world.createComponent(Asset.class);
		Type type = world.createComponent(Type.class);
		TextureComponent texture = new TextureComponent();
		
		//anim.setFileName("data/spaceship.xml");
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
		texture.setFileName("data/spaceship.png");
		
		entity.addComponent(transform);
		//entity.addComponent(anim);
		entity.addComponent(state);
		entity.addComponent(asset);
		entity.addComponent(physics);
		entity.addComponent(new SpaceShip());
		entity.addComponent(texture);
		
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
}
