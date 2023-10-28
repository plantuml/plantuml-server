<div id="diagram-export" class="modal" style="display: none;" tabindex="-1">
  <div class="modal-content flex-rows">
    <div class="modal-header">
      <h2>Export Diagram</h2>
      <div class="hr"></div>
    </div>
    <div class="modal-main flex-main">
      <div class="label-input-pair flex-columns">
        <label for="download-name">Diagram name:</label>
        <input class="flex-main" id="download-name" value="diagram.puml" />
      </div>
      <div class="label-input-pair flex-columns">
        <label for="download-type">Diagram type:</label>
        <select class="flex-main" id="download-type" name="download-type">
          <option value="txt">ASCII Art</option>
          <option value="base64">Base64</option>
          <option value="eps">EPS</option>
          <option value="epstext">EPS Text</option>
          <option value="map">MAP</option>
          <option value="pdf">PDF</option>
          <option value="code" selected>PlantUML source code</option>
          <option value="png">PNG</option>
          <option value="svg">SVG</option>
        </select>
      </div>
    </div>
    <div class="modal-footer">
      <input id="diagram-export-ok-btn" class="ok" type="button" value="Export" />
      <input class="cancel" type="button" value="Cancel" onclick="closeModal('diagram-export')" />
    </div>
  </div>
</div>
