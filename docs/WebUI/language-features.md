# PlantUML Language Features

## Auto Completion

### Icons

- type `<&` to get a list of PlantUML available icons
- see a preview of the suggested icon in its description

![icons](https://raw.githubusercontent.com/plantuml/plantuml-server/master/docs/WebUI/gifs/auto-completion-icons.gif)

### Themes

- type `!t` to get the suggestion `theme`
- type `!theme ` to get a list of (local) available PlantUML themes.

![themes](https://raw.githubusercontent.com/plantuml/plantuml-server/master/docs/WebUI/gifs/auto-completion-themes.gif)


## Validation

### `@start...` and `@end...`

- `@start...` should always be the first command
- `@end...` should alway be the last command
- `@start...` should only exists once
- `@end...` should only exists once
- `@end...` should have the same type as `@start...`  
  e.g.: `@startjson ... @endjson`

![start-end](https://raw.githubusercontent.com/plantuml/plantuml-server/master/docs/WebUI/gifs/validation-start-end.gif)
