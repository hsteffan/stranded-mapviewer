/** Copyright © 2015 Holger Steffan H.St. Soft
 * 
 * This file is subject to the terms and conditions defined in file 'license', which is part of this source code
 * package. */
package de.hstsoft.sdeep.model;

import org.json.simple.JSONObject;

import de.hstsoft.sdeep.util.Utils;

public class Position {
	public float x;
	public float y;
	public float z;

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Position [x=");
		builder.append(x);
		builder.append(", y=");
		builder.append(y);
		builder.append(", z=");
		builder.append(z);
		builder.append("]");
		return builder.toString();
	}

	public static Position parseVector(JSONObject vec) {
		Position pos = new Position();
		pos.x = Utils.toFloat(vec.get("x").toString());
		pos.y = Utils.toFloat(vec.get("y").toString());
		pos.z = Utils.toFloat(vec.get("z").toString());
		return pos;
	}

}