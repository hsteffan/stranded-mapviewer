/** Copyright © 2015 Holger Steffan H.St. Soft
 * 
 * This file is subject to the terms and conditions defined in file 'license', which is part of this source code
 * package. */
package de.hstsoft.sdeep.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

import org.json.simple.JSONObject;

import de.hstsoft.sdeep.util.Utils;

/** @author Holger Steffan created: 03.03.2015 */
public class TerrainNode {

	private static final String NAME = "name";
	private static final String BIOME = "biome";
	private static final String FULLY_GENERATED = "fullyGenerated";
	private static final String HEIGHT_VALUE = "heightValue";
	private static final String SEED_EFFECT = "seedEffect";
	private static final String POSITION = "position";
	private static final String POSITION_OFFSET = "positionOffset";
	private static final String OBJECTS = "Objects";

	private final String key;
	private String name;
	private String biome;
	private boolean fullyGernerated;
	private int heightValue;
	private int seedEffect;
	private Position position;
	private Position positionOffset;
	private ArrayList<GameObject> children = new ArrayList<>();

	public TerrainNode(String key) {
		this.key = key;
	}

	public Position getPosition() {
		return this.position;
	}

	public String getName() {
		return this.name;
	}

	public String getBiome() {
		return this.biome;
	}

	public boolean isFullyGernerated() {
		return this.fullyGernerated;
	}

	public int getHeightValue() {
		return this.heightValue;
	}

	public int getSeedEffect() {
		return this.seedEffect;
	}

	public Position getPositionOffset() {
		return this.positionOffset;
	}

	public String getKey() {
		return this.key;
	}

	public ArrayList<GameObject> getChildren() {
		return this.children;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("TerrainNode [name=");
		builder.append(name);
		builder.append(", biome=");
		builder.append(biome);
		builder.append(", fullyGernerated=");
		builder.append(fullyGernerated);
		builder.append(", heightValue=");
		builder.append(heightValue);
		builder.append(", seedEffect=");
		builder.append(seedEffect);
		builder.append(", position=");
		builder.append(getPosition());
		builder.append(", positionOffset=");
		builder.append(positionOffset);
		builder.append("]");
		return builder.toString();
	}

	public static TerrainNode parseNode(String key, JSONObject node) {

		TerrainNode terrainNode = new TerrainNode(key);

		terrainNode.name = node.get(NAME).toString();
		terrainNode.biome = node.get(BIOME).toString();
		terrainNode.fullyGernerated = Utils.toBool(node.get(FULLY_GENERATED).toString());
		terrainNode.heightValue = Utils.toInt(node.get(HEIGHT_VALUE).toString());
		terrainNode.seedEffect = Utils.toInt(node.get(SEED_EFFECT).toString());

		JSONObject position = (JSONObject) node.get(POSITION);
		terrainNode.position = Position.parseVector(position);

		JSONObject positionOffset = (JSONObject) node.get(POSITION_OFFSET);
		terrainNode.positionOffset = Position.parseVector(positionOffset);

		JSONObject objects = (JSONObject) node.get(OBJECTS);

		if (objects != null) {
			Iterator<?> iter = objects.entrySet().iterator();
			while (iter.hasNext()) {
				Map.Entry<?, ?> entry = (Map.Entry<?, ?>) iter.next();

				JSONObject childJson = (JSONObject) objects.get(entry.getKey().toString());
				GameObject child = GameObject.parse(childJson);
				terrainNode.children.add(child);
			}
		}
		Collections.sort(terrainNode.children, new GameObjectComparator());
		return terrainNode;
	}
}