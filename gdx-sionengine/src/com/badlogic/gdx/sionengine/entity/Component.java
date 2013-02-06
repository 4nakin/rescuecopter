package com.badlogic.gdx.sionengine.entity;

import com.badlogic.gdx.utils.Pool.Poolable;

public abstract class Component implements Poolable {
	private static int m_nextComponentType = 0;

	abstract public int getType();
	
	protected static int generateType() {
		return m_nextComponentType++;
	}
}
