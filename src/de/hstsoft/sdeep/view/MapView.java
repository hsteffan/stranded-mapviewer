/** Copyright © 2015 Holger Steffan H.St. Soft
 * 
 * This file is subject to the terms and conditions defined in file 'license', which is part of this source code
 * package. */
package de.hstsoft.sdeep.view;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import de.hstsoft.sdeep.model.GameObject;
import de.hstsoft.sdeep.model.Position;
import de.hstsoft.sdeep.model.TerrainGeneration;
import de.hstsoft.sdeep.model.TerrainNode;

/** @author Holger Steffan created: 26.02.2015 */
public class MapView extends JPanel implements IslandLoadListener {
	private static final long serialVersionUID = -7556622085501030441L;

	private TerrainGeneration terrainGeneration;
	private IslandShapeGenerationManager islandShapeGenerationManager;

	private ConcurrentHashMap<String, Image> islandShapes = new ConcurrentHashMap<>();

	private HashMap<String, BufferedImage> images = new HashMap<>();
	private HashSet<String> doNotDraw = new HashSet<>();
	private ZoomAndPanListener zoomAndPanListener;

	private boolean showInfo = false;
	private boolean showGrid = true;

	private boolean init = true;

	private AffineTransform coordTransform;
	private Point2D mouseOnMap = new Point2D.Float();

	private TerrainNode hoveredTerrainNode;

	private FontMetrics fontMetrics;

	private InfoWindow popup;

	private double rotation = Math.toRadians(45);

	private AffineTransform identity = new AffineTransform();

