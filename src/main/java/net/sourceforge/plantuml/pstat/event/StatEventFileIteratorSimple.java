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

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;

class StatEventFileIteratorSimple extends StatEventFileIterator {

	private DataInputStream is;
	private StatEvent potentialNext;

	public StatEventFileIteratorSimple(File f) throws IOException {
		if (f.getName().endsWith(".gz")) {
			this.is = new DataInputStream(new GZIPInputStream(new BufferedInputStream(new FileInputStream(f))));
		} else {
			this.is = new DataInputStream(new BufferedInputStream(new FileInputStream(f)));
		}
		this.potentialNext = StatEvent.read(is);

	}

	public void close() throws IOException {
		is.close();
		is = null;
	}

	public boolean hasNext() {
		return potentialNext != null;
	}

	public StatEvent next() {
		final StatEvent result = potentialNext;
		try {
			potentialNext = StatEvent.read(is);
		} catch (IOException e) {
			e.printStackTrace();
			throw new UnsupportedOperationException();
		}
		return result;
	}

	public static void main(String[] args) throws IOException {
		final File f = new File("C:/eclipse/eclipse/pdata2013_11.pat");
		final StatEventFileIteratorSimple it = new StatEventFileIteratorSimple(f);
		while (it.hasNext()) {
			final StatEvent ev = it.next();
			System.out.println(ev);
		}
		it.close();

	}
}
