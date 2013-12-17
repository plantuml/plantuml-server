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
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

class StatEventFileIteratorZip extends StatEventFileIterator {

	private StatEvent potentialNext;
	private ZipInputStream zip;
	private ZipEntry current;
	private DataInputStream is;

	public StatEventFileIteratorZip(File f) throws IOException {
		this.zip = new ZipInputStream(new BufferedInputStream(new FileInputStream(f)));
		current = zip.getNextEntry();
		is = new DataInputStream(zip);
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
			if (potentialNext == null) {
				current = zip.getNextEntry();
				if (current != null) {
					// is = new DataInputStream(zip);
					this.potentialNext = StatEvent.read(is);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new UnsupportedOperationException();
		}
		return result;
	}

	public static void main(String[] args) throws IOException {
		final File f = new File("G:/log2/pdata2013_11.zip");
		final StatEventFileIteratorZip it = new StatEventFileIteratorZip(f);
		int nb = 0;
		while (it.hasNext()) {
			final StatEvent ev = it.next();
			nb++;
			// System.out.println(ev);
		}
		System.err.println("nb=" + nb);
		it.close();

	}
}
