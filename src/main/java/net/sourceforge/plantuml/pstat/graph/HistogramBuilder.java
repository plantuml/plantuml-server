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
package net.sourceforge.plantuml.pstat.graph;

import java.util.TreeMap;

import net.sourceforge.plantuml.pstat.time.DateManager;

public class HistogramBuilder {

	private final DateManager dateManager;
	private final int histoSize;
	private final TreeMap<Integer, MutableInteger> data = new TreeMap<Integer, MutableInteger>();

	public HistogramBuilder(DateManager dateManager, int histoSize) {
		this.dateManager = dateManager;
		this.histoSize = histoSize;
	}

	public Histogram getHistogram(double height, double singleBarWidth, int fontSize) {
		final Histogram histogram = new Histogram(height, singleBarWidth, fontSize);
		if (data.size() > 0) {
			final int last = data.lastKey();
			for (int i = last - histoSize + 1; i <= last; i++) {
				final String label = dateManager.getDescriptionShort(i);
				final MutableInteger n = data.get(i);
				final int value = n == null ? 0 : n.intValue();
				final String title = "[" + value + " diagrams] at [" + dateManager.getDescription(i) + "]";
				histogram.addData(label, value, title);
			}
		}
		return histogram;
	}

	public void incKey(long time, int value) {
		final int k = dateManager.toNumber(time);
		final MutableInteger v = data.get(k);
		if (v == null) {
			data.put(k, new MutableInteger(value));
		} else {
			v.inc(value);
		}
	}

}
