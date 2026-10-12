/****************
* Theme Helpers *
*****************/

function setTheme(theme) {
  document.documentElement.setAttribute("data-theme", theme);
}

function initTheme() {
  function getBrowserThemePreferences() {
    if (window.matchMedia("(prefers-color-scheme: dark)").matches) {
      return "dark";
    }
    if (window.matchMedia("(prefers-color-scheme: light)").matches) {
      return "light";
    }
    return undefined;
  }
  function changeEditorThemeSettingIfNecessary(theme) {
    if (theme === "dark" && document.appConfig.editorCreateOptions.theme === "vs") {
      document.appConfig.editorCreateOptions.theme = "vs-dark";
    }
    if (theme === "light" && document.appConfig.editorCreateOptions.theme === "vs-dark") {
      document.appConfig.editorCreateOptions.theme = "vs";
    }
  }
  function onMediaColorPreferencesChanged(event) {
    const theme = event.matches ? "dark" : "light";
    document.appConfig.uiTheme = theme;
    if (["dark", "light"].includes(document.appConfig.umlColorMapper)) {
      // if uml theme is not explicitly set to specific color map use media color
      document.appConfig.umlColorMapper = theme;
    }
    changeEditorThemeSettingIfNecessary(theme);
    updateConfig(document.appConfig);
  }
  // set theme to last saved settings or browser preference or "light"
  document.appConfig.uiTheme = document.appConfig.uiTheme || getBrowserThemePreferences() || "light";
  document.appConfig.umlColorMapper = document.appConfig.umlColorMapper || getBrowserThemePreferences() || "light";
  setTheme(document.appConfig.uiTheme);
  changeEditorThemeSettingIfNecessary(document.appConfig.uiTheme);
  // listen to browser change event
  window.matchMedia("(prefers-color-scheme: dark)").addEventListener("change", onMediaColorPreferencesChanged);
}
