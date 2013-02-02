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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Logger;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.XmlReader;
import com.badlogic.gdx.utils.XmlReader.Element;

/**
 * @class LanguageManager
 * @author David Saltares Márquez
 * @date 05/09/2012
 * 
 * @brief Provides i18n support
 *
 */
public class LanguageManager {
	private static final String DEFAULT_LANGUAGE = "en_UK";
	
	private String m_folder;
	private ObjectMap<String, String> m_strings = new ObjectMap<String, String>();
	private String m_languageName;
	
	private Logger m_logger = new Logger("LanguagesManager", Logger.INFO);
	
	/**
	 * Will load lang/en_UK.xml
	 */
	public LanguageManager() {
		this("data/lang", DEFAULT_LANGUAGE, Logger.INFO);
	}
	
	/**
	 * Will load the folder + / + language + .xml file
	 * 
	 * @param folder
	 * @param language
	 * @param loggingLevel
	 */
	public LanguageManager(String folder, String language, int loggingLevel) {
		m_folder = folder;
		m_languageName = language;
		m_logger.setLevel(loggingLevel);
		
		if (!loadLanguage(m_languageName) && !m_languageName.equals(DEFAULT_LANGUAGE)) {
			loadLanguage(DEFAULT_LANGUAGE);
		}
	}
	
	/**
	 * @return current language name
	 */
	public String getLanguage() {
		return m_languageName;
	}
	
	/**
	 * @param key key for the string to translate
	 * @return translated string in the current language, the key if it's not found
	 */
	public String getString(String key) {
		String string = m_strings.get(key);
		
		if (string != null) {
			return string;
		}
	
		m_logger.error("string " + key + " not found");
		return key;
	}
	
	/**
	 * Tries to load a new language replacing the old one. If it fails, nothing it's done.
	 * 
	 * @param languageName name of the language to load
	 * @return true if succeeded, false in any other case
	 */
	public boolean loadLanguage(String languageName) {
		m_logger.info("loading " + languageName);
		
		try {
			XmlReader reader = new XmlReader();
			Element root = reader.parse(Gdx.files.internal(m_folder + "/" + languageName + ".xml").read());
			
			// Don't clear the previous lang just yet
			Array<Element> strings = root.getChildrenByName("string");
			ObjectMap<String, String> newStrings = new ObjectMap<String, String>(strings.size);
			
			// Load all the strings for that language
			for (int j = 0; j < strings.size; ++j) {
				Element stringNode = strings.get(j);
				String key = stringNode.getAttribute("key");
				String value = stringNode.getAttribute("value");
				value = value.replace("<br />", "\n");
				newStrings.put(key, value);
				m_logger.info("LanguageManager: loading key " + key);
			}
			
			// Swap the languages now that is safe to do so
			m_languageName = languageName;
			m_strings = newStrings;
			
			m_logger.info("LanguageManager: " + languageName + " language sucessfully loaded");
		}
		catch (Exception e) {
			m_logger.error("error loading " + languageName + " we keep the previous one");
			return false;
		}
		
		return true;
	}
	
	// TODO formatted strings
}
