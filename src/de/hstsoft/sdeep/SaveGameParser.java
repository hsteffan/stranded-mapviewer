/** Copyright © 2015 Holger Steffan H.St. Soft
 * 
 * This file is subject to the terms and conditions defined in file 'license', which is part of this source code
 * package. */
package de.hstsoft.sdeep;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import de.hstsoft.sdeep.model.Atmosphere;
import de.hstsoft.sdeep.model.FollowSpawn;
import de.hstsoft.sdeep.model.PlayerCrafting;
import de.hstsoft.sdeep.model.PlayerInventory;
import de.hstsoft.sdeep.model.PlayerMovement;
import de.hstsoft.sdeep.model.PlayerStatistics;
import de.hstsoft.sdeep.model.SaveGame;
import de.hstsoft.sdeep.model.StatsManager;
import de.hstsoft.sdeep.model.TerrainGeneration;

/** @author Holger Steffan created: 26.02.2015 */
public class SaveGameParser {

	public SaveGame parse(File file) throws IOException {

		SaveGame saveGame = new SaveGame();
		Reader reader = new BufferedReader(new FileReader(file));
		try {
			JSONParser jsonParser = new JSONParser();
			JSONObject json = (JSONObject) jsonParser.parse(reader);

			JSONObject savegameJson = (JSONObject) json.get("Persistent");

			updateStateMsg("Parsing TerrainGeneration");
			JSONObject tgJson = (JSONObject) savegameJson.get("TerrainGeneration");
			TerrainGeneration terrainGeneration = TerrainGeneration.parse(tgJson);
			saveGame.setTerrainGeneration(terrainGeneration);

			updateStateMsg("Parsing PlayerStatistics");
			JSONObject psJson = (JSONObject) savegameJson.get("PlayerStatistics");
			PlayerStatistics playerStatistic = PlayerStatistics.parse(psJson);
			saveGame.setPlayerStatistic(playerStatistic);

			updateStateMsg("Parsing PlayerCrafting");
			JSONObject pcJson = (JSONObject) savegameJson.get("PlayerCrafting");
			PlayerCrafting playerCrafting = PlayerCrafting.parse(pcJson);
			saveGame.setPlayerCrafting(playerCrafting);

			updateStateMsg("Parsing FollowSpawn");
			JSONObject fsJson = (JSONObject) savegameJson.get("FollowSpawn");
			FollowSpawn followSpawn = FollowSpawn.parse(fsJson);
			saveGame.setFollowSpawn(followSpawn);

			updateStateMsg("Parsing PlayerMovement");
			JSONObject pmJson = (JSONObject) savegameJson.get("PlayerMovement");
			PlayerMovement playerMovement = PlayerMovement.parse(pmJson);
			saveGame.setPlayerMovement(playerMovement);

			updateStateMsg("Parsing Atmosphere");
			JSONObject asJson = (JSONObject) savegameJson.get("Atmosphere");
			Atmosphere atmosphere = Atmosphere.parse(asJson);
			saveGame.setAtmosphere(atmosphere);

			updateStateMsg("Parsing StatsManager");
			JSONObject smJson = (JSONObject) savegameJson.get("StatsManager");
			StatsManager statsManager = StatsManager.parse(smJson);
			saveGame.setStatsManager(statsManager);

			updateStateMsg("Parsing PlayerInventory");
			JSONObject piJson = (JSONObject) savegameJson.get("PlayerInventory");
			PlayerInventory playerInventory = PlayerInventory.parse(piJson);
			saveGame.setPlayerInventory(playerInventory);

			saveGame.setFile(file.getPath());

		} catch (ParseException e) {
			System.out.println("Error parsing SavegameFile at line: " + e.getPosition());
			e.printStackTrace();
		} finally {
			reader.close();
		}

		saveGame.updateItemTypeSet();

		System.out.println("Parsed file:" + file.toString());

		return saveGame;
	}

	private void updateStateMsg(String msg) {
		System.out.println(msg);
	}

}
