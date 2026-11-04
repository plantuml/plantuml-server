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
import java.io.InputStreamReader;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.stream.Collectors;

import javax.imageio.IIOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import net.sourceforge.plantuml.FileFormat;

/**
 * Proxy servlet of the webapp.
 * This servlet retrieves the diagram source of a web resource (web html page)
 * and renders it.
 */
@SuppressWarnings("SERIAL")
public class ProxyServlet extends HttpServlet {

    public static boolean forbiddenURL(String full) {
        if (full == null) {
            return true;
        }
        if (full.contains("@")) {
            return true;
        }
        if (full.startsWith("https://") == false && full.startsWith("http://") == false) {
            return true;
        }
        if (full.matches("^https?://[-#.0-9:\\[\\]+]+([/?].*)?$")) {
            return true;
        }
        if (full.matches("^https?://[^.]+/.*")) {
            return true;
        }
        if (full.matches("^https?://[^.]+$")) {
            return true;
        }
        return false;
    }

    /**
     * Validate external URL.
     *
     * @param url URL to validate
     * @param response  response object to `sendError` including error message; if `null` no error will be send
     *
     * @return valid URL; otherwise `null`
     *
     * @throws IOException  `response.sendError` can result in a `IOException`
     */
    public static URL validateURL(String url, HttpServletResponse response) throws IOException {
        final URL parsedUrl;
        try {
            parsedUrl = new URL(url);
        } catch (MalformedURLException mue) {
            if (response != null) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "URL malformed.");
            }
            return null;
        }
        // The SANDBOX security profile does not allow any remote URL access
        if (isRemoteAccessDisabled()) {
            if (response != null) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Remote URL access is disabled.");
            }
            return null;
        }
        // Check if URL is in a forbidden format (e.g. IP-Address)
        if (forbiddenURL(url)) {
            if (response != null) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Forbidden URL format.");
            }
            return null;
        }
        // Check that the host does not resolve to an internal address (SSRF protection)
        if (resolvesToForbiddenAddress(parsedUrl.getHost())) {
            if (response != null) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Forbidden URL host.");
            }
            return null;
        }
        return parsedUrl;
    }

    /**
     * Whether the security profile ({@code PLANTUML_SECURITY_PROFILE}, environment variable
     * or system property) forbids any remote URL access: this is the case of {@code SANDBOX}.
     */
    static boolean isRemoteAccessDisabled() {
        return "SANDBOX".equalsIgnoreCase(String.valueOf(System.getenv("PLANTUML_SECURITY_PROFILE")).trim())
            || "SANDBOX".equalsIgnoreCase(String.valueOf(System.getProperty("PLANTUML_SECURITY_PROFILE")).trim());
    }

    /**
     * Whether internal addresses are allowed (environment variable or system property
     * {@code PLANTUML_ALLOW_PRIVATE_URLS}); intended for tests and trusted deployments only.
     */
    static boolean allowPrivateUrls() {
        return "true".equalsIgnoreCase(System.getenv("PLANTUML_ALLOW_PRIVATE_URLS"))
            || "true".equalsIgnoreCase(System.getProperty("PLANTUML_ALLOW_PRIVATE_URLS"));
    }

    /**
     * Resolve the host name and check every resulting address.
     *
     * @param host host name of the URL
     *
     * @return {@code true} if the host cannot be resolved or if any of its addresses is not public
     */
    static boolean resolvesToForbiddenAddress(final String host) {
        if (allowPrivateUrls()) {
            return false;
        }
        if (host == null || host.isEmpty()) {
            return true;
        }
        try {
            for (final InetAddress address : InetAddress.getAllByName(host)) {
                if (isForbiddenAddress(address)) {
                    return true;
                }
            }
        } catch (UnknownHostException e) {
            return true;
        }
        return false;
    }

    /**
     * Whether the address is loopback, wildcard, link-local (e.g. cloud metadata), private,
     * multicast, carrier-grade NAT, unique-local IPv6 or an IPv4-mapped/compatible form of those.
     */
    static boolean isForbiddenAddress(final InetAddress address) {
        if (address.isAnyLocalAddress() || address.isLoopbackAddress() || address.isLinkLocalAddress()
            || address.isSiteLocalAddress() || address.isMulticastAddress()) {
            return true;
        }
        final byte[] b = address.getAddress();
        if (address instanceof Inet6Address) {
            // unique local addresses fc00::/7
            return (b[0] & 0xFE) == 0xFC;
        }
        final int b0 = b[0] & 0xFF;
        final int b1 = b[1] & 0xFF;
        return b0 == 0                             // 0.0.0.0/8
            || (b0 == 100 && b1 >= 64 && b1 <= 127) // 100.64.0.0/10 carrier-grade NAT
            || (b0 == 192 && b1 == 0 && (b[2] & 0xFF) == 0) // 192.0.0.0/24
            || (b0 == 198 && (b1 == 18 || b1 == 19)) // 198.18.0.0/15 benchmarking
            || b0 >= 240;                          // reserved + broadcast
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        final String fmt = request.getParameter("fmt");
        final String source = request.getParameter("src");
        final String index = request.getParameter("idx");

        final int idx = index == null ? 0 : Integer.parseInt(index);
        final URL srcUrl = validateURL(source, response);
        if (srcUrl == null) {
            return;  // error is already set/handled inside `validateURL`
        }

        // fetch diagram from URL
        final String uml = getSource(srcUrl);

        // generate the response
        DiagramResponse dr = new DiagramResponse(response, getOutputFormat(fmt), request);
        try {
            // special handling for the MAP since it's not using "#sendDiagram()" like the other types
            if ("map".equals(fmt)) {
                dr.sendMap(uml, idx);
            } else {
                dr.sendDiagram(uml, idx);
            }
        } catch (IIOException e) {
            // Browser has closed the connection, so the HTTP OutputStream is closed
            // Silently catch the exception to avoid annoying log
        }
    }

    /**
     * Get textual uml diagram source from URL.
     *
     * @param url source URL
     *
     * @return textual uml diagram source
     *
     * @throws IOException if an input or output exception occurred
     */
    private String getSource(final URL url) throws IOException {
        try (SafeHttpClient.Response response = fetch(url);
             BufferedReader br = new BufferedReader(new InputStreamReader(response.getInputStream()))) {
            return br.lines().collect(Collectors.joining("\n"));
        }
    }

    /**
     * Get {@link FileFormat} instance from string.
     *
     * @param format file format name
     *
     * @return corresponding file format instance,
     *         if {@code format} is null or unknown the default {@link FileFormat#PNG} will be returned
     */
    private FileFormat getOutputFormat(String format) {
        if (format == null) {
            return FileFormat.PNG;
        }
        switch (format.toLowerCase()) {
            case "png": return FileFormat.PNG;
            case "svg": return FileFormat.SVG;
            case "eps": return FileFormat.EPS;
            case "epstext": return FileFormat.EPS_TEXT;
            case "txt": return FileFormat.UTXT;
            case "map": return FileFormat.UTXT;
            case "pdf": return FileFormat.PDF;
            default: return FileFormat.PNG;
        }
    }

    /**
     * Fetch a URL (GET).
     * The host is resolved and checked once and the connection is opened on the checked address
     * (protection against DNS rebinding); redirects are followed manually and checked again.
     * See {@link SafeHttpClient}.
     *
     * @param url URL to fetch
     *
     * @return response; the caller has to close it
     *
     * @throws IOException if an input or output exception occurred
     *                     or if the URL or a redirect points to a forbidden target
     */
    static SafeHttpClient.Response fetch(final URL url) throws IOException {
        final String token = System.getenv("HTTP_AUTHORIZATION");
        final String timeoutString = System.getenv("HTTP_PROXY_READ_TIMEOUT");
        int timeout = 10000; // 10 seconds as default
        if (timeoutString != null && timeoutString.matches("^\\d+$")) {
            timeout = Integer.parseInt(timeoutString);
        }
        return SafeHttpClient.get(url, token, timeout);
    }
}
