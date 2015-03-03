/** Copyright © 2015 Holger Steffan H.St. Soft
 * 
 * This file is subject to the terms and conditions defined in file 'license', which is part of this source code
 * package. */
package de.hstsoft.sdeep.model;

import org.json.simple.JSONObject;

/** @author Holger Steffan created: 26.02.2015 */
public class FollowSpawn {

	private static final String CENTER_GRID_POS = "centerGridPos";

	private Position centerGridPos;

	public Position getCenterGridPos() {
		return this.centerGridPos;
	}

	public static FollowSpawn parse(JSONObject json) {
		FollowSpawn followSpawn = new FollowSpawn();

		JSONObject centerGridPos = (JSONObject) json.get(CENTER_GRID_POS);
		followSpawn.centerGridPos = Position.parseVector(centerGridPos);

		return followSpawn;
	}

}
