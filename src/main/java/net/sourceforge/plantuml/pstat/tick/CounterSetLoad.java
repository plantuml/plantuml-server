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

import java.util.ArrayList;
import java.util.Collection;

import net.sourceforge.plantuml.pstat.graph.Chart;
import net.sourceforge.plantuml.pstat.graph.ChartProducer;

public class CounterSetLoad {

	private final Collection<Integer> pageLoad = new ArrayList<Integer>();
	private final Collection<Integer> imageLoad = new ArrayList<Integer>();

	public CounterSetLoad() {
	}

	public void increment(TickBrowser tick) {
		if (tick.getImageLoad() > 0) {
			imageLoad.add(tick.getImageLoad());
		}
		if (tick.getPageLoad() > 0) {
			pageLoad.add(tick.getPageLoad());
		}
	}

	public Chart getChartPageLoad(double height) {
		return new ChartProducer(pageLoad).getChart(height);
	}

	public Chart getChartImageLoad(double height) {
		return new ChartProducer(imageLoad).getChart(height);
	}

}
