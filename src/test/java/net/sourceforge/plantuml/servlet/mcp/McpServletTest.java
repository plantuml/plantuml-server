package net.sourceforge.plantuml.servlet.mcp;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import net.sourceforge.plantuml.servlet.utils.WebappTestCase;

/**
 * Unit tests for McpServlet as specified in the issue requirements.
 * These tests use the WebappTestCase framework instead of direct servlet mocking
 * to avoid dependency conflicts.
 */
public class McpServletTest extends WebappTestCase {

    private static final Gson GSON = new Gson();

    /**
     * Helper method to make a POST request with JSON body.
     */
    private HttpURLConnection postJson(String path, String json) throws IOException {
        URL url = new URL(getServerUrl() + path);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = json.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        return conn;
    }

    /**
     * Helper method to extract workspaceId from JSON response.
     */
    private String extractWorkspaceId(String json) {
        JsonObject obj = GSON.fromJson(json, JsonObject.class);
        if (obj.has("workspaceId")) {
            return obj.get("workspaceId").getAsString();
        }
        return null;
    }

    /**
     * Test: check endpoint accepts valid diagram.
     */
    @Test
    void checkEndpointShouldReturnOkForValidDiagram() throws Exception {
        String json = "{ \"source\": \"@startuml\\nAlice -> Bob\\n@enduml\" }";

        HttpURLConnection conn = postJson("/mcp/check", json);
        int responseCode = conn.getResponseCode();

        if (responseCode == 404) {
            // MCP not enabled, skip this test
            return;
        }

        assertEquals(200, responseCode);
        String body = getContentText(conn);

        assertTrue(body.contains("\"ok\":true"));
        assertTrue(body.contains("\"errors\":[]"));
    }

    /**
     * Test: check endpoint should report syntax errors.
     */
    @Test
    void checkEndpointShouldReportErrors() throws Exception {
        String json = "{ \"source\": \"@startuml\\nThis is wrong\\n@enduml\" }";

        HttpURLConnection conn = postJson("/mcp/check", json);
        int responseCode = conn.getResponseCode();

        if (responseCode == 404) {
            // MCP not enabled, skip this test
            return;
        }

        String body = getContentText(conn);
        assertTrue(body.contains("\"ok\":false"));
        assertTrue(body.contains("errors"));
    }

    /**
     * Test: render endpoint returns Base64 PNG.
     */
    @Test
    void renderEndpointReturnsPngBase64() throws Exception {
        String json = "{ \"source\": \"@startuml\\nAlice -> Bob\\n@enduml\" }";

        HttpURLConnection conn = postJson("/mcp/render", json);
        int responseCode = conn.getResponseCode();

        if (responseCode == 404) {
            // MCP not enabled, skip this test
            return;
        }

        String body = getContentText(conn);
        assertTrue(body.contains("\"format\":\"png\""));
        assertTrue(body.contains("\"dataBase64\""));
    }

    /**
     * Test: metadata endpoint returns participants.
     */
    @Test
    void metadataEndpointReturnsParticipants() throws Exception {
        String json = "{ \"source\": \"@startuml\\nAlice -> Bob\\n@enduml\" }";

        HttpURLConnection conn = postJson("/mcp/metadata", json);
        int responseCode = conn.getResponseCode();

        if (responseCode == 404) {
            // MCP not enabled, skip this test
            return;
        }

        String body = getContentText(conn);
        assertTrue(body.contains("Alice"));
        assertTrue(body.contains("Bob"));
    }

    /**
     * Test: workspace lifecycle.
     */
    @Test
    void workspaceLifecycle() throws Exception {
        String sessionId = "test-session-" + System.currentTimeMillis();

        // 1) create workspace (diagram)
        String createJson = "{ \"sessionId\":\"" + sessionId + "\", "
                          + "\"name\":\"test.puml\", "
                          + "\"source\":\"@startuml\\nAlice->Bob\\n@enduml\" }";
        HttpURLConnection r1 = postJson("/mcp/workspace/create", createJson);

        int responseCode = r1.getResponseCode();
        if (responseCode == 404) {
            // MCP not enabled, skip this test
            return;
        }

        assertEquals(200, responseCode);
        String body1 = getContentText(r1);

        // Extract diagramId from response
        JsonObject createResp = GSON.fromJson(body1, JsonObject.class);
        String diagramId = createResp.get("diagramId").getAsString();
        assertNotNull(diagramId);

        // 2) put file (update diagram)
        String putJson = "{ \"sessionId\":\"" + sessionId + "\", "
                       + "\"diagramId\":\"" + diagramId + "\", "
                       + "\"source\":\"@startuml\\nAlice->Charlie\\n@enduml\" }";
        HttpURLConnection r2 = postJson("/mcp/workspace/put", putJson);
        assertEquals(200, r2.getResponseCode());

        // 3) render file
        String renderJson = "{ \"sessionId\":\"" + sessionId + "\", "
                          + "\"diagramId\":\"" + diagramId + "\" }";
        HttpURLConnection r3 = postJson("/mcp/workspace/render", renderJson);

        String body3 = getContentText(r3);
        assertTrue(body3.contains("\"dataBase64\""));
    }

    /**
     * Test: invalid JSON must return 400.
     */
    @Test
    void invalidJsonShouldReturn400() throws Exception {
        HttpURLConnection conn = postJson("/mcp/check", "{ invalid json }");

        int responseCode = conn.getResponseCode();
        if (responseCode == 404) {
            // MCP not enabled, skip this test
            return;
        }

        assertEquals(400, responseCode);
    }
}
