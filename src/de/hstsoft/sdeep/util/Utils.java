/** Copyright © 2015 Holger Steffan H.St. Soft
 * 
 * This file is subject to the terms and conditions defined in file 'license', which is part of this source code
 * package. */
package de.hstsoft.sdeep.util;

/** @author Holger Steffan created: 26.02.2015 */
public class Utils {

	public static float toFloat(String str) {
		if (!str.startsWith("~f")) throw new NumberFormatException("Can't convert '" + str + "' to float");
		return Float.parseFloat(str.substring(2));
	}

	public static int toInt(String str) {
		if (!str.startsWith("~i")) throw new NumberFormatException("Can't convert '" + str + "' to int");
		return Integer.parseInt(str.substring(2));
	}

	public static boolean toBool(String str) {
		if (!str.toLowerCase().equals("false") && !str.toLowerCase().equals("true"))
			throw new NumberFormatException("Can't convert '" + str + "' to bool");
		return Boolean.parseBoolean(str);
	}

}
