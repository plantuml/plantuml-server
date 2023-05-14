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
  return (el.offsetParent !== null);
}

function setVisibility(el, visibility, focus=false) {
  if (visibility) {
    el.style.removeProperty("display");
    if (focus) el.focus();
  } else {
    el.style.display = "none";
  }
}

const isMac = (function() {
	const PLATFORM = navigator?.userAgentData?.platform || navigator?.platform || "unknown";
  return PLATFORM.match("Mac");
})();


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

function requestMetadata(file) {
  return new Promise((resolve, reject) => {
    const fd = new FormData();
    fd.append("diagram", file, file.name);
    const xhr = new XMLHttpRequest();
    xhr.onreadystatechange = function() {
      if (xhr.readyState === XMLHttpRequest.DONE) {
        if (xhr.status >= 200 && xhr.status <= 300) {
          resolve(xhr.response);
        } else {
          reject({ status: xhr.status, response: xhr.response });
        }
      }
    }
    xhr.open("POST", "metadata", true);
    xhr.setRequestHeader("Accept", "application/json");
    xhr.responseType = "json";
    xhr.send(fd);
  });
}


// ==========================================================================================================
// == modal ==

const { registerModalListener, openModal, closeModal } = (function() {
  const modalListener = {};
  return {
    registerModalListener: (id, fnOpen=undefined, fnClose=undefined) => modalListener[id] = { fnOpen, fnClose },
    openModal: (id, ...args) => {
      modalListener[id]?.fnOpen?.call(...args) || setVisibility(document.getElementById(id), true, true)
    },
    closeModal: (id, ...args) => {
      modalListener[id]?.fnClose?.call(...args) || setVisibility(document.getElementById(id), false);
    },
  };
})();

function initModals() {
  document.querySelectorAll(".modal").forEach(modal => {
    modal.addEventListener("keydown", (event) => {
      if (event.key === "Escape" || event.key === "Esc") {
        event.preventDefault();
        closeModal(modal.id);
      } else if (event.key === "Enter") {
        event.preventDefault();
        const okBtn = modal.querySelector('input.ok[type="button"]');
        if (okBtn && !okBtn.disabled) {
          okBtn.click();
        }
      }
    }, false);
  });
}

function isModalOpen(id) {
  return isVisible(document.getElementById(id));
}

function closeAllModals() {
  document.querySelectorAll(".modal").forEach(modal => closeModal(modal.id));
}


// ==========================================================================================================
// == settings ==

