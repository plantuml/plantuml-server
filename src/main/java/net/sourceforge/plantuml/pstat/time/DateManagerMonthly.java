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

import java.util.Date;

class DateManagerMonthly implements DateManager {

	static private final String[] MONTH = { "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct",
			"Nov", "Dec" };

	public int toNumber(long d) {
		final Date date = new Date(d);
		return date.getYear() * 12 + date.getMonth();
	}

	public String getDescription(int number) {
		final int month = number % 12;
		final int year = number / 12;
		return MONTH[month] + " " + (1900 + year);
	}

	public String getDescriptionShort(int number) {
		final int month = number % 12;
		return MONTH[month];
		// final int month = number % 12;
		// final int year = number / 12;
		// return "" + (year - 100) + "/" + String.format("%02d", month + 1);
	}

}
