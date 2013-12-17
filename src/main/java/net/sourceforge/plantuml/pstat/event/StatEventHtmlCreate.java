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

public class StatEventHtmlCreate extends StatEvent {

	private final long date1;
	private final String token;
	private final String encoded;

	@Override
	protected void saveSuppData(DataOutputStream os) throws IOException {
		os.writeLong(date1);
		writeStringSecure(os, token);
		writeStringSecure(os, encoded);
	}

	public StatEventHtmlCreate(DataInputStream is, long serverTime, String ipClient, Map<String, String> header,
			int activeCount, long freeMemory, long totalMemory) throws IOException {
		super(0x43, serverTime, ipClient, header, activeCount, freeMemory, totalMemory);
		this.date1 = is.readLong();
		this.token = is.readUTF();
		this.encoded = is.readUTF();
	}

	public StatEventHtmlCreate(HttpServletRequest request, long date0, long date1, String token, String encoded) {
		super(0x43, date0, request);
		this.date1 = date1;
		this.token = token;
		this.encoded = encoded;
	}

	@Override
	public String toString() {
		final long duration = date1 - getStartServerTime();
		return super.toString() + " " + token + " (ht=" + duration + "ms) " + shorten(encoded);
	}

	public String getToken() {
		return token;
	}

}
