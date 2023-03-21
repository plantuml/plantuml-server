/*************************
* PlantUMLServlet script *
**************************/

let clipboard_write = false;
let clipboard_read = false;

function copyToClipboard(fieldid, fielddesc) {
    if (clipboard_write) {
        navigator.clipboard.writeText(document.getElementById(fieldid).value);
        alert(fielddesc + " copied to clipboard");
    }
    return false;
}

function loadCodeMirror() {
    document.myCodeMirror = CodeMirror.fromTextArea(
        document.getElementById("text"), 
        {
            lineNumbers: true,
            extraKeys: {Tab: false, "Shift-Tab": false} 
        }
    );
}

function resolvePath(path) {
    if (path.startsWith("http")) return path;
    if (path.startsWith("/"))  return window.location.origin + path;

    if (path.slice(0, 2) == "./") path = path.slice(2);
    let base = (document.querySelector('base') || {}).href || window.location.origin;
    if (base.slice(-1) == "/") base = base.slice(0, -1);
    return base + "/" + path;
}

window.onload = function() {
    loadCodeMirror();

    // resolve relative path inside url input once
    const url = document.getElementById("url");
    url.value = resolvePath(url.value);

    // clipboard check (from PR#250)
    // TODO: not supported by Firefox, Safari or WebView Android
    // https://developer.mozilla.org/en-US/docs/Web/API/Permissions_API#browser_compatibility
    if (navigator.permissions){
        navigator.permissions.query({ name: "clipboard-write" }).then((result) => {
            if (result.state == "granted" || result.state == "prompt") {
                clipboard_write = true;
            }
        });
        navigator.permissions.query({ name: "clipboard-read" }).then((result) => {
            if (result.state == "granted" || result.state == "prompt") {
                clipboard_read = true;
            }
        });
    };
};
