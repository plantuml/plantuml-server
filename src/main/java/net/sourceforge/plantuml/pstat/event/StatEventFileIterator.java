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

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

public abstract class StatEventFileIterator implements Closeable, Iterator<StatEvent> {

	final public void remove() {
		throw new UnsupportedOperationException();
	}

	public static StatEventFileIterator fromFile(File f) throws IOException {
		if (f.getName().endsWith(".zip")) {
			return new StatEventFileIteratorZip(f);
		}
		return new StatEventFileIteratorSimple(f);
	}

}
