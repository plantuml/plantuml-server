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

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.EOFException;
import java.io.FileNotFoundException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

/**
 * Minimal HTTP/1.1 GET client protected against server-side request forgery (SSRF).
 * <p>
 * The host name is resolved <b>once</b>, every resulting address is checked
 * (see {@link ProxyServlet#isForbiddenAddress(InetAddress)}) and the connection is then opened
 * on that very address. A second, different DNS answer (DNS rebinding) can therefore not redirect
 * the request to an internal address. For HTTPS, the TLS handshake still uses the original host
 * name (SNI and certificate verification).
 * Redirects are followed manually and each target is checked again.
 * <p>
 * If a JVM proxy is configured for the URL, the name resolution is done by that proxy
 * and the request is sent through {@link HttpURLConnection} (redirects checked as well).
 */
final class SafeHttpClient {

    private static final int MAX_REDIRECTS = 5;
    private static final int MAX_LINE_LENGTH = 8192;
    private static final int MAX_HEADERS = 100;

    private SafeHttpClient() {
    }

    /**
     * HTTP response; closing it releases the underlying connection.
     */
    static final class Response implements Closeable {
        private final URL url;
        private final int status;
        private final Map<String, String> headers;
        private final InputStream body;
        private final Closeable connection;

        Response(URL requestUrl, int statusCode, Map<String, String> responseHeaders, InputStream content,
                 Closeable resource) {
            this.url = requestUrl;
            this.status = statusCode;
            this.headers = responseHeaders;
            this.connection = resource;
            this.body = new FilterInputStream(content) {
                @Override
                public void close() throws IOException {
                    try {
                        super.close();
                    } finally {
                        resource.close();
                    }
                }
            };
        }

        int getStatus() {
            return status;
        }

        /**
         * @param name header name (case insensitive)
         *
         * @return first value of the header; otherwise {@code null}
         */
        String getHeader(String name) {
            return headers.get(name.toLowerCase(Locale.ROOT));
        }

        String getContentType() {
            return getHeader("Content-Type");
        }

        /**
         * Get response body.
         *
         * @return body stream; closing it closes the connection
         *
         * @throws IOException if the status code is an error (the connection is closed in this case)
         */
        InputStream getInputStream() throws IOException {
            if (status >= 400) {
                close();
                if (status == 404 || status == 410) {
                    throw new FileNotFoundException(url.toString());
                }
                throw new IOException("Server returned HTTP response code: " + status + " for URL: " + url);
            }
            return body;
        }

        @Override
        public void close() throws IOException {
            connection.close();
        }
    }

    /**
     * Send a GET request.
     *
     * @param url           URL to fetch
     * @param authorization value of the Authorization header (only sent to the host of {@code url}); may be null
     * @param timeoutMs     connect and read timeout in milliseconds
     *
     * @return response; the caller has to close it
     *
     * @throws IOException if an I/O error occurred or the URL (or a redirect target) is forbidden
     */
    static Response get(final URL url, final String authorization, final int timeoutMs) throws IOException {
        if (ProxyServlet.isRemoteAccessDisabled()) {
            throw new IOException("Remote URL access is disabled.");
        }
        URL current = url;
        for (int hop = 0; hop <= MAX_REDIRECTS; hop++) {
            if (ProxyServlet.forbiddenURL(current.toString())) {
                throw new IOException("Forbidden URL.");
            }
            final String auth = current.getHost().equalsIgnoreCase(url.getHost()) ? authorization : null;
            final Response response = usesProxy(current)
                ? requestViaProxy(current, auth, timeoutMs)
                : request(current, auth, timeoutMs);
            final int code = response.getStatus();
            if (code != 301 && code != 302 && code != 303 && code != 307 && code != 308) {
                return response;
            }
            final String location = response.getHeader("Location");
            response.close();
            if (location == null) {
                throw new IOException("Redirect without location.");
            }
            current = new URL(current, location);
        }
        throw new IOException("Too many redirects.");
    }

    private static boolean usesProxy(final URL url) {
        try {
            final ProxySelector selector = ProxySelector.getDefault();
            if (selector != null) {
                for (final Proxy proxy : selector.select(url.toURI())) {
                    if (proxy.type() != Proxy.Type.DIRECT) {
                        return true;
                    }
                }
            }
        } catch (URISyntaxException | RuntimeException e) {
            // no usable proxy information: connect directly
        }
        return false;
    }

