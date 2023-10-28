/*********************************************
* PlantUML Language Icon Completion Provider *
**********************************************/

PlantUmlLanguageFeatures.prototype.getIcons = (function(){
  let icons = undefined;
  return async function() {
    if (icons === undefined) {
      icons = await PlantUmlLanguageFeatures.makeRequest("GET", "ui-helper?request=icons");
    }
    return icons;
  }
})();

PlantUmlLanguageFeatures.prototype.registerIconCompletion = function() {
  const createIconProposals = async (range, filter = undefined) => {
    const icons = await this.getIcons();
    return icons?.filter(icon => filter ? icon.includes(filter) : true)
      .map(icon => {
        // NOTE: markdown image path inside suggestions seems to have rendering issues while using relative paths
        const iconUrl = PlantUmlLanguageFeatures.absolutePath(
          PlantUmlLanguageFeatures.baseUrl + "ui-helper?request=icons.svg#" + icon
        );
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

  monaco.languages.registerCompletionItemProvider(PlantUmlLanguageFeatures.languageSelector, {
    triggerCharacters: ["&"],
    provideCompletionItems: async (model, position) => {
      const textUntilPosition = model.getValueInRange({
        startLineNumber: position.lineNumber,
        startColumn: 1,
        endLineNumber: position.lineNumber,
        endColumn: position.column,
      });
      const match = textUntilPosition.match(/<&([^\s>]*)$/);
      if (match) {
        const suggestions = await createIconProposals(this.getWordRange(model, position), match[1]);
        return { suggestions };
      }
      return { suggestions: [] };
    }
  });
};
