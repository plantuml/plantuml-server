/* ========================================================================
 * PlantUML : a free UML diagram generator
 * ========================================================================
 *
 * Project Info:  https://plantuml.com
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

import java.io.BufferedReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.IIOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.OptionFlags;
import net.sourceforge.plantuml.servlet.utility.UmlExtractor;

/**
 * Common service servlet to produce diagram from compressed UML source contained in the end part of the requested URI.
 */
@SuppressWarnings("SERIAL")
public abstract class UmlDiagramService extends HttpServlet {

    /**
     * Regex pattern to fetch encoded uml text from an URL.
     */
    private static final Pattern RECOVER_UML_PATTERN = Pattern.compile("/\\w+/(\\d+/)?(.*)");

    static {
        OptionFlags.ALLOW_INCLUDE = false;
        if ("true".equalsIgnoreCase(System.getenv("ALLOW_PLANTUML_INCLUDE"))) {
            OptionFlags.ALLOW_INCLUDE = true;
        }
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        // build the UML source from the compressed request parameter
        final String[] sourceAndIdx = getSourceAndIdx(request);
        final int idx = Integer.parseInt(sourceAndIdx[1]);
        final String uml;
        try {
            uml = UmlExtractor.getUmlSource(sourceAndIdx[0]);
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Bad Request");
            return;
        }

        doDiagramResponse(request, response, uml, idx);
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // build the UML source from the compressed request parameter
        final String[] sourceAndIdx = getSourceAndIdx(request);
        final int idx = Integer.parseInt(sourceAndIdx[1]);

        final StringBuilder uml = new StringBuilder();
        final BufferedReader in = request.getReader();
        while (true) {
            final String line = in.readLine();
            if (line == null) {
                break;
            }
            uml.append(line).append('\n');
        }

        doDiagramResponse(request, response, uml.toString(), idx);
    }

    /**
     * Send diagram response.
     *
     * @param request html request
     * @param response html response
     * @param uml textual UML diagram(s) source
     * @param idx diagram index of {@code uml} to send
     *
     * @throws IOException if an input or output exception occurred
     */
    private void doDiagramResponse(
        HttpServletRequest request,
        HttpServletResponse response,
        String uml,
        int idx
    ) throws IOException {
        // generate the response
        DiagramResponse dr = new DiagramResponse(response, getOutputFormat(), request);
        try {
            dr.sendDiagram(uml, idx);
        } catch (IIOException e) {
            // Browser has closed the connection, so the HTTP OutputStream is closed
            // Silently catch the exception to avoid annoying log
        }
    }

    /**
     * Extracts the UML source text and its index from the HTTP request.
     *
     * @param request http request
     *
     * @return the UML source text and its index
     */
    public final String[] getSourceAndIdx(HttpServletRequest request) {
        final Matcher recoverUml = RECOVER_UML_PATTERN.matcher(
            request.getRequestURI().substring(
            request.getContextPath().length()));
        // the URL form has been submitted
        if (recoverUml.matches()) {
            final String data = recoverUml.group(2);
            if (data.length() >= 4) {
                String idx = recoverUml.group(1);
                if (idx == null) {
                    idx = "0";
                } else {
                    idx = idx.substring(0, idx.length() - 1);
                }
                return new String[]{data, idx };
            }
        }
        return new String[]{"", "0"};
    }

    /**
     * Gives the wished output format of the diagram. This value is used by the DiagramResponse class.
     *
     * @return the format
     */
    abstract public FileFormat getOutputFormat();

}
