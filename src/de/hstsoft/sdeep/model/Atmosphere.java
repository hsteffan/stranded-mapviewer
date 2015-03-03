/** Copyright © 2015 Holger Steffan H.St. Soft
 * 
 * This file is subject to the terms and conditions defined in file 'license', which is part of this source code
 * package. */
package de.hstsoft.sdeep.model;

import org.json.simple.JSONObject;

import de.hstsoft.sdeep.util.Utils;

/** @author Holger Steffan created: 26.02.2015 */
public class Atmosphere {

	private static final String TIME_OF_DAY = "timeOfDay";
	private static final String YEAR = "year";
	private static final String MONTH = "month";
	private static final String DAY = "day";
	private static final String DAYS_ELAPSED = "daysElapsed";

	private float timeOfDay;
	private int year;
	private int month;
	private int day;
	private int daysElapsed;

	public float getTimeOfDay() {
		return this.timeOfDay;
	}

	public int getYear() {
		return this.year;
	}

	public int getMonth() {
		return this.month;
	}

	public int getDay() {
		return this.day;
	}

	public int getDaysElapsed() {
		return this.daysElapsed;
	}

	public static Atmosphere parse(JSONObject json) {
		Atmosphere atmosphere = new Atmosphere();

		atmosphere.timeOfDay = Utils.toFloat((String) json.get(TIME_OF_DAY));
		atmosphere.year = Utils.toInt(json.get(YEAR).toString());
		atmosphere.month = Utils.toInt(json.get(MONTH).toString());
		atmosphere.day = Utils.toInt(json.get(DAY).toString());
		atmosphere.daysElapsed = Utils.toInt(json.get(DAYS_ELAPSED).toString());

		return atmosphere;
	}

}
