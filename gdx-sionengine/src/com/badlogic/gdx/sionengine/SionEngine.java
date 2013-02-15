package com.badlogic.gdx.sionengine;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.sionengine.animation.AnimationData;
import com.badlogic.gdx.sionengine.animation.AnimationLoader;
import com.badlogic.gdx.sionengine.entity.EntityWorld;
import com.badlogic.gdx.sionengine.entity.components.Transform;
import com.badlogic.gdx.sionengine.maps.GleedMapLoader;
import com.badlogic.gdx.sionengine.maps.Map;
import com.badlogic.gdx.sionengine.physics.PhysicsData;
import com.badlogic.gdx.sionengine.physics.PhysicsLoader;
import com.badlogic.gdx.sionengine.tweeners.CameraTweener;
import com.badlogic.gdx.sionengine.tweeners.ColorTweener;
import com.badlogic.gdx.sionengine.tweeners.TransformTweener;
import com.badlogic.gdx.utils.Logger;
import com.badlogic.gdx.utils.ObjectMap;

public class SionEngine extends Game {

	private static Settings m_settings;
	private static IDGenerator m_IDGenerator;
	private static PlatformResolver m_resolver;
	private static AssetManager m_assetManager;
	private static TweenManager m_tweenManager;
	private static LanguageManager m_languageManager;
	private static World m_world;
	private static EntityWorld m_entityWorld;
	private static SpriteBatch m_batch;
	private static OrthographicCamera m_camera;
	private static int m_virtualWidth;
	private static int m_virtualHeight;
	private static float m_unitsPerPixel;
	private static float m_pixelsPerUnit;
	
	protected ObjectMap<String, Screen> m_screens;
	private Screen m_nextScreen = null;
	private Logger m_logger;
	private Rectangle m_viewport = new Rectangle(0, 0, 0, 0);
	private float m_aspectRatio;
	
	private ShapeRenderer m_shapeRenderer;
	
	@Override
	public void create() {
		m_settings = new Settings();
		
		m_logger = new Logger("SionEngine", m_settings.getInt("engineLoggingLevel", Logger.INFO));
		
		m_IDGenerator = new IDGenerator();
		
		m_assetManager = new AssetManager();
		m_assetManager.setLoader(AnimationData.class, new AnimationLoader(new InternalFileHandleResolver()));
		m_assetManager.setLoader(PhysicsData.class, new PhysicsLoader(new InternalFileHandleResolver()));
		m_assetManager.setLoader(Map.class, new GleedMapLoader(new InternalFileHandleResolver()));
		
		m_tweenManager = new TweenManager();
		Tween.registerAccessor(Transform.class, new TransformTweener());
		Tween.registerAccessor(OrthographicCamera.class, new CameraTweener());
		Tween.registerAccessor(Color.class, new ColorTweener());
		
		m_languageManager = new LanguageManager();
		
		m_screens = new ObjectMap<String, Screen>();
		
		m_virtualWidth = m_settings.getInt("virtualWidth", 1280);
		m_virtualHeight = m_settings.getInt("virtualHeight", 720);
		m_aspectRatio = (float)m_virtualWidth/(float)m_virtualHeight;
		
		m_pixelsPerUnit = m_settings.getFloat("pixelsPerUnit", 1.0f);
		m_unitsPerPixel = 1.0f / m_pixelsPerUnit;
		
		if (m_settings.getBoolean("enableBox2D", true)) {
			final Vector3 gravity = m_settings.getVector("gravity", Vector3.Zero);
			m_world = new World(new Vector2(gravity.x, gravity.y), m_settings.getBoolean("doSleep", true));
		}
		
		m_entityWorld = new EntityWorld(m_settings.getInt("entityPoolSize", 100),
										m_settings.getInt("maxCoponents", 10),
										m_settings.getInt("entityWorldLoggingLevel", Logger.INFO));
		
		m_batch = new SpriteBatch();
		m_camera = new OrthographicCamera(m_virtualWidth, m_virtualHeight);
		m_camera.zoom = m_unitsPerPixel;
		
		m_shapeRenderer = new ShapeRenderer();
		
		Gdx.graphics.setTitle(m_settings.getString("windowTitle", "SionEngine"));
		Gdx.graphics.setDisplayMode(m_virtualWidth, m_virtualHeight, m_settings.getBoolean("fullScreen", false));
	}
	
