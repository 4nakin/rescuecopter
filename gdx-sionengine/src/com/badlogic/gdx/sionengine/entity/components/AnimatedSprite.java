package com.badlogic.gdx.sionengine.entity.components;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.sionengine.SionEngine;
import com.badlogic.gdx.sionengine.animation.AnimationData;
import com.badlogic.gdx.sionengine.entity.Component;

public class AnimatedSprite extends Component implements AsynchronousAsset {
	// Vertices size
	public static final int VertexSize = 2 + 1 + 2;
	public static final int SpriteSize = 4 * VertexSize;
	
	// Basic data
	private String m_file = null;
	private AnimationData m_data = null;
	
	// Geometry
	private TextureRegion m_frame = null;
	private final float[] m_vertices = new float[SpriteSize];
	private Color m_color = new Color(1.0f, 1.0f, 1.0f, 1.0f);
	private Vector2 m_size = Vector2.Zero.cpy();
	private Vector2 m_origin = Vector2.Zero.cpy();
	private boolean m_dirty = true;
	
	// Cached trasform (to avoid vertex recalculations)
	private Vector3 m_position = Vector3.Zero.cpy();
	private float m_scale = 0.0f;
	private float m_angle = 0.0f;
	
	// Current state
	private Integer m_animationID = 0;
	private Animation m_animation = null;
	private float m_time = 0.0f;
	private boolean m_playing = true;
	private boolean m_flipX = false;
	private boolean m_flipY = false;
	
	public AnimatedSprite() {
		setColor(m_color);
	}
	
	public String getFileName() {
		return m_file;
	}
	
	public void setFileName(String file) {
		if (m_file != file) {
			reset();
			
			m_file = file;
			
			if (m_file != null) {
				SionEngine.getAssetManager().load(m_file, AnimationData.class);
			}
		}
	}
	
	public Color getColor() {
		return m_color;
	}
	
	public void setColor(Color tint) {
		float color = tint.toFloatBits();
		m_vertices[C1] = color;
		m_vertices[C2] = color;
		m_vertices[C3] = color;
		m_vertices[C4] = color;
	}
	
	public Vector2 getSize() {
		return m_size;
	}
	
	public Vector2 getOrigin() {
		return m_origin;
	}
	
	public float[] getVertices() {
		return m_vertices;
	}
	
	public Texture getTexture() {
		return m_data.getTexture();
	}
	
	public boolean getFlipX() {
		return m_flipX;
	}
	
	public boolean getFlipY() {
		return m_flipY;
	}
	
	public void flip (boolean x, boolean y) {
		m_flipX = x;
		m_flipY = y;
	}
	
	public void updateState(int state) {
		// Check whether we need to change animation or not
		if (state != m_animationID) {
			// Set animation
			m_animationID = state;
			
			// Fetch animation
			m_animation = m_data.getAnimation(m_animationID);
			
			// Restart timer
			m_time = 0.0f;
		}
	}
	
	public void updateFrame(float deltaT) {
		if (m_animation != null && m_playing) {
			m_time += deltaT;
			setRegion(m_animation.getKeyFrame(m_time));
		}
	}
	
	public void applyTransform(Vector3 position, float angle, float scale) {
		if (!m_position.equals(position) || m_angle != angle || m_scale != scale) {
			m_position.set(position);
			m_scale = scale;
			m_angle = angle;
			m_dirty = true;
		}
	}
	
	public void computeVertices() {
		// If we have recently applied a transform, we need to recompute the vertices
		if (m_dirty) {
			m_dirty = false;
			
			// Apply translation
			float localX = -m_origin.x;
			float localY = -m_origin.y;
			float localX2 = localX + m_size.x;
			float localY2 = localY + m_size.y;

			float finalScale = m_scale * SionEngine.getUnitsPerPixel();
			
			// Apply scale
			if (finalScale != 1.0) {
				localX *= finalScale;
				localY *= finalScale;
				localX2 *= finalScale;
				localY2 *= finalScale;
			}
			
			// Apply rotation
			if (m_angle != 0.0f) {
				final float cos = MathUtils.cos(m_angle);
				final float sin = MathUtils.sin(m_angle);
				final float localXCos = localX * cos;
				final float localXSin = localX * sin;
				final float localYCos = localY * cos;
				final float localYSin = localY * sin;
				final float localX2Cos = localX2 * cos;
				final float localX2Sin = localX2 * sin;
				final float localY2Cos = localY2 * cos;
				final float localY2Sin = localY2 * sin;

				final float x1 = localXCos - localYSin + m_position.x;
				final float y1 = localYCos + localXSin + m_position.y;
				m_vertices[X1] = x1;
				m_vertices[Y1] = y1;

				final float x2 = localXCos - localY2Sin + m_position.x;
				final float y2 = localY2Cos + localXSin + m_position.y;
				m_vertices[X2] = x2;
				m_vertices[Y2] = y2;

				final float x3 = localX2Cos - localY2Sin + m_position.x;
				final float y3 = localY2Cos + localX2Sin + m_position.y;
				m_vertices[X3] = x3;
				m_vertices[Y3] = y3;

				m_vertices[X4] = x1 + (x3 - x2);
				m_vertices[Y4] = y3 - (y2 - y1);
			}
			else {
				final float x1 = localX + m_position.x;
				final float y1 = localY + m_position.y;
				final float x2 = localX2 + m_position.x;
				final float y2 = localY2 + m_position.y;

				m_vertices[X1] = x1;
				m_vertices[Y1] = y1;

				m_vertices[X2] = x1;
				m_vertices[Y2] = y2;

				m_vertices[X3] = x2;
				m_vertices[Y3] = y2;

				m_vertices[X4] = x2;
				m_vertices[Y4] = y1;
			}
		}
	}
	
