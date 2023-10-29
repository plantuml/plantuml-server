/**********************************************
* PlantUML Language Completion Provider Utils *
***********************************************/

PlantUmlLanguageFeatures.prototype.getWordRange = function(model, position) {
  const word = model.getWordUntilPosition(position);
  return {
    startLineNumber: position.lineNumber,
    endLineNumber: position.lineNumber,
    startColumn: word.startColumn,
    endColumn: word.endColumn,
  };
}
