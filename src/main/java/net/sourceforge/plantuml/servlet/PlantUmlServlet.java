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
import java.net.URLDecoder;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SourceStringReader;
import net.sourceforge.plantuml.StringUtils;
import net.sourceforge.plantuml.code.Transcoder;
import net.sourceforge.plantuml.code.TranscoderUtil;
import net.sourceforge.plantuml.servlet.utility.Configuration;
import net.sourceforge.plantuml.api.PlantumlUtils;

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

    private static final Pattern urlPattern = Pattern.compile(".*/(.*)"); // Last part of the URL
    private static final Pattern encodedPattern = Pattern.compile("^[a-zA-Z0-9\\-\\_]+$"); // Format of a compressed
                                                                                           // diagram
    private static final Pattern startumlPattern = Pattern.compile("/\\w+/start/(.*)");

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

        final String uri = request.getRequestURI();
        Matcher startumlMatcher = startumlPattern.matcher(uri);
        if (startumlMatcher.matches()) {
            System.out.println("PlantUML WARNING This syntax is deprecated.");
            String source = startumlMatcher.group(1);
            handleImage(response, source, uri);
        } else {
            doPost(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {

        request.setCharacterEncoding("UTF-8");
        String text = request.getParameter("text");
        String url = request.getParameter("url");
        String encoded = "";

        Transcoder transcoder = getTranscoder();
        // the URL form has been submitted
        if (url != null && !url.trim().isEmpty()) {
            // Catch the last part of the URL if necessary
            Matcher m1 = urlPattern.matcher(url);
            if (m1.find()) {
                url = m1.group(1);
            }
            // Check it's a valid compressed text
            Matcher m2 = encodedPattern.matcher(url);
            if (m2.find()) {
                    url = m2.group(0);
                    text = transcoder.decode(url);
            } else {
                System.out.println("PlantUML ERROR Not a valid compressed string : " + url);
            }
        }
        // the Text form has been submitted
        if (text != null && !text.trim().isEmpty()) {
            encoded = transcoder.encode(text);
        }

        request.setAttribute("decoded", text);
        request.setAttribute("encoded", encoded);

        // check if an image map is necessary
        if (text != null && PlantumlUtils.hasCMapData(text)) {
            request.setAttribute("mapneeded", Boolean.TRUE);
        }

        // forward to index.jsp
        RequestDispatcher dispatcher = request.getRequestDispatcher("/index.jsp");
        dispatcher.forward(request, response);
        return;
    }

    public void init(ServletConfig config) throws ServletException {
        config.getServletContext().setAttribute("cfg", Configuration.get());
    }

    private Transcoder getTranscoder() {
        return TranscoderUtil.getDefaultTranscoder();
    }

    // This method will be removed in a near future, please don't use it.
    private void handleImage(HttpServletResponse response, String source, String uri) throws IOException {
        source = URLDecoder.decode(source, "UTF-8");
        StringBuilder plantUmlSource = new StringBuilder();

        StringTokenizer tokenizer = new StringTokenizer(source, "/@");
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            plantUmlSource.append(token).append("\n");
        }
        sendImage(response, plantUmlSource.toString(), uri);

    }

    // This method will be removed in a near future, please don't use it.
    private void sendImage(HttpServletResponse response, String text, String uri) throws IOException {
        final String uml;
        if (text.startsWith("@startuml")) {
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
        // Write the first image to "os"
        long today = System.currentTimeMillis();
        if (StringUtils.isDiagramCacheable(uml)) {
            // Add http headers to force the browser to cache the image
            response.addDateHeader("Expires", today + 31536000000L);
            // today + 1 year
            response.addDateHeader("Last-Modified", 1261440000000L);
            // 2009 dec 22 constant date in the past
            response.addHeader("Cache-Control", "public");
        }
        response.setContentType("image/png");
        SourceStringReader reader = new SourceStringReader(uml);
        reader.generateImage(response.getOutputStream(), new FileFormatOption(FileFormat.PNG, false));
    }

}
