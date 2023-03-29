package net.sourceforge.plantuml.servlet;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public final class PlantUmlUrlProcessor {
    private final String requestUri;

    public PlantUmlUrlProcessor(PlantUmlRequestAdapter adapter) {
        this(adapter.getRequest().getRequestURI());
    }

    public boolean isUml() {
        return requestUri.contains("/uml/") && !requestUri.endsWith("/uml/");
    }
}
