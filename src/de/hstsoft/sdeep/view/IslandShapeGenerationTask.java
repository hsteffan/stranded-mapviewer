/**
 * Copyright © 2015 Holger Steffan
 * H.St. Soft
 *
 * This file is subject to the terms and conditions defined in
 * file 'license', which is part of this source code package.
 *
 */
package de.hstsoft.sdeep.view;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import de.hstsoft.sdeep.model.GameObject;
import de.hstsoft.sdeep.model.Position;
import de.hstsoft.sdeep.model.TerrainNode;

public class IslandShapeGenerationTask {

	private TerrainNode node;

	public IslandShapeGenerationTask(TerrainNode node) {
		this.node = node;
	}

	public TerrainNode getNode() {
		return this.node;
	}

	public BufferedImage execute() throws IOException {

		File file = new File(node.getName() + ".png");
		BufferedImage image = null;
		if (file.exists()) {
			System.out.println("Loading shape for " + node.getName());
			image = ImageIO.read(file);

		} else {
			System.out.println("Generating shape for " + node.getName());
			image = new BufferedImage(256, 256, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = image.createGraphics();

			// iterate over all children to determine the bounds of the island
			ArrayList<GameObject> children = node.getChildren();
			BufferedImage brush = ImageIO.read(getClass().getResource("/de/hstsoft/sdeep/res/island.png"));

			for (GameObject gameObject : children) {
				String name = gameObject.getType();
				if (name.contains("ROCK") || name.contains("PALM_TREE") || name.contains("STICK")
						|| name.contains("POTATO_PLANT") || name.contains("YUCCA")) {
					Position localPosition = gameObject.getLocalPosition();
					final int worldX = (int) (128 - localPosition.x);
					final int worldZ = (int) (128 - localPosition.z);
					g.drawImage(brush, worldX - 20, worldZ - 20, null);
				}
			}

			saveImage(node, image);
		}
		return image;
	}

	private void saveImage(TerrainNode node, BufferedImage nodeImage) throws IOException {

		File output = new File(node.getName() + ".png");
		ImageIO.write(nodeImage, "PNG", output);

	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		IslandShapeGenerationTask other = (IslandShapeGenerationTask) obj;
		return this.node.equals(other.node);
	}

}