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
import java.net.URL;
import java.net.URLDecoder;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.RequestDispatcher;
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
@SuppressWarnings("serial")
public class PlantUmlServlet extends HttpServlet {

	private static final Pattern startumlPattern = Pattern.compile("/\\w+/start/(.*)");
	private static final Pattern proxyPattern = Pattern.compile("/\\w+/proxy/((\\d+)/)?(http://.*)");
	private static final Pattern oldStartumlPattern = Pattern.compile("/\\w+/uml/startuml/(.*)");
	private static final Pattern oldImagePattern = Pattern.compile("/\\w+/uml/image/(.*)");
	private static final Pattern oldProxyPattern = Pattern.compile("/\\w+/uml/proxy/((\\d+)/)?(http://.*)");

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {

		final String uri = request.getRequestURI();
		Matcher startumlMatcher = startumlPattern.matcher(uri);
		Matcher proxyMatcher = proxyPattern.matcher(uri);
		Matcher oldStartumlMatcher = oldStartumlPattern.matcher(uri);
		Matcher oldImageMatcher = oldImagePattern.matcher(uri);
		Matcher oldProxyMatcher = oldProxyPattern.matcher(uri);
		if (startumlMatcher.matches()) {
			String source = startumlMatcher.group(1);
			handleImage(response, source, uri);
		} else if (proxyMatcher.matches()) {
			String num = proxyMatcher.group(2);
			String source = proxyMatcher.group(3);
			handleImageProxy(response, num, source, uri);
		} else if (oldStartumlMatcher.matches()) {
			String source = oldStartumlMatcher.group(1);
			handleImage(response, source, uri);
		} else if (oldImageMatcher.matches()) {
			String source = oldImageMatcher.group(1);
			handleImageDecompress(response, source, uri);
		} else if (oldProxyMatcher.matches()) {
			String num = oldProxyMatcher.group(2);
			String source = oldProxyMatcher.group(3);
			handleImageProxy(response, num, source, uri);
		} else {
			doPost(request, response);
		}
	}


	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		request.setCharacterEncoding("UTF-8");
		String text = request.getParameter("text");
		String url = request.getParameter("url");
		String encoded = "";

		Transcoder transcoder = getTranscoder();
		// the URL form has been submitted
		if ((url != null) && (!url.trim().isEmpty())) {
			// TODO Verify the url is correct
			Pattern p = Pattern.compile(".*/(.*)");
			Matcher m = p.matcher(url);
			if (m.find()) {
				url = m.group(1);
				text = transcoder.decode(url);
			}
		}
		// the Text form has been submitted
		if ((text != null) && (!text.trim().isEmpty())) {
			encoded = transcoder.encode(text);
		}

		request.setAttribute("net.sourceforge.plantuml.servlet.decoded", text);
		request.setAttribute("net.sourceforge.plantuml.servlet.encoded", encoded);
		
		// forward to index.jsp
		RequestDispatcher dispatcher = request.getRequestDispatcher("/index.jsp");
		dispatcher.forward(request, response);
	}

	private Transcoder getTranscoder() {
		return TranscoderUtil.getDefaultTranscoder();
	}

	private void handleImage(HttpServletResponse response, String source, String uri)
			throws IOException {
		source = URLDecoder.decode(source, "UTF-8");
		StringBuilder plantUmlSource = new StringBuilder();

		StringTokenizer tokenizer = new StringTokenizer(source, "/@");
		while (tokenizer.hasMoreTokens()) {
			String token = tokenizer.nextToken();
			plantUmlSource.append(token).append("\n");
		}
		sendImage(response, plantUmlSource.toString(), uri);

	}

	private void handleImageDecompress(HttpServletResponse response,
			String source, String uri) throws IOException {
		source = URLDecoder.decode(source, "UTF-8");
		Transcoder transcoder = getTranscoder();
		String text2 = transcoder.decode(source);
		sendImage(response, text2, uri);
	}

	private void handleImageProxy(HttpServletResponse response, String num,
			String source, String uri) throws IOException {
		SourceStringReader reader = new SourceStringReader( getContent(source));
		int n = num == null ? 0 : Integer.parseInt(num);
		// Write the first image to "os"
		reader.generateImage(response.getOutputStream(), n);
	}

	private void sendImage(HttpServletResponse response, String text, String uri)
			throws IOException {
		StringBuilder plantUmlSource = new StringBuilder();
		plantUmlSource.append("@startuml\n");
		plantUmlSource.append(text);
        if (text.endsWith("\n") == false) {
            plantUmlSource.append("\n");
        }
        plantUmlSource.append("@enduml");
		final String uml = plantUmlSource.toString();
		// Write the first image to "os"
		long today = System.currentTimeMillis();
		if ( StringUtils.isDiagramCacheable( uml)) {
			// Add http headers to force the browser to cache the image
			response.addDateHeader("Expires", today + 31536000000L);
			// today + 1 year
			response.addDateHeader("Last-Modified", 1261440000000L);
			// 2009 dec 22 constant date in the past
			response.addHeader("Cache-Control", "public");
		}
		response.setContentType("image/png");
		SourceStringReader reader = new SourceStringReader(uml);
		reader.generateImage(response.getOutputStream(), new FileFormatOption(FileFormat.PNG));
		response.flushBuffer();
	}
	
	private String getContent(String adress) throws IOException {
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
