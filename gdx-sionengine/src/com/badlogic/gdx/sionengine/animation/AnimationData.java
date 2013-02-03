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

package com.badlogic.gdx.sionengine.animation;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.sionengine.SionEngine;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.Logger;

/**
 * @class AnimationData
 * @author David Saltares Márquez
 * @date 09/09/2012
 * 
 * @brief Holds animation frame sequences and reads them from JSON files and a spritesheet-like texture
 * 
 * It will look for a name.json and name.png files for loading.
 *
 */
public class AnimationData {
	private Logger m_logger; 
	
	// Package private members for the AnimationLoader ease of access
	Texture m_texture = null;
	int m_rows = 0;
	int m_columns = 0;
	float m_frameDuration = 0.0f;
	IntMap<Animation> m_animations = new IntMap<Animation>();
	Animation m_defaultAnimation = null;
	
	/**
	 * @param loggingLevel verbosity of the animation data logging
	 */
	public AnimationData(int loggingLevel) {
		m_logger = new Logger("AnimationData", loggingLevel);
	}
	
	/**
	 * @param animationName name of the desired animation
	 * @return animation object containing the sequence of frames, null if not found
	 */
	public Animation getAnimation(String animationName) {
		return getAnimation(SionEngine.getIDGenerator().getID(animationName));
	}
	
	/**
	 * @param animationID id of the desired animation
	 * @return animation object containing the sequence of frames, null if not found
	 */
	public Animation getAnimation(int animationID) {
		Animation animation = m_animations.get(animationID);
		
		if (animation == null) {
			m_logger.info("Animation: " +
						  SionEngine.getIDGenerator().getString(animationID) +
						  " not found returning default");
			
			return m_defaultAnimation;
		}
		
		return animation;
	}
}
