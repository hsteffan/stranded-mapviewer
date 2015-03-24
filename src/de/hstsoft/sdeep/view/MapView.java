/** Copyright © 2015 Holger Steffan H.St. Soft
 * 
 * This file is subject to the terms and conditions defined in file 'license', which is part of this source code
 * package. */
package de.hstsoft.sdeep.view;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.JPanel;

import de.hstsoft.sdeep.ItemTypes;
import de.hstsoft.sdeep.NoteManager;
import de.hstsoft.sdeep.model.GameObject;
import de.hstsoft.sdeep.model.Note;
import de.hstsoft.sdeep.model.Position;
import de.hstsoft.sdeep.model.SaveGame;
import de.hstsoft.sdeep.model.TerrainGeneration;
import de.hstsoft.sdeep.model.TerrainNode;
import de.hstsoft.sdeep.view.ZoomAndPanListener.ZoomPanRotationChangedListener;

/** @author Holger Steffan created: 26.02.2015 */
public class MapView extends JPanel implements IslandLoadListener {
	private static final long serialVersionUID = -7556622085501030441L;

	private TerrainGeneration terrainGeneration;
	private IslandShapeGenerationManager islandShapeGenerationManager;

	private ConcurrentHashMap<String, Image> islandShapes = new ConcurrentHashMap<>();

	private HashMap<String, ItemImage> images = new HashMap<>();
	private HashSet<String> doNotDraw = new HashSet<>();
	private ZoomAndPanListener zoomAndPanListener;

	private boolean showInfo = false;
	private boolean showGrid = true;
	private boolean showNotes = true;

	private boolean initialized = false;

	private AffineTransform coordTransform;
	private Point2D mouseOnMap = new Point2D.Float();

	private TerrainNode hoveredTerrainNode;

	private FontMetrics fontMetrics;

	private ItemInfoWindow itemInfoWindow;

	private NoteInfoWindow noteInfoWindow;

	private double rotation = Math.toRadians(45);

	private AffineTransform defaultTransform = new AffineTransform();

	private SaveGame saveGame;

	private NoteManager noteManager;

	private BufferedImage bufferedMapImage;

	private boolean isMapDirty;

	/** @author Holger Steffan created: 24.03.2015 */
	private final class ZoomPanRotationChangedListenerImpl implements ZoomPanRotationChangedListener {
		@Override
		public void onZoomChanged(ZoomAndPanListener source) {
			isMapDirty = true;
		}

		@Override
		public void onRotationChanged(ZoomAndPanListener source) {
			isMapDirty = true;
		}

		@Override
		public void onPanChanged(ZoomAndPanListener source) {
			isMapDirty = true;
		}
	}

	public static class ImageFactory {

		public static ItemImage createImage(String itemPath, int width, int height) throws IOException {
			return createImage(itemPath, width, height, true);
		}

		public static ItemImage createImage(String itemPath, int width, int height, boolean axisAligned) throws IOException {
			ItemImage itemImage = new ItemImage(axisAligned);
			itemImage.load(itemPath, width, height);
			return itemImage;
		}
	}

