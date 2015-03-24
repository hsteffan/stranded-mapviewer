/** Copyright © 2015 Holger Steffan H.St. Soft
 * 
 * This file is subject to the terms and conditions defined in file 'license', which is part of this source code
 * package. */
package de.hstsoft.sdeep;

import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Iterator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import de.hstsoft.sdeep.model.Note;

/** @author Holger Steffan created: 23.03.2015 */
public class NoteManager {

	private static final String FILENAME = "notes.json";
	private static final int VERSION = 1;

	public interface ChangeListener {
		void onNotesChanged();
	}

	private String directory;
	private ArrayList<Note> notes = new ArrayList<>();
	private ChangeListener listener;

	/** @param worldSeed */
	public NoteManager(int worldSeed, ChangeListener listener) {
		this.listener = listener;
		directory = "worlds/" + worldSeed + "/";
	}

	public void addNote(Note note) {
		notes.add(note);
		saveNotes();
		if (listener != null) listener.onNotesChanged();
	}

	/** @param note */
	public void updateNote(Note note) {
		// TODO implement updateing a Note.
		saveNotes();
		if (listener != null) listener.onNotesChanged();
	}

	public void remove(Note note) {
		notes.remove(note);
		saveNotes();
		if (listener != null) listener.onNotesChanged();
	}

	public Note getNoteAt(Point2D mapPosition, int size) {
		int x = (int) mapPosition.getX() - size / 2;
		int y = (int) mapPosition.getY() - size / 2;
		Rectangle rectangle = new Rectangle(x, y, size, size);

		for (Note n : notes) {
			if (rectangle.contains(n.getPosition())) {
				return n;
			}
		}
		return null;
	}

	/** @return the notes */
	public ArrayList<Note> getNotes() {
		return this.notes;
	}

	public void loadNotes() throws IOException, ParseException {

		File file = new File(directory + FILENAME);
		if (file.exists()) {

			Reader reader = new BufferedReader(new FileReader(file));
			JSONParser jsonParser = new JSONParser();
			JSONObject json = (JSONObject) jsonParser.parse(reader);
			reader.close();

			int version = Integer.parseInt(json.get("version").toString());
			if (VERSION != version) {
				System.out.println("Can not load Notes. invalid version.");
			}

			JSONArray notesArray = (JSONArray) json.get("notes");

			this.notes = new ArrayList<>();
			@SuppressWarnings("rawtypes")
			Iterator iterator = notesArray.iterator();
			while (iterator.hasNext()) {
				JSONObject noteJson = (JSONObject) iterator.next();
				Note note = Note.fromJson(noteJson);
				notes.add(note);
			}
		}

	}

	@SuppressWarnings("unchecked")
	private void saveNotes() {
		JSONObject envelope = new JSONObject();
		envelope.put("version", VERSION);

		JSONArray jsonArray = new JSONArray();
		for (Note n : notes) {
			jsonArray.add(n.toJson());
		}

		envelope.put("notes", jsonArray);

		try {
			File file = new File(directory + FILENAME);
			FileWriter fileWriter = new FileWriter(file);
			fileWriter.append(envelope.toJSONString());
			fileWriter.flush();
			fileWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
