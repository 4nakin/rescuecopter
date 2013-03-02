package com.siondream.rescue;

import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.sionengine.SionEngine;
import com.badlogic.gdx.sionengine.entity.EntityWorld;
import com.badlogic.gdx.sionengine.entity.systems.PhysicsSystem;
import com.badlogic.gdx.sionengine.maps.CircleMapObject;
import com.badlogic.gdx.sionengine.maps.Map;
import com.badlogic.gdx.sionengine.maps.MapLayer;
import com.badlogic.gdx.sionengine.maps.MapObject;
import com.badlogic.gdx.sionengine.maps.MapObjects;
import com.badlogic.gdx.sionengine.maps.MapProperties;
import com.badlogic.gdx.sionengine.maps.PolygonMapObject;
import com.badlogic.gdx.sionengine.maps.PolylineMapObject;
import com.badlogic.gdx.sionengine.maps.RectangleMapObject;
import com.badlogic.gdx.sionengine.maps.TextureMapObject;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Logger;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.XmlReader;
import com.badlogic.gdx.utils.XmlReader.Element;

public class MapBodyManager {
	private Logger m_logger;
	private Array<Body> m_bodies = new Array<Body>();
	private ObjectMap<String, FixtureDef> m_materials = new ObjectMap<String, FixtureDef>();
	
	public MapBodyManager() {
		this("data/materials.xml", Logger.INFO);
	}
	
	public MapBodyManager(String materialsFile, int loggingLevel) {
		m_logger = new Logger("MapBodyManager", loggingLevel);
		m_logger.info("initialising");
		
		if (materialsFile != null) {
			loadMaterialsFile(materialsFile);
		}
	}
	
	public void createPhysics(Map map) {
		createPhysics(map, "physics");
	}
	
	public void createPhysics(Map map, String layerName) {
		EntityWorld entityWorld = SionEngine.getEntityWorld();
		PhysicsSystem physicsSystem = entityWorld.getSystem(PhysicsSystem.class);
		World world = physicsSystem.getWorld();
		float units = SionEngine.getUnitsPerPixel();
		
		MapLayer layer = map.getLayers().getLayer(layerName);
		
		if (layer == null) {
			m_logger.error("layer " + layerName + " does not exist");
			return;
		}
		
		MapObjects objects = layer.getObjects();
		Iterator<MapObject> objectIt = objects.iterator();
			
		while(objectIt.hasNext()) {
			MapObject object = objectIt.next();
			
			if (object instanceof TextureMapObject){
				continue;
			}
			
			Shape shape;
			
			if (object instanceof RectangleMapObject) {
				RectangleMapObject rectangleObject = (RectangleMapObject)object;
				Rectangle rectangle = rectangleObject.getRectangle();
				PolygonShape polygon = new PolygonShape();
				polygon.setAsBox(rectangle.width * 0.5f * units,
								 rectangle.height * 0.5f * units,
								 new Vector2((rectangle.x + rectangle.width * 0.5f) * units, (rectangle.y - rectangle.height * 0.5f ) * units),
								 0.0f);
				shape = polygon;
			}
			else if (object instanceof PolygonMapObject) {
				PolygonMapObject polygonObject = (PolygonMapObject)object;
				PolygonShape polygon = new PolygonShape();
				float[] vertices = polygonObject.getPolygon().getVertices();
				float[] worldVertices = new float[vertices.length];
				
				for (int i = 0; i < vertices.length; ++i) { 
					worldVertices[i] = vertices[i] * units;
				}
				
				polygon.set(worldVertices);
				shape = polygon;
			}
			else if (object instanceof PolylineMapObject) {
				PolylineMapObject polylineObject = (PolylineMapObject)object;
				float[] vertices = polylineObject.getPolygon().getVertices();
				Vector2[] worldVertices = new Vector2[vertices.length / 2];
				
				for (int i = 0; i < vertices.length / 2; ++i) {
					worldVertices[i] = new Vector2();
					worldVertices[i].x = vertices[i * 2] * units;
					worldVertices[i].y = vertices[i * 2 + 1] * units;
				}
				
				ChainShape chain = new ChainShape(); 
				chain.createChain(worldVertices);
				shape = chain;
			}
			else if (object instanceof CircleMapObject) {
				CircleMapObject circleObject = (CircleMapObject)object;
				
				Circle circle = circleObject.getCircle();
				CircleShape circleShape = new CircleShape();
				circleShape.setRadius(circle.radius * units);
				circleShape.setPosition(new Vector2(circle.x * units, circle.y * units));
				shape = circleShape;
			}
			else {
				m_logger.error("non suported shape " + object);
				continue;
			}
			
			MapProperties properties = object.getProperties();
			String material = properties.getAsString("material", "default");
			FixtureDef fixtureDef = m_materials.get(material);
			
			if (fixtureDef == null) {
				m_logger.error("material does not exist " + material);
				continue;
			}
			
			fixtureDef.shape = shape;
			
			BodyDef bodyDef = new BodyDef();
			bodyDef.type = BodyDef.BodyType.StaticBody;
			
			Body body = world.createBody(bodyDef);
			body.createFixture(fixtureDef);
			
			m_bodies.add(body);
			
			fixtureDef.shape = null;
			shape.dispose();
		}
	}
	
	public void destroyPhysics() {
		EntityWorld entityWorld = SionEngine.getEntityWorld();
		PhysicsSystem physicsSystem = entityWorld.getSystem(PhysicsSystem.class);
		World world = physicsSystem.getWorld();
		
		for (Body body : m_bodies) {
			world.destroyBody(body);
		}
		
		m_bodies.clear();
	}
	
	private void loadMaterialsFile(String materialsFile) {
		m_logger.info("adding default material");
		
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.density = 1.0f;
		fixtureDef.friction = 1.0f;
		fixtureDef.restitution = 0.0f;
		m_materials.put("default", fixtureDef);
		
		m_logger.info("loading materials file");
		
		try {
			XmlReader reader = new XmlReader();
			Element root = reader.parse(Gdx.files.internal(materialsFile));
			
			Array<Element> materials = root.getChildrenByName("materials");
			
			for (Element material : materials) {
				String name = material.getAttribute("name");
				
				if (name == null) {
					m_logger.error("material without name");
					continue;
				}
				
				fixtureDef = new FixtureDef();
				fixtureDef.density = Float.parseFloat(material.getAttribute("density", "1.0"));
				fixtureDef.friction = Float.parseFloat(material.getAttribute("friction", "1.0"));
				fixtureDef.restitution = Float.parseFloat(material.getAttribute("restitution", "1.0"));
				m_logger.info("adding material " + name);
				m_materials.put(name, fixtureDef);
			}
			
		} catch (Exception e) {
			m_logger.error("error loading " + materialsFile + " " + e.getMessage());
		}
	}
}
