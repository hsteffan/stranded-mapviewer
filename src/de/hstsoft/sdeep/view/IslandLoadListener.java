/**
 * Copyright © 2015 Holger Steffan
 * H.St. Soft
 *
 * This file is subject to the terms and conditions defined in
 * file 'license', which is part of this source code package.
 *
 */
package de.hstsoft.sdeep.view;

import java.awt.image.BufferedImage;

import de.hstsoft.sdeep.model.TerrainNode;

public interface IslandLoadListener {
	void onIslandLoaded(TerrainNode node, BufferedImage islandShape);
}