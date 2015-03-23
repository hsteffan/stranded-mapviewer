/** Copyright © 2015 Holger Steffan H.St. Soft
 * 
 * This file is subject to the terms and conditions defined in file 'license', which is part of this source code
 * package. */
package de.hstsoft.sdeep.model;

import java.awt.geom.Point2D;

import org.json.simple.JSONObject;

/** @author Holger Steffan created: 22.03.2015 */
public class Note {

	private int year;
	private int month;
	private int day;
	private int daysSurvived;

	private String title;
	private String text;

	private Point2D position;

	public int getYear() {
		return this.year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public int getMonth() {
		return this.month;
	}

	public void setMonth(int month) {
		this.month = month;
	}

	public int getDay() {
		return this.day;
	}

	public void setDay(int day) {
		this.day = day;
	}

	public int getDaysSurvived() {
		return this.daysSurvived;
	}

	public void setDaysSurvived(int daysSurvived) {
		this.daysSurvived = daysSurvived;
	}

	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getText() {
		return this.text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Point2D getPosition() {
		return this.position;
	}

	public void setPosition(Point2D position) {
		this.position = position;
	}

	@SuppressWarnings("unchecked")
	public JSONObject toJson() {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("x", (float) position.getX());
		jsonObject.put("y", (float) position.getY());
		jsonObject.put("title", title);
		jsonObject.put("text", text);
		jsonObject.put("year", year);
		jsonObject.put("month", month);
		jsonObject.put("day", day);
		jsonObject.put("daysSurvived", daysSurvived);

		return jsonObject;
	}

	public static Note fromJson(JSONObject obj) {
		Note note = new Note();
		float xPosition = Float.valueOf(obj.get("x").toString());
		float yPosition = Float.valueOf(obj.get("y").toString());
		note.position = new Point2D.Float(xPosition, yPosition);
		note.year = Integer.valueOf(obj.get("year").toString());
		note.month = Integer.valueOf(obj.get("month").toString());
		note.day = Integer.valueOf(obj.get("day").toString());
		note.daysSurvived = Integer.valueOf(obj.get("daysSurvived").toString());
		note.title = obj.get("title").toString();
		note.text = obj.get("text").toString();
		return note;
	}

}
