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

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import net.sourceforge.plantuml.api.PlantumlUtils;
import net.sourceforge.plantuml.code.NoPlantumlCompressionException;
import net.sourceforge.plantuml.png.MetadataTag;
import net.sourceforge.plantuml.servlet.utility.Configuration;
import net.sourceforge.plantuml.servlet.utility.UmlExtractor;
import net.sourceforge.plantuml.servlet.utility.UrlDataExtractor;

/**
 * Original idea from Achim Abeling for Confluence macro.
 *
 * This class is the old all-in-one historic implementation of the PlantUml server.
 * See package.html for the new design. It's a work in progress.
 *
 * Modified by Arnaud Roques
 * Modified by Pablo Lalloni
 * Modified by Maxime Sinclair
 */
@SuppressWarnings("SERIAL")
public class PlantUmlServlet extends AsciiCoderServlet {

    static {
        // Initialize the PlantUML server.
        // You could say that this is like the `static void main(String[] args)` of the PlantUML server.
        DiagramResponse.init();
    }

    /**
     * Default encoded uml text.
     * Bob -> Alice : hello
     */
    private static final String DEFAULT_ENCODED_TEXT = "SyfFKj2rKt3CoKnELR1Io4ZDoSa70000";

    @Override
    protected String getServletContextPath() {
        return "uml";
    }

    /**
     * Encode arbitrary string to HTML string.
     *
     * @param string arbitrary string
     *
     * @return html encoded string
     */
    public static String stringToHTMLString(String string) {
        final StringBuilder sb = new StringBuilder(string.length());
        // true if last char was blank
        final int length = string.length();
        int offset = 0;
        while (offset < length) {
            final int c = string.codePointAt(offset);
            if (c == ' ') {
                sb.append(' ');
            } else if (c == '"') {
                sb.append("&quot;");
            } else if (c == '&') {
                sb.append("&amp;");
            } else if (c == '<') {
                sb.append("&lt;");
            } else if (c == '>') {
                sb.append("&gt;");
            } else if (c == '\r') {
                sb.append("\r");
            } else if (c == '\n') {
                sb.append("\n");
            } else {
                int ci = 0xffffff & c;
                if (ci < 160) {
                    // nothing special only 7 Bit
                    sb.append((char) c);
                } else {
                    // Not 7 Bit use the unicode system
                    sb.append("&#");
                    sb.append(ci);
                    sb.append(';');
                }
            }
            offset += Character.charCount(c);
        }
        return sb.toString();
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        request.setCharacterEncoding("UTF-8");

        // textual diagram source
        final String text = getText(request).trim();

        // no Text form has been submitted
        if (text.isEmpty()) {
            redirectNow(request, response, DEFAULT_ENCODED_TEXT);
            return;
        }

        // diagram index to render
        final int idx = UrlDataExtractor.getIndex(request.getRequestURI());

        // forward to index.jsp
        final String path;
        final String view = request.getParameter("view");
        if (view != null && view.equalsIgnoreCase("previewer")) {
            path = "/previewer.jsp";
        } else {
            path = "/index.jsp";
        }
        prepareRequestForDispatch(request, text, idx);
        final RequestDispatcher dispatcher = request.getRequestDispatcher(path);
        dispatcher.forward(request, response);
    }

    @Override
    protected void doPost(
        HttpServletRequest request,
        HttpServletResponse response
    ) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        // diagram index to render
        final int idx = UrlDataExtractor.getIndex(request.getRequestURI());

        // encoded diagram source
        String encoded;
        try {
            String text = getText(request).trim();
            encoded = getTranscoder().encode(text);
        } catch (Exception e) {
            encoded = DEFAULT_ENCODED_TEXT;
            e.printStackTrace();
        }