function initSettings() {
  document.getElementById("theme").addEventListener("change", (event) => {
    const theme = event.target.value;
    const editorCreateOptionsString = document.settingsEditor.getValue();
    const replaceTheme = (theme === "dark") ? "vs" : "vs-dark";
    const substituteTheme = (theme === "dark") ? "vs-dark" : "vs";
    const regex = new RegExp('("theme"\\s*:\\s*)"' + replaceTheme + '"', "gm");
    setEditorValue(document.settingsEditor, editorCreateOptionsString.replace(regex, '$1"' + substituteTheme + '"'));
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
  setEditorValue(document.settingsEditor, JSON.stringify(document.appConfig.editorCreateOptions, null, "  "));
}

function saveSettings() {
  const appConfig = Object.assign({}, document.appConfig);
  appConfig.theme = document.getElementById("theme").value;
  appConfig.editorWatcherTimeout = document.getElementById("editorWatcherTimeout").value;
  appConfig.diagramPreviewType = document.getElementById("diagramPreviewType").value;
  appConfig.editorCreateOptions = JSON.parse(document.settingsEditor.getValue());
  broadcastSettings(appConfig);
  closeModal("settings");
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
// == diagram import ==

function openDiagramImportDialog(isOpenManually = true) {
  const diagramImportDialog = document.getElementById("diagram-import");
  setVisibility(diagramImportDialog, true, true);
  diagramImportDialog.dataset.isOpenManually = isOpenManually.toString();
}

function onDiagramImportInputChange(fileInput) {
  document.getElementById("diagram-import-error-message").innerText = "";
  document.getElementById("diagram-import-ok-btn").disabled = fileInput.files?.length < 1;
}

function initDiagramImportDiaglog() {
  const diagramImportDialog = document.getElementById("diagram-import");
  const diagramInputElement = document.getElementById("diagram-import-input");
  const errorMessageElement = document.getElementById("diagram-import-error-message");

  function closeDiagramImportDialog() {
    diagramInputElement.value = "";  // reset or clear
    onDiagramImportInputChange(diagramInputElement);
    diagramImportDialog.removeAttribute("data-is-open-manually");
    setVisibility(diagramImportDialog, false);
  }
  function checkFileLocally(file) {
    function getImageFileType({name, type}) {
      const supported = ["png", "svg"];
      // get type by mime type
      let fileType = supported.filter(t => type.toLowerCase().indexOf(t) !== -1)[0];
      if (fileType) return fileType;
      // fallback: get type by filename extension
      if (name.indexOf(".") === -1) return undefined;
      const ext = name.substring(name.lastIndexOf(".")+1).toLowerCase();
      return supported.filter(t => ext === t)[0];
    }
    function isDiagramCode({name, type}) {
      // get type by mime type
      let supported = ["plain", "text", "plantuml", "puml"];
      if (supported.filter(t => type.toLowerCase().indexOf(t) !== -1).length > 0) {
        return true;
      }
      // fallback: get type by filename extension
      if (name.indexOf(".") === -1) return false;
      const ext = name.substring(name.lastIndexOf('.')+1).toLowerCase();
      supported = ["txt", "puml", "plantuml"];
      return supported.filter(t => ext === t).length > 0;
    }

    const type = getImageFileType(file);
    const isCode = type === undefined ? isDiagramCode(file) : false;
    if (!type && !isCode) {
      errorMessageElement.innerText = "File not supported. " +
        "Only PNG and SVG diagram images as well as PlantUML code text files are supported."
    }
    return { type, isDiagramCode: isCode, valid: type || isCode };
  }

  function importDiagram(file, fileCheck) {
    function loadDiagram(code) {
      syncCodeEditor(code);
      broadcastCodeEditorChanges("file-drop", code);
    }

    diagramImportDialog.classList.add("wait");
    return new Promise((resolve, reject) => {
      if (fileCheck.type) {
        // upload diagram image, get meta data from server and load diagram from result
        requestMetadata(file).then(
          metadata => { loadDiagram(metadata.decoded); resolve(); },
          ({ response }) => { errorMessageElement.innerText = response.message || response; reject(); }
        );
      } else if (fileCheck.isDiagramCode) {
        // read code (text) file
        const reader = new FileReader();
        reader.onload = event => loadDiagram(event.target.result);
        reader.readAsText(file);
        resolve();
      } else {
        // this error should already be handled.
        errorMessageElement.innerText = "File not supported. " +
          "Only PNG and SVG diagram images as well as PlantUML code text files are supported.";
        reject();
      }
    }).then(() => closeDiagramImportDialog(), () => {}).finally(() => diagramImportDialog.classList.remove("wait"));
  }

  function onGlobalDragEnter(event) {
    event.stopPropagation();
    event.preventDefault();
    if (!isVisible(diagramImportDialog)) {
      openDiagramImportDialog(false);
    }
  }

  function onDiagramImportDragOver(event) {
    event.stopPropagation();
    event.preventDefault();
    if (event.dataTransfer !== null) {
      event.dataTransfer.dropEffect = "copy";
    }
  }
  function onDiagramImportDrop(event) {
    function stop() {
      event.stopPropagation();
      event.preventDefault();
    }
    const files = event.dataTransfer.files || event.target.files;
    if (!files || files.length < 1) {
      return stop();
    }
    const file = files[0];
    const fileCheck = checkFileLocally(file);
    if (!fileCheck.valid) {
      return stop();
    }
    if (diagramImportDialog.dataset.isOpenManually === "true") {
      return;  // let file input handle this event => no `stop()`!
    }
    // drop and go - close modal without additional ok button click
    stop();
    importDiagram(file, fileCheck);
  }

  // global drag&drop events
  window.addEventListener("dragenter", onGlobalDragEnter, false);
  // diagram import dialog drag&drop events
  diagramImportDialog.addEventListener("dragenter", event => event.target.classList.add("drop-able"), false);
  diagramImportDialog.addEventListener("dragover", onDiagramImportDragOver, false);
  diagramImportDialog.addEventListener("dragexit", event => event.target.classList.remove("drop-able"), false);
  diagramImportDialog.addEventListener("drop", onDiagramImportDrop, false);
  // ok button
  document.getElementById("diagram-import-ok-btn").addEventListener("click", () => {
    const file = diagramInputElement.files[0];  // should be always a valid file
    importDiagram(file, checkFileLocally(file));  // otherwise button should be disabled
  });
  // reset or clear file input
  diagramInputElement.value = "";
  onDiagramImportInputChange(diagramInputElement);
  // register model listeners
  registerModalListener("diagram-import", openDiagramImportDialog, closeDiagramImportDialog);
}


// ==========================================================================================================
// == diagram export ==

function initFileExportDialog() {
  const filenameInput = document.getElementById("download-name");
  const fileTypeSelect = document.getElementById("download-type");

  function openDiagramExportDialog() {
    setVisibility(document.getElementById("diagram-export"), true, true);
    const code = document.editor.getValue();
    const name = Array.from(
      code.matchAll(/^\s*@start[a-zA-Z]*\s+([a-zA-Z-_äöüÄÖÜß ]+)\s*$/gm),
      m => m[1]
    )[0] || "diagram";
    filenameInput.value = name + ".puml";
    fileTypeSelect.value = "code";
    filenameInput.focus();
  }
  function splitFilename(filename) {
    const idx = filename.lastIndexOf(".");
    if (idx < 1) {
      return { name: filename, ext: null };
    }
    if (idx === filename.length - 1) {
      return { name: filename.slice(0, -1), ext: null };
    }
    return {
        name: filename.substring(0, idx),
        ext: filename.substring(idx + 1),
    };
  }
  function getExtensionByType(type) {
    switch (type) {
      case "epstext": return "eps";
      case "code": return "puml";
      default: return type;
    }
  }
  function getTypeByExtension(ext) {
    if (!ext) return ext;
    ext = ext.toLowerCase();
    switch (ext) {
      case "puml":
      case "plantuml":
      case "code":
        return "code";
      case "ascii": return "txt"
      default: return ext;
    }
  }
  function onTypeChanged(event) {
    const type = event.target.value;
    const ext = getExtensionByType(type);
    const { name } = splitFilename(filenameInput.value);
    filenameInput.value = name + "." + ext;
  }
  function onFilenameChanged(event) {
    const { ext } = splitFilename(event.target.value);
    const type = getTypeByExtension(ext);
    if (!type) return;
    fileTypeSelect.value = type;
  }
  function downloadFile() {
    const filename = filenameInput.value;
    const type = fileTypeSelect.value;
    const link = document.createElement("a");
    link.download = filename;
    if (type === "code") {
      const code = document.editor.getValue();
      link.href = "data:," + encodeURIComponent(code);
    } else {
      if (document.appData.index !== undefined) {
        link.href = type + "/" + document.appData.index + "/" + document.appData.encodedDiagram;
      } else {
        link.href = type + "/" + document.appData.encodedDiagram;
      }
    }
    link.click();
  }

  // register modal
  registerModalListener("diagram-export", openDiagramExportDialog);
  // add listener
  filenameInput.addEventListener("change", onFilenameChanged);
  fileTypeSelect.addEventListener("change", onTypeChanged);
  document.getElementById("diagram-export-ok-btn").addEventListener("click", downloadFile);
  // add Ctrl+S or Meta+S (Mac) key shortcut to open export dialog
  window.addEventListener("keydown", event => {
    if (event.key === "s" && (isMac ? event.metaKey : event.ctrlKey)) {
      event.preventDefault();
      if (!isModalOpen("diagram-export")) {
        openDiagramExportDialog();
      }
    }
  }, false);
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

function setEditorValue(editor, text, forceMoveMarkers=undefined) {
  // replace editor value but preserve undo stack
  editor.executeEdits('', [{ range: editor.getModel().getFullModelRange(),  text, forceMoveMarkers }]);
}

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
  setEditorValue(document.editor, code);
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
  initModals();
  if (view !== "previewer") {
    initDiagramImportDiaglog();
    initFileExportDialog();
    addSavePlantumlDocumentEvent();
  }
  if (["previewer", "editor"].includes(view)) {
    hidePreview();
  }
  document.appConfig.autoRefreshState = "complete";
  document.editor?.focus();
}

function loadCodeEditor() {
  // load Monaco editor asynchron
  return new Promise((resolve, _reject) => {
    require.config({ paths: { vs: "webjars/monaco-editor/0.36.1/min/vs" } });
    require(["vs/editor/editor.main"], resolve);
  });
}

const broadcastCodeEditorChanges = (function() {
  let plantumlFeatures;
  return function(sender, code) {
    plantumlFeatures = plantumlFeatures || new PlantUmlLanguageFeatures();
    document.appConfig.autoRefreshState = "started";
    const numberOfDiagramPages = getNumberOfDiagramPagesFromCode(code);
    let index = document.appData.index;
    if (index === undefined || numberOfDiagramPages === 1) {
      index = undefined;
    } else if (index >= numberOfDiagramPages) {
      index = numberOfDiagramPages - 1;
    }
    encodeDiagram(code, (encodedDiagram) => {
      sendMessage({
        sender,
        data: { encodedDiagram, numberOfDiagramPages, index },
        synchronize: true,
      });
    });
    const model = document.editor.getModel();
    plantumlFeatures.validateCode(model)
      .then(markers => monaco.editor.setModelMarkers(model, "plantuml", markers));
  };
})();

function initializeCodeEditor() {
  // create editor model including editor watcher
  let timer = 0;
  const uri = monaco.Uri.parse("inmemory://plantuml");
  const initCodeEl = document.getElementById("initCode");
  const initCode = initCodeEl.value;
  initCodeEl.remove();
  const model = monaco.editor.createModel(initCode, "apex", uri);
  model.onDidChangeContent(() => {
    clearTimeout(timer);
    if (document.appConfig.changeEventsEnabled) {
      document.appConfig.autoRefreshState = "waiting";
      timer = setTimeout(
        () => broadcastCodeEditorChanges("editor", model.getValue()),
        document.appConfig.editorWatcherTimeout
      );
    }
  });
  // create storage service to expand suggestion documentation by default
  const storageService = {
    get() {},
    getBoolean(key) { return key === 'expandSuggestionDocs'; },
    getNumber() { return 0; },
    remove() {},
    store() {},
    onWillSaveState() {},
    onDidChangeStorage() {},
    onDidChangeValue() {},
  };
  // create editor
  document.editor = monaco.editor.create(document.getElementById("monaco-editor"), {
    model, ...document.appConfig.editorCreateOptions
  }, { storageService });
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
  window.addEventListener("keydown", function(e) {
    if (e.key === "," && (isMac ? e.metaKey : e.ctrlKey)) {
      // support Ctrl+, to open the settings
      e.preventDefault();
      if (!isModalOpen("settings")) {
        openSettings();
      }
    }
  }, false);
}


// ==========================================================================================================
// == communication ==
//
// send and receive data: {
//   sender: string = ["editor"|"url"|"paginator"|"settings"|"file-drop"],
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
