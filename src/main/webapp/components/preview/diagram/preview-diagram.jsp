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
