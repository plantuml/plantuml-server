/********************
* Diagram Export JS *
*********************/

function initDiagramExport() {
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