	public MapView() {
		super(true);

		this.zoomAndPanListener = new ZoomAndPanListener(this);
		this.addMouseListener(zoomAndPanListener);
		this.addMouseMotionListener(zoomAndPanListener);
		this.addMouseWheelListener(zoomAndPanListener);
		this.addMouseMotionListener(new MouseMotionListener());

		islandShapeGenerationManager = new IslandShapeGenerationManager(this);

		try {
			// TODO move mapping to config file for modding maybe? ;)
			images.put("COMPASS_RING", ImageIO.read(getClass().getResource("/de/hstsoft/sdeep/res/compass_ring.png")));
			images.put("COMPASS_NEEDLE", ImageIO.read(getClass().getResource("/de/hstsoft/sdeep/res/compass_needle.png")));

			images.put("ISLAND", ImageIO.read(getClass().getResource("/de/hstsoft/sdeep/res/island.png")));
			images.put("ISLAND_UNDISCOVERED",
					ImageIO.read(getClass().getResource("/de/hstsoft/sdeep/res/island_undiscovered.png")));

			images.put("SHARK - TIGER", ImageIO.read(getClass().getResource("/de/hstsoft/sdeep/res/shark_tiger.png")));
			images.put("SHARK - REEF", ImageIO.read(getClass().getResource("/de/hstsoft/sdeep/res/shark_reef.png")));
			images.put("SHARK - GREAT WHITE", ImageIO.read(getClass().getResource("/de/hstsoft/sdeep/res/great_white.png")));
			images.put("MARLIN", ImageIO.read(getClass().getResource("/de/hstsoft/sdeep/res/marlin.png")));
			images.put("GREEN_SEA_TURTLE", ImageIO.read(getClass().getResource("/de/hstsoft/sdeep/res/turtle.png")));
			images.put("STING_RAY", ImageIO.read(getClass().getResource("/de/hstsoft/sdeep/res/sting_ray.png")));
			images.put("WHALE", ImageIO.read(getClass().getResource("/de/hstsoft/sdeep/res/whale.png")));

			images.put("PALM_TREE", ImageIO.read(getClass().getResource("/de/hstsoft/sdeep/res/palm_tree.png")));
			images.put("PALM_TREE_1", ImageIO.read(getClass().getResource("/de/hstsoft/sdeep/res/palm_tree.png")));
			images.put("PALM_TREE_2", ImageIO.read(getClass().getResource("/de/hstsoft/sdeep/res/palm_tree.png")));
			images.put("PALM_TREE_3", ImageIO.read(getClass().getResource("/de/hstsoft/sdeep/res/palm_tree.png")));
			images.put("PALM_TREE_4", ImageIO.read(getClass().getResource("/de/hstsoft/sdeep/res/palm_tree.png")));

			images.put("ROCK", ImageIO.read(getClass().getResource("/de/hstsoft/sdeep/res/rock.png")));
			images.put("STICK", ImageIO.read(getClass().getResource("/de/hstsoft/sdeep/res/stick.png")));
			images.put("YUCCA", ImageIO.read(getClass().getResource("/de/hstsoft/sdeep/res/yucca.png")));
			images.put("POTATO_PLANT", ImageIO.read(getClass().getResource("/de/hstsoft/sdeep/res/potato.png")));
			images.put("CRAB_HOME", ImageIO.read(getClass().getResource("/de/hstsoft/sdeep/res/crap.png")));
			BufferedImage coconut = ImageIO.read(getClass().getResource("/de/hstsoft/sdeep/res/coconut.png"));
			images.put("COCONUT_GREEN", coconut);
			images.put("COCONUT_ORANGE", coconut);
			images.put("COCONUT_DRINKABLE", coconut);

			images.put("HARDCASE_1", ImageIO.read(getClass().getResource("/de/hstsoft/sdeep/res/hard_case.png")));
			images.put("TOOLBOX_1", ImageIO.read(getClass().getResource("/de/hstsoft/sdeep/res/toolbox.png")));

			BufferedImage locker = ImageIO.read(getClass().getResource("/de/hstsoft/sdeep/res/locker.png"));
			images.put("LOCKER_1", locker);
			images.put("LOCKER_4", locker);

			BufferedImage door = ImageIO.read(getClass().getResource("/de/hstsoft/sdeep/res/door.png"));
			images.put("DOOR_1", door);
			images.put("DOOR_2", door);

			images.put("PLANEWRECK", ImageIO.read(getClass().getResource("/de/hstsoft/sdeep/res/plane_wreck.png")));

			images.put("RowBoat_3", ImageIO.read(getClass().getResource("/de/hstsoft/sdeep/res/row_boat.png")));

			BufferedImage ship_wreck = ImageIO.read(getClass().getResource("/de/hstsoft/sdeep/res/ship_wreck.png"));
			images.put("SHIPWRECK_1A", ship_wreck);
			images.put("SHIPWRECK_2A", ship_wreck);
			images.put("SHIPWRECK_3A", ship_wreck);
			images.put("SHIPWRECK_4A", ship_wreck);
			images.put("SHIPWRECK_5A", ship_wreck);
			images.put("SHIPWRECK_6A", ship_wreck);
			images.put("SHIPWRECK_7A", ship_wreck);

			images.put("RAFT_V1", ImageIO.read(getClass().getResource("/de/hstsoft/sdeep/res/raft_v1.png")));

			images.put("FIRE", ImageIO.read(getClass().getResource("/de/hstsoft/sdeep/res/fire_place.png")));
			images.put("ROCK_SHARD", ImageIO.read(getClass().getResource("/de/hstsoft/sdeep/res/rock_shard.png")));
			images.put("PALM_FROND", ImageIO.read(getClass().getResource("/de/hstsoft/sdeep/res/palm_frond.png")));
			images.put("DUCTTAPE", ImageIO.read(getClass().getResource("/de/hstsoft/sdeep/res/ducttape.png")));

			images.put("FOUNDATION", ImageIO.read(getClass().getResource("/de/hstsoft/sdeep/res/foundation.png")));
			images.put("FOUNDATION_ROOF", ImageIO.read(getClass().getResource("/de/hstsoft/sdeep/res/foundation_roof.png")));
			images.put("FOUNDATION_SUPPORT", ImageIO.read(getClass().getResource("/de/hstsoft/sdeep/res/foundation_support.png")));

		} catch (IOException e) {
			e.printStackTrace();
		}

		doNotDraw.add("CONSOLE_1");
		doNotDraw.add("CONSOLE_3");
		doNotDraw.add("DOOR_1");
		doNotDraw.add("DOOR_2");
		doNotDraw.add("FLOOR_HATCH");
		doNotDraw.add("HARDCASE_1");
		doNotDraw.add("LOCKER_1");
		doNotDraw.add("LOCKER_2");
		doNotDraw.add("LOCKER_4");
		doNotDraw.add("TOOLBOX_1");
		doNotDraw.add("WALL_CABINET_1");

	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		Graphics2D g2 = (Graphics2D) g.create();

		g2.setRenderingHints(new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF));
		// g2.setRenderingHints(new RenderingHints(RenderingHints.KEY_INTERPOLATION,
		// RenderingHints.VALUE_INTERPOLATION_BILINEAR));

