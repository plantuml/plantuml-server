/*****************
* Configurations *
******************/

const { applyConfig, updateConfig } = (function() {
  const DEFAULT_APP_CONFIG = {
    changeEventsEnabled: true,
    // `autoRefreshState` is mostly used for unit testing puposes.
    // states: disabled | waiting | started | syncing | complete
    autoRefreshState: "disabled",
    theme: undefined,  // dark | light  (will be set via `initTheme` if undefined)
    diagramPreviewType: "png",
    editorWatcherTimeout: 500,
    editorCreateOptions: {
      automaticLayout: true,
      fixedOverflowWidgets: true,
      minimap: { enabled: false },
      scrollbar: { alwaysConsumeMouseWheel: false },
      scrollBeyondLastLine: false,
      tabSize: 2,
      theme: "vs",  // "vs-dark"
    }
  };

  function applyConfig() {
    setTheme(document.appConfig.theme);
    document.editor?.updateOptions(document.appConfig.editorCreateOptions);
    document.settingsEditor?.updateOptions(document.appConfig.editorCreateOptions);
  }
  function updateConfig(appConfig) {
    localStorage.setItem("document.appConfig", JSON.stringify(appConfig));
    sendMessage({
      sender: "config",
      data: { appConfig },
      synchronize: true,
    });
  }

  document.appConfig = Object.assign({}, window.opener?.document.appConfig);
  if (Object.keys(document.appConfig).length === 0) {
    document.appConfig = JSON.parse(localStorage.getItem("document.appConfig")) || DEFAULT_APP_CONFIG;
  }

  return { applyConfig, updateConfig };
})();
