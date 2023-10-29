package net.sourceforge.plantuml.servlet;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.hc.client5.http.entity.mime.ByteArrayBody;
import org.apache.hc.client5.http.entity.mime.ContentBody;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.core5.http.HttpEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import net.sourceforge.plantuml.json.Json;
import net.sourceforge.plantuml.json.JsonObject;
import net.sourceforge.plantuml.servlet.utils.TestUtils;
import net.sourceforge.plantuml.servlet.utils.WebappTestCase;


public class TestMetadata extends WebappTestCase {

    @ParameterizedTest
    @CsvSource({
        "png, false, true",
        "png, true,  true",
        "svg, false, false",  // Note: PlantUML SVG diagram images do not include version information
        "svg, true,  false",
    })
    public void testBobAliceSampleMetadataProxy(
        String format,
        boolean isJsonResponse,
        boolean hasVersion
    ) throws IOException {
        final String resourceFilename = "bob." + format;
        final URL url = new URL(getServerUrl() + "/metadata?src=" + getTestResourceUrl(resourceFilename));
        checkMetadataResponse(url, isJsonResponse, hasVersion);
    }

    @ParameterizedTest
    @CsvSource({
        "png, false, true",
        "png, true,  true",
        "svg, false, false",  // Note: PlantUML SVG diagram images do not include version information
        "svg, true,  false",
    })
    public void testBobAliceSampleMetadataFileUpload(
        String format,
        boolean isJsonResponse,
        boolean hasVersion
    ) throws IOException {
        // build file upload request entity
        final String resourceFilename = "bob." + format;
        final URL resourceUrl = new URL(getTestResourceUrl(resourceFilename));
        final byte[] resource = getContentAsBytes(resourceUrl);
        final ContentBody contentPart = new ByteArrayBody(resource, resourceFilename);
        final MultipartEntityBuilder requestBuilder = MultipartEntityBuilder.create();
        requestBuilder.addPart("diagram", contentPart);
        HttpEntity requestEntity = requestBuilder.build();
        // send request including file
        final URL url = new URL(getServerUrl() + "/metadata");
        final HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        conn.setUseCaches(false);
        conn.setDoInput(true);
        conn.setDoOutput(true);
        conn.setRequestMethod("POST");
        if (isJsonResponse) {
            conn.setRequestProperty("Accept", "application/json");
        }
        conn.setRequestProperty("Cache-Control", "no-cache");
        conn.setRequestProperty("Connection", "Keep-Alive");
        conn.addRequestProperty("Content-length", Long.toString(requestEntity.getContentLength()));
        conn.setRequestProperty("Content-Type", requestEntity.getContentType());
        try (OutputStream out = conn.getOutputStream()) {
            requestEntity.writeTo(out);
        }
        // check response
        checkMetadataResponse(conn, isJsonResponse, hasVersion);
    }

    private void checkMetadataResponse(
        URL url,
        boolean isJsonResponse,
        boolean hasVersion
    ) throws IOException {
        final HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        if (isJsonResponse) {
            conn.setRequestProperty("Accept", "application/json");
        }
        checkMetadataResponse(conn, isJsonResponse, hasVersion);
    }

    private void checkMetadataResponse(
        HttpURLConnection conn,
        boolean isJsonResponse,
        boolean hasVersion
    ) throws IOException {
        // Verifies HTTP status code and the Content-Type
        Assertions.assertEquals(200, conn.getResponseCode(), "Bad HTTP status received");
        if (isJsonResponse) {
            Assertions.assertEquals(
                "application/json;charset=utf-8",
                conn.getContentType().toLowerCase(),
                "Response content type is not JSON or UTF-8"
            );
        } else {
            Assertions.assertEquals(
                "text/plain;charset=utf-8",
                conn.getContentType().toLowerCase(),
                "Response content type is not plain text or UTF-8"
            );
        }
        // Get and verify the content
        final String content = getContentText(conn);
        if (isJsonResponse) {
            checkMetadataContent(Json.parse(content).asObject(), hasVersion);
        } else {
            checkMetadataContent(content, hasVersion);
        }
    }

    private void checkMetadataContent(String metadata, boolean hasVersion) {
        Assertions.assertTrue(
            metadata.contains(TestUtils.SEQBOB),
            "Meta data does not contain encoded PlantUML diagram code"
        );
        Assertions.assertTrue(
            metadata.contains(TestUtils.SEQBOBCODE),
            "Meta data does not contain decoded PlantUML diagram code"
        );
        if (hasVersion) {
            Assertions.assertTrue(
                metadata.contains("PlantUML version"),
                "Meta data does not contain PlantUML version"
            );
        }
    }
    private void checkMetadataContent(JsonObject metadata, boolean hasVersion) {
        Assertions.assertEquals(TestUtils.SEQBOB, metadata.get("encoded").asString());
        Assertions.assertEquals(TestUtils.SEQBOBCODE, metadata.get("decoded").asString());
        if (hasVersion) {
            Assertions.assertNotNull(metadata.get("version").asString());
        }
    }
}
