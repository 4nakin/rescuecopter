package com.siondream.rescue;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.sionengine.SionEngine;
import com.badlogic.gdx.sionengine.entity.EntityWorld;
import com.badlogic.gdx.sionengine.entity.components.AnimatedSprite;
import com.badlogic.gdx.sionengine.entity.components.Asset;
import com.badlogic.gdx.sionengine.entity.components.Physics;
import com.badlogic.gdx.sionengine.entity.components.State;
import com.badlogic.gdx.sionengine.entity.components.Transform;
import com.badlogic.gdx.sionengine.entity.components.Type;
import com.badlogic.gdx.sionengine.entity.pools.AnimatedSpritePool;
import com.badlogic.gdx.sionengine.entity.pools.AssetPool;
import com.badlogic.gdx.sionengine.entity.pools.PhysicsPool;
import com.badlogic.gdx.sionengine.entity.pools.StatePool;
import com.badlogic.gdx.sionengine.entity.pools.TransformPool;
import com.badlogic.gdx.sionengine.entity.pools.TypePool;
import com.badlogic.gdx.sionengine.entity.systems.AssetSystem;
import com.badlogic.gdx.sionengine.entity.systems.DisposingSystem;
import com.badlogic.gdx.sionengine.entity.systems.PhysicsSystem;
import com.badlogic.gdx.sionengine.entity.systems.RenderingSystem;
import com.badlogic.gdx.utils.Logger;

public class RescueCopter extends SionEngine {
	
	private Skin m_skin = null;
	
	@Override
	public void create() {		
		super.create();
		
		EntityWorld world = getEntityWorld();
		
		world.setComponentPool(Transform.class, new TransformPool());
		world.setComponentPool(Physics.class, new PhysicsPool());
		world.setComponentPool(AnimatedSprite.class, new AnimatedSpritePool());
		world.setComponentPool(State.class, new StatePool());
		world.setComponentPool(Asset.class, new AssetPool());
		world.setComponentPool(Type.class, new TypePool());
		
		world.addSystem(new AssetSystem(1));
		world.addSystem(new RenderingSystem(2, SionEngine.getCamera()));
		world.addSystem(new PlayerController(3));
		world.addSystem(new AbductionSystem(4));
		world.addSystem(new PhysicsSystem(5, SionEngine.getCamera()));
		world.addSystem(new CollisionHandlingSystem(this, 6));
		world.addSystem(new DisposingSystem(7));
		
		world.prepare();
		
		m_skin = new Skin(Gdx.files.internal("data/uiskin.json"));
	
		m_screens.put("GameScreen", new GameScreen(this));
		setScreen("GameScreen");
	}
	
	public Skin getSkin() {
		return m_skin;
	}
}
