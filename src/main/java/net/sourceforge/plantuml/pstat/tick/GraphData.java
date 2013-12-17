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

import java.util.Iterator;

import net.sourceforge.plantuml.pstat.time.DateManager;

public class GraphData {

	// private final CounterSet2 days31 = new CounterSet2();
	private final CounterSetString days15 = new CounterSetString();
	private final CounterSetString hours48 = new CounterSetString();
	private final CounterSetString minutes60 = new CounterSetString();
	private final CounterSetString minutes10 = new CounterSetString();

	private final CounterSetInteger hdays15 = new CounterSetInteger(DateManager.DAILY, 15);
	private final CounterSetInteger hhours48 = new CounterSetInteger(DateManager.HOURLY, 48);
	private final CounterSetInteger hminutes60 = new CounterSetInteger(DateManager.MINUTELY, 60);
	private final CounterSetInteger hminutes10 = new CounterSetInteger(DateManager.MINUTELY, 10);

	private final CounterSetLoad browserDay15 = new CounterSetLoad();
	private final CounterSetLoad browserHours48 = new CounterSetLoad();
	private final CounterSetLoad browserMinutes60 = new CounterSetLoad();
	private final CounterSetLoad browserMinutes10 = new CounterSetLoad();

	public GraphData() {
	}
	
	public GraphData(Iterator<TickImageGeneration> iterator, Iterator<TickBrowser> iterator2) {
		final int now = (int) (System.currentTimeMillis() / 1000L / 60);
		while (iterator.hasNext()) {
			final TickImageGeneration tick = iterator.next();
			final int startMinute = tick.getStartMinute();
			// if (startMinute >= now - 31 * 24 * 50) {
			// days31.increment(tick);
			// } else {
			// iterator.remove();
			// }
			if (startMinute >= now - 15 * 24 * 60) {
				days15.increment(tick);
				hdays15.increment(tick);
			} else {
				iterator.remove();
			}
			if (startMinute >= now - 48 * 60) {
				hours48.increment(tick);
				hhours48.increment(tick);
			}
			if (startMinute >= now - 60) {
				minutes60.increment(tick);
				hminutes60.increment(tick);
			}
			if (startMinute >= now - 10) {
				minutes10.increment(tick);
				hminutes10.increment(tick);
			}
		}
		while (iterator2.hasNext()) {
			final TickBrowser tick = iterator2.next();
			final int startMinute = tick.getStartMinute();
			if (startMinute >= now - 15 * 24 * 60) {
				browserDay15.increment(tick);
			} else {
				iterator2.remove();
			}
			if (startMinute >= now - 24 * 60) {
				browserHours48.increment(tick);
			}
			if (startMinute >= now - 60) {
				browserMinutes60.increment(tick);
			}
			if (startMinute >= now - 10) {
				browserMinutes10.increment(tick);
			}
		}
	}

	// public final CounterSet2 days31() {
	// return days31;
	// }

	public final CounterSetString days15() {
		return days15;
	}

	public final CounterSetString hours48() {
		return hours48;
	}

	public final CounterSetString minutes60() {
		return minutes60;
	}

	public final CounterSetString minutes10() {
		return minutes10;
	}

	public final CounterSetInteger hdays15() {
		return hdays15;
	}

	public final CounterSetInteger hhours48() {
		return hhours48;
	}

	public final CounterSetInteger hminutes60() {
		return hminutes60;
	}

	public final CounterSetInteger hminutes10() {
		return hminutes10;
	}

	public final CounterSetLoad bdays15() {
		return browserDay15;
	}

	public final CounterSetLoad bhours48() {
		return browserHours48;
	}

	public final CounterSetLoad bminutes60() {
		return browserMinutes60;
	}

	public final CounterSetLoad bminutes10() {
		return browserMinutes10;
	}

}
