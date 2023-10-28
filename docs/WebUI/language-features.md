# PlantUML Language Features

## Auto Completion

### Icons

- type `<&` to get a list of PlantUML available icons
- see a preview of the suggested icon in its description
- [PlantUML documentation](https://plantuml.com/openiconic)

![icons](https://raw.githubusercontent.com/plantuml/plantuml-server/master/docs/WebUI/gifs/auto-completion-icons.gif)

### Emojis

- type `<:` to get a list of PlantUML available icons
- see a preview of the suggested icon in its description
- [PlantUML documentation](https://plantuml.com/creole#68305e25f5788db0)

![emojis](https://raw.githubusercontent.com/plantuml/plantuml-server/master/docs/WebUI/gifs/auto-completion-emojis.gif)

### Themes

- type `!t` to get the suggestion `theme`
- type `!theme ` to get a list of (local) available PlantUML themes.
- [PlantUML documentation](https://plantuml.com/theme)

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
