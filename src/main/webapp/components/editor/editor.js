/************
* Editor JS *
*************/

const { setEditorValue, initEditor } = (function() {
  function setEditorValue(
    editor,
    text,
    { suppressEditorChangedMessage=false, forceMoveMarkers=undefined } = {}
  ) {
    if (suppressEditorChangedMessage && editor === document.editor) {
      suppressNextMessage("editor");
    }
    // replace editor value but preserve undo stack
    editor.executeEdits("", [{ range: editor.getModel().getFullModelRange(),  text, forceMoveMarkers }]);
  }

  async function initEditor(view) {
    function loadMonacoCodeEditorAsync() {
      return new Promise((resolve, _reject) => {
        require.config({ paths: { vs: "webjars/monaco-editor/0.36.1/min/vs" } });
        require(["vs/editor/editor.main"], resolve);
      });
    }
    function createEditorModel() {
      let plantumlFeatures;
      function onPlantumlEditorContentChanged(code, sender=undefined, broadcastChanges=true) {
        function broadcastCodeEditorChanges() {
          document.appConfig.autoRefreshState = "started";
          const numberOfDiagramPages = getNumberOfDiagramPagesFromCode(code);
          let index = document.appData.index;
          if (index === undefined || numberOfDiagramPages === 1) {
            index = undefined;
          } else if (index >= numberOfDiagramPages) {
            index = numberOfDiagramPages - 1;
          }
          makeRequest("POST", "coder", { data: code }).then((encodedDiagram) => {
            sendMessage({
              sender,
              data: { encodedDiagram, numberOfDiagramPages, index },
              synchronize: true,
            });
          });
        }
        const updatePlantumlLanguageMarkers = (function() {
          return function() {
            const model = document.editor.getModel();
            plantumlFeatures = plantumlFeatures || new PlantUmlLanguageFeatures();
            plantumlFeatures.validateCode(model)
              .then(markers => monaco.editor.setModelMarkers(model, "plantuml", markers));
          }
        })();
        if (sender && broadcastChanges) broadcastCodeEditorChanges();
        updatePlantumlLanguageMarkers();
      }
      function getInitPlantumlCodeAndRemoveElement() {
        const initCodeEl = document.getElementById("initCode");
        const initCode = initCodeEl.value;
        initCodeEl.remove();
        return initCode;
      }
      // create editor model
      const model = monaco.editor.createModel(
        getInitPlantumlCodeAndRemoveElement(),
        "apex",
        monaco.Uri.parse("inmemory://plantuml")
      );
      // create editor model watcher
      let timer = 0;
      model.onDidChangeContent(() => {
        clearTimeout(timer);
        document.appConfig.autoRefreshState = "waiting";
        timer = setTimeout(
          () => onPlantumlEditorContentChanged(model.getValue(), "editor"),
          document.appConfig.editorWatcherTimeout
        );
      });
      return model;
    }
    function getDefaultStorageService() {
      // create own storage service to expand suggestion documentation by default
      return {
        get() {},
        getBoolean(key) { return key === "expandSuggestionDocs"; },
        getNumber() { return 0; },
        remove() {},
        store() {},
        onWillSaveState() {},
        onDidChangeStorage() {},
        onDidChangeValue() {},
      };
    }

    // load monaco editor requirements
    await loadMonacoCodeEditorAsync();
    if (view !== "previewer") {
      // create editor
      const model = createEditorModel();
      const storageService = getDefaultStorageService();
      document.editor = monaco.editor.create(document.getElementById("monaco-editor"), {
        model, ...document.appConfig.editorCreateOptions
      }, { storageService });
      // sometimes the monaco editor has resize problems
      document.addEventListener("resize", () => document.editor.layout());
      // init editor components
      initEditorUrlInput();
      initEditorMenu();
    }
  }

  return { setEditorValue, initEditor };
})();