		g2.setColor(new Color(105, 155, 195));
		g2.fillRect(0, 0, getWidth(), getHeight());

		if (init) {
			// Initialize the viewport by moving the origin to the center of the window.
			System.out.println("W:" + getWidth() + " H:" + getHeight());
			init = false;
			final int xc = getWidth() / 2;
			final int yc = getHeight() / 2;
			fontMetrics = g2.getFontMetrics();

			AffineTransform affineTransform = new AffineTransform();
			affineTransform.translate(xc, yc);
			affineTransform.scale(-1, 1);
			affineTransform.rotate(rotation);

			if (terrainGeneration != null) {
				Position worldOrigin = terrainGeneration.getWorldOrigin();
				Position playerPosition = terrainGeneration.getPlayerPosition();
				final int playerX = (int) (worldOrigin.x - playerPosition.x);
				final int playerZ = (int) (worldOrigin.z - playerPosition.z);
				Point2D playerOnScreen = new Point2D.Float(playerX, playerZ);
				affineTransform.translate(-playerOnScreen.getX(), -playerOnScreen.getY());
			}
			// Save the viewport to be updated by the ZoomAndPanListener
			zoomAndPanListener.setCoordTransform(affineTransform);
		}
		// Restore the viewport after it was updated by the ZoomAndPanListener
		this.coordTransform = zoomAndPanListener.getCoordTransform();
		this.rotation = zoomAndPanListener.getRotation();
		g2.setTransform(coordTransform);

		if (terrainGeneration != null) {

			ArrayList<TerrainNode> terrainNodes = terrainGeneration.getTerrainNodes();
			// draw the island ground
			drawTerrain(g2, terrainNodes);
			// draw the objects
			drawObjects(g2, terrainNodes);

			Position worldOrigin = terrainGeneration.getWorldOrigin();
			Position playerPosition = terrainGeneration.getPlayerPosition();
			final int playerX = (int) (worldOrigin.x - playerPosition.x);
			final int playerZ = (int) (worldOrigin.z - playerPosition.z);

			if (showInfo) {
				g2.setColor(Color.BLUE);
				g2.fillOval((int) worldOrigin.x - 2, (int) worldOrigin.z - 2, 4, 4);
			}

			g2.setColor(Color.RED);
			g2.fillOval(playerX - 3, playerZ - 3, 6, 6);

		}
		g2.setTransform(identity);
		AffineTransform transform = g2.getTransform();

		// drawCompass
		g2.setRenderingHints(new RenderingHints(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR));
		final int centerX = getWidth() - 70 - 10;
		final int centerY = 10 + 70;
		transform.translate(centerX, centerY);
		transform.rotate(-rotation - (Math.toRadians(-45)));
		g2.setTransform(transform);
		g2.drawImage(images.get("COMPASS_RING"), -70, -70, 140, 140, null);
		transform.rotate(rotation - (Math.toRadians(45)));
		g2.setTransform(transform);
		g2.drawImage(images.get("COMPASS_NEEDLE"), -70, -70, 140, 140, null);
		g2.setRenderingHints(new RenderingHints(RenderingHints.KEY_INTERPOLATION,
				RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR));

