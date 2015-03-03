/** Copyright © 2015 Holger Steffan H.St. Soft
 * 
 * This file is subject to the terms and conditions defined in file 'license', which is part of this source code
 * package. */
package de.hstsoft.sdeep.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import org.json.simple.JSONObject;

import de.hstsoft.sdeep.util.Utils;

/** @author Holger Steffan created: 26.02.2015 */
public class TerrainGeneration {

	private static final String WORLD_ORIGIN_POINT = "WorldOriginPoint";
	private static final String PLAYER_POSITION = "playerPosition";
	private static final String WORLD_SEED = "worldSeed";
	private static final String NODES = "Nodes";

	private int worldSeed;
	private Position worldOrigin = null;
	private Position playerPosition = null;
	private ArrayList<TerrainNode> terrainNodes = new ArrayList<>();

	public int getWorldSeed() {
		return this.worldSeed;
	}

	public Position getPlayerPosition() {
		return this.playerPosition;
	}

	public Position getWorldOrigin() {
		return this.worldOrigin;
	}

	public ArrayList<TerrainNode> getTerrainNodes() {
		return this.terrainNodes;
	}

	public static TerrainGeneration parse(JSONObject json) {
		TerrainGeneration terrainGeneration = new TerrainGeneration();

		JSONObject origin = (JSONObject) json.get(WORLD_ORIGIN_POINT);
		terrainGeneration.worldOrigin = Position.parseVector(origin);

		JSONObject playerPosition = (JSONObject) json.get(PLAYER_POSITION);
		terrainGeneration.playerPosition = Position.parseVector(playerPosition);

		terrainGeneration.worldSeed = Utils.toInt(json.get(WORLD_SEED).toString());

		JSONObject nodes = (JSONObject) json.get(NODES);
		Iterator<?> iter = nodes.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry<?, ?> entry = (Map.Entry<?, ?>) iter.next();

			String key = entry.getKey().toString();
			JSONObject node = (JSONObject) nodes.get(entry.getKey().toString());
			TerrainNode terrainNode = TerrainNode.parseNode(key, node);
			terrainGeneration.terrainNodes.add(terrainNode);

		}

		return terrainGeneration;
	}

}