    private static Response requestViaProxy(
        final URL url,
        final String authorization,
        final int timeoutMs
    ) throws IOException {
        if (ProxyServlet.resolvesToForbiddenAddress(url.getHost())) {
            throw new IOException("Forbidden URL host.");
        }
        final HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setInstanceFollowRedirects(false);
        if (authorization != null) {
            conn.setRequestProperty("Authorization", authorization);
        }
        conn.setConnectTimeout(timeoutMs);
        conn.setReadTimeout(timeoutMs);
        conn.connect();
        final int status = conn.getResponseCode();
        final Map<String, String> headers = new HashMap<>();
        for (final Map.Entry<String, List<String>> entry : conn.getHeaderFields().entrySet()) {
            if (entry.getKey() != null && entry.getValue().isEmpty() == false) {
                headers.put(entry.getKey().toLowerCase(Locale.ROOT), entry.getValue().get(0));
            }
        }
        InputStream body = status >= 400 ? conn.getErrorStream() : conn.getInputStream();
        if (body == null) {
            body = new EmptyInputStream();
        }
        return new Response(url, status, headers, body, conn::disconnect);
    }

    private static Response request(
        final URL url,
        final String authorization,
        final int timeoutMs
    ) throws IOException {
        final String host = url.getHost();
        final boolean https = "https".equalsIgnoreCase(url.getProtocol());
        if (https == false && "http".equalsIgnoreCase(url.getProtocol()) == false) {
            throw new IOException("Unsupported protocol.");
        }
        final int port = url.getPort() == -1 ? url.getDefaultPort() : url.getPort();
        String path = url.getFile();
        if (path.isEmpty()) {
            path = "/";
        }
        // refuse anything that could inject additional headers or requests
        checkToken(path);
        checkToken(host);
        if (authorization != null) {
            checkHeaderValue(authorization);
        }

        // resolve ONCE, check all addresses and connect on the checked address
        final InetAddress[] addresses = InetAddress.getAllByName(host);
        if (ProxyServlet.allowPrivateUrls() == false) {
            for (final InetAddress address : addresses) {
                if (ProxyServlet.isForbiddenAddress(address)) {
                    throw new IOException("Forbidden URL host.");
                }
            }
        }
        Socket socket = null;
        IOException lastError = null;
        for (final InetAddress address : addresses) {
            final Socket candidate = new Socket();
            try {
                candidate.connect(new InetSocketAddress(address, port), timeoutMs);
                socket = candidate;
                break;
            } catch (IOException e) {
                lastError = e;
                candidate.close();
            }
        }
        if (socket == null) {
            throw lastError != null ? lastError : new IOException("Cannot connect to " + host);
        }

        try {
            socket.setSoTimeout(timeoutMs);
            if (https) {
                socket = secure(socket, host, port);
            }
            final StringBuilder request = new StringBuilder();
            request.append("GET ").append(path).append(" HTTP/1.1\r\n");
            request.append("Host: ").append(host);
            if (url.getPort() != -1 && url.getPort() != url.getDefaultPort()) {
                request.append(':').append(url.getPort());
            }
            request.append("\r\n");
            request.append("User-Agent: PlantUML-Server\r\n");
            request.append("Accept: */*\r\n");
            request.append("Accept-Encoding: identity\r\n");
            request.append("Connection: close\r\n");
            if (authorization != null) {
                request.append("Authorization: ").append(authorization).append("\r\n");
            }
            request.append("\r\n");
            final OutputStream out = socket.getOutputStream();
            out.write(request.toString().getBytes(StandardCharsets.ISO_8859_1));
            out.flush();
            return readResponse(url, socket);
        } catch (IOException | RuntimeException e) {
            socket.close();
            throw e;
        }
    }

    /**
     * Wrap the already connected socket in TLS using the original host name
     * for SNI and for the certificate (host name) verification.
     */
    private static Socket secure(final Socket socket, final String host, final int port) throws IOException {
        final SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
        final SSLSocket ssl = (SSLSocket) factory.createSocket(socket, host, port, true);
        final SSLParameters parameters = ssl.getSSLParameters();
        parameters.setEndpointIdentificationAlgorithm("HTTPS");
        ssl.setSSLParameters(parameters);
        ssl.startHandshake();
        return ssl;
    }

    private static void checkToken(final String value) throws IOException {
        for (int i = 0; i < value.length(); i++) {
            final char c = value.charAt(i);
            if (c <= ' ' || c >= 0x7F) {
                throw new IOException("Forbidden character in URL.");
            }
        }
    }

    private static void checkHeaderValue(final String value) throws IOException {
        for (int i = 0; i < value.length(); i++) {
            final char c = value.charAt(i);
            if (c == '\r' || c == '\n' || c == 0) {
                throw new IOException("Forbidden character in header.");
            }
        }
    }

