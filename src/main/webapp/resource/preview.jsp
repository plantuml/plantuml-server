<div class="previewer-container flex-rows">
  <div class="preview-menu">
    <div class="diagram-links flex-columns">
      <span>View as:</span>
      <a class="diagram-link" data-img-type="png" href="png/<%= diagramUrl %>" title="View diagram as PNG">
        <img src="assets/file-types/png.svg" alt="PNG" />
      </a>
      <a class="diagram-link" data-img-type="svg" href="svg/<%= diagramUrl %>" title="View diagram as SVG">
        <img src="assets/file-types/svg.svg" alt="SVG" />
      </a>
      <a class="diagram-link" data-img-type="txt" href="txt/<%= diagramUrl %>" title="View diagram as ASCII Art">
        <img src="assets/file-types/ascii.svg" alt="ASCII Art" />
      </a>
      <a class="diagram-link" data-img-type="pdf" href="pdf/<%= diagramUrl %>" title="View diagram as PDF">
        <img src="assets/file-types/pdf.svg" alt="PDF" />
      </a>
      <a
        id="map-diagram-link"
        class="diagram-link"
        data-img-type="map"
        href="map/<%= diagramUrl %>"
        title="View diagram as Map Data"
        <% if (!hasMap) { %>
          style="display: none;"
        <% } %>
      >
          <img src="assets/file-types/map.svg" alt="MAP" />
      </a>
      <div class="flex-main menu-r">
        <div class="btn-float-r">
          <input
            id="btn-settings"
            class="btn-settings"
            type="image"
            src="assets/settings.svg"
            alt="settings"
            onclick="openSettings();"
          />
          <input
            id="btn-undock"
            class="btn-dock"
            type="image"
            src="assets/undock.svg"
            alt="undock"
            onclick="undock();"
          />
          <input
            id="btn-dock"
            class="btn-dock"
            type="image"
            src="assets/dock.svg"
            alt="dock"
            onclick="window.close();"
            style="display: none;"
          />
        </div>
      </div>
    </div>
  </div>
  <div class="hr"></div>
  <div id="paginator" data-number-of-diagram-pages="1" style="display: none;"></div>
  <div class="previewer-main flex-main">
    <div id="diagram" class="diagram">
      <div>
        <!-- PNG -->
        <img id="diagram-png" src="png/<%= diagramUrl %>" alt="PlantUML diagram" usemap="#plantuml_map" />
        <% if (hasMap) { %>
          <%= map %>
        <% } else { %>
          <map id="plantuml_map" name="plantuml_map"></map>
        <% } %>
        <!-- SVG -->
        <svg id="diagram-svg" style="display: none;"></svg>
        <!-- ASCII Art -->
        <pre id="diagram-txt" style="display: none;"></pre>
        <!-- PDF -->
        <object id="diagram-pdf" data="" type="application/pdf" width="100%" height="100%" style="display: none;">
          <p>Unable to display PDF file.</p>
        </object>
      </div>
    </div>
  </div>
  <% if (showSocialButtons) { %>
    <div>
      <%@ include file="socialbuttons2.jsp" %>
    </div>
  <% } %>
  <%@ include file="settings.jsp" %>
</div>
