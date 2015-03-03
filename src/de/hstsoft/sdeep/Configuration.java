/** Copyright © 2015 Holger Steffan H.St. Soft
 * 
 * This file is subject to the terms and conditions defined in file 'license', which is part of this source code
 * package. */
package de.hstsoft.sdeep;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/** @author Holger Steffan created: 03.03.2015 */
public class Configuration {

	public static final String PREF_SAVEGAME_PATH = "last_SaveGame";
	public static final String PREF_AUTOREFRESH = "auto_refresh";

	private File configFile;

	private String saveGamePath = "";
	private boolean autorefresh;

	public Configuration(File configFile) {
		this.configFile = configFile;
	}

	public void load() throws IOException, ParseException {

		Reader reader = new BufferedReader(new FileReader(configFile));
		JSONParser jsonParser = new JSONParser();
		JSONObject json = (JSONObject) jsonParser.parse(reader);
		saveGamePath = json.get(PREF_SAVEGAME_PATH).toString();
		autorefresh = Boolean.parseBoolean(json.get(PREF_AUTOREFRESH).toString());
		reader.close();
	}

	@SuppressWarnings("unchecked")
	public void save() {
		JSONObject jsonObject = new JSONObject();

		jsonObject.put(PREF_SAVEGAME_PATH, saveGamePath);
		jsonObject.put(PREF_AUTOREFRESH, autorefresh);

		try {
			String jsonString = jsonObject.toJSONString();
			FileWriter fileWriter = new FileWriter(configFile);
			fileWriter.append(jsonString);
			fileWriter.flush();
			fileWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void setSaveGamePath(String saveGamePath) {
		this.saveGamePath = saveGamePath;
		save();
	}

	public void setAutorefresh(boolean autorefresh) {
		this.autorefresh = autorefresh;
		save();
	}

	public String getSavegamePath() {
		return saveGamePath;
	}

	public boolean isAutorefresh() {
		return this.autorefresh;
	}

}