	public MapView() {
		super(true);

		this.setLayout(null);

		this.zoomAndPanListener = new ZoomAndPanListener(this);
		this.zoomAndPanListener.setListener(new ZoomPanRotationChangedListenerImpl());

		this.addMouseListener(zoomAndPanListener);
		this.addMouseMotionListener(zoomAndPanListener);
		this.addMouseWheelListener(zoomAndPanListener);
		this.addMouseMotionListener(new MouseMotionListener());
		this.addMouseListener(new MouseClickListener());

		islandShapeGenerationManager = new IslandShapeGenerationManager(this);

		try {
			// TODO move mapping to config file for modding maybe? ;)
			images.put("COMPASS_RING", ImageFactory.createImage("/de/hstsoft/sdeep/res/compass_ring.png", 140, 140));
			images.put("COMPASS_NEEDLE", ImageFactory.createImage("/de/hstsoft/sdeep/res/compass_needle.png", 140, 140));

			images.put("ISLAND", ImageFactory.createImage("/de/hstsoft/sdeep/res/island.png", 256, 256));
			images.put("ISLAND_UNDISCOVERED", ImageFactory.createImage("/de/hstsoft/sdeep/res/island_undiscovered.png", 128, 128));

			images.put("NOTE", ImageFactory.createImage("/de/hstsoft/sdeep/res/note.png", 16, 16));

			images.put(ItemTypes.SHARK_TIGER, ImageFactory.createImage("/de/hstsoft/sdeep/res/shark_tiger.png", 8, 8));
			images.put(ItemTypes.SHARK_REEF, ImageFactory.createImage("/de/hstsoft/sdeep/res/shark_reef.png", 8, 8));
			images.put(ItemTypes.SHARK_WHITE, ImageFactory.createImage("/de/hstsoft/sdeep/res/great_white.png", 8, 8));
			images.put(ItemTypes.MARLIN, ImageFactory.createImage("/de/hstsoft/sdeep/res/marlin.png", 8, 8));
			images.put(ItemTypes.GREEN_SEA_TURTLE, ImageFactory.createImage("/de/hstsoft/sdeep/res/turtle.png", 8, 8));
			images.put(ItemTypes.STING_RAY, ImageFactory.createImage("/de/hstsoft/sdeep/res/sting_ray.png", 8, 8));
			images.put(ItemTypes.WHALE, ImageFactory.createImage("/de/hstsoft/sdeep/res/whale.png", 8, 8));

			ItemImage palm = ImageFactory.createImage("/de/hstsoft/sdeep/res/palm_tree.png", 20, 20, false);
			images.put(ItemTypes.PALM_TREE, palm);
			images.put(ItemTypes.PALM_TREE_1, palm);
			images.put(ItemTypes.PALM_TREE_2, palm);
			images.put(ItemTypes.PALM_TREE_3, palm);
			images.put(ItemTypes.PALM_TREE_4, palm);

			images.put(ItemTypes.ROCK, ImageFactory.createImage("/de/hstsoft/sdeep/res/rock.png", 8, 8));
			images.put(ItemTypes.STICK, ImageFactory.createImage("/de/hstsoft/sdeep/res/stick.png", 8, 8));
			images.put(ItemTypes.YUCCA, ImageFactory.createImage("/de/hstsoft/sdeep/res/yucca.png", 8, 8));
			images.put(ItemTypes.POTATO_PLANT, ImageFactory.createImage("/de/hstsoft/sdeep/res/potato.png", 8, 8));
			images.put(ItemTypes.CRAB_HOME, ImageFactory.createImage("/de/hstsoft/sdeep/res/crap.png", 8, 8));
			ItemImage coconut = ImageFactory.createImage("/de/hstsoft/sdeep/res/coconut.png", 8, 8);
			images.put(ItemTypes.COCONUT_GREEN, coconut);
			images.put(ItemTypes.COCONUT_ORANGE, coconut);
			images.put(ItemTypes.COCONUT_DRINKABLE, coconut);

			images.put(ItemTypes.HARDCASE_1, ImageFactory.createImage("/de/hstsoft/sdeep/res/hard_case.png", 8, 8));
			images.put(ItemTypes.TOOLBOX_1, ImageFactory.createImage("/de/hstsoft/sdeep/res/toolbox.png", 8, 8));

			ItemImage locker = ImageFactory.createImage("/de/hstsoft/sdeep/res/locker.png", 8, 8);
			images.put(ItemTypes.LOCKER_1, locker);
			images.put(ItemTypes.LOCKER_2, locker);
			images.put(ItemTypes.LOCKER_3, locker);
			images.put(ItemTypes.LOCKER_4, locker);

			ItemImage door = ImageFactory.createImage("/de/hstsoft/sdeep/res/door.png", 8, 8);
			images.put(ItemTypes.DOOR_1, door);
			images.put(ItemTypes.DOOR_2, door);

			images.put(ItemTypes.PLANEWRECK, ImageFactory.createImage("/de/hstsoft/sdeep/res/plane_wreck.png", 8, 8));

			images.put(ItemTypes.ROWBOAT_3, ImageFactory.createImage("/de/hstsoft/sdeep/res/row_boat.png", 8, 8));

			ItemImage ship_wreck = ImageFactory.createImage("/de/hstsoft/sdeep/res/ship_wreck.png", 8, 8);
			images.put(ItemTypes.SHIPWRECK_1A, ship_wreck);
			images.put(ItemTypes.SHIPWRECK_2A, ship_wreck);
			images.put(ItemTypes.SHIPWRECK_3A, ship_wreck);
			images.put(ItemTypes.SHIPWRECK_4A, ship_wreck);
			images.put(ItemTypes.SHIPWRECK_5A, ship_wreck);
			images.put(ItemTypes.SHIPWRECK_6A, ship_wreck);
			images.put(ItemTypes.SHIPWRECK_7A, ship_wreck);

			images.put(ItemTypes.RAFT_V1, ImageFactory.createImage("/de/hstsoft/sdeep/res/raft_v1.png", 8, 8));
			images.put(ItemTypes.PADDLE, ImageFactory.createImage("/de/hstsoft/sdeep/res/paddle.png", 8, 8));

			ItemImage fort = ImageFactory.createImage("/de/hstsoft/sdeep/res/sea_fort.png", 32, 32, false);
			images.put(ItemTypes.SEA_FORT_1, fort);
			images.put(ItemTypes.SEA_FORT_2, fort);
			images.put(ItemTypes.SEA_FORT_3, fort);

			images.put(ItemTypes.FIRE, ImageFactory.createImage("/de/hstsoft/sdeep/res/fire_place.png", 8, 8));
			images.put(ItemTypes.ROCK_SHARD, ImageFactory.createImage("/de/hstsoft/sdeep/res/rock_shard.png", 8, 8));
			images.put(ItemTypes.PALM_FROND, ImageFactory.createImage("/de/hstsoft/sdeep/res/palm_frond.png", 8, 8));
			images.put(ItemTypes.DUCTTAPE, ImageFactory.createImage("/de/hstsoft/sdeep/res/ducttape.png", 8, 8));
			images.put(ItemTypes.AIRTANK, ImageFactory.createImage("/de/hstsoft/sdeep/res/airtank.png", 8, 8));
			images.put(ItemTypes.LIGHTER, ImageFactory.createImage("/de/hstsoft/sdeep/res/lighter.png", 8, 8));
			images.put(ItemTypes.COMPASS, ImageFactory.createImage("/de/hstsoft/sdeep/res/compass.png", 8, 8));
			images.put(ItemTypes.BUCKET, ImageFactory.createImage("/de/hstsoft/sdeep/res/bucket.png", 8, 8));
			images.put(ItemTypes.TORCH, ImageFactory.createImage("/de/hstsoft/sdeep/res/torch.png", 8, 8));

			ItemImage medicine = ImageFactory.createImage("/de/hstsoft/sdeep/res/vitamins.png", 8, 8);
			images.put(ItemTypes.VITAMINS, medicine);
			images.put(ItemTypes.ANTIBIOTICS, medicine);
			images.put(ItemTypes.BANDAGE, ImageFactory.createImage("/de/hstsoft/sdeep/res/bandage.png", 8, 8));

			images.put(ItemTypes.CAN_BEANS, ImageFactory.createImage("/de/hstsoft/sdeep/res/beans.png", 8, 8));

			images.put(ItemTypes.FUELCAN, ImageFactory.createImage("/de/hstsoft/sdeep/res/fueltank.png", 8, 8));

			images.put(ItemTypes.AXE, ImageFactory.createImage("/de/hstsoft/sdeep/res/axe.png", 8, 8));
			images.put(ItemTypes.HAMMER, ImageFactory.createImage("/de/hstsoft/sdeep/res/hammer.png", 8, 8));

			images.put(ItemTypes.FOUNDATION, ImageFactory.createImage("/de/hstsoft/sdeep/res/foundation.png", 8, 8));
			images.put(ItemTypes.FOUNDATION_ROOF, ImageFactory.createImage("/de/hstsoft/sdeep/res/foundation_roof.png", 8, 8));
			images.put(ItemTypes.FOUNDATION_SUPPORT,
					ImageFactory.createImage("/de/hstsoft/sdeep/res/foundation_support.png", 8, 8));

		} catch (IOException e) {
			e.printStackTrace();
		}

		doNotDraw.add(ItemTypes.CONSOLE_1);
		doNotDraw.add(ItemTypes.CONSOLE_2);
		doNotDraw.add(ItemTypes.CONSOLE_3);
		doNotDraw.add(ItemTypes.DOOR_1);
		doNotDraw.add(ItemTypes.DOOR_2);
		doNotDraw.add(ItemTypes.FLOOR_HATCH);
		doNotDraw.add(ItemTypes.HARDCASE_1);
		doNotDraw.add(ItemTypes.LOCKER_1);
		doNotDraw.add(ItemTypes.LOCKER_2);
		doNotDraw.add(ItemTypes.LOCKER_3);
		doNotDraw.add(ItemTypes.LOCKER_4);
		doNotDraw.add(ItemTypes.TOOLBOX_1);
		doNotDraw.add(ItemTypes.WALL_CABINET_1);
		doNotDraw.add(ItemTypes.SEA_FORT_BRIDGE);

	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		Graphics2D g2 = (Graphics2D) g.create();

		defaultTransform = g2.getTransform();
		g2.setRenderingHints(new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF));
		// g2.setRenderingHints(new RenderingHints(RenderingHints.KEY_INTERPOLATION,
		// RenderingHints.VALUE_INTERPOLATION_BILINEAR));

