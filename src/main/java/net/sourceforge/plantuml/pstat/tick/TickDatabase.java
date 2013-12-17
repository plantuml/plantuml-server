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

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;

import net.sourceforge.plantuml.pstat.event.StatEvent;
import net.sourceforge.plantuml.pstat.event.StatEventHtmlCreate;
import net.sourceforge.plantuml.pstat.event.StatEventHtmlLoadImage;
import net.sourceforge.plantuml.pstat.event.StatEventHtmlLoadPage;
import net.sourceforge.plantuml.pstat.event.StatEventImageGeneration;

public class TickDatabase {

	private final LinkedBlockingDeque<TickImageGeneration> all = new LinkedBlockingDeque<TickImageGeneration>();
	private final ConcurrentHashMap<String, TickBrowser> browser = new ConcurrentHashMap<String, TickBrowser>();
	private final ConcurrentHashMap<Day, DailyStat> dailyStats = new ConcurrentHashMap<Day, DailyStat>();

	public void updateStats(StatEvent ev) {
		if (ev instanceof StatEventImageGeneration) {
			final StatEventImageGeneration ev2 = (StatEventImageGeneration) ev;
			final TickImageGeneration tick = new TickImageGeneration(ev2);
			final int duration = tick.getDuration();
			if (duration > 0) {
				all.add(tick);
			}
			final Day day = new Day(ev2.getStartServerTime());
			DailyStat dailyStat = dailyStats.get(day);
			if (dailyStat == null) {
				dailyStat = new DailyStat();
				final DailyStat old = dailyStats.putIfAbsent(day, dailyStat);
				if (old != null) {
					dailyStat = old;
				}
			}
			dailyStat.updateStats(ev2);
		} else if (ev instanceof StatEventHtmlLoadImage || ev instanceof StatEventHtmlLoadPage
				|| ev instanceof StatEventHtmlCreate) {
			final String token = getToken(ev);
			TickBrowser tickBrowser = browser.get(token);
			if (tickBrowser == null) {
				tickBrowser = new TickBrowser(ev);
				final TickBrowser old = browser.putIfAbsent(token, tickBrowser);
				if (old != null) {
					tickBrowser = old;
				}
			}
			tickBrowser.update(ev);

		}

	}

	static private String getToken(StatEvent ev) {
		if (ev instanceof StatEventHtmlLoadImage) {
			return ((StatEventHtmlLoadImage) ev).getToken();
		}
		if (ev instanceof StatEventHtmlLoadPage) {
			return ((StatEventHtmlLoadPage) ev).getToken();
		}
		if (ev instanceof StatEventHtmlCreate) {
			return ((StatEventHtmlCreate) ev).getToken();
		}
		throw new IllegalArgumentException();
	}

	public GraphData getGraphData() {
		return new GraphData(all.iterator(), browser.values().iterator());
	}

	public GraphDataLongTerm getGraphDataLongTerm() {
		return new GraphDataLongTerm(dailyStats.entrySet().iterator());
	}

}
