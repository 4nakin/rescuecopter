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

package com.badlogic.gdx.sionengine;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

/**
 * @class IDGenerator
 * @author David Saltares Márquez
 * @date 02/09/2012
 * 
 * @brief Generates integer IDs from strings and keeps a cache to avoid string comparisons at runtime
 *
 */
public class IDGenerator {
	private int m_next = 1;
	private HashMap<String, Integer> m_ids = new HashMap<String, Integer>();
	
	/**
	 * Creates or retrieves an ID with the given key
	 * 
	 * @param string string representation for the ID
	 * @return newly created or just retrieved ID
	 */
	public int getID(String string) {
		// Try to fetch ID
		Integer id = m_ids.get(string);
		
		// If it's the first time we try to get it, generate it
		if (id == null) {
			id = m_next;
			m_ids.put(string, m_next++);
		}

		return id;
	}
	
	/**
	 * Retrieves the string representation of a previously existing ID
	 * 
	 * @param id id which string is wanted
	 * @return string represention of the ID, null if it wasn't previously registered
	 */
	public String getString(int id) {
		Iterator<Entry<String, Integer>> it = m_ids.entrySet().iterator();
		
		while (it.hasNext()) {
			Entry<String, Integer> entry = it.next();
			
			if (entry.getValue() == id) {
				return entry.getKey();
			}
		}
		
		return null;
	}
}
