package net.sourceforge.plantuml.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLDecoder;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sourceforge.plantuml.SourceStringReader;
import net.sourceforge.plantuml.code.Transcoder;
import net.sourceforge.plantuml.code.TranscoderUtil;
import HTTPClient.CookieModule;
import HTTPClient.HTTPConnection;
import HTTPClient.HTTPResponse;
import HTTPClient.ModuleException;
import HTTPClient.ParseException;

/* 
 * Original idea from Achim Abeling for Confluence macro
 * See http://www.banapple.de/display/BANAPPLE/plantuml+user+macro
 * 
 * Modified by Arnaud Roques
 * Packaged by Maxime Sinclair
 * 
 */
public class PlantUmlServlet extends HttpServlet {

    private static final Pattern startumlPattern = Pattern
            .compile("/\\w+/uml/startuml/(.*)");

    private static final Pattern imagePattern = Pattern
            .compile("/\\w+/uml/image/(.*)");

    private static final Pattern proxyPattern = Pattern
            .compile("/\\w+/uml/proxy/((\\d+)/)?(http://.*)");

    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        final String uri = request.getRequestURI();
        Matcher startumlMatcher = startumlPattern.matcher(uri);
        Matcher imageMatcher = imagePattern.matcher(uri);
        Matcher proxyMatcher = proxyPattern.matcher(uri);

        if (startumlMatcher.matches()) {
            String source = startumlMatcher.group(1);
            handleImage(response, source);
        } else if (imageMatcher.matches()) {
            String source = imageMatcher.group(1);
            handleImageDecompress(response, source);
        } else if (proxyMatcher.matches()) {
            String num = proxyMatcher.group(2);
            String source = proxyMatcher.group(3);
            handleImageProxy(response, num, source);
        } else {
            doPost(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse resp)
            throws ServletException, IOException {

        PrintWriter writer = resp.getWriter();
        writer.print("<html>");
        writer.print("<head>");
        writer.print("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />");
        writer.print("<meta http-equiv=\"expires\" content=\"0\">");
        writer.print("<meta http-equiv=\"pragma\" content=\"no-cache\">");
        writer.print("<meta http-equiv=\"cache-control\" content=\"no-cache, must-revalidate\">");
        writer.print("<link rel=\"SHORTCUT ICON\" href=\"/plantuml/favicon.ico\">");
        writer.print("</head>");
        writer.print("<body>");
        writer.print("<h1>PlantUMLServer</h1><p>This application provides a servlet which serves images createdby <a href=\"http://plantuml.sourceforge.net\">PlantUML</a>.</p>");

        String text = request.getParameter("text");
        String url = request.getParameter("url");
        String encode = "";

        Transcoder transcoder = getTranscoder();
        if (url != null) {
            Pattern p = Pattern.compile(".*/(.*)");
            Matcher m = p.matcher(url);
            if (m.find()) {
                url = m.group(1);
            }
            text = transcoder.decode(url);
        }
        writer.print("<form method=post action=\"/plantuml/uml/post\"><textarea name=\"text\" cols=\"120\" rows=\"10\">");
        if (text != null) {
            encode = transcoder.encode(text);
            writer.print(text);
        }
        writer.print("</textarea><br><input type=\"submit\"></form>");
        writer.print("<hr>");
        writer.print("You can enter here a previously generated URL:<p>");

        String host = "http://" + request.getServerName()+ ":" + request.getServerPort();
        String total = host + "/plantuml/uml/image/" + encode;

        writer.print("<form method=\"post\" action=\"/plantuml/uml/post\"><input name=\"url\" type=\"text\" size=\"150\" value=\""
                        + total + "\">");
        writer.print("<br><input type=\"submit\"></form>");

        if (text != null) {
            writer.print("<hr>");
            writer.print("You can use the following URL:<p>");

            String urlPart = "\"" + total + "\"";
            writer.print("<a href=" + urlPart + " >");
            writer.print("<code>");
            writer.print("&lt;img src=" + urlPart + " &gt;");
            writer.print("</code></a><p>");

            writer.print("<a href=" + urlPart + " >");
            writer.print("<img src=\"/plantuml/uml/image/" + encode + "\" >");
            writer.print("</a>");
        }
        writer.print("</body></html>");
        writer.flush();
    }

	private Transcoder getTranscoder() {
		return TranscoderUtil.getDefaultTranscoder();
	}
	
	
    private void handleImage(HttpServletResponse response, String source)
            throws IOException {
        source = URLDecoder.decode(source, "UTF-8");
        StringBuilder plantUmlSource = new StringBuilder();

        StringTokenizer tokenizer = new StringTokenizer(source, "/@");
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            plantUmlSource.append(token).append("\n");
        }
        sendImage(response, plantUmlSource.toString());

    }

    private void handleImageDecompress(HttpServletResponse response,
            String source) throws IOException {
        source = URLDecoder.decode(source, "UTF-8");
        Transcoder transcoder = getTranscoder();
        String text2 = transcoder.decode(source);
        sendImage(response, text2);
    }

    private void handleImageProxy(HttpServletResponse response, String num,
            String source) throws IOException {
        String s = getContent(source);
        SourceStringReader reader = new SourceStringReader(s);
        int n = num == null ? 0 : Integer.parseInt(num);
        // Write the first image to "os"
        reader.generateImage(response.getOutputStream(), n);
    }

    private void sendImage(HttpServletResponse response, String text)
            throws IOException {
        StringBuilder plantUmlSource = new StringBuilder();
        plantUmlSource.append("@startuml\n");
        plantUmlSource.append(text);
        plantUmlSource.append("\n@enduml");

        SourceStringReader reader = new SourceStringReader(plantUmlSource
                .toString());
        // Write the first image to "os"
        response.setContentType("image/png");
        reader.generateImage(response.getOutputStream());
        response.flushBuffer();
    }

    public String getContent(String adress) throws IOException {
        // HTTPConnection.setProxyServer("proxy", 8080);
        CookieModule.setCookiePolicyHandler(null);

        final Pattern p = Pattern.compile("http://[^/]+(/?.*)");
        final Matcher m = p.matcher(adress);
        if (m.find() == false) {
            throw new IOException(adress);
        }
        final URL url = new URL(adress);
        final HTTPConnection httpConnection = new HTTPConnection(url);
        try {
            final HTTPResponse resp = httpConnection.Get(m.group(1));
            return resp.getText();
        } catch (ModuleException e) {
            throw new IOException(e.toString());
        } catch (ParseException e) {
            throw new IOException(e.toString());
        }
    }

}