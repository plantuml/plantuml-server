/**************
* Settings JS *
***************/

function initSettings() {
  const themeElement = document.getElementById("theme");
  const diagramPreviewTypeElement = document.getElementById("diagramPreviewType");
  const editorWatcherTimeoutElement = document.getElementById("editorWatcherTimeout");

  function openSettings() {
    setVisibility(document.getElementById("settings"), true, true);
    // fill settings form
    themeElement.value = document.appConfig.theme;
    diagramPreviewTypeElement.value = document.appConfig.diagramPreviewType;
    editorWatcherTimeoutElement.value = document.appConfig.editorWatcherTimeout;
    setEditorValue(document.settingsEditor, JSON.stringify(document.appConfig.editorCreateOptions, null, "  "));
  }
  function saveSettings() {
    const appConfig = Object.assign({}, document.appConfig);
    appConfig.theme = themeElement.value;
    appConfig.editorWatcherTimeout = editorWatcherTimeoutElement.value;
    appConfig.diagramPreviewType = diagramPreviewTypeElement.value;
    appConfig.editorCreateOptions = JSON.parse(document.settingsEditor.getValue());
    updateConfig(appConfig);
    closeModal("settings");
  }
  function onThemeChanged(event) {
    const theme = event.target.value;
    const editorCreateOptionsString = document.settingsEditor.getValue();
    const replaceTheme = (theme === "dark") ? "vs" : "vs-dark";
    const substituteTheme = (theme === "dark") ? "vs-dark" : "vs";
    const regex = new RegExp('("theme"\\s*:\\s*)"' + replaceTheme + '"', "gm");
    setEditorValue(document.settingsEditor, editorCreateOptionsString.replace(regex, '$1"' + substituteTheme + '"'));
  }

  // create app config monaco editor
  document.settingsEditor = monaco.editor.create(document.getElementById("settings-monaco-editor"), {
		language: "json", ...document.appConfig.editorCreateOptions
	});
  // add listeners
  themeElement.addEventListener("change", onThemeChanged);
  document.getElementById("settings-ok-btn").addEventListener("click", saveSettings);
  // support Ctrl+, to open the settings
  window.addEventListener("keydown", function(e) {
    if (e.key === "," && (isMac ? e.metaKey : e.ctrlKey)) {
      e.preventDefault();
      if (!isModalOpen("settings")) {
        openSettings();
      }
    }
  }, false);
  // register model listeners
  registerModalListener("settings", openSettings);
}
