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

public class StatEventHtmlLoadPage extends StatEvent {

	private final long start;
	private final long end;
	private final int width;
	private final int height;
	private final String token;

	@Override
	protected void saveSuppData(DataOutputStream os) throws IOException {
		os.writeLong(start);
		os.writeLong(end);
		os.writeInt(width);
		os.writeInt(height);
		writeStringSecure(os, token);
	}

	public StatEventHtmlLoadPage(DataInputStream is, long serverTime, String ipClient, Map<String, String> header,
			int activeCount, long freeMemory, long totalMemory) throws IOException {
		super(0x44, serverTime, ipClient, header, activeCount, freeMemory, totalMemory);
		this.start = is.readLong();
		this.end = is.readLong();
		this.width = is.readInt();
		this.height = is.readInt();
		this.token = is.readUTF();
	}

	public StatEventHtmlLoadPage(HttpServletRequest request, long date0, long start, long end, String token, int width,
			int height) {
		super(0x44, date0, request);
		this.start = start;
		this.end = end;
		this.width = width;
		this.height = height;
		this.token = token;

	}

	@Override
	public String toString() {
		final long duration = end - start;
		return super.toString() + " " + token + " (pa=" + duration + "ms) " + width + "x" + height;
	}

	public int getPageLoad() {
		final long duration = end - start;
		return (int) duration;
	}
	
	public String getToken() {
		return token;
	}

}
