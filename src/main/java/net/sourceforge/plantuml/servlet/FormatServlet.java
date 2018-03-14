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

import net.sourceforge.plantuml.FileFormat;

import javax.imageio.IIOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/*
 * Image servlet of the webapp.
 * This servlet produces the UML diagram in PNG format.
 */
@SuppressWarnings("serial")
public class FormatServlet extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        final String format = request.getParameter("o");
        final String text = request.getParameter("t");

        String uml = getUmlSource(text);
        FileFormat output = getOutputFormat(format);
        System.out.println("Format(" + output.toString() + ") request:\n" + uml);

        // generate the response
        DiagramResponse dr = new DiagramResponse(response, output);
        try {
            dr.sendDiagram(uml);
        } catch (IIOException iioe) {
            // Browser has closed the connection, so the HTTP OutputStream is closed
            // Silently catch the exception to avoid annoying log
            System.out.println("Error: " + iioe.toString());
        }
        dr = null;
    }

    static public String getUmlSource(String text) {
        // encapsulate the UML syntax if necessary
        if (text == null) {
            text = "Bob -> Alice : hello";
        }
        String uml;
        if (text.startsWith("@start")) {
            uml = text;
        } else {
            StringBuilder plantUmlSource = new StringBuilder();
            plantUmlSource.append("@startuml\n");
            plantUmlSource.append(text);
            if (text.endsWith("\n") == false) {
                plantUmlSource.append("\n");
            }
            plantUmlSource.append("@enduml");
            uml = plantUmlSource.toString();
        }
        return uml;
    }

    private FileFormat getOutputFormat(String format) {
        FileFormat output = FileFormat.PNG;
        if (format == null) {
            output = FileFormat.PNG;
        } else if (format.equals("svg")) {
            output = FileFormat.SVG;
        } else if (format.equals("txt")) {
            output = FileFormat.UTXT;
        }
        return output;
    }
}