		// set the default transform to draw the popup and stuff
		g2.setTransform(identity);
		if (popup != null) {
			g2.setColor(new Color(0, 0, 0, 150));
			g2.fill(popup.bounds);

			int lineHeight = fontMetrics.getHeight();
			int startX = popup.bounds.x + 5;
			int startY = popup.bounds.y + lineHeight;

			g2.setColor(new Color(0, 0, 0, 200));
			g2.fillRect(popup.bounds.x - lineHeight - 4, popup.bounds.y, lineHeight + 4, popup.bounds.height);
			String[] lines = popup.lines;
			for (int i = 0; i < lines.length; i++) {

				if (images.containsKey(lines[i].split("x ")[1])) {
					BufferedImage bufferedImage = images.get(lines[i].split("x ")[1]);
					g2.drawImage(bufferedImage, startX - lineHeight - 7, startY + (lineHeight * i) - lineHeight + 3, lineHeight,
							lineHeight, null);
				} else {
					g2.setColor(Color.GREEN);
					g2.fillOval(startX - lineHeight - 4, startY + (lineHeight * i) - lineHeight + 6, 10, 10);
				}
				g2.setColor(Color.WHITE);
				g2.drawString(lines[i], startX, startY + (lineHeight * i));
			}

		}

		String zoomStr = String.format("zoomLevel: %d", zoomAndPanListener.getZoomLevel());
		String mapPos = String.format("mapPos: %.1f/%.1f", mouseOnMap.getX(), mouseOnMap.getY());
		int width = Math.max(fontMetrics.stringWidth(zoomStr), fontMetrics.stringWidth(mapPos));
		int height = 30;
		String nodeStr = null;
		if (hoveredTerrainNode != null) {
			nodeStr = "node: " + hoveredTerrainNode.getKey();
			width = Math.max(width, fontMetrics.stringWidth(nodeStr));
			height += 15;
		}

		g2.setColor(new Color(0, 0, 0, 150));
		g2.fillRect(5, 5, width + 10, height + 10);
		g2.setColor(Color.WHITE);
		g2.drawString(zoomStr, 10, 20);
		g2.drawString(mapPos, 10, 35);

		if (nodeStr != null) g2.drawString(nodeStr, 10, 50);

