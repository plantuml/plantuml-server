/* ========================================================================
 * PlantUML : a free UML diagram generator
 * ========================================================================
 *
 * Project Info:  http://plantuml.sourceforge.net
 *
 * This file is part of PlantUML.
 *
 * PlantUML is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * PlantUML distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301,
 * USA.
 */
package net.sourceforge.plantuml.pstat.time;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

class DateManagerHourly implements DateManager {

	private static final long COEF = 1000L * 3600;

	public int toNumber(long date) {
		final long m = date / COEF;
		return (int) m;
	}

	// private static DateFormat df = new SimpleDateFormat("yyyy MMM EEE dd HH:00", Locale.US);
	// private static DateFormat dfShort = new SimpleDateFormat("HH", Locale.US);

	public String getDescription(int number) {
		final DateFormat df = new SimpleDateFormat("yyyy MMM EEE dd HH:00", Locale.US);
		final Date date = new Date(number * COEF);
		return df.format(date);
		// return date.toString();
	}

	public String getDescriptionShort(int number) {
		final DateFormat dfShort = new SimpleDateFormat("HH", Locale.US);
		final Date date = new Date(number * COEF);
		return dfShort.format(date);
	}

}
