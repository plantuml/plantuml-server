<div id="settings" class="modal" style="display: none;" tabindex="-1">
  <div class="modal-content flex-rows">
    <div class="modal-header">
      <h2>Settings</h2>
      <div class="hr"></div>
    </div>
    <div class="modal-main flex-main">
      <div class="label-input-pair flex-columns">
        <label for="theme">Theme:</label>
        <select class="flex-main" id="theme" name="theme">
          <option value="light">Light</option>
          <option value="dark">Dark</option>
        </select>
      </div>
      <div class="label-input-pair flex-columns">
        <label for="diagramPreviewType">Diagram Preview Type:</label>
        <select class="flex-main" id="diagramPreviewType" name="diagramPreviewType">
          <option value="png">PNG</option>
          <option value="svg">SVG</option>
          <option value="txt">ASCII Art</option>
          <option value="pdf">PDF</option>
        </select>
      </div>
      <div class="label-input-pair flex-columns">
        <label for="editorWatcherTimeout">Editor Watcher Timeout:</label>
        <input class="flex-main" id="editorWatcherTimeout" type="number" pattern="[1-9]+[0-9]*" value="" />
      </div>
      <div class="label-input-pair flex-main">
        <label for="editorCreateOptions">Monaco Editor Create Options:</label>
        <br />
        <div id="settings-monaco-editor"></div>
      </div>
    </div>
    <div class="modal-footer">
      <input id="settings-ok-btn" class="ok" type="button" value="Save" />
      <input class="cancel" type="button" value="Cancel" onclick="closeModal('settings');" />
    </div>
  </div>
</div>
