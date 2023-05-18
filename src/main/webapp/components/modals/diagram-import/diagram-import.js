/********************
* Diagram Import JS *
*********************/

function initDiagramImport() {
  const dialogElement = document.getElementById("diagram-import");
  const fileInput = document.getElementById("diagram-import-input");
  const okButton = document.getElementById("diagram-import-ok-btn");
  const errorMessageElement = document.getElementById("diagram-import-error-message");

  function openDialog(isOpenManually = true) {
    setVisibility(dialogElement, true, true);
    dialogElement.dataset.isOpenManually = isOpenManually.toString();
    // reset or clear file input
    fileInput.value = "";
    onFileInputChange(fileInput);
  }
  function closeDialog() {
    fileInput.value = "";  // reset or clear
    onFileInputChange(fileInput);
    dialogElement.removeAttribute("data-is-open-manually");
    setVisibility(dialogElement, false);
  }

  function onFileInputChange(fileInput) {
    errorMessageElement.innerText = "";
    okButton.disabled = fileInput.files?.length < 1;
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
      setEditorValue(document.editor, code);
    }
    function requestMetadata(file) {
      const fd = new FormData();
      fd.append("diagram", file, file.name);
      return makeRequest("POST", "metadata", {
        data: fd,
        responseType: "json",
        headers: { "Accept": "application/json" },
      });
    }

    dialogElement.classList.add("wait");
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
    }).then(() => closeDialog(), () => {}).finally(() => dialogElement.classList.remove("wait"));
  }

  function onGlobalDragEnter(event) {
    event.stopPropagation();
    event.preventDefault();
    if (!isVisible(dialogElement)) {
      openDialog(false);
    }
  }

  function onFileInputDragOver(event) {
    event.stopPropagation();
    event.preventDefault();
    if (event.dataTransfer !== null) {
      event.dataTransfer.dropEffect = "copy";
    }
  }
  function onFileInputDrop(event) {
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
    if (dialogElement.dataset.isOpenManually === "true") {
      return;  // let file input handle this event => no `stop()`!
    }
    // drop and go - close modal without additional ok button click
    stop();
    importDiagram(file, fileCheck);
  }

  // global drag&drop events
  window.addEventListener("dragenter", onGlobalDragEnter, false);
  // diagram import dialog drag&drop events
  fileInput.addEventListener("dragenter", event => event.target.classList.add("drop-able"), false);
  fileInput.addEventListener("dragover", onFileInputDragOver, false);
  fileInput.addEventListener("dragexit", event => event.target.classList.remove("drop-able"), false);
  fileInput.addEventListener("drop", onFileInputDrop, false);
  fileInput.addEventListener("change", event => onFileInputChange(event.target));
  // ok button
  okButton.addEventListener("click", () => {
    const file = fileInput.files[0];              // should be always a valid file
    importDiagram(file, checkFileLocally(file));  // otherwise button should be disabled
  });
  // register model listeners
  registerModalListener("diagram-import", openDialog, closeDialog);
}
