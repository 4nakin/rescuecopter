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

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map.Entry;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Logger;
import com.badlogic.gdx.utils.XmlReader;
import com.badlogic.gdx.utils.XmlReader.Element;
import com.badlogic.gdx.utils.XmlWriter;

/**
 * @class Settings
 * @author David Saltares Márquez
 * @date 02/09/2012
 * 
 * @brief Holds configuration in a key value fashion
 * 
 * It accepts the following format:
 * 
 * @code
	<?xml version="1.0" encoding="UTF-8"?>
	<settings>
		<string key="appName" value="Sion Engine"
		<int key="virtualWidth" value="1280" />
		<float key="ppm" value="30.0" />
		<vector key="gravity" x="0.0" y="12.0" z="0.0" />
		<bool key="drawBodies" value="true" />
	</settings>
 * @endcode
 */
public class Settings {
	private final Logger m_logger = new Logger("Settings", Logger.ERROR);
	
	private String m_settingsFile;
	
	private HashMap<String, String> m_strings = new HashMap<String, String>();
	private HashMap<String, Float> m_floats = new HashMap<String, Float>();
	private HashMap<String, Integer> m_ints = new HashMap<String, Integer>();
	private HashMap<String, Boolean> m_booleans = new HashMap<String, Boolean>();
	private HashMap<String, Vector3> m_vectors = new HashMap<String, Vector3>();
	
	/**
	 * Loads the default settings file: data/settings.xml
	 */
	public Settings() {
		this("data/settings.xml");
	}
	
	/**
	 * @param settings configuration file to load
	 */
	public Settings(String settings) {
		m_settingsFile = settings;
		loadSettings();
	}
	
	/**
	 * @param key setting key
	 * @return setting value in string form
	 */
	public String getString(String key) {
		return getString(key, "");
	}
	
	/**
	 * @param key setting key
	 * @param defaultValue default value in case the key doesn't exist 
	 * @return setting value in string form
	 */
	public String getString(String key, String defaultValue) {
		String string = m_strings.get(key);
		
		if (string != null) {
			return string;
		}
		
		m_logger.error(key + " not found, returning default " + defaultValue);
		return defaultValue;
	}
	
	/**
	 * @param key setting key 
	 * @return setting value as a float
	 */
	public float getFloat(String key) {
		return getFloat(key, 0.0f);
	}
	
	/**
	 * @param key setting key
	 * @param defaultValue default value in case the key doesn't exist 
	 * @return setting value as a float
	 */
	public float getFloat(String key, float defaultValue) {
		Float f = m_floats.get(key);
		
		if (f != null) {
			return f;
		}
		
		m_logger.error(key + " not found, returning default " + defaultValue);
		return defaultValue;
	}
	
	/**
	 * @param key setting key 
	 * @return setting value as an int
	 */
	public int getInt(String key) {
		return getInt(key, 0);
	}
	
	/**
	 * @param key setting key
	 * @param defaultValue default value in case the key doesn't exist 
	 * @return setting value as an int
	 */
	public int getInt(String key, int defaultValue) {
		Integer i = m_ints.get(key);
		
		if (i != null) {
			return i;
		}
		
		m_logger.error(key + " not found, returning default " + defaultValue);
		return defaultValue;
	}
	
	/**
	 * @param key setting key 
	 * @return setting value as a boolean
	 */
	public boolean getBoolean(String key) {
		return getBoolean(key, false);
	}
	
	/**
	 * @param key setting key
	 * @param defaultValue default value in case the key doesn't exist 
	 * @return setting value as a boolean
	 */
	public boolean getBoolean(String key, boolean defaultValue) {
		Boolean b = m_booleans.get(key);
		
		if (b != null) {
			return b;
		}
		
		m_logger.error(key + " not found, returning default " + defaultValue);
		return defaultValue;
	}
	
	/**
	 * @param key setting key 
	 * @return setting value as a Vector3
	 */
	public Vector3 getVector(String key) {
		return getVector(key, Vector3.Zero.cpy());
	}
	
