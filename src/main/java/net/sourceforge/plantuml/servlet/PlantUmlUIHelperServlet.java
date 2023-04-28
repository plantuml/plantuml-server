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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import net.sourceforge.plantuml.theme.ThemeUtils;
import net.sourceforge.plantuml.openiconic.data.DummyIcon;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Small PlantUML frontend or UI helper.
 */
@SuppressWarnings("SERIAL")
public class PlantUmlUIHelperServlet extends HttpServlet {

    private interface HelperConsumer {
        void accept(HttpServletRequest request, HttpServletResponse response) throws IOException;
    }

    private final Map<String, HelperConsumer> helpers = new HashMap<>();
    private String svgIconsSpriteCache = null;

    public PlantUmlUIHelperServlet() {
        // add all supported request items/helper methods
        helpers.put("icons.svg", this::sendIconsSprite);
        helpers.put("icons", this::sendIcons);
        helpers.put("themes", this::sendThemes);
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        final String requestItem = request.getParameter("request");
        final HelperConsumer requestHelper = this.helpers.get(requestItem);
        String errorMsg = null;
        if (requestItem == null) {
            errorMsg = "Request item not set.";
        } else if (requestHelper == null) {
            errorMsg = "Unknown requested item: " + requestItem;
        }
        if (errorMsg != null) {
            response.addHeader("Access-Control-Allow-Origin", "*");
            response.setContentType("text/plain;charset=UTF-8");
            response.getWriter().write(errorMsg);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        requestHelper.accept(request, response);
    }

    private String toJson(List<String> list) {
        return "[" + list.stream()
            .map(item -> "\"" + item.replace("\"", "\\\"") + "\"")
            .collect(Collectors.joining(",")) + "]";
    }

    private void sendJson(HttpServletResponse response, String json) throws IOException {
        response.addHeader("Access-Control-Allow-Origin", "*");
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(json);
    }

    private List<String> getIcons() throws IOException {
        InputStream in = DummyIcon.class.getResourceAsStream("all.txt");
        try (BufferedReader br = new BufferedReader(new InputStreamReader(in))) {
            return br.lines().collect(Collectors.toList());
        }
    }

    private void sendIconsSprite(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (svgIconsSpriteCache == null) {
            // NOTE: all icons has the following svg tag attributes: width="8" height="8" viewBox="0 0 8 8"
            List<String> iconNames = getIcons();
            StringBuilder sprite = new StringBuilder();
            sprite.append("<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"8\" height=\"8\" viewBox=\"0 0 8 8\">\n");
            sprite.append("<defs>\n");
            sprite.append("  <style><![CDATA[\n");
            sprite.append("    .sprite { display: none; }\n");
            sprite.append("    .sprite:target { display: inline; }\n");
            sprite.append("  ]]></style>\n");
            sprite.append("</defs>\n");
            for (String name : iconNames) {
                try (InputStream in = DummyIcon.class.getResourceAsStream(name + ".svg")) {
                    DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                    Document doc = db.parse(in);
                    Writer out = new StringWriter();
                    out.write("<g class=\"sprite\" id=\"" + name + "\">");

                    Transformer tf = TransformerFactory.newInstance().newTransformer();
                    tf.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
                    tf.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
                    tf.setOutputProperty(OutputKeys.INDENT, "no");
                    NodeList svgInnerNodes = doc.getElementsByTagName("svg").item(0).getChildNodes();
                    for (int index = 0; index < svgInnerNodes.getLength(); index++) {
                        tf.transform(new DOMSource(svgInnerNodes.item(index)), new StreamResult(out));
                    }

                    out.write("</g>");
                    sprite.append(out.toString() + "\n");
                } catch (ParserConfigurationException | SAXException | TransformerException ex) {
                    // skip icons which can not be parsed/read
                    Logger logger = Logger.getLogger("com.plantuml");
                    logger.log(Level.WARNING, "SVG icon '{0}' could not be parsed. Skip!", name);
                }
            }
            sprite.append("</svg>\n");
            svgIconsSpriteCache = sprite.toString();
        }
        response.addHeader("Access-Control-Allow-Origin", "*");
        response.setContentType("image/svg+xml;charset=UTF-8");
        response.getWriter().write(svgIconsSpriteCache);
    }

    private void sendIcons(HttpServletRequest request, HttpServletResponse response) throws IOException {
        sendJson(response, toJson(getIcons()));
    }

    private void sendThemes(HttpServletRequest request, HttpServletResponse response) throws IOException {
        sendJson(response, toJson(ThemeUtils.getAllThemeNames()));
    }
}
