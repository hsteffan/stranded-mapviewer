/** Copyright © 2015 Holger Steffan H.St. Soft
 * 
 * This file is subject to the terms and conditions defined in file 'license', which is part of this source code
 * package. */
package de.hstsoft.sdeep.view;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashSet;
import java.util.concurrent.LinkedBlockingQueue;

public class IslandShapeGenerationManager implements Runnable {

	private LinkedBlockingQueue<IslandShapeGenerationTask> tasks = new LinkedBlockingQueue<>();
	private HashSet<IslandShapeGenerationTask> queuedTasks = new HashSet<>();
	private IslandLoadListener listener;

	public IslandShapeGenerationManager(IslandLoadListener listener) {
		this.listener = listener;
		new Thread(this, "IslandShapeGenerationManager").start();
	}

	public void enqueue(IslandShapeGenerationTask t) {
		if (queuedTasks.contains(t)) {
			System.out.println("Taks for " + t.getNode().getName() + " already queued.");
			return;
		}
		queuedTasks.add(t);
		tasks.offer(t);
	}

	@Override
	public void run() {

		while (true) {
			IslandShapeGenerationTask task;
			try {
				task = tasks.take();
			} catch (InterruptedException e) {
				return;
			}

			BufferedImage islandShape;
			try {
				islandShape = task.execute();
				listener.onIslandLoaded(task.getNode(), islandShape);
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				queuedTasks.remove(task);
			}
		}

	}

}