		if (bufferedMapImage == null || isMapDirty) {
			bufferedMapImage = drawMap(getWidth(), getHeight());
			isMapDirty = false;
		}
		g2.drawImage(bufferedMapImage, 0, 0, null);

		g2.setTransform(defaultTransform);
		drawCompass(g2);

		// set the default transform to draw the itemInfoPopup
		g2.setTransform(defaultTransform);
		if (itemInfoWindow != null) drawItemInfoWindow(g2, itemInfoWindow);

		g2.setTransform(defaultTransform);
		if (noteInfoWindow != null && showNotes) drawNoteInfoWindow(g2, noteInfoWindow);

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

	private void drawCompass(Graphics2D g2) {
		AffineTransform transform = g2.getTransform();

		// drawCompass
		g2.setRenderingHints(new RenderingHints(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR));
		final int centerX = getWidth() - 70 - 10;
		final int centerY = 10 + 70;
		transform.translate(centerX, centerY);
		transform.rotate(-rotation - (Math.toRadians(-45)));
		g2.setTransform(transform);
		ItemImage compassImg = images.get("COMPASS_RING");
		g2.drawImage(compassImg.getImage(), -compassImg.getWidth() / 2, -compassImg.getHeight() / 2, compassImg.getWidth(),
				compassImg.getHeight(), null);
		transform.rotate(rotation - (Math.toRadians(45)));
		g2.setTransform(transform);
		ItemImage needleImg = images.get("COMPASS_NEEDLE");
		g2.drawImage(needleImg.getImage(), -needleImg.getWidth() / 2, -needleImg.getHeight() / 2, needleImg.getWidth(),
				needleImg.getHeight(), null);
		g2.setRenderingHints(new RenderingHints(RenderingHints.KEY_INTERPOLATION,
				RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR));
	}

