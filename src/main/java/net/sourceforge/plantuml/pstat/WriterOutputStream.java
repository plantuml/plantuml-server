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
package net.sourceforge.plantuml.pstat;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

public class WriterOutputStream extends OutputStream {

	final Writer pw;

	public WriterOutputStream(Writer pw) {
		this.pw = pw;
	}

	@Override
	public void write(int data) throws IOException {
		pw.write(data);

	}

	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		final String s = new String(b, off, len);
		pw.write(s);
	}

}
