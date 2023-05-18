/*********************************
* PlantUML Server Application JS *
**********************************/
"use strict";

async function initApp() {
  const view = new URL(window.location.href).searchParams.get("view")?.toLowerCase();

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

  await initEditor(view);
  initializeAppData();
  initTheme();
  initAppCommunication();
  await initPreview(view);
  initModals(view);

  if (document.editor) {
    document.editor.focus();
    if (document.appData.encodedDiagram == "SyfFKj2rKt3CoKnELR1Io4ZDoSa70000") {
      // if default `Bob -> Alice : hello` example mark example code for faster editing
      document.editor.setSelection({
        startLineNumber: 2,
        endLineNumber: 2,
        startColumn: 1,
        endColumn: 21,
      });
    }
  }

  document.appConfig.autoRefreshState = "complete";
}

// main entry
window.onload = initApp;
