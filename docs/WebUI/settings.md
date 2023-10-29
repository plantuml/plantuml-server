# Settings

Via the menu or <kbd>Ctrl</kbd> + <kbd>,</kbd> (or <kbd>Meta</kbd> + <kbd>,</kbd> in the case of a Mac) you can open the Setting dialog.

## Theme

_The sun is too bright? You live on the dark side or only in the basement?_
Choose between the `dark` and `light` theme.

![theme](https://raw.githubusercontent.com/plantuml/plantuml-server/master/docs/WebUI/gifs/settings-theme.gif)

## Rendering Type

You want always to work and see only the SVG version? Not Problem.  
Choose the rendering type you want to see.

![rendering-type](https://raw.githubusercontent.com/plantuml/plantuml-server/master/docs/WebUI/gifs/settings-rendering-type.gif)

## Editor Watcher Timeout

You can change the Editor Watcher Timeout, by default it is `500 ms`.


## Editor Options

You can change the options of the editor:

```yaml
{
  automaticLayout: true,
  fixedOverflowWidgets: true,
  minimap: { enabled: false },
  scrollbar: { alwaysConsumeMouseWheel: false },
  scrollBeyondLastLine: false,
  tabSize: 2,
  theme: "vs",  // "vs-dark"
}
```