	@Override
	public void reset() {
		m_data = null;
		m_animationID = 0;
		m_animation = null;
		m_time = 0.0f;
		m_playing = true;
		m_flipX = false;
		m_flipY = false;
		m_dirty = true;
		m_color.r = 1.0f;
		m_color.g = 1.0f;
		m_color.b = 1.0f;
		m_color.a = 1.0f;
		
		if (m_file != null) {
			SionEngine.getAssetManager().unload(m_file);
			m_file = null;
		}
	}
	
	@Override
	public void fetchAssets() {
		AssetManager manager = SionEngine.getAssetManager();
		
		if (manager.isLoaded(m_file, AnimationData.class)) {
			m_data = manager.get(m_file, AnimationData.class);
			m_animationID = 0;
			m_animation = m_data.getAnimation(m_animationID);
			TextureRegion region = m_animation.getKeyFrame(m_time);
			setRegion(region);
		}
	}
	
	@Override
	public boolean isLoaded() {
		return m_data != null;
	}
	
	@Override
	public String toString() {
		return "animated sprite " + m_file;
	}
	
	private void setRegion (TextureRegion region) {
		m_frame = region;
		
		if (m_frame != null) {			
			// Set UV coordinates in the texture
			m_vertices[U1] = m_frame.getU();
			m_vertices[V1] = m_frame.getV2();
			m_vertices[U2] = m_frame.getU();
			m_vertices[V2] = m_frame.getV();
			m_vertices[U3] = m_frame.getU2();
			m_vertices[V3] = m_frame.getV();
			m_vertices[U4] = m_frame.getU2();
			m_vertices[V4] = m_frame.getV2();
			
			// Flip to use an y-down system
			setFlip();
			
			// Update size
			m_size.x = m_frame.getRegionWidth();
			m_size.y = m_frame.getRegionHeight();
			
			// Update center
			m_origin.x = m_size.x / 2.0f;
			m_origin.y = m_size.y / 2.0f;
		}
	}
	
	private void setFlip() {
		m_frame.flip(m_flipX, m_flipY);
		
		if (m_flipX) {
			float u = m_vertices[U1];
			float u2 = m_vertices[U3];
			m_frame.setU(u);
			m_frame.setU2(u2);
			m_vertices[U1] = u2;
			m_vertices[U2] = u2;
			m_vertices[U3] = u;
			m_vertices[U4] = u;
		}
		if (m_flipY) {
			float v = m_vertices[V2];
			float v2 = m_vertices[V1];
			m_frame.setV(v);
			m_frame.setV2(v2);
			m_vertices[V1] = v;
			m_vertices[V2] = v2;
			m_vertices[V3] = v2;
			m_vertices[V4] = v;
		}
	}
	
	// Constant to help with the vertices indices
	static private final int X1 = 0;
	static private final int Y1 = 1;
	static private final int C1 = 2;
	static private final int U1 = 3;
	static private final int V1 = 4;
	static private final int X2 = 5;
	static private final int Y2 = 6;
	static private final int C2 = 7;
	static private final int U2 = 8;
	static private final int V2 = 9;
	static private final int X3 = 10;
	static private final int Y3 = 11;
	static private final int C3 = 12;
	static private final int U3 = 13;
	static private final int V3 = 14;
	static private final int X4 = 15;
	static private final int Y4 = 16;
	static private final int C4 = 17;
	static private final int U4 = 18;
	static private final int V4 = 19;
}
