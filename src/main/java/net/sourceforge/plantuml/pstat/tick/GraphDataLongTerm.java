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
import java.util.Map;
import java.util.Map.Entry;

import net.sourceforge.plantuml.pstat.graph.HistogramBuilder;
import net.sourceforge.plantuml.pstat.time.DateManager;

public class GraphDataLongTerm {

	private final HistogramBuilder builder = new HistogramBuilder(DateManager.MONTHLY, 24);

	public GraphDataLongTerm(Iterator<Entry<Day, DailyStat>> it) {
		while (it.hasNext()) {
			Map.Entry<Day, DailyStat> ent = it.next();
			final Day day = ent.getKey();
			final DailyStat stat = ent.getValue();
			builder.incKey(day.getTime(), stat.getSize());
		}
	}

	public GraphDataLongTerm() {
	}

	public HistogramBuilder getHistogramBuilder() {
		return builder;
	}

}
