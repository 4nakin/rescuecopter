package com.badlogic.gdx.sionengine.entity.pools;

import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.sionengine.SionEngine;
import com.badlogic.gdx.sionengine.entity.components.AnimatedSprite;

public class AnimatedSpritePool extends Pool<AnimatedSprite> {

	public AnimatedSpritePool() {
		super(SionEngine.getEntityWorld().getMaxEntities());
	}
	
	public AnimatedSpritePool(int poolSize) {
		super(poolSize);
	}
	
	@Override
	protected AnimatedSprite newObject() {
		return new AnimatedSprite();
	}
	
	@Override
	public String toString() {
		return "AnimatedSprite";
	}
}
