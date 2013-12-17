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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.sf.uadetector.OperatingSystem;
import net.sf.uadetector.ReadableUserAgent;
import net.sf.uadetector.UserAgentFamily;
import net.sf.uadetector.UserAgentStringParser;
import net.sf.uadetector.service.UADetectorServiceFactory;

public class UAParser {

	private static final UserAgentStringParser parser = UADetectorServiceFactory.getResourceModuleParser();

	private static final Map<String, String> osMap = new ConcurrentHashMap<String, String>();
	private static final Map<String, String> browserMap = new ConcurrentHashMap<String, String>();

	public String getOperatingSystem(String userAgent) {
		String result = osMap.get(userAgent);
		if (result == null) {
			final ReadableUserAgent agent = parser.parse(userAgent);
			final OperatingSystem os = agent.getOperatingSystem();
			result = os.getName();
			osMap.put(userAgent, result);
			// System.err.println("os=" + osMap.size());
		}
		return result;
	}

	public String getBrowser(String userAgent) {
		String result = browserMap.get(userAgent);
		if (result == null) {
			final ReadableUserAgent agent = parser.parse(userAgent);
			final UserAgentFamily family = agent.getFamily();
			result = family.getName();
			browserMap.put(userAgent, result);
			// System.err.println("browser=" + browserMap.size());
		}
		return result;
	}
}
