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

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.sourceforge.plantuml.OptionFlags;
import net.sourceforge.plantuml.api.PlantumlUtils;
import net.sourceforge.plantuml.code.Transcoder;
import net.sourceforge.plantuml.code.TranscoderUtil;
import net.sourceforge.plantuml.png.MetadataTag;
import net.sourceforge.plantuml.servlet.utility.Assertions;
import net.sourceforge.plantuml.servlet.utility.Configuration;
import net.sourceforge.plantuml.servlet.utility.HtmlUtils;
import net.sourceforge.plantuml.servlet.utility.UmlExtractor;
import net.sourceforge.plantuml.servlet.utility.UrlDataExtractor;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
public class PlantUmlServlet extends HttpServlet {

    /**
     * Default encoded uml text.
     * Bob -> Alice : hello
     */
    private static final String DEFAULT_ENCODED_TEXT = "SyfFKj2rKt3CoKnELR1Io4ZDoSa70000";

    /**
     * Regex pattern to fetch last part of the URL.
     */
    private static final Pattern URL_PATTERN = Pattern.compile("^.*[^a-zA-Z0-9\\-\\_]([a-zA-Z0-9\\-\\_]+)");

    static {
        OptionFlags.ALLOW_INCLUDE = false;
        if ("true".equalsIgnoreCase(System.getenv("ALLOW_PLANTUML_INCLUDE"))) {
            OptionFlags.ALLOW_INCLUDE = true;
        }
    }

    private static String stringToHTMLString(String string) {
        return HtmlUtils.htmlEscape(string);
    }


    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        request.setCharacterEncoding("UTF-8");
        PlantUmlRequestAdapter adapter = new PlantUmlRequestAdapter(request);

        // textual diagram source
        Optional<String> textFromRequest = getTextFromRequest(adapter);
        Assertions.assertTrimmedIfPresent(textFromRequest, "text parameter from request");

        // no Text form has been submitted
        if (textFromRequest.isEmpty()) {
            redirectNow(request, response, DEFAULT_ENCODED_TEXT);

        } else {
            // diagram index to render
            final int idx = UrlDataExtractor.getIndex(adapter.getRequest().getRequestURI());

            // forward to index.jsp
            prepareRequestForDispatch(request, textFromRequest.get(), idx);
            final RequestDispatcher dispatcher = request.getRequestDispatcher("/index.jsp");
            dispatcher.forward(request, response);
        }
    }

    @Override
    protected void doPost(
        HttpServletRequest request,
        HttpServletResponse response
    ) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        PlantUmlRequestAdapter adapter = new PlantUmlRequestAdapter(request);
        // diagram index to render
        final int idx = UrlDataExtractor.getIndex(request.getRequestURI());

        // encoded diagram source
        String encoded = DEFAULT_ENCODED_TEXT;
        //TODO reveal intention: do not hide the error.
        try {
            Optional<String> text = getTextFromRequest(adapter);
            Assertions.assertTrimmedIfPresent(text, "text request parameter");
            if (text.isPresent()) {
                encoded = getTranscoder().encode(text.get());
            }
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
    private Optional<String> getTextFromRequest(PlantUmlRequestAdapter request) throws IOException {
        Optional<String> text;
        // 1. URL
        try {
            text = getTextFromUrl(request);
            if (text.isPresent()) {
                return text;
            }
        } catch (Exception e) {
            //TODO can we remove this try/catch
            e.printStackTrace();
        }
        // 2. metadata
        Optional<String> metadata = request.getMetadata();
        if (metadata.isPresent()) {
            try (InputStream img = getImage(new URL(metadata.get()))) {
                MetadataTag metadataTag = new MetadataTag(img, "plantuml");
                Optional<String> data = Optional.ofNullable(metadataTag.getData());
                if (data.isPresent()) {
                    return data;
                }
            }
        }
        // 3. request parameter text
        return request.getCleanedText();
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
        // textual diagram source from request URI
        String url = request.getRequestURI();
        if (url.contains("/uml/") && !url.endsWith("/uml/")) {
            final String encoded = UrlDataExtractor.getEncodedDiagram(request.getRequestURI(), "");
            if (!encoded.isEmpty()) {
                return getTranscoder().decode(encoded);
            }
        }
        // textual diagram source from "url" parameter
        url = request.getParameter("url");
        if (url != null && !url.trim().isEmpty()) {
            // Catch the last part of the URL if necessary
            final Matcher matcher = URL_PATTERN.matcher(url);
            if (matcher.find()) {
                url = matcher.group(1);
            }
            return getTranscoder().decode(url);
        }
        // nothing found
        return "";
    }

    /**
     * Get textual diagram source from URL.
     *
     * @param request http request which contains the source URL
     * @return if successful textual diagram source from URL; otherwise empty string
     * @throws IOException if an input or output exception occurred
     */
    private Optional<String> getTextFromUrl(PlantUmlRequestAdapter adapter) throws IOException {
        // textual diagram source from request URI
        PlantUmlUrlProcessor processor = new PlantUmlUrlProcessor(adapter);
        Optional<String> url = adapter.getRequestURI();
        if (url.isPresent() && processor.isUml()) {
            final String encoded = UrlDataExtractor.getEncodedDiagram(url.get(), "");
            if (!encoded.isEmpty()) {
                //not sure if a null value can be returned : we are defensive programming here
                return Optional.ofNullable(getTranscoder().decode(encoded));
            }
        }
        // textual diagram source from "url" parameter
        url = adapter.getCleanedUrl();
        if (url.isPresent()) {
            // Catch the last part of the URL if necessary
            final Matcher matcher = URL_PATTERN.matcher(url.get());
            if (matcher.find()) {
                url = Optional.ofNullable(matcher.group(1));
            }
            if (url.isPresent()) {
                //not sure if a null value can be returned : we are defensive programming here
                return Optional.ofNullable(getTranscoder().decode(url.get()));
            }
        }
        // nothing found
        return Optional.empty();
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
        final String index = (idx < 0) ? "" : idx + "/";
        // diagram sources
        request.setAttribute("decoded", text);
        request.setAttribute("index", idx);
        // properties
        request.setAttribute("showSocialButtons", Configuration.get("SHOW_SOCIAL_BUTTONS"));
        request.setAttribute("showGithubRibbon", Configuration.get("SHOW_GITHUB_RIBBON"));
        // image URLs
        final boolean hasImg = !text.isEmpty();
        request.setAttribute("hasImg", hasImg);
        request.setAttribute("imgurl", "png/" + index + encoded);
        request.setAttribute("svgurl", "svg/" + index + encoded);
        request.setAttribute("pdfurl", "pdf/" + index + encoded);
        request.setAttribute("txturl", "txt/" + index + encoded);
        request.setAttribute("mapurl", "map/" + index + encoded);
        // map for diagram source if necessary
        final boolean hasMap = PlantumlUtils.hasCMapData(text);
        request.setAttribute("hasMap", hasMap);
        String map = "";
        if (hasMap) {
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
     * Get PlantUML transcoder.
     *
     * @return transcoder instance
     */
    private Transcoder getTranscoder() {
        return TranscoderUtil.getDefaultTranscoder();
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
            // printHttpsCert(con);
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
