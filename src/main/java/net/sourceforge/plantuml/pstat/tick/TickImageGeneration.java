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

import net.sourceforge.plantuml.pstat.event.StatEventImageGeneration;

public class TickImageGeneration {

	private final String country;
	private final String acceptLanguage;
	private final String browser;
	private final String operatingSystem;
	private final String referer;
	private final String type;
	private final String incomming;
	private final String host;
	private final int startMinute;
	private final int duration;

	public TickImageGeneration(StatEventImageGeneration stat) {
		this.country = stat.getCountry();
		this.acceptLanguage = stat.getAcceptLanguage();
		this.browser = stat.getBrowser();
		this.operatingSystem = stat.getOperatingSystem();
		this.referer = stat.getReferer();
		this.incomming = stat.getIncomming();
		this.host = stat.getHost();
		this.type = stat.getType();
		this.startMinute = (int) (stat.getStartServerTime() / 1000L / 60);
		this.duration = stat.getDuration();

	}

	@Override
	public String toString() {
		return "tick " + new Date(startMinute * 1000L * 60).toGMTString();
	}

	public final String getCountry() {
		return country;
	}

	public final String getAcceptLanguage() {
		return acceptLanguage;
	}

	public final String getBrowser() {
		return browser;
	}

	public final String getOperatingSystem() {
		return operatingSystem;
	}

	public final String getReferer() {
		return referer;
	}

	public final String getType() {
		return type;
	}

	public final int getStartMinute() {
		return startMinute;
	}

	public final int getDuration() {
		return duration;
	}

	public final String getIncomming() {
		return incomming;
	}

	public final String getHost() {
		return host;
	}

}
