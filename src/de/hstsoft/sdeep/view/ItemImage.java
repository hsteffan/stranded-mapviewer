/** Copyright © 2015 Holger Steffan H.St. Soft
 * 
 * This file is subject to the terms and conditions defined in file 'license', which is part of this source code
 * package. */
package de.hstsoft.sdeep.view;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ItemImage {
	private BufferedImage image;
	private int width;
	private int height;
	private boolean axisAligned;

	/** @param axisAligned */
	public ItemImage(boolean axisAligned) {
		this.axisAligned = axisAligned;
	}

	public void load(String itemPath, int width, int height) throws IOException {
		this.width = width;
		this.height = height;
		image = ImageIO.read(getClass().getResource(itemPath));
	}

	/** @return the image */
	public BufferedImage getImage() {
		return this.image;
	}

	/** @return the width */
	public int getWidth() {
		return this.width;
	}

	/** @return the height */
	public int getHeight() {
		return this.height;
	}

	/** @return the axisAligned */
	public boolean isAxisAligned() {
		return this.axisAligned;
	}

}