	/**
	 * @param key setting key
	 * @param defaultValue default value in case the key doesn't exist 
	 * @return setting value as a Vector3
	 */
	public Vector3 getVector(String key, Vector3 defaultValue) {
		Vector3 v = m_vectors.get(key);
		
		if (v != null) {
			return new Vector3(v);
		}
		
		m_logger.error(key + " not found, returning default " + defaultValue);
		return defaultValue;
	}
	
	/**
	 * Modifies or creates a new setting with the given key
	 * 
	 * @param key key to identify the setting
	 * @param value value of the setting
	 */
	public void setString(String key, String value) {
		m_strings.put(key, value);
	}
	
	/**
	 * Modifies or creates a new setting with the given key
	 * 
	 * @param key key to identify the setting
	 * @param value value of the setting
	 */
	public void setFloat(String key, float value) {
		m_floats.put(key, value);
	}
	
	/**
	 * Modifies or creates a new setting with the given key
	 * 
	 * @param key key to identify the setting
	 * @param value value of the setting
	 */
	public void setInt(String key, int value) {
		m_ints.put(key, value);
	}
	
	/**
	 * Modifies or creates a new setting with the given key
	 * 
	 * @param key key to identify the setting
	 * @param value value of the setting
	 */
	public void setBoolean(String key, boolean value) {
		m_booleans.put(key, value);
	}
	
	/**
	 * Modifies or creates a new setting with the given key
	 * 
	 * @param key key to identify the setting
	 * @param value value of the setting
	 */
	public void setVector(String key, Vector3 value) {
		m_vectors.put(key, new Vector3(value));
	}
	
	/**
	 * Modifies or creates a new setting with the given key
	 * 
	 * @param key key to identify the setting
	 * @param value value of the setting
	 */
	public String getFile() {
		return m_settingsFile;
	}
	
	/**
	 * Modifies or creates a new setting with the given key
	 * 
	 * @param key key to identify the setting
	 * @param value value of the setting
	 */
	public void setFile(String settingsFile) {
		m_settingsFile = settingsFile;
	}
	
	/**
	 * Reloads all the settings trying to use the external storage by default
	 */
	public void loadSettings() {
		loadSettings(true);
	}
	
	/**
	 * Reloads all the settings
	 * 
	 * @param tryExternal if true, it looks for an external settings file first (not in the app internal storage).
	 * Note that external files won't be taken into account in WebGL applications.
	 */
	public void loadSettings(boolean tryExternal) {
		m_logger.info("Settings: loading file " + m_settingsFile);
		
		try {
			FileHandle fileHandle = null;
			
			if (tryExternal &&
				Gdx.app.getType() != Application.ApplicationType.WebGL &&
				Gdx.files.external(m_settingsFile).exists()) {
				fileHandle = Gdx.files.external(m_settingsFile);
				m_logger.info("Settings: loading as external file");
			}
			else {
				fileHandle = Gdx.files.internal(m_settingsFile);
				m_logger.info("Settings: loading as internal file");
			}
			
			XmlReader reader = new XmlReader();
			Element root = reader.parse(fileHandle);

			// Load strings
			m_strings.clear();
			Array<Element> stringNodes = root.getChildrenByName("string");
			
			for (int i = 0; i < stringNodes.size; ++i) {
				Element stringNode = stringNodes.get(i);
				String key = stringNode.getAttribute("key");
				String value = stringNode.getAttribute("value");
				m_strings.put(key, value);
				m_logger.info("Settings: loaded string " + key + " = " + value);
			}
			
			// Load floats
			m_floats.clear();
			Array<Element> floatNodes = root.getChildrenByName("float");
			
			for (int i = 0; i < floatNodes.size; ++i) {
				Element floatNode = floatNodes.get(i);
				String key = floatNode.getAttribute("key");
				Float value = Float.parseFloat(floatNode.getAttribute("value"));
				m_floats.put(key, value);
				m_logger.info("Settings: loaded float " + key + " = " + value);
			}
			
			// Load ints
			m_ints.clear();
			Array<Element> intNodes = root.getChildrenByName("int");
			
			for (int i = 0; i < intNodes.size; ++i) {
				Element intNode = intNodes.get(i);
				String key = intNode.getAttribute("key");
				Integer value = Integer.parseInt(intNode.getAttribute("value"));
				m_ints.put(key, value);
				m_logger.info("Settings: loaded int " + key + " = " + value);
			}
			
			// Load booleans
			m_booleans.clear();
			Array<Element> boolNodes = root.getChildrenByName("bool");
			
			for (int i = 0; i < boolNodes.size; ++i) {
				Element boolNode = boolNodes.get(i);
				String key = boolNode.getAttribute("key");
				Boolean value = Boolean.parseBoolean(boolNode.getAttribute("value"));
				m_booleans.put(key, value);
				m_logger.info("Settings: loaded boolean " + key + " = " + value);
			}
			
			// Load vectors
			m_vectors.clear();
			Array<Element> vectorNodes = root.getChildrenByName("vector");
			
			for (int i = 0; i < vectorNodes.size; ++i) {
				Element vectorNode = vectorNodes.get(i);
				String key = vectorNode.getAttribute("key");
				Float x = Float.parseFloat(vectorNode.getAttribute("x"));
				Float y = Float.parseFloat(vectorNode.getAttribute("y"));
				Float z = Float.parseFloat(vectorNode.getAttribute("z"));
				m_vectors.put(key, new Vector3(x, y, z));
				m_logger.info("Settings: loaded vector " + key + " = (" + x + ", " + y + ", " + z + ")");
			}
			
			m_logger.info("Settings: successfully finished loading settings");
		}
		catch (Exception e) {
			m_logger.error("Settings: error loading file: " + m_settingsFile + " " + e.getMessage());
		}
	}
	