	@Override
	public void dispose() {
		m_assetManager.dispose();
		m_batch.dispose();
		m_world.dispose();
		m_shapeRenderer.dispose();
	}
	
	@Override
	public void render () {
		
		Gdx.gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		Gdx.gl.glEnable(GL10.GL_BLEND);
		
		m_camera.update();
		m_batch.setProjectionMatrix(m_camera.combined);
		
		Gdx.gl.glViewport((int) m_viewport.x,
						  (int) m_viewport.y,
						  (int) m_viewport.width,
						  (int) m_viewport.height);
		
		super.render();
		
		m_shapeRenderer.setProjectionMatrix(m_camera.combined);
		m_shapeRenderer.begin(ShapeType.Line);
		m_shapeRenderer.setColor(1.0f, 0.0f, 0.0f, 1.0f);
		m_shapeRenderer.line(0.0f, -m_virtualHeight * 0.5f * m_unitsPerPixel, 0.0f, m_virtualHeight * 0.5f * m_unitsPerPixel);
		m_shapeRenderer.line(-m_virtualWidth * 0.5f * m_unitsPerPixel, 0.0f, m_virtualWidth * 0.5f * m_unitsPerPixel, 0.0f);
		m_shapeRenderer.end();
		
		
		applyScreenChange();
	}
	
	@Override
	public void resize(int width, int height) {
		// Calculate new aspect ratio
        float aspectRatio = (float)width / (float)height;
        
        float scale = 1.0f;
        
        // Calculate the scale we need to apply and the possible crop
        if(aspectRatio > m_aspectRatio)
        {
            scale = (float)height / (float)m_virtualHeight;
            m_viewport.x = (width - m_virtualWidth * scale) * 0.5f;
        }
        else if(aspectRatio < m_aspectRatio)
        {
            scale = (float)width / (float)m_virtualWidth;
            m_viewport.y = (height - m_virtualHeight * scale) * 0.5f;
        }
        else
        {
            scale = (float)width/(float)m_virtualWidth;
        }
        
        // New witdh and  height
        m_viewport.width = (float)m_virtualWidth * scale;
        m_viewport.height = (float)m_virtualHeight * scale;
	}
	
	public void setScreen(String name) {
		Screen screen = m_screens.get(name);
		
		if (screen != null) {
			m_nextScreen = screen;
		}
		else
		{
			m_logger.error("screen " + name + "does not exist");
		}
	}
	
	@Override
	public void setScreen(Screen screen) {
		if (screen != null) {
			m_nextScreen = screen;
		}
		else
		{
			m_logger.error("trying to set an invalid screen");
		}
	}
	
	public Screen getScreen(String name) {
		Screen screen = m_screens.get(name);
		
		if (screen == null) {
			m_logger.error("screen " + name + "does not exist");
		}
		
		return screen;
	}
	
	public static int getVirtualWidth() {
		return m_virtualWidth;
	}
	
	public static int getVirtualHeight() {
		return m_virtualHeight;
	}
	
	public static Settings getSettings() {
		return m_settings;
	}
	
	public static AssetManager getAssetManager() {
		return m_assetManager;
	}
	
	public static IDGenerator getIDGenerator() {
		return m_IDGenerator;
	}
	
	public static PlatformResolver getPlatformResolver() {
		return m_resolver;
	}
	
	public static void setPlatformResolver(PlatformResolver platformResolver) {
		m_resolver = platformResolver;
	}
	
	public static TweenManager getTweenManager() {
		return m_tweenManager;
	}
	
	public static LanguageManager getLanguageManager() {
		return m_languageManager;
	}
	
	public static float getUnitsPerPixel() {
		return m_unitsPerPixel;
	}
	
	public static float getPixelsPerUnit() {
		return m_pixelsPerUnit;
	}
	
	public static World getWorld() {
		return m_world;
	}
	
	public static EntityWorld getEntityWorld() {
		return m_entityWorld;
	}
	
	public static SpriteBatch getBatch() {
		return m_batch;
	}
	
	public static OrthographicCamera getCamera() {
		return m_camera;
	}
	
	private void applyScreenChange() {
		if (m_nextScreen != null) {
			super.setScreen(m_nextScreen);
			m_nextScreen = null;
			m_logger.info("changing to screen " + m_screens.findKey(getScreen(), true));
		}
	}
}
