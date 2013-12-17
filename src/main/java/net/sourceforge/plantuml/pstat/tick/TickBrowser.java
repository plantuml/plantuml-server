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

import net.sourceforge.plantuml.pstat.event.StatEvent;
import net.sourceforge.plantuml.pstat.event.StatEventHtmlCreate;
import net.sourceforge.plantuml.pstat.event.StatEventHtmlLoadImage;
import net.sourceforge.plantuml.pstat.event.StatEventHtmlLoadPage;

public class TickBrowser {

	// private final String token;
	private int pageLoad = -1;
	private int imageLoad = -1;
	private final int startMinute;

	public TickBrowser(StatEvent stat) {
		startMinute = (int) (stat.getStartServerTime() / 1000L / 60);
//		if (stat instanceof StatEventHtmlLoadImage) {
//			final StatEventHtmlLoadImage ev = (StatEventHtmlLoadImage) stat;
//			// token = ev.getToken();
//
//		} else if (stat instanceof StatEventHtmlLoadPage) {
//			final StatEventHtmlLoadPage ev = (StatEventHtmlLoadPage) stat;
//			// token = ev.getToken();
//
//		} else if (stat instanceof StatEventHtmlCreate) {
//			final StatEventHtmlCreate ev = (StatEventHtmlCreate) stat;
//			// token = ev.getToken();
//		} else {
//			throw new IllegalArgumentException();
//		}
		update(stat);

	}

	public void update(StatEvent stat) {
		if (stat instanceof StatEventHtmlLoadImage) {
			final StatEventHtmlLoadImage ev = (StatEventHtmlLoadImage) stat;
			imageLoad = ev.getImageLoad();

		} else if (stat instanceof StatEventHtmlLoadPage) {
			final StatEventHtmlLoadPage ev = (StatEventHtmlLoadPage) stat;
			pageLoad = ev.getPageLoad();

		} else if (stat instanceof StatEventHtmlCreate) {
			final StatEventHtmlCreate ev = (StatEventHtmlCreate) stat;
		} else {
			throw new IllegalArgumentException();
		}

	}

	public final int getPageLoad() {
		return pageLoad;
	}

	public final int getImageLoad() {
		return imageLoad;
	}

	public final int getStartMinute() {
		return startMinute;
	}

}
