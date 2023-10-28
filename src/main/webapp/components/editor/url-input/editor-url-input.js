/**********************
* Editor URL Input JS *
***********************/

const { setUrlValue, initEditorUrlInput } = (function() {
  function setUrlValue(
    url=undefined,
    { encodedDiagram=undefined, index=undefined } = {},
    { suppressEditorChangedMessage=false } = {}
  ) {
    if (!url && !encodedDiagram) return;
    if (suppressEditorChangedMessage) {
      suppressNextMessage("url");
    }
    document.getElementById("url").value = url ? url : resolvePath(buildUrl("png", encodedDiagram, index));
  }

  function initEditorUrlInput() {
    const input = document.getElementById("url");

    function copyUrlToClipboard() {
      input.focus();
      input.select();
      navigator.clipboard?.writeText(input.value).catch(() => {});
    }
    async function onInputChanged(event) {
      document.appConfig.autoRefreshState = "started";
      event.target.title = event.target.value;
      const analysedUrl = analyseUrl(event.target.value);
      // decode diagram (server request)
      const code = await makeRequest("GET", "coder/" + analysedUrl.encodedDiagram);
      // change editor content without sending the editor change message
      setEditorValue(document.editor, code, { suppressEditorChangedMessage: true });
      sendMessage({
        sender: "url",
        data: {
          encodedDiagram: analysedUrl.encodedDiagram,
          index: analysedUrl.index,
        },
        synchronize: true,
      });
    }

    // resolve relative path inside url input once
    setUrlValue(resolvePath(input.value));
    // update editor and everything else if the URL input is changed
    input.addEventListener("change", onInputChanged);
    // add listener
    document.getElementById("url-copy-btn").addEventListener("click", copyUrlToClipboard);
  }

  return { setUrlValue, initEditorUrlInput };
})();