        redirectNow(request, response, encoded, idx);
    }

    /**
     * Get textual diagram.
     * Search for textual diagram in following order:
     * 1. URL {@link PlantUmlServlet.getTextFromUrl}
     * 2. metadata
     * 3. request parameter "text"
     *
     * @param request http request
     *
     * @return if successful textual diagram source; otherwise empty string
     *
     * @throws IOException if an input or output exception occurred
     */
    private String getText(final HttpServletRequest request) throws IOException {
        String text;
        // 1. URL
        try {
            text = getTextFromUrl(request);
            if (text != null && !text.isEmpty()) {
                return text;
            }
        } catch (NoPlantumlCompressionException e) {
            // no textual diagram source available from Url
            // ignore and try 2. method (metadata) below
            // do not spam output console
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 2. metadata
        String metadata = request.getParameter("metadata");
        if (metadata != null) {
            try (InputStream img = getImage(new URL(metadata))) {
                MetadataTag metadataTag = new MetadataTag(img, "plantuml");
                String data = metadataTag.getData();
                if (data != null) {
                    return data;
                }
            }
        }
        // 3. request parameter text
        text = request.getParameter("text");
        if (text != null && !text.isEmpty()) {
            return text;
        }
        // nothing found
        return "";
    }

    /**
     * Get textual diagram source from URL.
     *
     * @param request http request which contains the source URL
     *
     * @return if successful textual diagram source from URL; otherwise empty string
     *
     * @throws IOException if an input or output exception occurred
     */
    private String getTextFromUrl(HttpServletRequest request) throws IOException {
        return getTranscoder().decode(getEncodedTextFromUrl(request));
    }

    /**
     * Prepare request for dispatch and get request dispatcher.
     *
     * @param request http request which will be further prepared for dispatch
     * @param text textual diagram source
     *
     * @throws IOException if an input or output exception occurred
     */
    private void prepareRequestForDispatch(HttpServletRequest request, String text, int idx) throws IOException {
        final String encoded = getTranscoder().encode(text);
        // diagram sources
        request.setAttribute("encoded", encoded);
        request.setAttribute("decoded", text);
        request.setAttribute("index", (idx < 0) ? "" : idx);
        // properties
        request.setAttribute("showSocialButtons", Configuration.get("SHOW_SOCIAL_BUTTONS"));
        request.setAttribute("showGithubRibbon", Configuration.get("SHOW_GITHUB_RIBBON"));
        // map for diagram source if necessary
        String map = "";
        if (PlantumlUtils.hasCMapData(text)) {
            try {
                map = UmlExtractor.extractMap(text);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        request.setAttribute("map", map);
    }

    /**
     * Send redirect response to encoded uml text.
     *
     * @param request http request
     * @param response http response
     * @param encoded encoded uml text
     *
     * @throws IOException if an input or output exception occurred
     */
    private void redirectNow(
        HttpServletRequest request,
        HttpServletResponse response,
        String encoded
    ) throws IOException {
        redirectNow(request, response, encoded, null);
    }

    /**
     * Send redirect response to encoded uml text.
     *
     * @param request http request
     * @param response http response
     * @param encoded encoded uml text
     * @param index diagram index
     *
     * @throws IOException if an input or output exception occurred
     */
    private void redirectNow(
        HttpServletRequest request,
        HttpServletResponse response,
        String encoded,
        Integer index
    ) throws IOException {
        final String path;
        if (index == null || index < 0) {
            path = request.getContextPath() + "/uml/" + encoded;
        } else {
            path = request.getContextPath() + "/uml/" + index + "/" + encoded;
        }
        response.sendRedirect(path);
    }

    /**
     * Get open http connection from URL.
     *
     * @param url URL to open connection
     *
     * @return open http connection
     *
     * @throws IOException if an input or output exception occurred
     */
    private static HttpURLConnection getConnection(URL url) throws IOException {
        if (url.getProtocol().startsWith("https")) {
            HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setReadTimeout(10000); // 10 seconds
            con.connect();
            return con;
        } else {
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setReadTimeout(10000); // 10 seconds
            con.connect();
            return con;
        }
    }

    /**
     * Get image input stream from URL.
     *
     * @param url URL to open connection
     *
     * @return response input stream from URL
     *
     * @throws IOException if an input or output exception occurred
     */
    private static InputStream getImage(URL url) throws IOException {
        InputStream is = null;
        HttpURLConnection con = getConnection(url);
        is = con.getInputStream();
        return is;
    }

}
