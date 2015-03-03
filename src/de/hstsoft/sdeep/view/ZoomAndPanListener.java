/** Copyright © 2015 Holger Steffan H.St. Soft
 * 
 * This file is subject to the terms and conditions defined in file 'license', which is part of this source code
 * package. */
package de.hstsoft.sdeep.view;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;

/** @author Holger Steffan created: 28.02.2015 */
public class ZoomAndPanListener implements MouseListener, MouseMotionListener, MouseWheelListener {
	public static final int DEFAULT_MIN_ZOOM_LEVEL = -10;
	public static final int DEFAULT_MAX_ZOOM_LEVEL = 20;
	public static final double DEFAULT_ZOOM_MULTIPLICATION_FACTOR = 1.2;

	private Component targetComponent;

	private int zoomLevel = 0;
	private int minZoomLevel = DEFAULT_MIN_ZOOM_LEVEL;
	private int maxZoomLevel = DEFAULT_MAX_ZOOM_LEVEL;
	private double zoomMultiplicationFactor = DEFAULT_ZOOM_MULTIPLICATION_FACTOR;

	private Point dragStartScreen;
	private Point dragEndScreen;
	private Point lastDragScreen = new Point();
	private AffineTransform coordTransform = new AffineTransform();
	private int button;

	public ZoomAndPanListener(Component targetComponent) {
		this.targetComponent = targetComponent;
	}

	public ZoomAndPanListener(Component targetComponent, int minZoomLevel, int maxZoomLevel, double zoomMultiplicationFactor) {
		this.targetComponent = targetComponent;
		this.minZoomLevel = minZoomLevel;
		this.maxZoomLevel = maxZoomLevel;
		this.zoomMultiplicationFactor = zoomMultiplicationFactor;
	}

	public void mouseClicked(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {
		dragStartScreen = e.getPoint();
		dragEndScreen = null;
		lastDragScreen = dragStartScreen;
		button = e.getButton();
	}

	public void mouseReleased(MouseEvent e) {
		button = MouseEvent.NOBUTTON;
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mouseMoved(MouseEvent e) {
	}

	public void mouseDragged(MouseEvent e) {
		if (button == MouseEvent.BUTTON3) moveCamera(e);
		if (button == MouseEvent.BUTTON2) {
			rotateCamera(e);
			lastDragScreen = e.getPoint();
		}
	}

	public void mouseWheelMoved(MouseWheelEvent e) {
		zoomCamera(e);
	}

	private void rotateCamera(MouseEvent e) {
		try {
			Point center = new Point(targetComponent.getWidth() / 2, targetComponent.getHeight() / 2);

			Point2D.Float anchor = transformPoint(center);
			coordTransform.translate(anchor.x, anchor.y);

			double dx = e.getPoint().getX() - lastDragScreen.getX();
			double angle = dx * 0.1f;
			// System.out.println(dx + " angle:" + Math.toDegrees(angle));
			coordTransform.rotate(Math.toRadians(angle));

			coordTransform.translate(-anchor.x, -anchor.y);

			targetComponent.repaint();
		} catch (NoninvertibleTransformException e1) {
			e1.printStackTrace();
		}
	}

	private void moveCamera(MouseEvent e) {
		try {
			dragEndScreen = e.getPoint();
			Point2D.Float dragStart = transformPoint(dragStartScreen);
			Point2D.Float dragEnd = transformPoint(dragEndScreen);
			double dx = dragEnd.getX() - dragStart.getX();
			double dy = dragEnd.getY() - dragStart.getY();
			coordTransform.translate(dx, dy);
			dragStartScreen = dragEndScreen;
			dragEndScreen = null;
			targetComponent.repaint();
		} catch (NoninvertibleTransformException ex) {
			ex.printStackTrace();
		}
	}

	private void zoomCamera(MouseWheelEvent e) {
		try {
			int wheelRotation = e.getWheelRotation();
			Point p = e.getPoint();
			if (wheelRotation > 0) {
				if (zoomLevel < maxZoomLevel) {
					zoomLevel++;
					Point2D p1 = transformPoint(p);
					coordTransform.scale(1 / zoomMultiplicationFactor, 1 / zoomMultiplicationFactor);
					Point2D p2 = transformPoint(p);
					coordTransform.translate(p2.getX() - p1.getX(), p2.getY() - p1.getY());
					targetComponent.repaint();
				}
			} else {
				if (zoomLevel > minZoomLevel) {
					zoomLevel--;
					Point2D p1 = transformPoint(p);
					coordTransform.scale(zoomMultiplicationFactor, zoomMultiplicationFactor);
					Point2D p2 = transformPoint(p);
					coordTransform.translate(p2.getX() - p1.getX(), p2.getY() - p1.getY());
					targetComponent.repaint();
				}
			}
		} catch (NoninvertibleTransformException ex) {
			ex.printStackTrace();
		}
	}

	private Point2D.Float transformPoint(Point p1) throws NoninvertibleTransformException {
		AffineTransform inverse = coordTransform.createInverse();
		Point2D.Float p2 = new Point2D.Float();
		inverse.transform(p1, p2);
		return p2;
	}

	public int getZoomLevel() {
		return zoomLevel;
	}

	public void setZoomLevel(int zoomLevel) {
		this.zoomLevel = zoomLevel;
	}

	public double getRotation() {
		return Math.atan2(coordTransform.getShearY(), coordTransform.getScaleY());
	}

	public AffineTransform getCoordTransform() {
		return coordTransform;
	}

	public void setCoordTransform(AffineTransform coordTransform) {
		this.coordTransform = coordTransform;
	}

}
