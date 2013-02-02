package com.badlogic.gdx.sionengine;

import aurelienribon.tweenengine.TweenManager;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.World;
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
	private static int m_virtualWidth;
	private static int m_virtualHeight;
	private static float m_unitsPerPixel;
	private static float m_pixelsPerUnit;
	
	private ObjectMap<String, Screen> m_screens;
	private Screen m_nextScreen = null;
	private Logger m_logger;
	private Rectangle m_viewport = new Rectangle(0, 0, 0, 0);
	private float m_aspectRatio;
	
	
	@Override
	public void create() {
		m_settings = new Settings();
		
		m_logger = new Logger("SionEngine", m_settings.getInt("engineLoggingLevel", Logger.INFO));
		
		m_IDGenerator = new IDGenerator();
		
		m_assetManager = new AssetManager();
		
		m_tweenManager = new TweenManager();
		
		m_languageManager = new LanguageManager();
		
		m_screens = new ObjectMap<String, Screen>();
		
		m_virtualWidth = m_settings.getInt("virtualWidth", 1280);
		m_virtualHeight = m_settings.getInt("virtualHeight", 720);
		m_aspectRatio = (float)m_virtualWidth/(float)m_virtualHeight;
		
		m_pixelsPerUnit = m_settings.getFloat("pixelsPerUnit", 1.0f);
		m_unitsPerPixel = 1.0f / m_pixelsPerUnit;
		
		if (m_settings.getBoolean("enableBox2D", true)) {
			final Vector3 gravity = m_settings.getVector("gravity", Vector3.Zero);
			m_world = new World(new Vector2(gravity.x, gravity.y),
								m_settings.getBoolean("doSleep", true));
		}
		
		Gdx.graphics.setTitle(m_settings.getString("windowTitle", "SionEngine"));
		Gdx.graphics.setDisplayMode(m_virtualWidth, m_virtualHeight, m_settings.getBoolean("fullScreen", false));
	}
	
	@Override
	public void render () {
		
		Gdx.gl.glClearColor(0, 0, 0, 0);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		Gdx.gl.glEnable(GL10.GL_BLEND);
		
		Gdx.gl.glViewport((int) m_viewport.x,
						  (int) m_viewport.y,
						  (int) m_viewport.width,
						  (int) m_viewport.height);
		
		super.render();
		
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
	
	private void applyScreenChange() {
		if (m_nextScreen != null) {
			setScreen(m_nextScreen);
			m_nextScreen = null;
		}
	}
}
