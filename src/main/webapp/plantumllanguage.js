/*******************************************
* Monaco Editor PlantUML language features *
********************************************/

/**
 * Monaco Editor PlantUML Language Features.
 *
 * @example
 * ```js
 * plantumlFeatures = new PlantUmlLanguageFeatures();
 * const model = monaco.editor.createModel(initCode, "apex", uri);
 * model.onDidChangeContent(() => plantumlFeatures.validateCode(model));
 * ```
 */
const PlantUmlLanguageFeatures = (function() {
  'use strict';

  /**
   * Create Monaco Editor PlantUML Language Features instance.
   *
   * @param {object} [options]  global instance options
   */
  function PlantUmlLanguageFeatures({
    baseUrl = "",
    languageSelector = ["apex", "plantuml"],
    initialize = true
  } = {}) {

    const validationEventListeners = {};


    // ==========================================================================================================
    // == PlantUML valdation methods ==

    /**
     * Add validation event listener.
     *
     * Validation Event Order:
     * before -> code -> line -> after
     *
     * @param {("before"|"code"|"line"|"after")} type  before|code|line|after event type
     * @param {(event: any) => Promise<editor.IMarkerData>|editor.IMarkerData|Promise<editor.IMarkerData[]>|editor.IMarkerData[]|Promise<void>|void} listener  event listener
     */
    this.addValidationEventListener = (type, listener) => {
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
    this.validateCode = async (model) => {
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

    /**
     * Add PlantUML `@start` and `@end` command validation.
     */
    this.addStartEndValidationListeners = () => {
      let diagramType = undefined;
      let startCounter = 0;
      let endCounter = 0;

      // reset validation cache
      this.addValidationEventListener("before", () => {
        diagramType = undefined;
        startCounter = 0;
        endCounter = 0;
      });

      // @start should be the first command
      this.addValidationEventListener("code", ({ model, code }) => {
        const match = code.match(/^(('.*)|\s)*@start(?<type>\w+)/);
        if (match) {
          diagramType = match.groups.type;
          return;  // diagram code starts with a `@start`
        }
        return {
          message: "PlantUML diagrams should begin with the `@start` command and `@start` should also be the first command.",
          severity: monaco.MarkerSeverity.Warning,
          startLineNumber: 1,
          startColumn: 1,
          endLineNumber: 1,
          endColumn: model.getLineLength(1) + 1,
        };
      });

      // @end should be the last command and should be of the same type (e.g. @startjson ... @endjson)
      this.addValidationEventListener("code", ({ model, code }) => {
        const lineCount = model.getLineCount();
        const match = code.match(/\s+@end(?<type>\w+)(('.*)|\s)*$/);
        if (match) {
          if (diagramType === match.groups.type) {
            return;  // diagram code ends with a `@end` of the same type as the `@start`
          }
          return {
            message: "PlantUML diagrams should start and end with the type.\nExample: `@startjson ... @endjson`",
            severity: monaco.MarkerSeverity.Error,
            startLineNumber: lineCount,
            startColumn: 1,
            endLineNumber: lineCount,
            endColumn: model.getLineLength(lineCount) + 1,
          };
        }
        return {
          message: "PlantUML diagrams should end with the `@end` command and `@end` should also be the last command.",
          severity: monaco.MarkerSeverity.Warning,
          startLineNumber: lineCount,
          startColumn: 1,
          endLineNumber: lineCount,
          endColumn: model.getLineLength(lineCount) + 1,
        };
      });

      // @start should only be used once
      this.addValidationEventListener("line", ({ range, line }) => {
        const match = line.match(/^\s*@start(?<type>\w+)(\s+.*)?$/);
        if (!match) return;

        startCounter += 1;
        if (startCounter > 1) {
          const word = "@start" + match.groups.type;
          const wordIndex = line.indexOf(word);
          return {
            message: "Multiple @start commands detected.",
            severity: monaco.MarkerSeverity.Warning,
            startLineNumber: range.startLineNumber,
            startColumn: wordIndex + 1,
            endLineNumber: range.endLineNumber,
            endColumn: wordIndex + word.length + 1,
          };
        }
      });

      // @end should only be used once
      this.addValidationEventListener("line", ({ range, line }) => {
        const match = line.match(/^\s*@end(?<type>\w+)(\s+.*)?$/);
        if (!match) return;

        endCounter += 1;
        if (endCounter > 1) {
          const word = "@end" + match.groups.type;
          const wordIndex = line.indexOf(word);
          return {
            message: "Multiple @end commands detected.",
            severity: monaco.MarkerSeverity.Warning,
            startLineNumber: range.startLineNumber,
            startColumn: wordIndex + 1,
            endLineNumber: range.endLineNumber,
            endColumn: wordIndex + word.length + 1,
          };
        }
      });
    };


    // ==========================================================================================================
    // == PlantUML code completion methods ==

    this.registerThemeCompletion = () => {
      const createThemeProposals = async (range, filter = undefined) => {
        const themes = await this.getThemes();
        return themes?.filter(theme => filter ? theme.includes(filter) : true)
          .map(theme => ({
            label: theme,
            kind: monaco.languages.CompletionItemKind.Text,
            documentation: "PlantUML " + theme + " theme",
            insertText: theme,
            range: range,
          })) || [];
      };

      monaco.languages.registerCompletionItemProvider(languageSelector, {
        triggerCharacters: [" "],
        provideCompletionItems: async (model, position) => {
          const textUntilPosition = model.getValueInRange({
            startLineNumber: position.lineNumber,
            startColumn: 1,
            endLineNumber: position.lineNumber,
            endColumn: position.column,
          });
          if (textUntilPosition.match(/^\s*!(t(h(e(m(e)?)?)?)?)?$/)) {
            return {
              suggestions: [
                {
                  label: 'theme',
                  kind: monaco.languages.CompletionItemKind.Keyword,
                  documentation: "PlantUML theme command",
                  insertText: 'theme',
                  range: getWordRange(model, position),
                }
              ]
            };
          }
          const match = textUntilPosition.match(/^\s*!theme\s+(?<theme>[^\s]*)$/);
          if (match) {
            const suggestions = await createThemeProposals(getWordRange(model, position), match.groups.theme);
            return { suggestions };
          }
          return { suggestions: [] };
        }
      });
    };

    this.registerIconCompletion = () => {
      const createIconProposals = async (range, filter = undefined) => {
        const icons = await this.getIcons();
        return icons?.filter(icon => filter ? icon.includes(filter) : true)
          .map(icon => {
            // NOTE: markdown image path inside suggestions seems to have rendering issues while using relative paths
            const iconUrl = this.resolvePath(baseUrl + "ui-helper?request=icons.svg#" + icon);
            return {
              label: icon,
              kind: monaco.languages.CompletionItemKind.Constant,
              documentation: {
                //supportHtml: true,  // also a possibility but quite limited html
                value: "![icon](" + iconUrl + ") &nbsp; " + icon
              },
              insertText: icon + ">",
              range: range
            };
          }) || [];
      };

      monaco.languages.registerCompletionItemProvider(languageSelector, {
        triggerCharacters: ["&"],
        provideCompletionItems: async (model, position) => {
          const textUntilPosition = model.getValueInRange({
            startLineNumber: position.lineNumber,
            startColumn: 1,
            endLineNumber: position.lineNumber,
            endColumn: position.column,
          });
          const match = textUntilPosition.match(/<&(?<icon>[^\s>]*)$/);
          if (match) {
            const suggestions = await createIconProposals(getWordRange(model, position), match.groups.icon);
            return { suggestions };
          }
          return { suggestions: [] };
        }
      });
    };


    // ==========================================================================================================
    // == helper methods ==

    this.resolvePath = (path) => {
      if (path.startsWith("http")) return path;
      if (path.startsWith("/")) return window.location.origin + path;

      if (path.slice(0, 2) == "./") path = path.slice(2);
      let base = (document.querySelector("base") || {}).href || window.location.origin;
      if (base.slice(-1) == "/") base = base.slice(0, -1);
      return base + "/" + path;
    };

    this.getIcons = (function(){
      let icons = undefined;
      return async () => {
        if (icons === undefined) {
          icons = await makeRequest("GET", "ui-helper?request=icons", { responseType: "json" });
        }
        return icons;
      }
    })();

    this.getThemes = (function(){
      let themes = undefined;
      return async () => {
        if (themes === undefined) {
          themes = await makeRequest("GET", "ui-helper?request=themes", { responseType: "json" });
        }
        return themes;
      }
    })();

    const makeRequest = (
      method,
      url,
      { data = null, headers = { "Content-Type": "text/plain" }, responseType = "text", ignoreBaseUrl = false } = {}
    ) => {
      const targetUrl = (ignoreBaseUrl === true) ? url : baseUrl + url;
      return new Promise((resolve, reject) => {
        const xhr = new XMLHttpRequest();
        xhr.onreadystatechange = function() {
          if (xhr.readyState === XMLHttpRequest.DONE) {
            if (xhr.status >= 200 && xhr.status <= 300) {
              if (responseType === "json") {
                resolve(xhr.response);
              } else {
                resolve(xhr.responseText);
              }
            } else {
              if (responseType === "json") {
                reject({ status: xhr.status, response: xhr.response });
              } else {
                reject({ status: xhr.status, responseText: xhr.responseText });
              }
            }
          }
        }
        xhr.open(method, targetUrl, true);
        xhr.responseType = responseType;
        headers && Object.keys(headers).forEach(key => xhr.setRequestHeader(key, headers[key]));
        xhr.send(data);
      });
    };

    const getWordRange = (model, position) => {
      const word = model.getWordUntilPosition(position);
      return {
        startLineNumber: position.lineNumber,
        endLineNumber: position.lineNumber,
        startColumn: word.startColumn,
        endColumn: word.endColumn,
      };
    }


    // ==========================================================================================================
    // == constructor running code ==

    // prepare base URL
    if (baseUrl === null || baseUrl === undefined) {
      baseUrl = "";
    } else if (baseUrl !== "") {
      // add tailing "/"
      if (baseUrl.slice(-1) !== "/") baseUrl = baseUrl + "/";
    }

    // initialize default validation and code completion
    if (initialize) {
      this.addStartEndValidationListeners();
      this.registerThemeCompletion();
      this.registerIconCompletion();
    }
  }

  return PlantUmlLanguageFeatures;
})();
