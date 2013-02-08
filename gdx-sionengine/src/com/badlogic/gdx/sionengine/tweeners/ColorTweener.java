package com.badlogic.gdx.sionengine.tweeners;

import com.badlogic.gdx.graphics.Color;
import aurelienribon.tweenengine.TweenAccessor;


public class ColorTweener implements TweenAccessor<Color> {

	public static final int Color = 1;
	
	@Override
	public int getValues(Color color, int tweenType, float[] returnValues) {
		returnValues[0] = color.r;
		returnValues[1] = color.g;
		returnValues[2] = color.b;
		returnValues[3] = color.a;
		return 4;
	}

	@Override
	public void setValues(Color color, int tweenType, float[] newValues) {
		color.r = newValues[0];
		color.g = newValues[1];
		color.b = newValues[2];
		color.a = newValues[3];
	}
	
}