	/**
	 *	Saves settings as an external file. If called from a WebGL application, nothing will be done.
	 */
	public void saveSettings() {
		if (Gdx.app.getType() != Application.ApplicationType.WebGL) {
			m_logger.info("Settings: saving file " + m_settingsFile);
			XmlWriter xml;
			
			try {
				StringWriter writer = new StringWriter();
				xml = new XmlWriter(writer);
				
				// Create root
				xml = xml.element("settings");
				
				// Create string nodes
				for (Entry<String, String> entry : m_strings.entrySet()) {
					xml = xml.element("string");
					xml.attribute("key", entry.getKey());
					xml.attribute("value", entry.getValue());
					xml = xml.pop();
				}
				
				// Create float nodes
				for (Entry<String, Float> entry : m_floats.entrySet()) {
					xml = xml.element("float");
					xml.attribute("key", entry.getKey());
					xml.attribute("value", entry.getValue());
					xml = xml.pop();
				}
				
				// Create int nodes
				for (Entry<String, Integer> entry : m_ints.entrySet()) {
					xml = xml.element("int");
					xml.attribute("key", entry.getKey());
					xml.attribute("value", entry.getValue());
					xml = xml.pop();
				}
				
				// Create boolean nodes
				for (Entry<String, Boolean> entry : m_booleans.entrySet()) {
					xml = xml.element("bool");
					xml.attribute("key", entry.getKey());
					xml.attribute("value", entry.getValue());
					xml = xml.pop();
				}
				
				// Create vector nodes
				for (Entry<String, Vector3> entry : m_vectors.entrySet()) {
					xml = xml.element("vector");
					xml.attribute("key", entry.getKey());
					Vector3 vector = entry.getValue();
					xml.attribute("x", vector.x);
					xml.attribute("y", vector.y);
					xml.attribute("z", vector.z);
					xml = xml.pop();
				}
				
				xml = xml.pop();
				Gdx.files.external(m_settingsFile).writeString(writer.toString(), true);
				xml.close();
				
				m_logger.info("Settings: successfully saved");
			}
			catch (Exception e) {
				m_logger.error("Settings: error saving file " + m_settingsFile);
			}
		}
		else {
			m_logger.error("Settings: saving feature not supported in HTML5");
		}
	}
}