		g2.dispose();

	}

	private void drawTerrain(Graphics2D g2, ArrayList<TerrainNode> terrainNodes) {
		AffineTransform t = new AffineTransform();
		for (TerrainNode terrainNode : terrainNodes) {

			Position positionOffset = terrainNode.getPositionOffset();

			final int nodeWorldX = (int) positionOffset.x;
			final int nodeWorldZ = (int) positionOffset.z;

			if (showGrid) {
				g2.setColor(Color.GRAY);
				g2.drawRect(nodeWorldX - 128, nodeWorldZ - 128, 256, 256);
			}

			// no need to draw anything
			if (!terrainNode.getBiome().equals("ISLAND")) continue;

			// draw undiscovered island an image
			if (terrainNode.isFullyGernerated() && islandShapes.containsKey(terrainNode.getName())) {
				// island shape was created previously so we can draw it
				Image img = islandShapes.get(terrainNode.getName());
				g2.drawImage(img, nodeWorldX - 128, nodeWorldZ - 128, null);
			} else {
				// the shape has to be created, loaded or island is undiscovered. draw a place holder instead.
				AffineTransform originalTransform = g2.getTransform();
				t.setToIdentity();
				t.translate(nodeWorldX, nodeWorldZ);
				t.rotate(-rotation);
				t.scale(-1, 1);
				g2.transform(t);
				g2.drawImage(images.get("ISLAND_UNDISCOVERED"), -64, -64, null);
				g2.setTransform(originalTransform);
			}
		}
	}

	private void drawObjects(Graphics2D g2, ArrayList<TerrainNode> terrainNodes) {
		AffineTransform t = new AffineTransform();
		for (TerrainNode terrainNode : terrainNodes) {

			Position nodePosition = terrainNode.getPosition();
			Position positionOffset = terrainNode.getPositionOffset();

			final float nodeWorldX = positionOffset.x;
			final float nodeWorldZ = positionOffset.z;

			ArrayList<GameObject> children = terrainNode.getChildren();
			for (GameObject gameObject : children) {

				if (doNotDraw.contains(gameObject.getType())) continue;

				Position localPosition = gameObject.getLocalPosition();
				final int worldX = (int) (nodeWorldX - localPosition.x);
				final int worldZ = (int) (nodeWorldZ - localPosition.z);

				AffineTransform originalTransform = g2.getTransform();
				// rotate the the canvas back to draw the object images aligned with the x-axis
				t.setToIdentity();
				t.translate(worldX, worldZ);
				t.rotate(-rotation);
				t.scale(-1, 1);
				g2.transform(t);

				if (images.containsKey(gameObject.getType())) {
					BufferedImage bufferedImage = images.get(gameObject.getType());
					g2.drawImage(bufferedImage, -4, -4, null);
				} else {
					g2.setColor(Color.GREEN);
					g2.fillOval(-2, -2, 4, 4);
				}

				g2.setTransform(originalTransform);
			}

			if (showInfo) {

				g2.setColor(Color.BLACK);

				final int left = (int) nodeWorldX - 128;
				final int top = (int) nodeWorldZ - 128;

				AffineTransform originalTransform = g2.getTransform();
				t.setToIdentity();
				t.translate(left, top);
				t.rotate(-rotation);
				t.scale(-1, 1);
				g2.transform(t);

				g2.drawString(terrainNode.getKey(), -20, 30);
				g2.drawString(terrainNode.getBiome(), -20, 45);
				g2.drawString("generated: " + (terrainNode.isFullyGernerated() ? "true" : "false"), -20, 60);
				g2.drawString("pos: " + nodePosition.x + "/" + nodePosition.z, -20, 75);
				g2.drawString("off: " + positionOffset.x + "/" + positionOffset.z, -20, 90);
				g2.drawString("seed: " + terrainNode.getSeedEffect(), -20, 105);

				g2.setTransform(originalTransform);
			}
		}
	}

	private class MouseMotionListener extends MouseMotionAdapter {

		@Override
		public void mouseMoved(MouseEvent e) {
			if (terrainGeneration == null) return;

			Point mousePosition = e.getPoint();

			try {
				AffineTransform inverse = coordTransform.createInverse();
				inverse.transform(mousePosition, mouseOnMap);
			} catch (NoninvertibleTransformException e1) {
				e1.printStackTrace();
			}

			hoveredTerrainNode = null;
			popup = null;
			ArrayList<TerrainNode> terrainNodes = terrainGeneration.getTerrainNodes();
			for (TerrainNode terrainNode : terrainNodes) {
				Position positionOffset = terrainNode.getPositionOffset();
				Rectangle nodeBounds = new Rectangle((int) positionOffset.x - 128, (int) positionOffset.z - 128, 256, 256);
				if (!nodeBounds.contains(mouseOnMap)) continue;

				hoveredTerrainNode = terrainNode;
				ArrayList<GameObject> objects = new ArrayList<>();

				HashMap<String, Integer> objCount = new HashMap<>();

				Rectangle bounds = new Rectangle();
				for (GameObject gameObject : hoveredTerrainNode.getChildren()) {
					Position localPosition = gameObject.getLocalPosition();
					final int worldX = (int) (positionOffset.x - localPosition.x);
					final int worldZ = (int) (positionOffset.z - localPosition.z);
					// TODO resize item bounds with the zoomlevel. smaller size when zoomed in.
					bounds.setBounds(worldX - 8, worldZ - 8, 16, 16);
					if (bounds.contains(mouseOnMap)) {
						objects.add(gameObject);

						if (objCount.containsKey(gameObject.getType())) {
							int intValue = objCount.get(gameObject.getType()).intValue();
							objCount.put(gameObject.getType(), intValue + 1);
						} else {
							objCount.put(gameObject.getType(), 1);
						}

					}
				}

				if (!objCount.isEmpty()) {
					popup = new InfoWindow();
					popup.lines = new String[objCount.size()];
					final int lineHeight = fontMetrics.getHeight();
					final int totalHeight = (lineHeight) * objCount.size();
					int width = 0;
					int i = 0;
					for (Entry<String, Integer> entry : objCount.entrySet()) {
						popup.lines[i] = entry.getValue().intValue() + "x " + entry.getKey();
						width = Math.max(width, fontMetrics.stringWidth(popup.lines[i]));
						i++;
					}
					Arrays.sort(popup.lines);
					Rectangle infoBounds = new Rectangle(mousePosition.x - width - 15, mousePosition.y - totalHeight - 5,
							width + 10, totalHeight + 5);
					popup.bounds = infoBounds;
					repaint();
				}

			}
			repaint();
		}
	}

	private class InfoWindow {
		private Rectangle bounds;
		private String[] lines;
	}

	public void setTerrainGeneration(TerrainGeneration terrainGeneration) {
		setTerrainGeneration(terrainGeneration, false);
	}

	public void setTerrainGeneration(TerrainGeneration terrainGeneration, boolean resetView) {
		this.terrainGeneration = terrainGeneration;

		String directory = "worlds/" + terrainGeneration.getWorldSeed() + "/";

		// load or generate island shapes
		for (TerrainNode node : terrainGeneration.getTerrainNodes()) {
			if (!node.getBiome().equals("ISLAND")) continue;
			if (node.isFullyGernerated()) {
				islandShapeGenerationManager.enqueue(new IslandShapeGenerationTask(node, directory));
			}
		}

		if (resetView)
			resetView();
		else
			paintImmediately(0, 0, getWidth(), getHeight());
	}

	public boolean isShowInfo() {
		return this.showInfo;
	}

	public void setShowInfo(boolean showInfo) {
		this.showInfo = showInfo;
		paintImmediately(0, 0, getWidth(), getHeight());
	}

	public boolean isShowGrid() {
		return this.showGrid;
	}

	public void setShowGrid(boolean showGrid) {
		this.showGrid = showGrid;
		paintImmediately(0, 0, getWidth(), getHeight());
	}

	public void resetView() {
		int xc = getWidth() / 2;
		int yc = getHeight() / 2;

		AffineTransform affineTransform = new AffineTransform();
		affineTransform.translate(xc, yc);
		affineTransform.scale(-1, 1);
		affineTransform.rotate(Math.toRadians(45));

		if (terrainGeneration != null) {
			Position worldOrigin = terrainGeneration.getWorldOrigin();
			Position playerPosition = terrainGeneration.getPlayerPosition();
			final int playerX = (int) (worldOrigin.x - playerPosition.x);
			final int playerZ = (int) (worldOrigin.z - playerPosition.z);
			Point2D playerOnScreen = new Point2D.Float(playerX, playerZ);
			affineTransform.translate(-playerOnScreen.getX(), -playerOnScreen.getY());
		}
		zoomAndPanListener.setZoomLevel(0);
		zoomAndPanListener.setCoordTransform(affineTransform);
		repaint();
	}

	@Override
	public void onIslandLoaded(TerrainNode node, BufferedImage islandShape) {
		islandShapes.put(node.getName(), islandShape);
		paintImmediately(0, 0, getWidth(), getHeight());
	}

}
