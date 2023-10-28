/********************************************
* PlantUML Language Validation Feature Base *
*********************************************/

(function() {

  const validationEventListeners = {};

  /**
   * Add validation event listener.
   *
   * Validation Event Order:
   * before -> code -> line -> after
   *
   * @param {("before"|"code"|"line"|"after")} type  before|code|line|after event type
   * @param {(event: any) => Promise<editor.IMarkerData>|editor.IMarkerData|Promise<editor.IMarkerData[]>|editor.IMarkerData[]|Promise<void>|void} listener  event listener
   */
  PlantUmlLanguageFeatures.prototype.addValidationEventListener = function(type, listener) {
    if (!["before", "code", "line", "after"].includes(type)) {
      throw Error("Unknown validation event type: " + type);
    }
    validationEventListeners[type] = validationEventListeners[type] || [];
    validationEventListeners[type].push(listener);
  };

  /**
   * Validate PlantUML language of monaco editor model.
   *
   * @param {editor.ITextModel} model  editor model to validate
   *
   * @returns editor markers as promise
   *
   * @example
   * ```js
   * validateCode(editor.getModel())
   *   .then(markers => monaco.editor.setModelMarkers(model, "plantuml", markers));
   * ```
   */
  PlantUmlLanguageFeatures.prototype.validateCode = async function(model) {
    const promises = [];

    // raise before events
    promises.push(validationEventListeners.before?.map(listener => listener({ model })));

    // raise code events
    promises.push(validationEventListeners.code?.map(listener => listener({ model, code: model.getValue() })));

    if (validationEventListeners.line && validationEventListeners.line.length > 0) {
      // NOTE: lines and columns start at 1
      const lineCount = model.getLineCount();
      for (let lineNumber = 1; lineNumber <= lineCount; lineNumber++) {
        const range = {
          startLineNumber: lineNumber,
          startColumn: 1,
          endLineNumber: lineNumber,
          endColumn: model.getLineLength(lineNumber) + 1,
        };
        const line = model.getValueInRange(range);
        // raise line events
        promises.push(validationEventListeners.line?.map(listener => listener({ model, range, line, lineNumber, lineCount })));
      }
    }

    // raise after events
    promises.push(validationEventListeners.after?.map(listener => listener({ model })));

    // collect all markers and ...
    // - since each event can results in an array of markers -> `flat(1)`
    // - since not each event has to results in markers and can be `undef
    return Promise.all(promises).then(results => results.flat(1).filter(marker => marker));
  };

})();
