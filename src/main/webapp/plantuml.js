/*************************
* PlantUMLServlet script *
**************************/

// ==========================================================================================================
// == global configuration ==

document.appConfig = Object.assign({}, window.opener?.document.appConfig);
if (Object.keys(document.appConfig).length === 0) {
  document.appConfig = JSON.parse(localStorage.getItem("document.appConfig")) || {
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
}


// ==========================================================================================================
// == DOM helpers ==

function removeChildren(el) {
  if (el.replaceChildren) {
    el.replaceChildren();
  } else {
    el.innerHTML = "";
  }
}

function isVisible(el) {
  // `offsetParent` returns `null` if the element, or any of its parents,
  // is hidden via the display style property.
  // see: https://developer.mozilla.org/en-US/docs/Web/API/HTMLElement/offsetParent
  return (el.offsetParent === null)
}

function setVisibility(el, visibility, focus=false) {
  if (visibility) {
    el.style.removeProperty("display");
    if (focus) el.focus();
  } else {
    el.style.display = "none";
  }
}


// ==========================================================================================================
// == URL helpers ==

function resolvePath(path) {
  // also see `PlantUmlLanguageFeatures.resolvePath(path)`
  if (path.startsWith("http")) return path;
  if (path.startsWith("/")) return window.location.origin + path;

  if (path.slice(0, 2) == "./") path = path.slice(2);
  let base = (document.querySelector("base") || {}).href || window.location.origin;
  if (base.slice(-1) == "/") base = base.slice(0, -1);
  return base + "/" + path;
}

function prepareUrl(url) {
  if (!(url instanceof URL)) {
    url = new URL(resolvePath(url));
  }
  // pathname excluding context path
  let base = new URL((document.querySelector("base") || {}).href || window.location.origin).pathname;
  if (base.slice(-1) === "/") base = base.slice(0, -1);
  const pathname = url.pathname.startsWith(base) ? url.pathname.slice(base.length) : url.pathname;
  // same as `UrlDataExtractor.URL_PATTERN`
  const regex = /\/\w+(?:\/(?<idx>\d+))?(?:\/(?<encoded>[^\/]+))?\/?$/gm;
  const match = regex.exec(pathname);
  return [ url, pathname, match ];
}

function analyseUrl(url) {
  let match;
  [url, _, match] = prepareUrl(url);
  return {
    index: match.groups.idx,
    encodedDiagram: match.groups.encoded || url.searchParams.get("url"),
  };
}

function replaceUrl(url, encodedDiagram, index) {
  let oldPathname, match;
  [url, oldPathname, match] = prepareUrl(url);
  let pathname = oldPathname.slice(1);
  pathname = pathname.slice(0, pathname.indexOf("/"));
  if (index && index >= 0) pathname += "/" + index;
  if (match.groups.encoded) pathname += "/" + encodedDiagram;
  if (oldPathname.slice(-1) === "/") pathname += "/";
  url.pathname = new URL(resolvePath(pathname)).pathname;
  if (url.searchParams.get("url")) {
    url.searchParams.set("url", encodedDiagram);
  }
  return { url, pathname };
}

function buildUrl(serletpath, encodedDiagram, index) {
  let pathname = serletpath;
  if (index && index >= 0) pathname += "/" + index;
  pathname += "/" + encodedDiagram;
  return pathname;
}


// ==========================================================================================================
// == clipboard helpers ==

function copyUrlToClipboard() {
  const input = document.getElementById("url");
  input.focus();
  input.select();
  navigator.clipboard?.writeText(input.value).catch(() => {});
}

function copyCodeToClipboard() {
  const range = document.editor.getModel().getFullModelRange();
  document.editor.focus();
  document.editor.setSelection(range);
  const code = document.editor.getValue();
  navigator.clipboard?.writeText(code).catch(() => {});
}


// ==========================================================================================================
// == theme helpers ==

function getBrowserThemePreferences() {
  if (window.matchMedia("(prefers-color-scheme: dark)").matches) {
    return "dark";
  }
  if (window.matchMedia("(prefers-color-scheme: light)").matches) {
    return "light";
  }
  return undefined;
}

function setTheme(theme) {
  document.documentElement.setAttribute("data-theme", theme);
}


// ==========================================================================================================
// == asynchron server calls ==

function call(method, url, data, callback) {
  const xhr = new XMLHttpRequest();
  xhr.onreadystatechange = function() {
    if (xhr.readyState == 4 && xhr.status == 200) {
      callback(xhr.responseText);
    }
  }
  xhr.open(method, url, true);
  xhr.setRequestHeader("Content-Type", "text/plain");
  xhr.send(data);
}

function decodeDiagram(encodedDiagram, callback) {
  call("GET", "coder/" + encodedDiagram, null, callback);
}

function encodeDiagram(diagram, callback) {
  call("POST", "coder", diagram, callback);
}

function requestDiagram(type, encodedDiagram, index, callback) {
  call("GET", buildUrl(type, encodedDiagram, index), null, callback);
}

function requestDiagramMap(encodedDiagram, index, callback) {
  requestDiagram("map", encodedDiagram, index, callback);
}


// ==========================================================================================================
// == settings ==

function initSettings() {
  document.getElementById("settings").addEventListener("keydown", (event) => {
    event.preventDefault();
    console.log(event);
    if (event.key === "Escape" || event.key === "Esc") {
      closeSettings();
    }
  }, false);
  document.getElementById("theme").addEventListener("change", (event) => {
    const theme = event.target.value;
    const editorCreateOptionsString = document.settingsEditor.getValue();
    const replaceTheme = (theme === "dark") ? "vs" : "vs-dark";
    const substituteTheme = (theme === "dark") ? "vs-dark" : "vs";
    const regex = new RegExp('("theme"\\s*:\\s*)"' + replaceTheme + '"', "gm");
    document.settingsEditor.getModel().setValue(
      editorCreateOptionsString.replace(regex, '$1"' + substituteTheme + '"')
    );
  });
  document.settingsEditor = monaco.editor.create(document.getElementById("settings-monaco-editor"), {
		language: "json", ...document.appConfig.editorCreateOptions
	});
}

function openSettings() {
  setVisibility(document.getElementById("settings"), true, true);
  if (!document.settingsEditor) {
    initSettings();
  }
  // fill settings form
  document.getElementById("theme").value = document.appConfig.theme;
  document.getElementById("diagramPreviewType").value = document.appConfig.diagramPreviewType;
  document.getElementById("editorWatcherTimeout").value = document.appConfig.editorWatcherTimeout;
  document.settingsEditor.getModel().setValue(
    JSON.stringify(document.appConfig.editorCreateOptions, null, "  ")
  );
}

function closeSettings() {
  setVisibility(document.getElementById("settings"), false);
}

function saveSettings() {
  const appConfig = Object.assign({}, document.appConfig);
  appConfig.theme = document.getElementById("theme").value;
  appConfig.editorWatcherTimeout = document.getElementById("editorWatcherTimeout").value;
  appConfig.diagramPreviewType = document.getElementById("diagramPreviewType").value;
  appConfig.editorCreateOptions = JSON.parse(document.settingsEditor.getValue());
  broadcastSettings(appConfig);
  closeSettings();
}

function broadcastSettings(appConfig) {
  localStorage.setItem("document.appConfig", JSON.stringify(appConfig));
  sendMessage({
    sender: "settings",
    data: { appConfig },
    synchronize: true,
  });
}

function applySettings() {
  setTheme(document.appConfig.theme);
  document.editor?.updateOptions(document.appConfig.editorCreateOptions);
  document.settingsEditor?.updateOptions(document.appConfig.editorCreateOptions);
}


// ==========================================================================================================
// == dock (pop in) and undock (pop out) previewer ==

function getDockUndockElements() {
  return {
    "btnUndock": document.getElementById("btn-undock"),
    "btnDock": document.getElementById("btn-dock"),
    "editorContainer": document.getElementById("editor-main-container"),
    "previewContainer": document.getElementById("previewer-main-container"),
  };
}

function hidePreview() {
  const elements = getDockUndockElements();
  setVisibility(elements.btnUndock, false);
  // if not opened via button and therefore a popup, `window.close` won't work
  setVisibility(elements.btnDock, window.opener);
  if (elements.editorContainer) elements.editorContainer.style.width = "100%";
  if (elements.previewContainer) setVisibility(elements.previewContainer, false);
}

function showPreview() {
  const elements = getDockUndockElements();
  setVisibility(elements.btnUndock, true);
  setVisibility(elements.btnDock, false);
  if (elements.editorContainer) elements.editorContainer.style.removeProperty("width");
  if (elements.previewContainer) setVisibility(elements.previewContainer, true);
}

function undock() {
  const url = new URL(window.location.href);
  url.searchParams.set("view", "previewer");
  const previewer = window.open(url, "PlantUML Diagram Previewer", "popup");
  if (previewer) {
    previewer.onbeforeunload = showPreview;
    hidePreview();
  }
}


// ==========================================================================================================
// == paginator ==

function getNumberOfDiagramPagesFromCode(code) {
  // count `newpage` inside code
  // known issue: a `newpage` starting in a newline inside a multiline comment will also be counted
  return code.match(/^\s*newpage\s?.*$/gm)?.length + 1 || 1;
}

function updateNumberOfPagingElements(paginator, pages) {
  // remove elements (buttons) if there are to many
  while (paginator.childElementCount > pages) {
    paginator.removeChild(paginator.lastChild)
  }
  // add elements (buttons) if there are to less
  while (paginator.childElementCount < pages) {
    const radioBtn = document.createElement("input");
    radioBtn.name = "paginator";
    radioBtn.type = "radio";
    radioBtn.value = paginator.childElementCount;
    radioBtn.addEventListener("click", (event) => {
      sendMessage({
        sender: "paginator",
        data: { index: event.target.value },
        synchronize: true,
      });
    });
    paginator.appendChild(radioBtn);
  }
}

function updatePaginator() {
  const paginator = document.getElementById("paginator");
  const pages = document.appData.numberOfDiagramPages;
  if (pages > 1) {
    updateNumberOfPagingElements(paginator, pages);
    setVisibility(paginator, true);
  } else {
    setVisibility(paginator, false);
  }
}

function updatePaginatorSelection() {
  const paginator = document.getElementById("paginator");
  const index = document.appData.index;
  if (index === undefined) {
    for (const node of paginator.childNodes) {
      node.checked = false;
    }
  } else {
    paginator.childNodes[index].checked = true;
  }
}


// ==========================================================================================================
// == sync data ==

function updateDiagramMap(mapString, mapEl) {
  const mapBtn = document.getElementById("map-diagram-link");
  mapEl = mapEl || document.getElementById("plantuml_map");
  if (mapString) {
    const div = document.createElement("div");
    div.innerHTML = mapString;
    mapEl.parentNode.replaceChild(div.firstChild, mapEl);
    setVisibility(mapBtn, true);
  } else {
    removeChildren(mapEl);
    setVisibility(mapBtn, false);
  }
}

function updateSvgDiagram(svgString, svgEl) {
  svgEl = svgEl || document.getElementById("diagram-svg");
  const div = document.createElement("div");
  div.innerHTML = svgString;
  const newSvg = div.querySelector("svg");
  newSvg.id = "diagram-svg";
  svgEl.parentNode.replaceChild(newSvg, svgEl);
}

function updateTxtDiagram(txtString, txtEl) {
  txtEl = txtEl || document.getElementById("diagram-txt");
  txtEl.innerHTML = txtString;
}

function syncDiagram(type, encodedDiagram, index) {
  const container = document.getElementById("diagram");
  const png = document.getElementById("diagram-png");
  const map = document.getElementById("plantuml_map");
  const svg = document.getElementById("diagram-svg");
  const txt = document.getElementById("diagram-txt");
  const pdf = document.getElementById("diagram-pdf");

  return new Promise((resolve, reject) => {
    if (type === "png") {
      png.src = buildUrl(type, encodedDiagram, index);
      requestDiagramMap(encodedDiagram, index, (mapString) => {
        updateDiagramMap(mapString, map);
        resolve();
      });
    } else if (type === "svg") {
      requestDiagram(type, encodedDiagram, index, (svgString) => {
        updateSvgDiagram(svgString, svg);
        resolve();
      });
    } else if (type === "txt") {
      requestDiagram(type, encodedDiagram, index, (svgString) => {
        updateTxtDiagram(svgString, txt);
        resolve();
      });
    } else if (type === "pdf") {
      pdf.data = buildUrl(type, encodedDiagram, index);
      resolve();
    } else {
      (console.error || console.log)("unknown diagram type:", type);
      reject();
    }
  }).then(() => {
    container.setAttribute("data-diagram-type", type);
    setVisibility(png, type === "png");
    setVisibility(map, type === "png");
    setVisibility(svg, type === "svg");
    setVisibility(txt, type === "txt");
    setVisibility(pdf, type === "pdf");
  });
}

function syncUrlTextInput(encodedDiagram, index) {
  const target = document.getElementById("url");
  document.appConfig.changeEventsEnabled = false;
  target.value = resolvePath(buildUrl("png", encodedDiagram, index));
  target.title = target.value;
  document.appConfig.changeEventsEnabled = true;
}

function syncCodeEditor(code) {
  document.appConfig.changeEventsEnabled = false;
  document.editor.getModel().setValue(code);
  document.appConfig.changeEventsEnabled = true;
}

function syncBrowserHistory(encodedDiagram, index) {
  const url = replaceUrl(window.location.href, encodedDiagram, index).url;
  history.replaceState(history.stat, document.title, url);
}

function syncStaticPageData(includePaginatorUpdates) {
  document.appConfig.autoRefreshState = "syncing";
  const encodedDiagram = document.appData.encodedDiagram;
  const index =  document.appData.index;
  return Promise.all([
    // update URL input
    new Promise((resolve, _reject) => {
      if (document.getElementById("url")) {
        syncUrlTextInput(encodedDiagram, index);
      }
      resolve();
    }),
    // update diagram image
    syncDiagram(document.appConfig.diagramPreviewType, encodedDiagram, index),
    // update external diagram links
    new Promise((resolve, _reject) => {
      for (let target of document.getElementsByClassName("diagram-link")) {
        target.href = buildUrl(target.dataset.imgType, encodedDiagram, index);
      }
      resolve();
    }),
    // update paginator
    new Promise((resolve, _reject) => {
      if (includePaginatorUpdates) {
        updatePaginator();
        updatePaginatorSelection();
      }
      resolve();
    }),
    // update browser url as well as the browser history
    new Promise((resolve, _reject) => {
      syncBrowserHistory(encodedDiagram, index);
      resolve();
    }),
  ]).then(() => {
    // set auto refresh state to complete
    document.appConfig.autoRefreshState = "complete";
  });
}


// ==========================================================================================================
// == initialize app ==

async function initializeApp(view) {
  await loadCodeEditor();
  if (view !== "previewer") {
    initializeCodeEditor();
    initializeUrlInput();
  }
  initializeAppData();
  initTheme();
  await initializeDiagram();
  initializePaginator();
  if (view !== "previewer") {
    addSavePlantumlDocumentEvent();
  }
  if (["previewer", "editor"].includes(view)) {
    hidePreview();
  }
  document.appConfig.autoRefreshState = "complete";
}

function loadCodeEditor() {
  // load Monaco editor asynchron
  return new Promise((resolve, _reject) => {
    require.config({ paths: { vs: "webjars/monaco-editor/0.36.1/min/vs" } });
    require(["vs/editor/editor.main"], resolve);
  });
}

function initializeCodeEditor() {
  // create editor model including editor watcher
  let timer = 0;
  const uri = monaco.Uri.parse("inmemory://plantuml");
  const initCodeEl = document.getElementById("initCode");
  const initCode = initCodeEl.value;
  initCodeEl.remove();
  const plantumlFeatures = new PlantUmlLanguageFeatures();
  const model = monaco.editor.createModel(initCode, "apex", uri);
  model.onDidChangeContent(() => {
    clearTimeout(timer);
    if (document.appConfig.changeEventsEnabled) {
      document.appConfig.autoRefreshState = "waiting";
      timer = setTimeout(() => {
        document.appConfig.autoRefreshState = "started";
        const code = model.getValue();
        const numberOfDiagramPages = getNumberOfDiagramPagesFromCode(code);
        let index = document.appData.index;
        if (index === undefined || numberOfDiagramPages === 1) {
          index = undefined;
        } else if (index >= numberOfDiagramPages) {
          index = numberOfDiagramPages - 1;
        }
        encodeDiagram(code, (encodedDiagram) => {
          sendMessage({
            sender: "editor",
            data: { encodedDiagram, numberOfDiagramPages, index },
            synchronize: true,
          });
        });
        plantumlFeatures.validateCode(model)
          .then(markers => monaco.editor.setModelMarkers(model, "plantuml", markers));
      }, document.appConfig.editorWatcherTimeout);
    }
  });
  // create editor
  document.editor = monaco.editor.create(document.getElementById("monaco-editor"), {
    model, ...document.appConfig.editorCreateOptions
  });
  // sometimes the monaco editor has resize problems
  document.addEventListener("resize", () => document.editor.layout());
}

function initializeUrlInput() {
  // resolve relative path inside url input once
  const urlInput = document.getElementById("url");
  urlInput.value = resolvePath(urlInput.value);
  urlInput.title = urlInput.value;

  // update editor and everything else if the URL input is changed
  urlInput.addEventListener("change", (event) => {
    if (document.appConfig.changeEventsEnabled) {
      document.appConfig.autoRefreshState = "started";
      const analysedUrl = analyseUrl(event.target.value);
      decodeDiagram(analysedUrl.encodedDiagram, (code) => {
        syncCodeEditor(code);
        sendMessage({
          sender: "url",
          data: {
            encodedDiagram: analysedUrl.encodedDiagram,
            index: analysedUrl.index,
          },
          synchronize: true,
        });
      });
    }
  });
}

function initializeAppData() {
  const analysedUrl = analyseUrl(window.location.href);
  const code = document.editor?.getValue();
  document.appData = Object.assign({}, window.opener?.document.appData);
  if (Object.keys(document.appData).length === 0) {
    document.appData = {
      encodedDiagram: analysedUrl.encodedDiagram,
      index: analysedUrl.index,
      numberOfDiagramPages: (code) ? getNumberOfDiagramPagesFromCode(code) : 1,
    };
  }
}

function initTheme() {
  function changeEditorThemeSettingIfNecessary(theme) {
    if (theme === "dark" && document.appConfig.editorCreateOptions.theme === "vs") {
      document.appConfig.editorCreateOptions.theme = "vs-dark";
    }
    if (theme === "light" && document.appConfig.editorCreateOptions.theme === "vs-dark") {
      document.appConfig.editorCreateOptions.theme = "vs";
    }
  }
  // set theme to last saved settings or browser preference or "light"
  document.appConfig.theme = document.appConfig.theme || getBrowserThemePreferences() || "light";
  setTheme(document.appConfig.theme);
  changeEditorThemeSettingIfNecessary(document.appConfig.theme);
  // listen to browser change event
  window.matchMedia("(prefers-color-scheme: dark)").addEventListener("change", event => {
    const theme = event.matches ? "dark" : "light";
    document.appConfig.theme = theme
    changeEditorThemeSettingIfNecessary(theme);
    broadcastSettings(document.appConfig);
  });
}

function initializeDiagram() {
  if (document.appConfig.diagramPreviewType === "png") {
    return Promise.resolve();  // png is initialized by default
  }
  return syncDiagram(
    document.appConfig.diagramPreviewType,
    document.appData.encodedDiagram,
    document.appData.index
  );
}

function initializePaginator() {
  if (document.appData.numberOfDiagramPages > 1) {
    updatePaginator();
    updatePaginatorSelection();
  }
}

function addSavePlantumlDocumentEvent() {
  const PLATFORM = navigator?.userAgentData?.platform || navigator?.platform || "unknown";
  document.addEventListener("keydown", function(e) {
    if (e.key === "s" && (PLATFORM.match("Mac") ? e.metaKey : e.ctrlKey)) {
      // support Ctrl+S to download diagram
      e.preventDefault();
      const code = document.editor.getValue();
      const name = Array.from(
        code.matchAll(/^\s*@start[a-zA-Z]*\s+([a-zA-Z-_äöüÄÖÜß ]+)\s*$/gm),
        m => m[1]
      )[0] || "diagram";
      // download via link
      const link = document.createElement("a");
      link.download = name + ".puml";
      link.href = "data:," + encodeURIComponent(code);
      link.click();
    }
    if (e.key === "," && (PLATFORM.match("Mac") ? e.metaKey : e.ctrlKey)) {
      // support Ctrl+, to open the settings
      e.preventDefault();
      if (document.getElementById("settings")?.style?.display === "none") {
        openSettings();
      }
    }
  }, false);
}


// ==========================================================================================================
// == communication ==
//
// send and receive data: {
//   sender: string = ["editor"|"url"|"paginator"|"settings"],
//   data: {
//     encodedDiagram: string | undefined,
//     index: integer | undefined,
//     numberOfDiagramPages: integer | undefined,
//     appConfig: object | undefined
//   } |  undefined,
//   synchronize: boolean = false,
//   reload: boolean = false,  // reload page
//   force: boolean = false  // force synchronize or reload
// }

function sendMessage(data) {
  (new BroadcastChannel("plantuml-server")).postMessage(data, window.location.origin);
}

function updateReceiveMessageData(data) {
  if (!data || Object.keys(data).length === 0) return {};

  const changedFlags = {};
  if ("encodedDiagram" in data && data.encodedDiagram !== document.appData.encodedDiagram) {
    document.appData.encodedDiagram = data.encodedDiagram;
    changedFlags.diagram = true;
  }
  if ("index" in data && data.index !== document.appData.index) {
    document.appData.index = data.index;
    changedFlags.index = true;
  }
  if ("numberOfDiagramPages" in data && data.numberOfDiagramPages !== document.appData.numberOfDiagramPages) {
    document.appData.numberOfDiagramPages = data.numberOfDiagramPages;
    changedFlags.numberOfDiagramPages = true;
  }
  if ("appConfig" in data && data.appConfig !== document.appConfig) {
    document.appConfig = data.appConfig;
    changedFlags.appConfig = true;
  }
  return changedFlags;
}

async function receiveMessage(event) {
  const data = event.data.data;
  const force = event.data.force || false;
  const changedFlags = updateReceiveMessageData(data);
  if (event.data.synchronize === true) {
    if (force || changedFlags.diagram || changedFlags.index || changedFlags.appConfig) {
      await syncStaticPageData(false);
    }
    if (force || changedFlags.numberOfDiagramPages) {
      updatePaginator();
    }
    if (force || changedFlags.numberOfDiagramPages || changedFlags.index) {
      updatePaginatorSelection();
    }
    if (changedFlags.appConfig) {
      applySettings();
    }
  }
  if (event.data.reload === true) {
    window.location.reload();
  }
}


// ==========================================================================================================
// == main entry ==

window.onload = function() {
  const view = new URL(window.location.href).searchParams.get("view")?.toLowerCase();
  initializeApp(view);

  // broadcast channel
  const bc = new BroadcastChannel("plantuml-server");
  bc.onmessage = receiveMessage;
};
