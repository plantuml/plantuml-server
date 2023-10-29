/************************
* Browser Communication *
*************************
* send and receive data object:
* {
*   sender: string = ["editor"|"url"|"paginator"|"settings"|"file-drop"],
*   data: {
*     encodedDiagram: string | undefined,
*     index: integer | undefined,
*     numberOfDiagramPages: integer | undefined,
*     appConfig: object | undefined
*   } |  undefined,
*   synchronize: boolean = false,
*   reload: boolean = false,  // reload page
*   force: boolean = false  // force synchronize or reload
* }
*************************/

const { sendMessage, suppressNextMessage, initAppCommunication } = (function() {
  const BROADCAST_CHANNEL = "plantuml-server";

  const { suppressNextMessage, isMessageSuppressed } = (function() {
    const suppressMessages = [];
    function suppressNextMessage(sender, condition=undefined) {
      suppressMessages.push({ sender, condition });
    }
    function isMessageSuppressed(data) {
      for (let i = 0; i < suppressMessages.length; i++) {
        const suppressMessage = suppressMessages[i];
        if (!suppressMessage.sender || suppressMessage.sender === data.sender) {
          if (!suppressMessage.condition || suppressMessage.condition(data)) {
            suppressMessages.splice(i, 1);
            return true;
          }
        }
      }
      return false;
    }
    return { suppressNextMessage, isMessageSuppressed };
  })();

  function sendMessage(data) {
    if (isMessageSuppressed(data)) return;
    (new BroadcastChannel(BROADCAST_CHANNEL)).postMessage(data);
  }

  function initAppCommunication() {
    function updateReceiveMessageData(data) {
      if (!data || Object.keys(data).length === 0) return {};

      const changedFlags = {};
      if ("encodedDiagram" in data && data.encodedDiagram !== document.appData.encodedDiagram) {
        document.appData.encodedDiagram = data.encodedDiagram;
        changedFlags.diagram = true;
      }
      if ("index" in data && data.index !== document.appData.index) {
        document.appData.index = data.index;
        changedFlags.index = true;
      }
      if ("numberOfDiagramPages" in data && data.numberOfDiagramPages !== document.appData.numberOfDiagramPages) {
        document.appData.numberOfDiagramPages = data.numberOfDiagramPages;
        changedFlags.numberOfDiagramPages = true;
      }
      if ("appConfig" in data && data.appConfig !== document.appConfig) {
        document.appConfig = data.appConfig;
        changedFlags.appConfig = true;
      }
      return changedFlags;
    }

    async function receiveMessage(event) {
      async function updateStaticPageData(sender) {
        document.appConfig.autoRefreshState = "syncing";
        const encodedDiagram = document.appData.encodedDiagram;
        const index =  document.appData.index;

        if (sender !== "url" && document.getElementById("url")) {
          // update URL input
          setUrlValue(undefined, { encodedDiagram, index }, { suppressEditorChangedMessage: true });
        }
        // update diagram image
        await setDiagram(document.appConfig.diagramPreviewType, encodedDiagram, index);
        // update external diagram links
        for (let target of document.getElementsByClassName("diagram-link")) {
          target.href = buildUrl(target.dataset.imgType, encodedDiagram, index);
        }
        // update browser url as well as the browser history
        const url = replaceUrl(window.location.href, encodedDiagram, index).url;
        history.replaceState(history.stat, document.title, url);

        // set auto refresh state to complete
        document.appConfig.autoRefreshState = "complete";
      }

      const data = event.data.data;
      const force = event.data.force || false;
      const changedFlags = updateReceiveMessageData(data);
      if (event.data.synchronize === true) {
        if (force || changedFlags.diagram || changedFlags.index || changedFlags.appConfig) {
          await updateStaticPageData(event.data.sender);
        }
        if (force || changedFlags.numberOfDiagramPages) {
          updatePaginator();
        }
        if (force || changedFlags.numberOfDiagramPages || changedFlags.index) {
          updatePaginatorSelection();
        }
        if (changedFlags.appConfig) {
          applyConfig();
        }
      }
      if (event.data.reload === true) {
        window.location.reload();
      }
    }

    // create broadcast channel
    const bc = new BroadcastChannel(BROADCAST_CHANNEL);
    bc.onmessage = receiveMessage;
  }

  return { sendMessage, suppressNextMessage, initAppCommunication };
})();
