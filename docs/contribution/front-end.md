# Front-end Contribution

## Web UI

The Web UI uses vanilla javascript.

As online editor Microsoft's [Monaco Editor](https://github.com/microsoft/monaco-editor).
The documentation can be found [here](https://microsoft.github.io/monaco-editor/docs.html).
You may recognize the editor since it's the code editor from [VS Code](https://github.com/microsoft/vscode).

The main entry file are `index.jsp`, `previewer.jsp` and `error.jsp`.

The code structure is mainly divided into `components` and `js`:
- `components` are for example a modal or dialog.
Anything that include things directly seen and rendered on the page.
- `js` contains more the things that do not have a direct influence on the UI. For example the PlantUML language features or the methods for cross-browser/cross-tab communication.


## PlantUML Language Features

At the moment there is no defined PlantUML language.
Feel free to create one!
But until then the syntax highlighting form `apex` is used.
IMHO it works quite well.

All PlantUML language features are bundled into a seperate file `plantuml-language.min.js`.
Therefore anything under `js/language` should be independent!

### Code Completion
What do you need to do to create a new code completion feature:
1. create a new JS file under `js/language/completion` - let's say `xxx.js`
2. create a new `registerXxxCompletion` method  
   _It may help you if you look into the [documentation](https://microsoft.github.io/monaco-editor/docs.html#functions/languages.registerCompletionItemProvider.html) or at the provided [sample code](https://microsoft.github.io/monaco-editor/playground.html?source=v0.38.0#example-extending-language-services-completion-provider-example) to understand more about `monaco.languages.registerCompletionItemProvider`._
   ```js
   PlantUmlLanguageFeatures.prototype.registerEmojiCompletion = function() {
     monaco.languages.registerCompletionItemProvider(PlantUmlLanguageFeatures.languageSelector, {
       provideCompletionItems: async (model, position) => {
         // ...
         return { suggestions };
       }
     });
   };
   ```
4. add your new method inside the language initialization inside `js/language/language.js`
   ```diff
   const PlantUmlLanguageFeatures = function(initialize = true) {
     if (initialize) {
       // initialize all validation and code completion methods
       this.addStartEndValidationListeners();
       this.registerThemeCompletion();
       this.registerIconCompletion();
       this.registerEmojiCompletion();
   +   this.registerXxxCompletion();
     }
   };
    ```

### Code Validation
What do you need to do to create a new code validation feature:
1. create a new JS file under `js/language/validation/listeners` - let's say `zzz-validation.js`
2. register your validation methods to the designated event listener  
   The validation event order is: `before` &#8594; `code` &#8594; `line` &#8594; `after`  
   You may look at `js/language/validation/listeners/start-end-validation.js` to get an idea how to register a new listener.
3. add your new method inside the language initialization inside `js/language/language.js`
   ```diff
   const PlantUmlLanguageFeatures = function(initialize = true) {
     if (initialize) {
       // initialize all validation and code completion methods
       this.addStartEndValidationListeners();
   +   this.addZzzValidationListeners();
       this.registerThemeCompletion();
       this.registerIconCompletion();
       this.registerEmojiCompletion();
     }
   };
    ```


### Tipps

- `pom.xml`: set `withoutCSSJSCompress` to `true` to deactivate the minification
- use `mvn fizzed-watcher:run` to watch changes and automatically update the bundled `plantuml.min.{css,js}` and `plantuml-language.min.js` files
- if the browser get the error `ReferenceError: require is not defined` or something similar related to the webjars, try `mvn clean install` to get things straight
