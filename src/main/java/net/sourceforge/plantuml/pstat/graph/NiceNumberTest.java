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
package net.sourceforge.plantuml.pstat.graph;

import junit.framework.TestCase;

public class NiceNumberTest extends TestCase {

	public void testGetNicer203() {
		assertEquals(200, NiceNumber.getNicer(203));
		assertEquals(1200, NiceNumber.getNicer(1154));
	}

	public void testGetNicer5249() {
		assertEquals(5000, NiceNumber.getNicer(5249));
		assertEquals(5500, NiceNumber.getNicer(5250));
	}

	public void testGetNicerFull() {
		assertEquals(-1, NiceNumber.getNicer(-1));
		assertEquals(1, NiceNumber.getNicer(1));
		assertEquals(2, NiceNumber.getNicer(2));
		assertEquals(3, NiceNumber.getNicer(3));
		assertEquals(4, NiceNumber.getNicer(4));
		assertEquals(5, NiceNumber.getNicer(5));
		assertEquals(6, NiceNumber.getNicer(6));
		assertEquals(7, NiceNumber.getNicer(7));
		assertEquals(8, NiceNumber.getNicer(8));
		assertEquals(9, NiceNumber.getNicer(9));
		assertEquals(10, NiceNumber.getNicer(10));
		assertEquals(11, NiceNumber.getNicer(11));
		assertEquals(12, NiceNumber.getNicer(12));
		assertEquals(13, NiceNumber.getNicer(13));
		assertEquals(14, NiceNumber.getNicer(14));
		assertEquals(15, NiceNumber.getNicer(15));
		assertEquals(16, NiceNumber.getNicer(16));
		assertEquals(17, NiceNumber.getNicer(17));
		assertEquals(18, NiceNumber.getNicer(18));
		assertEquals(20, NiceNumber.getNicer(19));
		assertEquals(20, NiceNumber.getNicer(20));
		assertEquals(20, NiceNumber.getNicer(21));
		assertEquals(20, NiceNumber.getNicer(22));
		assertEquals(25, NiceNumber.getNicer(23));
		assertEquals(25, NiceNumber.getNicer(24));
		assertEquals(25, NiceNumber.getNicer(25));
		assertEquals(25, NiceNumber.getNicer(26));
		assertEquals(25, NiceNumber.getNicer(27));
		assertEquals(30, NiceNumber.getNicer(28));
		assertEquals(30, NiceNumber.getNicer(29));
		assertEquals(30, NiceNumber.getNicer(30));
		assertEquals(80, NiceNumber.getNicer(80));
		assertEquals(80, NiceNumber.getNicer(81));
		assertEquals(80, NiceNumber.getNicer(82));
		assertEquals(85, NiceNumber.getNicer(83));
		assertEquals(85, NiceNumber.getNicer(84));
		assertEquals(85, NiceNumber.getNicer(85));
		assertEquals(85, NiceNumber.getNicer(86));
		assertEquals(85, NiceNumber.getNicer(87));
		assertEquals(90, NiceNumber.getNicer(88));
		assertEquals(90, NiceNumber.getNicer(89));
		assertEquals(90, NiceNumber.getNicer(90));
		assertEquals(90, NiceNumber.getNicer(91));
		assertEquals(90, NiceNumber.getNicer(92));
		assertEquals(90, NiceNumber.getNicer(93));
		assertEquals(90, NiceNumber.getNicer(94));
		assertEquals(100, NiceNumber.getNicer(95));
		assertEquals(100, NiceNumber.getNicer(96));
		assertEquals(100, NiceNumber.getNicer(97));
		assertEquals(100, NiceNumber.getNicer(98));
		assertEquals(100, NiceNumber.getNicer(99));

	}

}
