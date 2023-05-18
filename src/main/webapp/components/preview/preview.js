/*************
* Preview JS *
**************/

async function initPreview(view) {
  const btnUndock = document.getElementById("btn-undock");
  const btnDock = document.getElementById("btn-dock");
  const editorContainer = document.getElementById("editor-main-container");
  const previewContainer = document.getElementById("previewer-main-container");

  function hidePreview() {
    setVisibility(btnUndock, false);
    // if not opened via button and therefore a popup, `window.close` won't work
    setVisibility(btnDock, window.opener);
    if (editorContainer) editorContainer.style.width = "100%";
    if (previewContainer) setVisibility(previewContainer, false);
  }
  function showPreview() {
    setVisibility(btnUndock, true);
    setVisibility(btnDock, false);
    if (editorContainer) editorContainer.style.removeProperty("width");
    if (previewContainer) setVisibility(previewContainer, true);
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
  // add listener
  btnUndock.addEventListener("click", undock);
  // init preview components
  await initializeDiagram();
  initializePaginator()
  // check preview visibility
  if (["previewer", "editor"].includes(view)) {
    hidePreview();
  }
}
