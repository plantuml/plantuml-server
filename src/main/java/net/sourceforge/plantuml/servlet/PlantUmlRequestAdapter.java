package net.sourceforge.plantuml.servlet;

import jakarta.servlet.http.HttpServletRequest;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

/**
 * Typesafe accessors to request parameters.
 */
@Data
@RequiredArgsConstructor
public final class PlantUmlRequestAdapter {
    private static final String TEXT = "text";
    public static final String URL = "url";
    public static final String METADATA = "metadata";

    @NonNull
    private final HttpServletRequest request;

    /**
     * @deprecated Use {@link #optionalParameter(String)} instead.
     */
    @Deprecated
    public String getParameter(String name) {
        return request.getParameter(name);
    }

    private Optional<String> optionalParameter(String name) {
        return Optional.ofNullable(request.getParameter(name));
    }

    private Optional<String> optionalNonEmptyParameter(String name) {
        return optionalParameter(name).map(String::trim).filter(v -> !v.isEmpty());
    }

    public Optional<String> getMetadata() {
        return optionalParameter(METADATA);
    }

    /**
     * @deprecated use {@link #getCleanedText()} instead;
     */
    @Deprecated
    public Optional<String> getText() {
        return optionalParameter(TEXT);
    }

    public Optional<String> getCleanedText() {
        return optionalNonEmptyParameter(TEXT);
    }

    /**
     * @deprecated use {@link #getCleanedUrl()} instead;
     */
    @Deprecated
    public Optional<String> getUrl() {
        return optionalParameter(URL);
    }

    public Optional<String> getCleanedUrl() {
        return optionalNonEmptyParameter(URL);
    }

    public Optional<String> getRequestURI() {
        return Optional.of(request.getRequestURI());
    }

    /**
     * Use (or add) methods on this class to hide the request.
     * This method will be removed after refactoring.
     */
    @Deprecated
    public HttpServletRequest getRequest() {
        return request;
    }
}
