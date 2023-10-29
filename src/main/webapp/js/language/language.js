/************************************************
* Monaco Editor PlantUML Language Features Base *
*************************************************/
"use strict";

/**
 * Monaco Editor PlantUML Language Features.
 *
 * @param {boolean} [initialize]  `true` if all default validation and code completion
 *                                functions should be activated; otherwise `false`
 *
 * @example
 * ```js
 * plantumlFeatures = new PlantUmlLanguageFeatures();
 * const model = monaco.editor.createModel(initCode, "apex", uri);
 * model.onDidChangeContent(() => plantumlFeatures.validateCode(model));
 * ```
 */
const PlantUmlLanguageFeatures = function(initialize = true) {
  if (initialize) {
    // initialize all validation and code completion methods
    this.addStartEndValidationListeners();
    this.registerThemeCompletion();
    this.registerIconCompletion();
    this.registerEmojiCompletion();
  }
};

PlantUmlLanguageFeatures.baseUrl = "";
PlantUmlLanguageFeatures.setBaseUrl = function(baseUrl) {
  if (baseUrl === null || baseUrl === undefined) {
    baseUrl = "";
  } else if (baseUrl !== "") {
    if (baseUrl.slice(-1) !== "/") {
      baseUrl = baseUrl + "/";  // add tailing "/"
    }
  }
  PlantUmlLanguageFeatures.baseUrl = baseUrl;
}

PlantUmlLanguageFeatures.languageSelector = ["apex", "plantuml"];
PlantUmlLanguageFeatures.setLanguageSelector = function(languageSelector) {
  PlantUmlLanguageFeatures.languageSelector = languageSelector;
}

PlantUmlLanguageFeatures.makeRequest = function(
  method,
  url,
  {
    data = null,
    headers = { "Content-Type": "text/plain" },
    responseType = "json",
    baseUrl = PlantUmlLanguageFeatures.baseUrl,
  } = {}
) {
  function getResolveResponse(xhr) {
    return responseType === "json" ? xhr.response : xhr.responseText;
  }
  function getRejectResponse(xhr) {
    return responseType === "json"
      ? { status: xhr.status, response: xhr.response }
      : { status: xhr.status, responseText: xhr.responseText };
  }
  const targetUrl = !baseUrl ? url : baseUrl.replace(/\/*$/g, "/") + url;
  return new Promise((resolve, reject) => {
    const xhr = new XMLHttpRequest();
    xhr.onreadystatechange = function() {
      if (xhr.readyState === XMLHttpRequest.DONE) {
        if (xhr.status >= 200 && xhr.status <= 300) {
          resolve(getResolveResponse(xhr));
        } else {
          reject(getRejectResponse(xhr));
        }
      }
    }
    xhr.open(method, targetUrl, true);
    xhr.responseType = responseType;
    headers && Object.keys(headers).forEach(key => xhr.setRequestHeader(key, headers[key]));
    xhr.send(data);
  });
}

PlantUmlLanguageFeatures.absolutePath = function(path) {
  if (path.startsWith("http")) return path;
  if (path.startsWith("//")) return window.location.protocol + path;
  if (path.startsWith("/")) return window.location.origin + path;

  if (path.slice(0, 2) == "./") path = path.slice(2);
  let base = (document.querySelector("base") || {}).href || window.location.origin;
  if (base.slice(-1) == "/") base = base.slice(0, -1);
  return base + "/" + path;
}
