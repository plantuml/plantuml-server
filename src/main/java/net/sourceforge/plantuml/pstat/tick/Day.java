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
package net.sourceforge.plantuml.pstat.tick;

import java.util.Date;

public class Day {

	private final int nday;

	public Day(long time) {
		nday = (int) (time / 1000L / 3600 / 24);
	}

	@Override
	public int hashCode() {
		return nday;
	}

	@Override
	public boolean equals(Object o) {
		final Day other = (Day) o;
		return nday == other.nday;
	}

	@Override
	public String toString() {
		return new Date(nday * 1000L * 3600 * 24).toGMTString();
	}

	public long getTime() {
		return nday * 1000L * 3600 * 24;
	}

}
