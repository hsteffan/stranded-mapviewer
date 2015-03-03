/** Copyright © 2015 Holger Steffan H.St. Soft
 * 
 * This file is subject to the terms and conditions defined in file 'license', which is part of this source code
 * package. */
package de.hstsoft.sdeep.model;

import org.json.simple.JSONObject;

/** @author Holger Steffan created: 03.03.2015 */
public class GameObject {

	private static final String NAME = "name";
	private static final String TRANSFORM = "Transform";
	private static final String LOCAL_POSITION = "localPosition";

	private String displayName;
	private String name;
	private String type;

	private Position localPosition;

	public String getDisplayName() {
		return this.displayName;
	}

	public String getName() {
		return this.name;
	}

	public String getType() {
		return this.type;
	}

	public Position getLocalPosition() {
		return this.localPosition;
	}

	public static GameObject parse(JSONObject json) {
		GameObject gameObject = new GameObject();

		gameObject.name = json.get(NAME).toString();
		gameObject.type = gameObject.name.substring(0, gameObject.name.indexOf("("));
		// retVal.displayName = json.get("displayName").toString();

		JSONObject transform = (JSONObject) json.get(TRANSFORM);
		gameObject.localPosition = Position.parseVector((JSONObject) transform.get(LOCAL_POSITION));
		// System.out.println(retVal.localPosition);

		// System.out.println("***** Begin Game Object *****");
		// Iterator<?> iter = json.entrySet().iterator();
		// while (iter.hasNext()) {
		// Map.Entry<?, ?> entry = (Map.Entry<?, ?>) iter.next();
		// System.out.println(entry.getKey());
		// }
		// System.out.println("***** END *****");

		return gameObject;
	}

}