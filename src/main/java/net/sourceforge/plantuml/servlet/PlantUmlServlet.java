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
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sourceforge.plantuml.OptionFlags;
import net.sourceforge.plantuml.api.PlantumlUtils;
import net.sourceforge.plantuml.code.Transcoder;
import net.sourceforge.plantuml.code.TranscoderUtil;
import net.sourceforge.plantuml.png.MetadataTag;

/*
 * Original idea from Achim Abeling for Confluence macro
 * See http://www.banapple.de/display/BANAPPLE/plantuml+user+macro
 *
 * This class is the old all-in-one historic implementation of the PlantUml server.
 * See package.html for the new design. It's a work in progress.
 *
 * Modified by Arnaud Roques
 * Modified by Pablo Lalloni
 * Modified by Maxime Sinclair
 *
 */
@SuppressWarnings("serial")
public class PlantUmlServlet extends HttpServlet {

    private static final String DEFAULT_ENCODED_TEXT = "SyfFKj2rKt3CoKnELR1Io4ZDoSa70000";

    // Last part of the URL
    public static final Pattern URL_PATTERN = Pattern.compile("^.*[^a-zA-Z0-9\\-\\_]([a-zA-Z0-9\\-\\_]+)");

    private static final Pattern RECOVER_UML_PATTERN = Pattern.compile("/uml/(.*)");
    static {
        OptionFlags.ALLOW_INCLUDE = false;
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        request.setCharacterEncoding("UTF-8");
        String text = request.getParameter("text");

        String metadata = request.getParameter("metadata");
        if (metadata != null) {
            InputStream img = null;
            try {
                img = getImage(new URL(metadata));
                MetadataTag metadataTag = new MetadataTag(img, "plantuml");
                String data = metadataTag.getData();
                if (data != null) {
                    text = data;
                }
            } finally {
                if (img != null) {
                    img.close();
                }
            }
        }
        try {
            text = getTextFromUrl(request, text);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // no Text form has been submitted
        if (text == null || text.trim().isEmpty()) {
            redirectNow(request, response, DEFAULT_ENCODED_TEXT);
            return;
        }

        final String encoded = getTranscoder().encode(text);
        request.setAttribute("decoded", text);
        request.setAttribute("encoded", encoded);

        // check if an image map is necessary
        if (text != null && PlantumlUtils.hasCMapData(text)) {
            request.setAttribute("mapneeded", Boolean.TRUE);
        }
        // forward to index.jsp
        final RequestDispatcher dispatcher = request.getRequestDispatcher("/index.jsp");
        dispatcher.forward(request, response);

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {
        request.setCharacterEncoding("UTF-8");

        String text = request.getParameter("text");
        String encoded = DEFAULT_ENCODED_TEXT;

        try {
            text = getTextFromUrl(request, text);
            encoded = getTranscoder().encode(text);
        } catch (Exception e) {
            e.printStackTrace();
        }

        redirectNow(request, response, encoded);
    }

    private String getTextFromUrl(HttpServletRequest request, String text) throws IOException {
        String url = request.getParameter("url");
        final Matcher recoverUml = RECOVER_UML_PATTERN.matcher(request.getRequestURI().substring(
                request.getContextPath().length()));
        // the URL form has been submitted
        if (recoverUml.matches()) {
            final String data = recoverUml.group(1);
            text = getTranscoder().decode(data);
        } else if (url != null && !url.trim().isEmpty()) {
            // Catch the last part of the URL if necessary
            final Matcher m1 = URL_PATTERN.matcher(url);
            if (m1.find()) {
                url = m1.group(1);
            }
            text = getTranscoder().decode(url);
        }
        return text;
    }

    private void redirectNow(HttpServletRequest request, HttpServletResponse response, String encoded)
            throws IOException {
        final String result = request.getContextPath() + "/uml/" + encoded;
        response.sendRedirect(result);
    }

    private Transcoder getTranscoder() {
        return TranscoderUtil.getDefaultTranscoder();
    }

    static private HttpURLConnection getConnection(URL url) throws IOException {
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

    static public InputStream getImage(URL url) throws IOException {
        InputStream is = null;
        HttpURLConnection con = getConnection(url);
        is = con.getInputStream();
        return is;
    }



}
