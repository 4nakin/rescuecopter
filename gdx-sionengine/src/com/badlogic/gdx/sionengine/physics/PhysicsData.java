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

import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.MassData;
import com.badlogic.gdx.utils.Array;

public class PhysicsData {
	
	BodyDef m_bodyDef = new BodyDef();
	MassData m_massData = new MassData();
	Array<FixtureDef> m_fixtureDefs = new Array<FixtureDef>();
	Array<Filter> m_filters = new Array<Filter>();
	Array<Integer> m_fixtureIds = new Array<Integer>();
	
	PhysicsData() {
	}
	
	public BodyDef getBodyDef() {
		return m_bodyDef;
	}
	
	public MassData getMassData() {
		return m_massData;
	}
	
	public Array<FixtureDef> getFixtureDefs() {
		return m_fixtureDefs;
	}
	
	public Array<Integer> getFixtureIds() {
		return m_fixtureIds;
	}
	
	public Array<Filter> getFilters() {
		return m_filters;
	}
}