	private BufferedImage drawMap(int width, int height) {

		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = image.createGraphics();

		g2.setColor(new Color(105, 155, 195));
		g2.fillRect(0, 0, width, height);

		if (!initialized) {
			// Initialize the viewport by moving the origin to the center of the window.
			initialized = true;
			final int xc = width / 2;
			final int yc = height / 2;
			fontMetrics = g2.getFontMetrics();

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
			// Save the viewport to be updated by the ZoomAndPanListener
			zoomAndPanListener.setZoomLevel(0);
			zoomAndPanListener.setCoordTransform(affineTransform);
		}

		if (terrainGeneration != null) {
			// Restore the viewport after it was updated by the ZoomAndPanListener
			this.coordTransform = zoomAndPanListener.getCoordTransform();
			this.rotation = zoomAndPanListener.getRotation();
			g2.setTransform(coordTransform);

			ArrayList<TerrainNode> terrainNodes = terrainGeneration.getTerrainNodes();
			// draw the island shape
			drawTerrain(g2, terrainNodes);
			// draw the objects
			drawObjects(g2, terrainNodes);

			if (showNotes) drawNotes(g2, noteManager.getNotes());

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

		} else {
			Font saveFont = g2.getFont();
			g2.setRenderingHints(new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON));
			g2.setColor(Color.BLACK);
			g2.setFont(new Font("Arial", Font.PLAIN, 28));
			String str = "Please use the file menu to load a save game file.";
			int w = g2.getFontMetrics().stringWidth(str);
			g2.drawString(str, width / 2 - w / 2, height / 2);

			g2.setFont(new Font("Arial", Font.ITALIC, 14));
			str = "(The save game file can be found at <Stranded Deep installation folder>\\Stranded_Deep_x64_Data\\Data\\Save.json)";
			w = g2.getFontMetrics().stringWidth(str);
			g2.drawString(str, width / 2 - w / 2, height / 2 + 20);
			g2.setFont(saveFont);
		}
		return image;
	}

	private void drawItemInfoWindow(Graphics2D g2, ItemInfoWindow itemInfo) {
		g2.setColor(new Color(0, 0, 0, 150));
		g2.fill(itemInfo.bounds);

		int lineHeight = fontMetrics.getHeight();
		int startX = itemInfo.bounds.x + 5;
		int startY = itemInfo.bounds.y + lineHeight;

		g2.setColor(new Color(0, 0, 0, 200));
		g2.fillRect(itemInfo.bounds.x - lineHeight - 4, itemInfo.bounds.y, lineHeight + 4, itemInfo.bounds.height);
		String[] lines = itemInfo.lines;
		for (int i = 0; i < lines.length; i++) {

			if (images.containsKey(lines[i].split("x ")[1])) {
				ItemImage image = images.get(lines[i].split("x ")[1]);
				g2.drawImage(image.getImage(), startX - lineHeight - 7, startY + (lineHeight * i) - lineHeight + 3, lineHeight,
						lineHeight, null);
			} else {
				g2.setColor(Color.GREEN);
				g2.fillOval(startX - lineHeight - 4, startY + (lineHeight * i) - lineHeight + 6, 10, 10);
			}
			g2.setColor(Color.WHITE);
			g2.drawString(lines[i], startX, startY + (lineHeight * i));
		}
	}

	private void drawNoteInfoWindow(Graphics2D g2, NoteInfoWindow info) {

		int lineHeight = fontMetrics.getHeight();

		String lines[] = info.note.getText().split("\n");

		// calculate the width and height
		info.bounds.width = fontMetrics.stringWidth(info.note.getTitle()) + 10;
		for (int i = 0; i < lines.length; i++) {
			info.bounds.width = Math.max(info.bounds.width, fontMetrics.stringWidth(lines[i] + 10));
		}
		info.bounds.height = (lines.length * lineHeight) + lineHeight + 10;

		// move the window to the left and top of the mouse pointer
		info.bounds.x = info.bounds.x - info.bounds.width;
		info.bounds.y = info.bounds.y - info.bounds.height;

		// draw the background
		g2.setColor(new Color(0, 0, 0, 150));
		g2.fill(info.bounds);

		g2.setColor(new Color(0, 0, 0, 80));
		g2.fillRect(info.bounds.x, info.bounds.y, info.bounds.width, lineHeight + 4);

		// draw the note title and text
		int startX = info.bounds.x + 5;
		int startY = info.bounds.y + lineHeight;
		g2.setColor(Color.WHITE);
		g2.drawString(info.note.getTitle(), startX, startY);
		startY += lineHeight;
		for (int i = 0; i < lines.length; i++) {
			g2.drawString(lines[i], startX, startY + 5 + (i * lineHeight));
		}

	}

	private void drawNotes(Graphics2D g2, ArrayList<Note> notes) {
		AffineTransform t = new AffineTransform();
		for (Note note : notes) {

			AffineTransform originalTransform = g2.getTransform();

			// if (images.containsKey(gameObject.getType())) {
			double worldX = note.getPosition().getX();
			double worldZ = note.getPosition().getY();

			ItemImage image = images.get("NOTE");

			t.setToIdentity();
			t.translate(worldX, worldZ);
			// rotate the the canvas back to draw the object images aligned with the x-axis
			if (image.isAxisAligned()) t.rotate(-rotation);
			t.scale(-1, 1);
			g2.transform(t);

			g2.drawImage(image.getImage(), -image.getWidth() / 2, -image.getHeight() / 2, image.getWidth(), image.getHeight(),
					null);

			g2.setColor(Color.DARK_GRAY);
			g2.drawString(note.getTitle(), 8, 4);

			g2.setTransform(originalTransform);

		}
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
				ItemImage image = images.get("ISLAND_UNDISCOVERED");
				g2.drawImage(image.getImage(), -image.getWidth() / 2, -image.getHeight() / 2, null);
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

				if (images.containsKey(gameObject.getType())) {

					ItemImage image = images.get(gameObject.getType());

					t.setToIdentity();
					t.translate(worldX, worldZ);
					// rotate the the canvas back to draw the object images aligned with the x-axis
					if (image.isAxisAligned()) t.rotate(-rotation);
					t.scale(-1, 1);
					g2.transform(t);

					g2.drawImage(image.getImage(), -image.getWidth() / 2, -image.getHeight() / 2, image.getWidth(),
							image.getHeight(), null);
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

	private class MouseClickListener extends MouseAdapter {
		@Override
		public void mouseClicked(MouseEvent e) {
			super.mouseClicked(e);

			Point mousePosition = e.getPoint();

			Point2D mapCoordinates = new Point2D.Float();
			try {
				AffineTransform inverse = coordTransform.createInverse();
				inverse.transform(mousePosition, mapCoordinates);
			} catch (NoninvertibleTransformException e1) {
				e1.printStackTrace();
			}

			NoteDlg noteDlg;
			Note noteAt = noteManager.getNoteAt(mapCoordinates, 16);
			if (noteAt != null) {
				noteDlg = new NoteDlg(noteManager, noteAt);
			} else {
				noteDlg = new NoteDlg(noteManager, saveGame.getAtmosphere(), mapCoordinates);
			}

			Point locationOnScreen = e.getLocationOnScreen();
			noteDlg.setLocation(locationOnScreen);
			noteDlg.setVisible(true);

		}
	}

	private class MouseMotionListener extends MouseMotionAdapter {

		@Override
		public void mouseMoved(MouseEvent e) {
			if (terrainGeneration == null || !initialized) return;
			hoveredTerrainNode = null;
			itemInfoWindow = null;
			noteInfoWindow = null;

			Point mousePosition = e.getPoint();

			try {
				AffineTransform inverse = coordTransform.createInverse();
				inverse.transform(mousePosition, mouseOnMap);
			} catch (NoninvertibleTransformException e1) {
				e1.printStackTrace();
			}

			Note noteAt = noteManager.getNoteAt(mouseOnMap, 16);
			if (noteAt != null) {
				noteInfoWindow = new NoteInfoWindow();
				noteInfoWindow.note = noteAt;
				noteInfoWindow.bounds = new Rectangle(mousePosition);
			}

			ArrayList<TerrainNode> terrainNodes = terrainGeneration.getTerrainNodes();
			for (TerrainNode terrainNode : terrainNodes) {
				Position positionOffset = terrainNode.getPositionOffset();
				Rectangle nodeBounds = new Rectangle((int) positionOffset.x - 128, (int) positionOffset.z - 128, 256, 256);
				if (!nodeBounds.contains(mouseOnMap)) continue;

				hoveredTerrainNode = terrainNode;
				ArrayList<GameObject> objects = new ArrayList<>();

				TreeMap<String, Integer> objCount = new TreeMap<>();

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
					itemInfoWindow = new ItemInfoWindow();
					itemInfoWindow.lines = new String[objCount.size()];
					final int lineHeight = fontMetrics.getHeight();
					final int totalHeight = (lineHeight) * objCount.size();
					int width = 0;
					int i = 0;
					for (Entry<String, Integer> entry : objCount.entrySet()) {
						itemInfoWindow.lines[i] = entry.getValue().intValue() + "x " + entry.getKey();
						width = Math.max(width, fontMetrics.stringWidth(itemInfoWindow.lines[i]));
						i++;
					}
					Rectangle infoBounds = new Rectangle(mousePosition.x - width - 15, mousePosition.y - totalHeight - 5,
							width + 10, totalHeight + 5);
					itemInfoWindow.bounds = infoBounds;
					repaint();
				}

			}
			repaint();
		}
	}

	private class ItemInfoWindow {
		private Rectangle bounds;
		private String[] lines;
	}

	private class NoteInfoWindow {
		private Rectangle bounds;
		private Note note;
	}

	private void setTerrainGeneration(TerrainGeneration terrainGeneration) {
		this.terrainGeneration = terrainGeneration;
		isMapDirty = true;

		String directory = "worlds/" + terrainGeneration.getWorldSeed() + "/";

		// load or generate island shapes
		for (TerrainNode node : terrainGeneration.getTerrainNodes()) {
			if (!node.getBiome().equals("ISLAND")) continue;
			if (node.isFullyGernerated()) {
				islandShapeGenerationManager.enqueue(new IslandShapeGenerationTask(node, directory));
			}
		}
		repaint();
	}

	public boolean isShowInfo() {
		return this.showInfo;
	}

	public void setShowInfo(boolean showInfo) {
		this.showInfo = showInfo;
		isMapDirty = true;
		repaint();
	}

	public boolean isShowGrid() {
		return this.showGrid;
	}

	public void setShowGrid(boolean showGrid) {
		this.showGrid = showGrid;
		isMapDirty = true;
		repaint();
	}

	public boolean isShowNotes() {
		return this.showNotes;
	}

	public void setShowNotes(boolean showNotes) {
		this.showNotes = showNotes;
		isMapDirty = true;
		repaint();
	}

	public void resetView() {
		initialized = false;
		isMapDirty = true;
		repaint();
	}

	@Override
	public void onIslandLoaded(TerrainNode node, BufferedImage islandShape) {
		islandShapes.put(node.getName(), islandShape);
		isMapDirty = true;
		repaint();
	}

	/** @param saveGame */
	public void setSaveGame(SaveGame saveGame) {
		this.saveGame = saveGame;
		setTerrainGeneration(saveGame.getTerrainGeneration());
	}

	/** @param noteManager */
	public void setNoteManager(NoteManager noteManager) {
		this.noteManager = noteManager;
	}

}
