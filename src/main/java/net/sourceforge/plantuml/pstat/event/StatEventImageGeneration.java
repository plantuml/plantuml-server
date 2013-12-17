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
package net.sourceforge.plantuml.pstat.event;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

public class StatEventImageGeneration extends StatEvent {

	private final long date1;
	private final String uri;
	private String type = "??";

	@Override
	protected void saveSuppData(DataOutputStream os) throws IOException {
		os.writeLong(date1);
		writeStringSecure(os, uri);
		writeStringSecure(os, type);
	}

	public StatEventImageGeneration(int byteHeader, DataInputStream is, long serverTime, String ipClient,
			Map<String, String> header, int activeCount, long freeMemory, long totalMemory) throws IOException {
		super(byteHeader, serverTime, ipClient, header, activeCount, freeMemory, totalMemory);
		this.date1 = is.readLong();
		this.uri = is.readUTF();
		if (byteHeader == 0x47) {
			this.type = is.readUTF();
		}
	}

	public StatEventImageGeneration(HttpServletRequest request, long date0, long date1, String type) {
		super(0x47, date0, request);
		this.date1 = date1;
		this.uri = request.getRequestURI();
		this.type = type;
	}

	public StatEventImageGeneration(long date0, String uri, String type) {
		super(0x47, date0, null);
		this.date1 = 0;
		this.uri = uri;
		this.type = type;
	}

	@Override
	public String toString() {
		// final long duration = date1 == 0 ? -1 : date1 - getStartServerTime();
		return super.toString() + "(" + type + ") [" + getDuration() + "ms] " + shorten(uri);
	}

	public int getDuration() {
		final long duration = date1 == 0 ? -1 : date1 - getStartServerTime();
		return (int) duration;
	}

	public String getUri() {
		return uri;
	}

	public String getType() {
		return type;
	}

	public String getIncomming() {
		final String refererer = getReferer();
		if (refererer.startsWith("http://www.plantuml.com")) {
			return "www.plantuml.com";
		}
		if (refererer.startsWith("http://plantuml.com")) {
			return "plantuml.com";
		}
		return "Other website";
	}

}
