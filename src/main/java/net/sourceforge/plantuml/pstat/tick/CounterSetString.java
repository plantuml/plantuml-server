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

import java.util.Map;
import java.util.TreeMap;

import net.sourceforge.plantuml.pstat.graph.Chart;
import net.sourceforge.plantuml.pstat.graph.HistoList;

public class CounterSetString {

	private final Map<String, Integer> language = new TreeMap<String, Integer>();
	private final Map<String, Integer> host = new TreeMap<String, Integer>();
	private final Map<String, Integer> operationSystem = new TreeMap<String, Integer>();
	private final Map<String, Integer> browser = new TreeMap<String, Integer>();
	private final Map<String, Integer> referer = new TreeMap<String, Integer>();
	private final Map<String, Integer> type = new TreeMap<String, Integer>();
	private final Map<String, Integer> incomming = new TreeMap<String, Integer>();
	private final Map<String, Integer> country = new TreeMap<String, Integer>();

	public void increment(TickImageGeneration tick) {
		increment(language, tick.getAcceptLanguage());
		increment(host, tick.getHost());
		increment(operationSystem, tick.getOperatingSystem());
		increment(browser, tick.getBrowser());
		increment(referer, tick.getReferer());
		increment(type, tick.getType());
		increment(incomming, tick.getIncomming());
		increment(country, tick.getCountry());
	}

	private void increment(Map<String, Integer> data, String s) {
		final Integer v = data.get(s);
		if (v == null) {
			data.put(s, 1);
		} else {
			data.put(s, v + 1);
		}
	}

	public String getTitle() {
		return "language.getTitle()";
	}

	public Chart getChartLanguage(double height) {
		return getChart(height, language);
	}

	public Chart getChartHost(double height) {
		return getChart(height, host);
	}

	public Chart getChartOperationSystem(double height) {
		return getChart(height, operationSystem);
	}

	public Chart getChartBrowser(double height) {
		return getChart(height, browser);
	}

	public Chart getChartReferer(double height) {
		return getChart(height, referer);
	}

	public Chart getChartType(double height) {
		return getChart(height, type);
	}

	public Chart getChartIncomming(double height) {
		return getChart(height, incomming);
	}

	private Chart getChart(double height, Map<String, Integer> counter) {
		final Chart chart = new Chart(height);
		for (Map.Entry<String, Integer> ent : counter.entrySet()) {
			final String s = ent.getKey();
			if ("??".equals(s)) {
				continue;
			}
			chart.addData(s, ent.getValue());
		}
		chart.compact();
		return chart;
	}

	public Chart getChartRefererOther(double height) {
		final Chart chart = new Chart(height);
		for (Map.Entry<String, Integer> ent : referer.entrySet()) {
			final String s = ent.getKey();
			if ("??".equals(s) || "http://www.plantuml.com".equals(s) || "http://plantuml.com".equals(s)
					|| "http://plantuml.sourceforge.net".equals(s) || "Other".equals(s)) {
				continue;
			}
			chart.addData(s, ent.getValue());
		}
		chart.removeBig(0.5);
		return chart;
	}

	public HistoList getHistoListReferer(double graphWidth, double singleBarHeight, int fontSize) {
		final HistoList chart = new HistoList(graphWidth, singleBarHeight, fontSize);
		for (Map.Entry<String, Integer> ent : referer.entrySet()) {
			final String s = ent.getKey();
			if ("??".equals(s)) {
				continue;
			}
			chart.addData(s, ent.getValue());
		}
		return chart;
	}

	public HistoList getHistoListCountry(double graphWidth, double singleBarHeight, int fontSize) {
		final HistoList chart = new HistoList(graphWidth, singleBarHeight, fontSize);
		for (Map.Entry<String, Integer> ent : country.entrySet()) {
			final String s = ent.getKey();
			if ("??".equals(s)) {
				continue;
			}
			chart.addData(s, ent.getValue());
		}
		return chart;
	}

}
