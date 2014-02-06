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
import java.io.PrintWriter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SourceStringReader;
import net.sourceforge.plantuml.StringUtils;
import net.sourceforge.plantuml.core.DiagramDescription;
import net.sourceforge.plantuml.servlet.utility.NullOutputStream;

/**
 * Delegates the diagram generation from the UML source and the filling of the HTTP response with the diagram in the
 * right format. Its own responsibility is to produce the right HTTP headers.
 */
class DiagramResponse {
    private HttpServletResponse response;
    private FileFormat format;
    private static final Map<FileFormat, String> CONTENT_TYPE;
    static {
        Map<FileFormat, String> map = new HashMap<FileFormat, String>();
        map.put(FileFormat.PNG, "image/png");
        map.put(FileFormat.SVG, "image/svg+xml");
        map.put(FileFormat.UTXT, "text/plain;charset=UTF-8");
        CONTENT_TYPE = Collections.unmodifiableMap(map);
    }

    DiagramResponse(HttpServletResponse r, FileFormat f) {
        response = r;
        format = f;
    }

    void sendDiagram(String uml) throws IOException {
        if (StringUtils.isDiagramCacheable(uml)) {
            addHeaderForCache();
        }
        response.setContentType(getContentType());
        SourceStringReader reader = new SourceStringReader(uml);
        reader.generateImage(response.getOutputStream(), new FileFormatOption(format, false));
    }

    void sendMap(String uml) throws IOException {
        if (StringUtils.isDiagramCacheable(uml)) {
            addHeaderForCache();
        }
        response.setContentType(getContentType());
        SourceStringReader reader = new SourceStringReader(uml);
        String map = reader.generateImage(new NullOutputStream(), new FileFormatOption(FileFormat.PNG, false));
        String[] mapLines = map.split("[\\r\\n]");
        PrintWriter httpOut = response.getWriter();
        for (int i = 2; (i + 1) < mapLines.length; i++) {
            httpOut.print(mapLines[i]);
        }
   }

    void sendCheck(String uml) throws IOException {
        response.setContentType(getContentType());
        SourceStringReader reader = new SourceStringReader(uml);
        DiagramDescription desc = reader.generateDiagramDescription(
            new NullOutputStream(), new FileFormatOption(FileFormat.PNG, false));
        PrintWriter httpOut = response.getWriter();
        httpOut.print(desc.getDescription());
 }
    private void addHeaderForCache() {
        long today = System.currentTimeMillis();
        // Add http headers to force the browser to cache the image
        response.addDateHeader("Expires", today + 31536000000L);
        // today + 1 year
        response.addDateHeader("Last-Modified", 1261440000000L);
        // 2009 dec 22 constant date in the past
        response.addHeader("Cache-Control", "public");
    }

    private String getContentType() {
        return CONTENT_TYPE.get(format);
    }

}
