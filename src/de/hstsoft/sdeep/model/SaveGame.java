/** Copyright © 2015 Holger Steffan H.St. Soft
 * 
 * This file is subject to the terms and conditions defined in file 'license', which is part of this source code
 * package. */
package de.hstsoft.sdeep.model;

import java.util.HashSet;

/** @author Holger Steffan created: 26.02.2015 */
public class SaveGame {

	private HashSet<String> itemTypes = new HashSet<>();

	TerrainGeneration terrainGeneration;
	PlayerMovement playerMovement;
	FollowSpawn followSpawn;
	PlayerCrafting playerCrafting;
	PlayerInventory playerInventory;
	PlayerStatistics playerStatistic;
	Atmosphere atmosphere;
	StatsManager statsManager;

	private String path;

	public TerrainGeneration getTerrainGeneration() {
		return this.terrainGeneration;
	}

	public void setTerrainGeneration(TerrainGeneration terrainGeneration) {
		this.terrainGeneration = terrainGeneration;
	}

	public PlayerMovement getPlayerMovement() {
		return this.playerMovement;
	}

	public void setPlayerMovement(PlayerMovement playerMovement) {
		this.playerMovement = playerMovement;
	}

	public FollowSpawn getFollowSpawn() {
		return this.followSpawn;
	}

	public void setFollowSpawn(FollowSpawn followSpawn) {
		this.followSpawn = followSpawn;
	}

	public PlayerCrafting getPlayerCrafting() {
		return this.playerCrafting;
	}

	public void setPlayerCrafting(PlayerCrafting playerCrafting) {
		this.playerCrafting = playerCrafting;
	}

	public PlayerInventory getPlayerInventory() {
		return this.playerInventory;
	}

	public void setPlayerInventory(PlayerInventory playerInventory) {
		this.playerInventory = playerInventory;
	}

	public PlayerStatistics getPlayerStatistic() {
		return this.playerStatistic;
	}

	public void setPlayerStatistic(PlayerStatistics playerStatistic) {
		this.playerStatistic = playerStatistic;
	}

	public Atmosphere getAtmosphere() {
		return this.atmosphere;
	}

	public void setAtmosphere(Atmosphere atmosphere) {
		this.atmosphere = atmosphere;
	}

	public StatsManager getStatsManager() {
		return this.statsManager;
	}

	public void setStatsManager(StatsManager statsManager) {
		this.statsManager = statsManager;
	}

	public String getPath() {
		return this.path;
	}

	public void setFile(String path) {
		this.path = path;
	}

	public HashSet<String> getItemTypes() {
		return this.itemTypes;
	}

	public void updateItemTypeSet() {
		itemTypes = new HashSet<>();
		if (terrainGeneration == null) return;

		for (TerrainNode terrainNode : terrainGeneration.getTerrainNodes()) {
			for (GameObject gameObject : terrainNode.getChildren()) {
				itemTypes.add(gameObject.getType());
			}
		}
	}

}
