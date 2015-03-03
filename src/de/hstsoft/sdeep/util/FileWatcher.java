/** Copyright © 2015 Holger Steffan H.St. Soft
 * 
 * This file is subject to the terms and conditions defined in file 'license', which is part of this source code
 * package. */
package de.hstsoft.sdeep.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.List;

/** @author Holger Steffan created: 27.02.2015 */
public class FileWatcher {

	private final FileChangeListener listener;
	private Thread thread;

	public static interface FileChangeListener {
		void onFileChanged(File file);
	}

	public FileWatcher(FileChangeListener l) {
		this.listener = l;
	}

	public void stop() {
		thread.interrupt();
	}

	public void start(final File file) throws IOException {
		final WatchService watchService = FileSystems.getDefault().newWatchService();

		final Path path = FileSystems.getDefault().getPath(file.getParent());
		path.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);

		thread = new Thread(new Runnable() {

			@Override
			public void run() {
				while (true) {

					// wait for key to be signaled
					WatchKey key;
					try {
						key = watchService.take();
					} catch (InterruptedException x) {
						return;
					}

					List<WatchEvent<?>> pollEvents = key.pollEvents();
					for (WatchEvent<?> event : pollEvents) {
						Kind<?> kind = event.kind();

						if (kind == StandardWatchEventKinds.OVERFLOW) continue;

						// The filename is the context of the event.
						@SuppressWarnings("unchecked")
						WatchEvent<Path> ev = (WatchEvent<Path>) event;
						Path filename = ev.context();

						Path resolve = path.resolve(filename);
						System.out.println("File event for '" + resolve + "' kind=" + kind.toString());

						if (listener != null) {
							String watchedFilePath = file.getPath();
							if (resolve.endsWith(watchedFilePath)) listener.onFileChanged(file);
						}

					}

					// Reset the key -- this step is critical if you want to
					// receive further watch events. If the key is no longer valid,
					// the directory is inaccessible so exit the loop.
					boolean valid = key.reset();
					if (!valid) {
						break;
					}
				}
			}
		}, "fileWatcher");
		thread.start();
	}
}
