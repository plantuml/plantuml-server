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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import net.sourceforge.plantuml.BlockUml;
import net.sourceforge.plantuml.ErrorUml;
import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.NullOutputStream;
import net.sourceforge.plantuml.OptionFlags;
import net.sourceforge.plantuml.SourceStringReader;
import net.sourceforge.plantuml.StringUtils;
import net.sourceforge.plantuml.code.Base64Coder;
import net.sourceforge.plantuml.core.Diagram;
import net.sourceforge.plantuml.core.DiagramDescription;
import net.sourceforge.plantuml.core.ImageData;
import net.sourceforge.plantuml.error.PSystemError;
import net.sourceforge.plantuml.version.Version;

/**
 * Delegates the diagram generation from the UML source and the filling of the HTTP response with the diagram in the
 * right format. Its own responsibility is to produce the right HTTP headers.
 */
public class DiagramResponse {

    /**
     * {@link FileFormat} to http content type mapping.
     */
    private static final Map<FileFormat, String> CONTENT_TYPE;
    /**
     * X-Powered-By http header value included in every response by default.
     */
    private static final String POWERED_BY = "PlantUML Version " + Version.versionString();

    static {
        OptionFlags.ALLOW_INCLUDE = false;
        if ("true".equalsIgnoreCase(System.getenv("ALLOW_PLANTUML_INCLUDE"))) {
            OptionFlags.ALLOW_INCLUDE = true;
        }
        CONTENT_TYPE = Collections.unmodifiableMap(new HashMap<FileFormat, String>() {{
            put(FileFormat.PNG, "image/png");
            put(FileFormat.SVG, "image/svg+xml");
            put(FileFormat.EPS, "application/postscript");
            put(FileFormat.UTXT, "text/plain;charset=UTF-8");
            put(FileFormat.BASE64, "text/plain; charset=x-user-defined");
        }});
    }

    /**
     * Response format.
     */
    private FileFormat format;
    /**
     * Http request.
     */
    private HttpServletRequest request;
    /**
     * Http response.
     */
    private HttpServletResponse response;

    /**
     * Create new diagram response instance.
     *
     * @param res http response
     * @param fmt target file format
     * @param req http request
     */
    public DiagramResponse(HttpServletResponse res, FileFormat fmt, HttpServletRequest req) {
        response = res;
        format = fmt;
        request = req;
    }

    /**
     * Render and send a specific uml diagram.
     *
     * @param uml textual UML diagram(s) source
     * @param idx diagram index of {@code uml} to send
     *
     * @throws IOException if an input or output exception occurred
     */
    public void sendDiagram(String uml, int idx) throws IOException {
        response.addHeader("Access-Control-Allow-Origin", "*");
        response.setContentType(getContentType());
        SourceStringReader reader = new SourceStringReader(uml);
        if (format == FileFormat.BASE64) {
            byte[] imageBytes;
            try (ByteArrayOutputStream outstream = new ByteArrayOutputStream()) {
                reader.outputImage(outstream, idx, new FileFormatOption(FileFormat.PNG));
                imageBytes = outstream.toByteArray();
            }
            final String base64 = Base64Coder.encodeLines(imageBytes).replaceAll("\\s", "");
            final String encodedBytes = "data:image/png;base64," + base64;
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
        diagram.exportDiagram(response.getOutputStream(), idx, new FileFormatOption(format));
    }

    /**
     * Is block uml unmodified?
     *
     * @param blockUml block uml
     *
     * @return true if unmodified; otherwise false
     */
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

    /**
     * Produce and send the image map of the uml diagram in HTML format.
     *
     * @param uml textual UML diagram source
     * @param idx diagram index of {@code uml} to send
     *
     * @throws IOException if an input or output exception occurred
     */
    public void sendMap(String uml, int idx) throws IOException {
        if (idx < 0) {
            idx = 0;
        }
        response.setContentType(getContentType());
        SourceStringReader reader = new SourceStringReader(uml);
        final BlockUml blockUml = reader.getBlocks().get(0);
        if (StringUtils.isDiagramCacheable(uml)) {
            addHeaderForCache(blockUml);
        }
        final Diagram diagram = blockUml.getDiagram();
        ImageData map = diagram.exportDiagram(new NullOutputStream(), idx,
                new FileFormatOption(FileFormat.PNG, false));
        if (map.containsCMapData()) {
            PrintWriter httpOut = response.getWriter();
            final String cmap = map.getCMapData("plantuml");
            httpOut.print(cmap);
        }
    }

    /**
     * Check the syntax of the diagram and send a report in TEXT format.
     *
     * @param uml textual UML diagram source
     *
     * @throws IOException if an input or output exception occurred
     */
    public void sendCheck(String uml) throws IOException {
        response.setContentType(getContentType());
        SourceStringReader reader = new SourceStringReader(uml);
        DiagramDescription desc = reader.outputImage(
            new NullOutputStream(),
            new FileFormatOption(FileFormat.PNG, false)
        );
        PrintWriter httpOut = response.getWriter();
        httpOut.print(desc.getDescription());
    }

    /**
     * Add default header including cache headers to response.
     *
     * @param blockUml response block uml
     */
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

    /**
     * Add default headers to response.
     *
     * @param response http response
     */
    private static void addHeaders(HttpServletResponse response) {
        response.addHeader("X-Powered-By", POWERED_BY);
        response.addHeader("X-Patreon", "Support us on https://plantuml.com/patreon");
        response.addHeader("X-Donate", "https://plantuml.com/paypal");
    }

    /**
     * Get response content type.
     *
     * @return response content type
     */
    private String getContentType() {
        return CONTENT_TYPE.get(format);
    }

}
