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
import java.io.ByteArrayOutputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;

import net.sourceforge.plantuml.BlockUml;
import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.OptionFlags;
import net.sourceforge.plantuml.NullOutputStream;
import net.sourceforge.plantuml.SourceStringReader;
import net.sourceforge.plantuml.StringUtils;
import net.sourceforge.plantuml.code.Base64Coder;
import net.sourceforge.plantuml.core.DiagramDescription;
import net.sourceforge.plantuml.core.Diagram;
import net.sourceforge.plantuml.core.ImageData;
import net.sourceforge.plantuml.version.Version;
import net.sourceforge.plantuml.error.PSystemError;
import net.sourceforge.plantuml.ErrorUml;


/**
 * Delegates the diagram generation from the UML source and the filling of the HTTP response with the diagram in the
 * right format. Its own responsibility is to produce the right HTTP headers.
 */
class DiagramResponse {

    private static final String POWERED_BY = "PlantUML Version " + Version.versionString();

    private HttpServletResponse response;
    private FileFormat format;
    private HttpServletRequest request;
    private static final Map<FileFormat, String> CONTENT_TYPE;
    static {
        Map<FileFormat, String> map = new HashMap<FileFormat, String>();
        map.put(FileFormat.PNG, "image/png");
        map.put(FileFormat.SVG, "image/svg+xml");
        map.put(FileFormat.EPS, "application/postscript");
        map.put(FileFormat.UTXT, "text/plain;charset=UTF-8");
        map.put(FileFormat.BASE64, "text/plain; charset=x-user-defined");
        CONTENT_TYPE = Collections.unmodifiableMap(map);
    }
    static {
        OptionFlags.ALLOW_INCLUDE = false;
        if ("true".equalsIgnoreCase(System.getenv("ALLOW_PLANTUML_INCLUDE"))) {
            OptionFlags.ALLOW_INCLUDE = true;
        }
    }

    DiagramResponse(HttpServletResponse r, FileFormat f, HttpServletRequest rq) {
        response = r;
        format = f;
        request = rq;
    }

    void sendDiagram(String uml, int idx) throws IOException {
        response.addHeader("Access-Control-Allow-Origin", "*");
        response.setContentType(getContentType());
        SourceStringReader reader = new SourceStringReader(uml);
        if (format == FileFormat.BASE64) {
            final ByteArrayOutputStream baos = new ByteArrayOutputStream();
            final DiagramDescription result = reader.outputImage(baos, idx, new FileFormatOption(FileFormat.PNG));
            baos.close();
            final String encodedBytes = "data:image/png;base64,"
                + Base64Coder.encodeLines(baos.toByteArray()).replaceAll("\\s", "");
            response.getOutputStream().write(encodedBytes.getBytes());
            return;
        }
        final BlockUml blockUml = reader.getBlocks().get(0);
        if (notModified(blockUml)) {
            addHeaderForCache(blockUml);
            response.sendError(HttpServletResponse.SC_NOT_MODIFIED);
            return;
        }
        if (StringUtils.isDiagramCacheable(uml)) {
            addHeaderForCache(blockUml);
        }
        final Diagram diagram = blockUml.getDiagram();
        if (diagram instanceof PSystemError) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
        final ImageData result = diagram.exportDiagram(response.getOutputStream(), idx, new FileFormatOption(format));
    }

    private boolean notModified(BlockUml blockUml) {
        final String ifNoneMatch = request.getHeader("If-None-Match");
        final long ifModifiedSince = request.getDateHeader("If-Modified-Since");
        if (ifModifiedSince != -1 && ifModifiedSince != blockUml.lastModified()) {
            return false;
        }
        final String etag = blockUml.etag();
        if (ifNoneMatch == null) {
            return false;
        }
        return ifNoneMatch.contains(etag);
    }


    void sendMap(String uml) throws IOException {
        response.setContentType(getContentType());
        SourceStringReader reader = new SourceStringReader(uml);
        final BlockUml blockUml = reader.getBlocks().get(0);
        if (StringUtils.isDiagramCacheable(uml)) {
            addHeaderForCache(blockUml);
        }
        final Diagram diagram = blockUml.getDiagram();
        ImageData map = diagram.exportDiagram(new NullOutputStream(), 0,
                new FileFormatOption(FileFormat.PNG, false));
        if (map.containsCMapData()) {
            PrintWriter httpOut = response.getWriter();
            final String cmap = map.getCMapData("plantuml");
            httpOut.print(cmap);
        }
   }

    void sendCheck(String uml) throws IOException {
        response.setContentType(getContentType());
        SourceStringReader reader = new SourceStringReader(uml);
        DiagramDescription desc = reader.outputImage(
            new NullOutputStream(), new FileFormatOption(FileFormat.PNG, false));
        PrintWriter httpOut = response.getWriter();
        httpOut.print(desc.getDescription());
 }
    private void addHeaderForCache(BlockUml blockUml) {
        long today = System.currentTimeMillis();
        // Add http headers to force the browser to cache the image
        final int maxAge = 3600 * 24 * 5;
        response.addDateHeader("Expires", today + 1000L * maxAge);
        response.addDateHeader("Date", today);

        response.addDateHeader("Last-Modified", blockUml.lastModified());
        response.addHeader("Cache-Control", "public, max-age=" + maxAge);
        // response.addHeader("Cache-Control", "max-age=864000");
        response.addHeader("Etag", "\"" + blockUml.etag() + "\"");
        final Diagram diagram = blockUml.getDiagram();
        response.addHeader("X-PlantUML-Diagram-Description", diagram.getDescription().getDescription());
        if (diagram instanceof PSystemError) {
            final PSystemError error = (PSystemError) diagram;
            for (ErrorUml err : error.getErrorsUml()) {
                response.addHeader("X-PlantUML-Diagram-Error", err.getError());
                response.addHeader("X-PlantUML-Diagram-Error-Line", "" + err.getLineLocation().getPosition());
            }
        }
        addHeaders(response);
    }

    public static void addHeaders(HttpServletResponse response) {
        response.addHeader("X-Powered-By", POWERED_BY);
        response.addHeader("X-Patreon", "Support us on http://plantuml.com/patreon");
        response.addHeader("X-Donate", "http://plantuml.com/paypal");
    }


    private String getContentType() {
        return CONTENT_TYPE.get(format);
    }

}
