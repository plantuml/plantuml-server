<div id="diagram-import" class="modal" style="display: none;" tabindex="-1">
  <div class="modal-content flex-rows">
    <div class="modal-header">
      <h2>Import Diagram</h2>
      <div class="hr"></div>
    </div>
    <div class="modal-main flex-main">
      <input id="diagram-import-input" type="file" name="diagram" />
      <p id="diagram-import-error-message" class="error-message"></p>
    </div>
    <div class="modal-footer">
      <input id="diagram-import-ok-btn" class="ok" type="button" value="Import" disabled />
      <input id="diagram-import-cancel-btn" class="cancel" type="button" value="Cancel" onclick="closeModal('diagram-import');" />
    </div>
  </div>
</div>
