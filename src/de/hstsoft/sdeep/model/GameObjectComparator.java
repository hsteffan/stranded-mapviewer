/**
 * Copyright © 2015 Holger Steffan
 * H.St. Soft
 *
 * This file is subject to the terms and conditions defined in
 * file 'license', which is part of this source code package.
 *
 */
package de.hstsoft.sdeep.model;

import java.util.Comparator;

public class GameObjectComparator implements Comparator<GameObject> {

	@Override
	public int compare(GameObject o1, GameObject o2) {
		if (o1.order == o2.order) return 0;
		return o1.order < o2.order ? -1 : 1;
	}

}