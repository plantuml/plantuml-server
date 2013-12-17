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

public class StatEventAdminSaveLog extends StatEvent {

	private final int saveDuration;
	private final int nbSaved;
	private final int nbRemainingAfterSave;

	@Override
	protected void saveSuppData(DataOutputStream os) throws IOException {
		os.writeInt(saveDuration);
		os.writeInt(nbSaved);
		os.writeInt(nbRemainingAfterSave);
	}

	public StatEventAdminSaveLog(int saveDuration, int nbSaved, int nbRemainingAfterSave) {
		super(0x53, System.currentTimeMillis(), null);
		this.saveDuration = saveDuration;
		this.nbSaved = nbSaved;
		this.nbRemainingAfterSave = nbRemainingAfterSave;
	}

	public StatEventAdminSaveLog(DataInputStream is, long serverTime, String ipClient, Map<String, String> header,
			int activeCount, long freeMemory, long totalMemory) throws IOException {
		super(0x53, serverTime, ipClient, header, activeCount, freeMemory, totalMemory);
		this.saveDuration = is.readInt();
		this.nbSaved = is.readInt();
		this.nbRemainingAfterSave = is.readInt();
	}

	@Override
	public String toString() {
		return super.toString() + " SAVELOG " + saveDuration + " ms, nb=" + nbSaved + " remain=" + nbRemainingAfterSave;
	}

}
