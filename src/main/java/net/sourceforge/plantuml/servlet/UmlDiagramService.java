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
package net.sourceforge.plantuml.servlet;

import java.io.IOException;
import javax.imageio.IIOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.servlet.utility.UmlExtractor;

/**
 * Common service servlet to produce diagram from compressed UML source contained in the end part of the requested URI.
 */
@SuppressWarnings("serial")
public abstract class UmlDiagramService extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

        // build the UML source from the compressed request parameter
        String uml = UmlExtractor.getUmlSource(getSource(request.getRequestURI()));

        // generate the response
        DiagramResponse dr = new DiagramResponse(response, getOutputFormat());
        try {
            dr.sendDiagram(uml);
        } catch (IIOException iioe) {
            // Browser has closed the connection, so the HTTP OutputStream is closed
            // Silently catch the exception to avoid annoying log
        }
        dr = null;
    }

    /**
     * Extracts the compressed UML source from the HTTP URI.
     *
     * @param uri
     *            the complete URI as returned by request.getRequestURI()
     * @return the compressed UML source
     */
    abstract public String getSource(String uri);

    /**
     * Gives the wished output format of the diagram. This value is used by the DiagramResponse class.
     *
     * @return the format
     */
    abstract public FileFormat getOutputFormat();

}
