package com.badlogic.gdx.sionengine.tweeners;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.sionengine.entity.components.Transform;

import aurelienribon.tweenengine.TweenAccessor;

public class TransformTweener implements TweenAccessor<Transform> {

	public static final int Position = 1;
	public static final int Rotation = 2;
	public static final int Scale = 3;
	
	@Override
	public int getValues(Transform transform, int tweenType, float[] returnValues) {
		switch (tweenType) {
		case Position:
			Vector3 position = transform.getPosition();
			returnValues[0] = position.x;
			returnValues[1] = position.y;
			returnValues[2] = position.z;
			return 3;
		case Scale:
			returnValues[0] = transform.getScale();
			return 1;
		case Rotation:
			returnValues[0] = transform.getRotation();
			return 1;	
		default:
			return 0;
		}
	}

	@Override
	public void setValues(Transform transform, int tweenType, float[] newValues) {
		switch (tweenType) {
		case Position:
			Vector3 position = transform.getPosition();
			position.x = newValues[0];
			position.y = newValues[1];
			position.z = newValues[2];
			break;
		case Scale:
			transform.setScale(newValues[0]);
			break;
		case Rotation:
			transform.setRotation(newValues[0]);
			break;
		default:
			break;
		}
	}

}
