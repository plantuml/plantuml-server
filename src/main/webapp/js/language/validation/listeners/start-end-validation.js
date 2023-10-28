/****************************************
* Language Start-End Validation Feature *
*****************************************/

/**
 * Add PlantUML `@start` and `@end` command validation.
 */
PlantUmlLanguageFeatures.prototype.addStartEndValidationListeners = function() {
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
    const match = code.match(/^(?:(?:'.*)|\s)*@start(\w+)/);
    if (match) {
      diagramType = match[1];
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
    const match = code.match(/\s+@end(\w+)(?:(?:'.*)|\s)*$/);
    if (match) {
      if (diagramType === match[1]) {
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
    const match = line.match(/^\s*@start(\w+)(?:\s+.*)?$/);
    if (!match) return;

    startCounter += 1;
    if (startCounter > 1) {
      const word = "@start" + match[1];
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
    const match = line.match(/^\s*@end(\w+)(?:\s+.*)?$/);
    if (!match) return;

    endCounter += 1;
    if (endCounter > 1) {
      const word = "@end" + match[1];
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
