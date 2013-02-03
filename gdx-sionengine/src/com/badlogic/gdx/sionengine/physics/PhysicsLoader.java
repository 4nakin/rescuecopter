/*  Copyright 2012 SionEngine
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package com.badlogic.gdx.sionengine.physics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.sionengine.SionEngine;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Logger;
import com.badlogic.gdx.utils.XmlReader;
import com.badlogic.gdx.utils.XmlReader.Element;

public class PhysicsLoader extends AsynchronousAssetLoader<PhysicsData, PhysicsLoader.PhysicsParameter > {
	
	static public class PhysicsParameter extends AssetLoaderParameters<PhysicsData> {}

	private Logger m_logger;
	private PhysicsData m_physicsData = null;
	
	public PhysicsLoader(FileHandleResolver resolver) {
		this(resolver, Logger.INFO);
	}
	
	public PhysicsLoader(FileHandleResolver resolver, int loggingLevel) {
		super(resolver);
		m_logger = new Logger("PhysicsLoader", loggingLevel);
	}

	@Override
	public void loadAsync(AssetManager manager, String fileName, PhysicsParameter parameter) {
		m_physicsData = new PhysicsData();
		loadData(fileName);
	}

	@Override
	public PhysicsData loadSync(AssetManager manager, String fileName, PhysicsParameter parameter) {
		return m_physicsData;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Array<AssetDescriptor> getDependencies(String fileName, PhysicsParameter parameter) {
		return new Array<AssetDescriptor>();
	}
	
	private void loadData(String fileName) {
		m_logger.info("loading " + fileName);
		
		try {
			XmlReader reader = new XmlReader();
			Element root = reader.parse(Gdx.files.internal(fileName));
			
			loadBodyDef(root);
			loadMassData(root);
			loadFixtureDefs(root);
			
		} catch (Exception e) {
			m_logger.error("error loading " + fileName + " " + e.getMessage());
		}
	}
	
	private void loadBodyDef(Element root) {
		m_logger.info("loading BodyDef");
		
		m_physicsData.m_bodyDef.bullet = root.getBoolean("bullet", false);
		m_physicsData.m_bodyDef.active = root.getBoolean("active", true);
		m_physicsData.m_bodyDef.fixedRotation = root.getBoolean("fixedRotation", false);
		m_physicsData.m_bodyDef.gravityScale = root.getFloat("gravityScale", 1.0f);
		
		String type = root.get("type", "dynamic");
		
		if (type.equals("dynamic")) {
			m_physicsData.m_bodyDef.type = BodyDef.BodyType.DynamicBody;
		}
		else if (type.equals("kynematic")) {
			m_physicsData.m_bodyDef.type = BodyDef.BodyType.KinematicBody;
		}
		else if (type.equals("static")) {
			m_physicsData.m_bodyDef.type = BodyDef.BodyType.KinematicBody;
		}
		else {
			m_logger.error("unknown body type " + type);
		}
	}
	
	private void loadMassData(Element root) {
		Element massData = root.getChildByName("massData");
		if (massData != null) {
			m_logger.info("loading mass data");
			m_physicsData.m_massData.center.x = massData.getFloat("centerX", 0.0f);
			m_physicsData.m_massData.center.y = massData.getFloat("centerY", 0.0f);
			m_physicsData.m_massData.I = massData.getFloat("i", 0.0f);
			m_physicsData.m_massData.mass = massData.getFloat("mass", 1.0f);
		}
	}
	
	private void loadFixtureDefs(Element root) {
		Array<Element> fixtures = root.getChildrenByName("fixture");
		
		for (int i = 0; i < fixtures.size; ++i) {
			Element fixtureElement = fixtures.get(i);
			
			FixtureDef fixtureDef = new FixtureDef();
			
			fixtureDef.density = fixtureElement.getFloat("density", 1.0f);
			fixtureDef.restitution = fixtureElement.getFloat("restitution", 0.0f);
			fixtureDef.friction = fixtureElement.getFloat("friction", 1.0f);
			fixtureDef.isSensor = fixtureElement.getBoolean("isSensor", false);
			fixtureDef.shape = loadShape(fixtureElement);
			String id = fixtureElement.get("id", "");
			
			m_logger.info("loading fixture with id " + id);
			
			m_physicsData.m_filters.add(loadFilter(fixtureElement));
			m_physicsData.m_fixtureIds.add(SionEngine.getIDGenerator().getID(id));
			m_physicsData.m_fixtureDefs.add(fixtureDef);
		}
	}
	
	private Filter loadFilter(Element root) {
		Element filterElement = root.getChildByName("filter");
		Filter filter = new Filter();
		
		if (filterElement == null) { 
			m_logger.info("no filter for shape, returning default one");
			return filter;
		}
		
		m_logger.info("loading filter");
		filter.categoryBits = (short)filterElement.getInt("categoryBits", 0);
		filter.groupIndex = (short)filterElement.getInt("groupIndex", 0);
		filter.maskBits = (short)filterElement.getInt("maskBits", 0);
		
		return filter;
	}
	
	private Shape loadShape(Element root) {
		Shape shape = null;
		Element shapeElement = root.getChildByName("shape");
		
		if (shapeElement == null) {
			return shape;
		}
		
		String type = shapeElement.get("type");

		float x = shapeElement.getFloat("centerX", 0.0f);
		float y = shapeElement.getFloat("centerY", 0.0f);
		
		if (type.equals("circle")) {
			m_logger.info("loading cicle shape");
			CircleShape circle = new CircleShape();
			circle.setPosition(new Vector2(x, y));
			circle.setRadius(shapeElement.getFloat("radius", 1.0f));
			shape = circle;
		}
		else if (type.equals("polygon")) {
			m_logger.info("loading polygon shape");
			PolygonShape polygon = new PolygonShape();
			polygon.setAsBox(shapeElement.getFloat("width", 1.0f),
							 shapeElement.getFloat("height", 1.0f),
							 new Vector2(x, y),
							 0.0f);
			shape = polygon;
		}
		else {
			m_logger.error("shape unknown " + type);
		}
		
		
		return shape;
	}
}
