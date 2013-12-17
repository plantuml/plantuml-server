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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ChartProducer {

	private final Collection<Integer> datas;

	public ChartProducer(Collection<Integer> all) {
		datas = all;
	}

	public Chart getChart(double height) {
		final Chart chart = new Chart(height);
		final int nb = datas.size();
		if (nb == 0) {
			return chart;
		}

		long sum = 0;
		int max = 0;
		for (int v : datas) {
			sum += v;
			max = Math.max(max, v);
		}

		final int mean = NiceNumber.getNicer((int) (sum / nb));

		final List<Step> steps = new ArrayList<Step>();
		steps.add(new Step(0, NiceNumber.getNicer(mean / 3)));
		steps.add(new Step(NiceNumber.getNicer(mean / 3), NiceNumber.getNicer(2 * mean / 3)));
		steps.add(new Step(NiceNumber.getNicer(2 * mean / 3), mean));
		final int delta = (max - mean) / 3;
		steps.add(new Step(mean, NiceNumber.getNicer(mean + delta)));
		steps.add(new Step(NiceNumber.getNicer(mean + delta), NiceNumber.getNicer(mean + 2 * delta)));
		steps.add(new Step(NiceNumber.getNicer(mean + 2 * delta), max + 1));

		for (int v : datas) {
			count(steps, v);
		}
		final int lim = datas.size() / 50;
		while (eventuallyMerge(steps, lim)) {

		}

		for (Step s : steps) {
			chart.addData(s.getTitle(), s.count);
		}
		return chart;
	}

	static class Step {
		final private int from;
		final private int to;
		private int count;

		Step(int from, int to) {
			this.from = from;
			this.to = to;
		}

		Step mergeWith(Step other) {
			if (this.to != other.from) {
				throw new IllegalArgumentException();
			}
			final Step result = new Step(this.from, other.to);
			result.count = this.count + other.count;
			return result;
		}

		@Override
		public String toString() {
			return "" + from + "->" + to + " (" + count + ")";
		}

		boolean count(int v) {
			if (v >= from && v < to) {
				count++;
				return true;
			}
			return false;
		}

		String getTitle() {
			return "" + from + " ms to " + to + " ms";
		}
	}

	private void count(final List<Step> steps, int v) {
		for (Step s : steps) {
			if (s.count(v)) {
				return;
			}
		}
	}

	private boolean eventuallyMerge(final List<Step> steps, final int lim) {
		for (int i = 0; i < steps.size() - 1; i++) {
			final Step s1 = steps.get(i);
			final Step s2 = steps.get(i + 1);
			if (s1.count < lim && s2.count < lim) {
				steps.set(i, s1.mergeWith(s2));
				steps.remove(i + 1);
				return true;
			}
		}
		return false;
	}

}