    private static Response readResponse(final URL url, final Socket socket) throws IOException {
        final InputStream in = new BufferedInputStream(socket.getInputStream());
        int status;
        do {
            final String statusLine = readLine(in);
            final String[] parts = statusLine.split(" ", 3);
            if (parts.length < 2 || parts[0].startsWith("HTTP/") == false) {
                throw new IOException("Invalid HTTP response.");
            }
            try {
                status = Integer.parseInt(parts[1].trim());
            } catch (NumberFormatException e) {
                throw new IOException("Invalid HTTP status.");
            }
            if (status >= 100 && status < 200) {
                skipHeaders(in);
            }
        } while (status >= 100 && status < 200);

        final Map<String, String> headers = new HashMap<>();
        for (int i = 0; i <= MAX_HEADERS; i++) {
            final String line = readLine(in);
            if (line.isEmpty()) {
                break;
            }
            if (i == MAX_HEADERS) {
                throw new IOException("Too many headers.");
            }
            final int colon = line.indexOf(':');
            if (colon > 0) {
                final String name = line.substring(0, colon).trim().toLowerCase(Locale.ROOT);
                if (headers.containsKey(name) == false) {
                    headers.put(name, line.substring(colon + 1).trim());
                }
            }
        }

        final InputStream body;
        final String transferEncoding = headers.get("transfer-encoding");
        final String contentLength = headers.get("content-length");
        if (status == 204 || status == 304) {
            body = new EmptyInputStream();
        } else if (transferEncoding != null && transferEncoding.toLowerCase(Locale.ROOT).contains("chunked")) {
            body = new ChunkedInputStream(in);
        } else if (contentLength != null) {
            try {
                body = new BoundedInputStream(in, Long.parseLong(contentLength.trim()));
            } catch (NumberFormatException e) {
                throw new IOException("Invalid Content-Length.");
            }
        } else {
            body = in; // until the server closes the connection
        }
        return new Response(url, status, headers, body, socket);
    }

    private static void skipHeaders(final InputStream in) throws IOException {
        for (int i = 0; i <= MAX_HEADERS; i++) {
            if (readLine(in).isEmpty()) {
                return;
            }
        }
        throw new IOException("Too many headers.");
    }

    private static String readLine(final InputStream in) throws IOException {
        final ByteArrayOutputStream line = new ByteArrayOutputStream();
        while (true) {
            final int c = in.read();
            if (c == -1) {
                throw new EOFException("Unexpected end of HTTP response.");
            }
            if (c == '\n') {
                break;
            }
            if (line.size() >= MAX_LINE_LENGTH) {
                throw new IOException("HTTP line too long.");
            }
            line.write(c);
        }
        final String text = new String(line.toByteArray(), StandardCharsets.ISO_8859_1);
        return text.endsWith("\r") ? text.substring(0, text.length() - 1) : text;
    }

    private static final class EmptyInputStream extends InputStream {
        @Override
        public int read() {
            return -1;
        }
    }

    private static final class BoundedInputStream extends InputStream {
        private final InputStream in;
        private long remaining;

        BoundedInputStream(InputStream source, long length) {
            this.in = source;
            this.remaining = Math.max(length, 0);
        }

        @Override
        public int read() throws IOException {
            final byte[] b = new byte[1];
            final int n = read(b, 0, 1);
            return n == -1 ? -1 : b[0] & 0xFF;
        }

        @Override
        public int read(byte[] b, int off, int len) throws IOException {
            if (remaining == 0) {
                return -1;
            }
            final int n = in.read(b, off, (int) Math.min(len, remaining));
            if (n > 0) {
                remaining -= n;
            }
            return n;
        }
    }

    private static final class ChunkedInputStream extends InputStream {
        private final InputStream in;
        private long remaining = 0;
        private boolean first = true;
        private boolean eof = false;

        ChunkedInputStream(InputStream source) {
            this.in = source;
        }

        @Override
        public int read() throws IOException {
            final byte[] b = new byte[1];
            final int n = read(b, 0, 1);
            return n == -1 ? -1 : b[0] & 0xFF;
        }

        @Override
        public int read(byte[] b, int off, int len) throws IOException {
            if (eof) {
                return -1;
            }
            if (len == 0) {
                return 0;
            }
            if (remaining == 0) {
                if (first == false) {
                    readLine(in); // CRLF terminating the previous chunk
                }
                first = false;
                String line = readLine(in);
                final int semicolon = line.indexOf(';');
                if (semicolon >= 0) {
                    line = line.substring(0, semicolon);
                }
                try {
                    remaining = Long.parseLong(line.trim(), 16);
                } catch (NumberFormatException e) {
                    throw new IOException("Invalid chunk size.");
                }
                if (remaining < 0) {
                    throw new IOException("Invalid chunk size.");
                }
                if (remaining == 0) {
                    eof = true; // trailers are ignored
                    return -1;
                }
            }
            final int n = in.read(b, off, (int) Math.min(len, remaining));
            if (n == -1) {
                throw new EOFException("Truncated chunked response.");
            }
            remaining -= n;
            return n;
        }
    }
}
