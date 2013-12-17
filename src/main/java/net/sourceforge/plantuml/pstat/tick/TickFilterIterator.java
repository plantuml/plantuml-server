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

import java.util.Iterator;
import java.util.NoSuchElementException;

class TickFilterIterator implements Iterator<TickImageGeneration> {

	private final Iterator<TickImageGeneration> it;
	private final int timeLimitMinute;
	private TickImageGeneration potentialNext;

	TickFilterIterator(Iterator<TickImageGeneration> it, int timeLimitMinute) {
		this.it = it;
		this.timeLimitMinute = timeLimitMinute;
		this.potentialNext = getPotentialNext();
	}

	private TickImageGeneration getPotentialNext() {
		while (it.hasNext()) {
			potentialNext = it.next();
			if (potentialNext.getStartMinute() >= timeLimitMinute) {
				return potentialNext;
			}
		}
		return null;
	}

	public boolean hasNext() {
		return potentialNext != null;
	}

	public TickImageGeneration next() {
		if (potentialNext == null) {
			throw new NoSuchElementException();
		}
		final TickImageGeneration result = potentialNext;
		potentialNext = getPotentialNext();
		return result;
	}

	public void remove() {
		throw new UnsupportedOperationException();
	}

}
