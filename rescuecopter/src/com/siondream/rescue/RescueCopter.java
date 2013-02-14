package com.siondream.rescue;

import com.badlogic.gdx.sionengine.Settings;
import com.badlogic.gdx.sionengine.SionEngine;
import com.badlogic.gdx.sionengine.entity.EntityWorld;
import com.badlogic.gdx.sionengine.entity.components.AnimatedSprite;
import com.badlogic.gdx.sionengine.entity.components.Asset;
import com.badlogic.gdx.sionengine.entity.components.Physics;
import com.badlogic.gdx.sionengine.entity.components.State;
import com.badlogic.gdx.sionengine.entity.components.Transform;
import com.badlogic.gdx.sionengine.entity.pools.AnimatedSpritePool;
import com.badlogic.gdx.sionengine.entity.pools.AssetPool;
import com.badlogic.gdx.sionengine.entity.pools.PhysicsPool;
import com.badlogic.gdx.sionengine.entity.pools.StatePool;
import com.badlogic.gdx.sionengine.entity.pools.TransformPool;
import com.badlogic.gdx.sionengine.entity.systems.AssetSystem;
import com.badlogic.gdx.sionengine.entity.systems.PhysicsSystem;
import com.badlogic.gdx.sionengine.entity.systems.RenderingSystem;
import com.badlogic.gdx.utils.Logger;

public class RescueCopter extends SionEngine {
	
	@Override
	public void create() {		
		super.create();
		
		EntityWorld world = getEntityWorld();
		world.setComponentPool(Transform.class, new TransformPool());
		world.setComponentPool(Physics.class, new PhysicsPool());
		world.setComponentPool(AnimatedSprite.class, new AnimatedSpritePool());
		world.setComponentPool(State.class, new StatePool());
		world.setComponentPool(Asset.class, new AssetPool());
		
		Settings settings = getSettings();
		
		world.addSystem(new AssetSystem(world,
										1,
										settings.getInt("assetSystemLoggingLevel", Logger.INFO)));
		
		world.addSystem(new RenderingSystem(world,
											2,
											settings.getInt("renderingSystemLoggingLevel", Logger.INFO),
											SionEngine.getBatch(),
											SionEngine.getCamera()));
		
		world.addSystem(new PlayerController(world,
											 3,
											 Logger.INFO));
		
		world.addSystem(new PhysicsSystem(world,
										  4,
										  settings.getInt("physicsSystemLoggingLevel", Logger.INFO),
										  SionEngine.getWorld(),
										  SionEngine.getCamera()));
		
		world.prepare();
		
		m_screens.put("GameScreen", new GameScreen(this));
		setScreen("GameScreen");
	}
}
