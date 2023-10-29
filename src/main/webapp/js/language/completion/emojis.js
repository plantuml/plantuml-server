/**********************************************
* PlantUML Language Emoji Completion Provider *
***********************************************/

PlantUmlLanguageFeatures.prototype.getEmojis = (function(){
  let emojis = undefined;
  return async function() {
    if (emojis === undefined) {
      emojis = await PlantUmlLanguageFeatures.makeRequest("GET", "ui-helper?request=emojis");
    }
    return emojis;
  }
})();

PlantUmlLanguageFeatures.prototype.registerEmojiCompletion = function() {
  const createEmojiProposals = async (range, filter = undefined) => {
    const emojis = await this.getEmojis();
    return emojis?.filter(([unicode, name]) => filter ? unicode.includes(filter) || name?.includes(filter) : true)
      .map(([unicode, name]) => {
        // NOTE: load images direct from GitHub source: https://github.com/twitter/twemoji#download
        const emojiUrl = "https://raw.githubusercontent.com/twitter/twemoji/gh-pages/v/13.1.0/svg/" + unicode + ".svg";
        const docHint = (name) ? name + " (" + unicode + ")" : unicode;
        const isUnicode = !name || (filter && unicode.includes(filter));
        const label = isUnicode ? unicode : name;
        return {
          label: label,
          kind: monaco.languages.CompletionItemKind.Constant,
          documentation: {
            //supportHtml: true,  // also a possibility but quite limited html
            value: "![emoji](" + emojiUrl + ") &nbsp; " + docHint
          },
          insertText: label + ":>",
          range: range
        };
      }) || [];
  };

  monaco.languages.registerCompletionItemProvider(PlantUmlLanguageFeatures.languageSelector, {
    triggerCharacters: [":"],
    provideCompletionItems: async (model, position) => {
      const textUntilPosition = model.getValueInRange({
        startLineNumber: position.lineNumber,
        startColumn: 1,
        endLineNumber: position.lineNumber,
        endColumn: position.column,
      });
      const match = textUntilPosition.match(/<:([^\s>]*)$/);
      if (match) {
        const suggestions = await createEmojiProposals(this.getWordRange(model, position), match[1]);
        return { suggestions };
      }
      return { suggestions: [] };
    }
  });
};
