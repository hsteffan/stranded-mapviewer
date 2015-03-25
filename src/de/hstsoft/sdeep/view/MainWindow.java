/** Copyright © 2015 Holger Steffan H.St. Soft
 * 
 * This file is subject to the terms and conditions defined in file 'license', which is part of this source code
 * package. */
package de.hstsoft.sdeep.view;

import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.json.simple.parser.ParseException;

import de.hstsoft.sdeep.Configuration;
import de.hstsoft.sdeep.NoteManager;
import de.hstsoft.sdeep.SaveGameParser;
import de.hstsoft.sdeep.model.SaveGame;
import de.hstsoft.sdeep.util.FileWatcher;
import de.hstsoft.sdeep.util.FileWatcher.FileChangeListener;

/** @author Holger Steffan created: 26.02.2015 */
public class MainWindow extends JFrame {
	private static final long serialVersionUID = -8466802646984355229L;

	public static final String VERSION = "v0.4-alpha";

	private MapView mapView;
	private FileWatcher fileWatcher;
	private Configuration configuration;
	private JCheckBoxMenuItem menuItemFileWatcher;

	/** @author Holger Steffan created: 01.03.2015 */
	private final class SaveFileModifiedListener implements FileChangeListener {
		private final MapView mapView;

		/** @param mapView */
		private SaveFileModifiedListener(MapView mapView) {
			this.mapView = mapView;
		}

		@Override
		public void onFileChanged(File file) {

			try {
				// TODO get rid of the sleep. queue a task or something and ignore multiple parse requests if one
				// request is in the queue.
				Thread.sleep(250);
				SaveGame saveGame = new SaveGameParser().parse(file);
				mapView.setSaveGame(saveGame);
				// mapView.setTerrainGeneration(parse.getTerrainGeneration());
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public MainWindow() {
		setIconImage(Toolkit.getDefaultToolkit().getImage(
				MainWindow.class.getResource("/de/hstsoft/sdeep/res/island_undiscovered.png")));
		setTitle("Stranded Deep Mapviewer " + VERSION);

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		getContentPane().setLayout(new BorderLayout(0, 0));
	}

	private void openFileChooser() {

		final JFileChooser fileChooser = new JFileChooser(new File("."));
		int retVal = fileChooser.showOpenDialog(this);
		if (retVal == JFileChooser.APPROVE_OPTION) {
			File selectedFile = fileChooser.getSelectedFile();
			openFile(selectedFile);
		}

	}

	private void openFile(File file) {
		if (!file.exists()) {
			System.out.println("Savegame file '" + file.getPath() + "' does not exist.");
			return;
		}
		try {
			SaveGame saveGame = new SaveGameParser().parse(file);
			NoteManager noteManager = new NoteManager(saveGame.getTerrainGeneration().getWorldSeed(), mapView);
			noteManager.loadNotes();
			mapView.setNoteManager(noteManager);
			mapView.setSaveGame(saveGame);
			mapView.resetView();
			configuration.setSaveGamePath(saveGame.getPath());
			toggleAutoRefresh(configuration.isAutorefresh());
		} catch (IOException | ParseException e) {
			e.printStackTrace();
		}
	}

	private void toggleAutoRefresh(boolean enable) {

		configuration.setAutorefresh(enable);

		if (fileWatcher != null) {
			fileWatcher.stop();
			fileWatcher = null;
		}

		if (enable) {
			try {
				fileWatcher = new FileWatcher(new SaveFileModifiedListener(mapView));
				String savegamePath = configuration.getSavegamePath();
				File file = new File(savegamePath);
				if (file.exists()) fileWatcher.start(file);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}

	private void createUI() {
		mapView = new MapView();
		getContentPane().add(mapView, BorderLayout.CENTER);

		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);

		JMenuItem mntmOpenSaveGame = new JMenuItem("Open Savegame");
		mntmOpenSaveGame.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				openFileChooser();
			}
		});
		mnFile.add(mntmOpenSaveGame);

		mnFile.addSeparator();

		JMenuItem mntmExit = new JMenuItem("Exit");
		mntmExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		mnFile.add(mntmExit);

		JMenu mnView = new JMenu("View");
		menuBar.add(mnView);

		final JCheckBoxMenuItem menuItemShowinfo = new JCheckBoxMenuItem("ShowInfo");
		menuItemShowinfo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				boolean showInfo = menuItemShowinfo.isSelected();
				mapView.setShowInfo(showInfo);
			}
		});
		menuItemShowinfo.setSelected(mapView.isShowInfo());
		mnView.add(menuItemShowinfo);

		final JCheckBoxMenuItem menuItemShowgrid = new JCheckBoxMenuItem("ShowGrid");
		menuItemShowgrid.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				boolean showGrid = menuItemShowgrid.isSelected();
				mapView.setShowGrid(showGrid);
			}
		});
		menuItemShowgrid.setSelected(mapView.isShowGrid());
		mnView.add(menuItemShowgrid);

		final JCheckBoxMenuItem menuItemNotes = new JCheckBoxMenuItem("ShowNotes");
		menuItemNotes.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				boolean showNotes = menuItemNotes.isSelected();
				mapView.setShowNotes(showNotes);
			}
		});
		menuItemNotes.setSelected(mapView.isShowNotes());
		mnView.add(menuItemNotes);

		mnView.addSeparator();

		menuItemFileWatcher = new JCheckBoxMenuItem("Auto refresh");
		menuItemFileWatcher.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				boolean enabled = menuItemFileWatcher.isSelected();
				toggleAutoRefresh(enabled);
			}
		});
		mnView.add(menuItemFileWatcher);

		mnView.addSeparator();

		JMenuItem menuItemResetView = new JMenuItem("Reset view");
		menuItemResetView.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				mapView.resetView();
			}
		});
		mnView.add(menuItemResetView);

		JMenu mnInfo = new JMenu("Info");
		menuBar.add(mnInfo);

		JMenuItem mntmInfo = new JMenuItem("About");
		mntmInfo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showInfo();
			}
		});
		mnInfo.add(mntmInfo);

	}

	private void showInfo() {
		About about = new About();
		about.setLocationRelativeTo(this);
		about.setVisible(true);
	}

	private void loadConfiguration() {
		try {
			File file = new File("config.json");
			configuration = new Configuration(file);
			if (file.exists()) {
				configuration.load();
				openFile(new File(configuration.getSavegamePath()));
				menuItemFileWatcher.setSelected(configuration.isAutorefresh());
				toggleAutoRefresh(configuration.isAutorefresh());
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}

		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				MainWindow mainWindow = new MainWindow();
				mainWindow.createUI();
				mainWindow.setSize(1024, 768);
				mainWindow.loadConfiguration();
				mainWindow.setVisible(true);
			}
		});

	}

}
