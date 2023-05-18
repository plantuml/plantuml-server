/*****************
* Editor Menu JS *
******************/

function initEditorMenu() {
  function copyCodeToClipboard() {
    const range = document.editor.getModel().getFullModelRange();
    document.editor.focus();
    document.editor.setSelection(range);
    const code = document.editor.getValue();
    navigator.clipboard?.writeText(code).catch(() => {});
  }
  // add listener
  document.getElementById("menu-item-editor-code-copy").addEventListener("click", copyCodeToClipboard);
}
