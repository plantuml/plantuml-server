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
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.maxmind.geoip.LookupService;

public abstract class StatEvent {

	private final int byteHeader;
	private final long serverTime;
	private final Map<String, String> header = new HashMap<String, String>();
	private final String ipClient;

	private final int activeCount;
	private final long freeMemory;
	private final long totalMemory;

	private static final UAParser uaParser = new UAParser();
	private static final LookupService countryService = initCountryService();

	private static LookupService initCountryService() {
		try {
			return new LookupService("GeoIP.dat", LookupService.GEOIP_MEMORY_CACHE);
		} catch (FileNotFoundException e) {
			System.err.println("path=" + new File("GeoIP.dat").getAbsolutePath() + " not found");
			return null;
		} catch (IOException e) {
			System.err.println("path=" + new File("GeoIP.dat").getAbsolutePath());
			e.printStackTrace();
			return null;
		}
	}

	synchronized public void save(DataOutputStream os) throws IOException {
		os.write(0x43);
		os.writeInt(activeCount);
		os.writeLong(freeMemory);
		os.writeLong(totalMemory);
		os.write(byteHeader);
		os.writeLong(serverTime);
		writeStringSecure(os, ipClient);
		for (Map.Entry<String, String> ent : header.entrySet()) {
			writeStringSecure(os, ent.getKey());
			writeStringSecure(os, ent.getValue());
		}
		writeStringSecure(os, "");
		saveSuppData(os);
	}

	public static StatEvent read(DataInputStream is) throws IOException {
		final int sign = is.read();
		if (sign == -1) {
			return null;
		}
		final int activeCount;
		final long freeMemory;
		final long totalMemory;
		if (sign == 0x42) {
			activeCount = 0;
			freeMemory = 0;
			totalMemory = 0;
		} else if (sign == 0x43) {
			activeCount = is.readInt();
			freeMemory = is.readLong();
			totalMemory = is.readLong();
		} else {
			throw new IOException("No 0x42 header found");
		}
		final int byteHeader = is.read();
		final long serverTime = is.readLong();
		final String ipClient = is.readUTF();
		final Map<String, String> header = new HashMap<String, String>();
		while (true) {
			final String key = is.readUTF();
			if (key.length() == 0) {
				break;
			}
			final String value = is.readUTF();
			header.put(key.toLowerCase(), value);
		}
		switch (byteHeader) {
		case 0x51:
			return new StatEventAdminStart(is, serverTime, ipClient, header, activeCount, freeMemory, totalMemory);
		case 0x52:
			return new StatEventAdminStop(is, serverTime, ipClient, header, activeCount, freeMemory, totalMemory);
		case 0x53:
			return new StatEventAdminSaveLog(is, serverTime, ipClient, header, activeCount, freeMemory, totalMemory);
		case 0x43:
			return new StatEventHtmlCreate(is, serverTime, ipClient, header, activeCount, freeMemory, totalMemory);
		case 0x44:
			return new StatEventHtmlLoadPage(is, serverTime, ipClient, header, activeCount, freeMemory, totalMemory);
		case 0x45:
			return new StatEventHtmlLoadImage(is, serverTime, ipClient, header, activeCount, freeMemory, totalMemory);
		case 0x46:
		case 0x47:
			return new StatEventImageGeneration(byteHeader, is, serverTime, ipClient, header, activeCount, freeMemory,
					totalMemory);
		}
		throw new IOException("Unknown header " + byteHeader);
	}

	protected abstract void saveSuppData(DataOutputStream os) throws IOException;

	public StatEvent(int byteHeader, long serverTime, HttpServletRequest request) {
		this.byteHeader = byteHeader;
		this.serverTime = serverTime;
		this.activeCount = Thread.activeCount();
		this.freeMemory = Runtime.getRuntime().freeMemory();
		this.totalMemory = Runtime.getRuntime().totalMemory();

		if (request != null) {
			for (Enumeration foo = request.getHeaderNames(); foo.hasMoreElements();) {
				String h = foo.nextElement().toString().toLowerCase();
				String v = request.getHeader(h);
				header.put(h, v);
			}
			this.ipClient = request.getRemoteAddr();
		} else {
			this.ipClient = "";
		}
	}

