/**********************************************
* PlantUML Language Theme Completion Provider *
***********************************************/

PlantUmlLanguageFeatures.prototype.getThemes = (function(){
  let themes = undefined;
  return async function() {
    if (themes === undefined) {
      themes = await PlantUmlLanguageFeatures.makeRequest("GET", "ui-helper?request=themes");
    }
    return themes;
  }
})();

PlantUmlLanguageFeatures.prototype.registerThemeCompletion = function() {
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

  monaco.languages.registerCompletionItemProvider(PlantUmlLanguageFeatures.languageSelector, {
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
              range: this.getWordRange(model, position),
            }
          ]
        };
      }
      const match = textUntilPosition.match(/^\s*!theme\s+([^\s]*)$/);
      if (match) {
        const suggestions = await createThemeProposals(this.getWordRange(model, position), match[1]);
        return { suggestions };
      }
      return { suggestions: [] };
    }
  });
};
