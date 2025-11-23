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
package net.sourceforge.plantuml.servlet.mcp;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SourceStringReader;
import net.sourceforge.plantuml.servlet.utility.Configuration;
import net.sourceforge.plantuml.servlet.utility.UmlExtractor;
import net.sourceforge.plantuml.version.Version;
import net.sourceforge.plantuml.security.SecurityUtils;

/**
 * MCP (Model Context Protocol) servlet for PlantUML server.
 * Provides a JSON API for AI agents to interact with PlantUML.
 */
@SuppressWarnings("SERIAL")
public class McpServlet extends HttpServlet {

    private static final Gson GSON = new Gson();
    private static final WorkspaceManager WORKSPACE_MANAGER = new WorkspaceManager();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!isMcpEnabled()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "MCP API is not enabled");
            return;
        }

        if (!authenticate(request, response)) {
            return;
        }

        String pathInfo = request.getPathInfo();
        if (pathInfo == null) {
            pathInfo = "/";
        }

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            if (pathInfo.equals("/info")) {
                handleInfo(response);
            } else if (pathInfo.equals("/stats")) {
                handleStats(response);
            } else if (pathInfo.equals("/examples/list")) {
                handleExamplesList(response);
            } else if (pathInfo.startsWith("/examples/get")) {
                handleExamplesGet(request, response);
            } else {
                sendError(response, HttpServletResponse.SC_NOT_FOUND, "Endpoint not found");
            }
        } catch (Exception e) {
            sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                "Internal server error: " + e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!isMcpEnabled()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "MCP API is not enabled");
            return;
        }

        if (!authenticate(request, response)) {
            return;
        }

        String pathInfo = request.getPathInfo();
        if (pathInfo == null) {
            pathInfo = "/";
        }

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            JsonObject requestBody = readJsonRequest(request);

            if (pathInfo.equals("/check")) {
                handleCheck(requestBody, response);
            } else if (pathInfo.equals("/render")) {
                handleRender(requestBody, response);
            } else if (pathInfo.equals("/metadata")) {
                handleMetadata(requestBody, response);
            } else if (pathInfo.equals("/render-url")) {
                handleRenderUrl(requestBody, response);
            } else if (pathInfo.equals("/analyze")) {
                handleAnalyze(requestBody, response);
            } else if (pathInfo.equals("/workspace/create")) {
                handleWorkspaceCreate(requestBody, response);
            } else if (pathInfo.equals("/workspace/put")) {
                handleWorkspaceUpdate(requestBody, response);
            } else if (pathInfo.equals("/workspace/update")) {
                handleWorkspaceUpdate(requestBody, response);
            } else if (pathInfo.equals("/workspace/get")) {
                handleWorkspaceGet(requestBody, response);
            } else if (pathInfo.equals("/workspace/render")) {
                handleWorkspaceRender(requestBody, response);
            } else if (pathInfo.equals("/workspace/list")) {
                handleWorkspaceList(requestBody, response);
            } else {
                sendError(response, HttpServletResponse.SC_NOT_FOUND, "Endpoint not found");
            }
        } catch (com.google.gson.JsonSyntaxException e) {
            // Handle JSON parsing errors
            sendError(response, HttpServletResponse.SC_BAD_REQUEST,
                "Invalid JSON: " + e.getMessage());
        } catch (Exception e) {
            // Log error (servlet container will handle logging)
            sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                "Internal server error: " + e.getMessage());
        }
    }

    private boolean isMcpEnabled() {
        String enabled = Configuration.getString("PLANTUML_MCP_ENABLED", "false");
        return "true".equalsIgnoreCase(enabled);
    }

    private boolean authenticate(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String apiKey = Configuration.getString("PLANTUML_MCP_API_KEY", "");
        if (apiKey.isEmpty()) {
            return true; // No API key required
        }

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            sendError(response, HttpServletResponse.SC_UNAUTHORIZED, "Missing or invalid Authorization header");
            return false;
        }

        String token = authHeader.substring(7);
        if (!apiKey.equals(token)) {
            sendError(response, HttpServletResponse.SC_UNAUTHORIZED, "Invalid API key");
            return false;
        }

        return true;
    }

    private JsonObject readJsonRequest(HttpServletRequest request) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = request.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }
        return GSON.fromJson(sb.toString(), JsonObject.class);
    }

    private void handleInfo(HttpServletResponse response) throws IOException {
        Map<String, Object> info = new HashMap<>();
        info.put("plantumlServerVersion", "2025.2");
        info.put("plantumlCoreVersion", Version.versionString());
        info.put("securityProfile", SecurityUtils.getSecurityProfile().toString());

        info.put("limitSize", Configuration.getInt("PLANTUML_LIMIT_SIZE", 4096));

        boolean statsEnabled = "on".equalsIgnoreCase(
            Configuration.getString("PLANTUML_STATS", "off")
        );
        info.put("statsEnabled", statsEnabled);

        Map<String, Object> environment = new HashMap<>();
        environment.put("backend", "jetty");
        environment.put("readOnly", !SecurityUtils.getSecurityProfile().toString().equals("UNSECURE"));
        info.put("environment", environment);

        sendJson(response, info);
    }

    private void handleStats(HttpServletResponse response) throws IOException {
        boolean statsEnabled = "on".equalsIgnoreCase(
            Configuration.getString("PLANTUML_STATS", "off")
        );
        if (!statsEnabled) {
            sendError(response, HttpServletResponse.SC_NOT_FOUND, "Stats not enabled");
            return;
        }

        Map<String, Object> stats = new HashMap<>();
        stats.put("message", "Stats endpoint not yet implemented");
        sendJson(response, stats);
    }

    private void handleExamplesList(HttpServletResponse response) throws IOException {
        Map<String, Object> result = new HashMap<>();
        result.put("examples", new String[0]);
        result.put("message", "Examples not yet implemented");
        sendJson(response, result);
    }

    private void handleExamplesGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String name = request.getParameter("name");
        if (name == null || name.isEmpty()) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Missing 'name' parameter");
            return;
        }

        Map<String, Object> result = new HashMap<>();
        result.put("name", name);
        result.put("source", "");
        result.put("message", "Example not found");
        sendJson(response, result);
    }

    private void handleCheck(JsonObject requestBody, HttpServletResponse response)
            throws IOException {
        String source = getJsonString(requestBody, "source", null);
        if (source == null || source.isEmpty()) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Missing 'source' field");
            return;
        }

        try {
            // Try to parse the diagram to check for syntax errors
            SourceStringReader reader = new SourceStringReader(source);
            // Use NullOutputStream to avoid generating actual image data
            reader.outputImage(new net.sourceforge.plantuml.servlet.utility.NullOutputStream(),
                0, new FileFormatOption(FileFormat.PNG));

            Map<String, Object> result = new HashMap<>();
            result.put("ok", true);
            result.put("errors", new Object[0]);
            sendJson(response, result);
        } catch (Exception e) {
            Map<String, Object> result = new HashMap<>();
            result.put("ok", false);
            Map<String, Object> error = new HashMap<>();
            error.put("line", 0);
            error.put("message", e.getMessage() != null ? e.getMessage() : "Syntax error");
            result.put("errors", new Object[]{error});
            sendJson(response, result);
        }
    }

    private void handleMetadata(JsonObject requestBody, HttpServletResponse response)
            throws IOException {
        String source = getJsonString(requestBody, "source", null);
        if (source == null || source.isEmpty()) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Missing 'source' field");
            return;
        }

        try {
            // Extract basic metadata from the source
            Map<String, Object> result = new HashMap<>();

            // Parse participants/entities from the source
            java.util.List<String> participants = new java.util.ArrayList<>();
            String[] lines = source.split("\n");
            for (String line : lines) {
                // Simple parsing for common diagram elements
                line = line.trim();
                if (line.matches("^[a-zA-Z0-9_]+\\s*->.*") || line.matches(".*->\\s*[a-zA-Z0-9_]+.*")) {
                    // Extract participant names from arrow notations
                    String[] parts = line.split("->");
                    for (String part : parts) {
                        String trimmed = part.trim();
                        if (!trimmed.isEmpty()) {
                            String[] tokens = trimmed.split("\\s+");
                            if (tokens.length > 0) {
                                String name = tokens[0].replaceAll("[^a-zA-Z0-9_]", "");
                                if (!name.isEmpty() && !participants.contains(name)) {
                                    participants.add(name);
                                }
                            }
                        }
                    }
                } else if (line.matches("^(class|interface|entity|participant)\\s+[a-zA-Z0-9_]+.*")) {
                    String[] parts = line.split("\\s+");
                    if (parts.length >= 2) {
                        String name = parts[1].replaceAll("[^a-zA-Z0-9_]", "");
                        if (!name.isEmpty() && !participants.contains(name)) {
                            participants.add(name);
                        }
                    }
                }
            }

            result.put("participants", participants.toArray(new String[0]));
            result.put("directives", new String[0]);

            // Detect diagram type
            String diagramType = "unknown";
            if (source.contains("@startuml")) {
                if (source.contains("->") || source.contains("participant")) {
                    diagramType = "sequence";
                } else if (source.contains("class") || source.contains("interface")) {
                    diagramType = "class";
                } else if (source.contains("state")) {
                    diagramType = "state";
                } else if (source.contains("usecase") || source.contains("actor")) {
                    diagramType = "usecase";
                }
            }
            result.put("diagramType", diagramType);
            result.put("warnings", new String[0]);

            sendJson(response, result);
        } catch (Exception e) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST,
                "Metadata extraction failed: " + e.getMessage());
        }
    }

    private void handleRender(JsonObject requestBody, HttpServletResponse response)
            throws IOException {
        String source = getJsonString(requestBody, "source", null);
        if (source == null || source.isEmpty()) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Missing 'source' field");
            return;
        }

        String format = getJsonString(requestBody, "format", "png");
        FileFormat fileFormat = parseFileFormat(format);

        long startTime = System.currentTimeMillis();

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            SourceStringReader reader = new SourceStringReader(source);
            reader.outputImage(outputStream, 0, new FileFormatOption(fileFormat));

            byte[] imageBytes = outputStream.toByteArray();
            String dataBase64 = Base64.getEncoder().encodeToString(imageBytes);

            Map<String, Object> result = new HashMap<>();
            result.put("ok", true);
            result.put("format", format);
            result.put("dataBase64", dataBase64);

            sendJson(response, result);
        } catch (Exception e) {
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("ok", false);
            errorResult.put("errors", new Object[]{
                java.util.Collections.singletonMap("message", "Rendering failed: " + e.getMessage())
            });
            response.setStatus(HttpServletResponse.SC_OK);
            sendJson(response, errorResult);
        }
    }

    private void handleRenderUrl(JsonObject requestBody, HttpServletResponse response)
            throws IOException {
        String encodedUrl = getJsonString(requestBody, "encodedUrl", null);
        if (encodedUrl == null || encodedUrl.isEmpty()) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Missing 'encodedUrl' field");
            return;
        }

        // Extract encoded diagram from URL
        String encoded = encodedUrl;
        if (encoded.startsWith("/")) {
            String[] parts = encoded.split("/");
            if (parts.length >= 2) {
                encoded = parts[parts.length - 1];
            }
        }

        try {
            String source = UmlExtractor.getUmlSource(encoded);

            // Create new request body with decoded source
            JsonObject newRequest = new JsonObject();
            newRequest.addProperty("source", source);
            newRequest.addProperty("format", getJsonString(requestBody, "format", "png"));

            handleRender(newRequest, response);
        } catch (Exception e) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST,
                "Failed to decode URL: " + e.getMessage());
        }
    }

    private void handleAnalyze(JsonObject requestBody, HttpServletResponse response)
            throws IOException {
        String source = getJsonString(requestBody, "source", null);
        if (source == null || source.isEmpty()) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Missing 'source' field");
            return;
        }

        Map<String, Object> result = new HashMap<>();
        result.put("status", "ok");
        result.put("messages", new Object[0]);
        result.put("includes", new Object[0]);
        result.put("diagramType", "unknown");

        Map<String, Object> complexity = new HashMap<>();
        complexity.put("lineCount", source.split("\n").length);
        complexity.put("elementCount", 0);
        result.put("estimatedComplexity", complexity);

        sendJson(response, result);
    }

    private void handleWorkspaceCreate(JsonObject requestBody, HttpServletResponse response)
            throws IOException {
        String sessionId = getJsonString(requestBody, "sessionId", null);
        String name = getJsonString(requestBody, "name", null);
        String source = getJsonString(requestBody, "source", null);

        if (sessionId == null || name == null || source == null) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST,
                "Missing required fields: sessionId, name, source");
            return;
        }

        int limit = Configuration.getInt("PLANTUML_MCP_WORKSPACE_LIMIT", 20);
        String diagramId = WORKSPACE_MANAGER.createDiagram(sessionId, name, source, limit);

        if (diagramId == null) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST,
                "Workspace limit exceeded");
            return;
        }

        Map<String, Object> result = new HashMap<>();
        result.put("diagramId", diagramId);
        sendJson(response, result);
    }

    private void handleWorkspaceUpdate(JsonObject requestBody, HttpServletResponse response)
            throws IOException {
        String sessionId = getJsonString(requestBody, "sessionId", null);
        String diagramId = getJsonString(requestBody, "diagramId", null);
        String source = getJsonString(requestBody, "source", null);

        if (sessionId == null || diagramId == null || source == null) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST,
                "Missing required fields: sessionId, diagramId, source");
            return;
        }

        boolean success = WORKSPACE_MANAGER.updateDiagram(sessionId, diagramId, source);
        if (!success) {
            sendError(response, HttpServletResponse.SC_NOT_FOUND, "Diagram not found");
            return;
        }

        Map<String, Object> result = new HashMap<>();
        result.put("status", "ok");
        sendJson(response, result);
    }

    private void handleWorkspaceGet(JsonObject requestBody, HttpServletResponse response)
            throws IOException {
        String sessionId = getJsonString(requestBody, "sessionId", null);
        String diagramId = getJsonString(requestBody, "diagramId", null);

        if (sessionId == null || diagramId == null) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST,
                "Missing required fields: sessionId, diagramId");
            return;
        }

        String source = WORKSPACE_MANAGER.getDiagram(sessionId, diagramId);
        if (source == null) {
            sendError(response, HttpServletResponse.SC_NOT_FOUND, "Diagram not found");
            return;
        }

        Map<String, Object> result = new HashMap<>();
        result.put("diagramId", diagramId);
        result.put("source", source);
        sendJson(response, result);
    }

    private void handleWorkspaceRender(JsonObject requestBody, HttpServletResponse response)
            throws IOException {
        String sessionId = getJsonString(requestBody, "sessionId", null);
        String diagramId = getJsonString(requestBody, "diagramId", null);

        if (sessionId == null || diagramId == null) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST,
                "Missing required fields: sessionId, diagramId");
            return;
        }

        String source = WORKSPACE_MANAGER.getDiagram(sessionId, diagramId);
        if (source == null) {
            sendError(response, HttpServletResponse.SC_NOT_FOUND, "Diagram not found");
            return;
        }

        JsonObject renderRequest = new JsonObject();
        renderRequest.addProperty("source", source);
        renderRequest.addProperty("format", getJsonString(requestBody, "format", "png"));

        handleRender(renderRequest, response);
    }

    private void handleWorkspaceList(JsonObject requestBody, HttpServletResponse response)
            throws IOException {
        String sessionId = getJsonString(requestBody, "sessionId", null);

        if (sessionId == null) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST,
                "Missing required field: sessionId");
            return;
        }

        Map<String, String> diagrams = WORKSPACE_MANAGER.listDiagrams(sessionId);

        Map<String, Object> result = new HashMap<>();
        result.put("diagrams", diagrams.entrySet().stream()
            .map(e -> {
                Map<String, String> d = new HashMap<>();
                d.put("diagramId", e.getKey());
                d.put("name", e.getValue());
                return d;
            })
            .toArray());

        sendJson(response, result);
    }

    private FileFormat parseFileFormat(String format) {
        switch (format.toLowerCase()) {
            case "svg": return FileFormat.SVG;
            case "png": return FileFormat.PNG;
            case "txt": return FileFormat.UTXT;
            case "eps": return FileFormat.EPS;
            case "pdf": return FileFormat.PDF;
            default: return FileFormat.PNG;
        }
    }

    private String formatDataUrl(byte[] data, FileFormat format) {
        String base64 = Base64.getEncoder().encodeToString(data);
        String mimeType = format.getMimeType();
        return "data:" + mimeType + ";base64," + base64;
    }

    private String computeSha256(byte[] data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data);
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            return "";
        }
    }

    private String getJsonString(JsonObject json, String key, String defaultValue) {
        if (json.has(key) && !json.get(key).isJsonNull()) {
            return json.get(key).getAsString();
        }
        return defaultValue;
    }

    private void sendJson(HttpServletResponse response, Object data) throws IOException {
        response.setStatus(HttpServletResponse.SC_OK);
        PrintWriter writer = response.getWriter();
        writer.print(GSON.toJson(data));
        writer.flush();
    }

    private void sendError(HttpServletResponse response, int status, String message)
            throws IOException {
        response.setStatus(status);
        Map<String, String> error = new HashMap<>();
        error.put("error", message);
        PrintWriter writer = response.getWriter();
        writer.print(GSON.toJson(error));
        writer.flush();
    }
}