	public StatEvent(int byteHeader, long serverTime, String ipClient, Map<String, String> header, int activeCount,
			long freeMemory, long totalMemory) {
		this.byteHeader = byteHeader;
		this.serverTime = serverTime;
		this.ipClient = ipClient;
		this.header.putAll(header);
		this.activeCount = activeCount;
		this.freeMemory = freeMemory;
		this.totalMemory = totalMemory;
	}

	@Override
	public String toString() {
		// System.err.println(header.keySet());
		// System.err.println(header);
		final long f = freeMemory / 1024 / 1024;
		final long t = totalMemory / 1024 / 1024;
		return "{" + Integer.toHexString(byteHeader) + "} " + new Date(serverTime).toGMTString() + " " + f + "/" + t
				+ " " + activeCount + " " + ipClient + " {" + getAcceptLanguage() + "} {" + getCountry() + "} ["
				+ getRefererDetailed() + "] ";
	}

	public String getAcceptLanguage() {
		final String lang = header.get("accept-language");
		if (lang == null) {
			return "??";
		}
		if (lang.length() < 2) {
			return "??";
		}
		return lang.substring(0, 2);
	}

	public String getHost() {
		final String host = header.get("host");
		if (host == null) {
			return "??";
		}
		return host;
	}

	public String getUserAgent() {
		final String agent = header.get("user-agent");
		if (agent == null) {
			return "??";
		}
		return agent;
	}

	public String getReferer() {
		String agent = header.get("referer");
		if (agent == null) {
			return "??";
		}
		final int x = agent.indexOf('/', 9);
		if (x == -1) {
			return "??";
		}
		agent = agent.substring(0, x);
		if (agent.matches(".*\\.[a-z]{2,3}$")) {
			return agent;
		}
		return "Other";
	}

	private String getRefererDetailed() {
		String agent = header.get("referer");
		if (agent == null) {
			return "None";
		}
		final int x = agent.indexOf('/', 9);
		if (x == -1) {
			return "??";
		}
		agent = agent.substring(0, x);
		if (agent.matches(".*\\.[a-z]{2,3}$")) {
			return agent;
		}
		// return "Other";
		return agent;
	}

	public String getOperatingSystem() {
		final String user = getUserAgent();
		if ("??".equals(user)) {
			return "??";
		}
		final String result = uaParser.getOperatingSystem(user);
		if (result.startsWith("OS X")) {
			return "OS X";
		}
		if (result.startsWith("Android")) {
			return "Android";
		}
		if (result.startsWith("iOS")) {
			return "iOS";
		}
		return result;
	}

	public String getBrowser() {
		final String user = getUserAgent();
		if ("??".equals(user)) {
			return "??";
		}
		final String result = uaParser.getBrowser(user);
		return result;
	}

	public long getStartServerTime() {
		return serverTime;
	}

	protected Map<String, String> getHeader() {
		return Collections.unmodifiableMap(header);
	}

	public String getIpClient() {
		return ipClient;
	}

	public String getCountry() {
		if (countryService == null) {
			return "Not loaded";
		}
		if (ipClient == null) {
			return "??";
		}
		final String country = countryService.getCountry(ipClient).getName();
		if ("N/A".equals(country)) {
			return "??";
		}
		return country;
	}

	final protected void writeStringSecure(DataOutputStream oos, String s) throws IOException {
		if (s == null) {
			s = "";
		}
		oos.writeUTF(s);
	}

	final protected String shorten(String s) {
		if (s.length() > 20) {
			return s.substring(0, 20) + "...";
		}
		return s;
	}

	public final int getActiveCount() {
		return activeCount;
	}

	public final long getFreeMemory() {
		return freeMemory;
	}

	public final long getTotalMemory() {
		return totalMemory;
	}

}
