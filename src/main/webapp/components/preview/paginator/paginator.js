/***************
* Paginator JS *
***************/

function getNumberOfDiagramPagesFromCode(code) {
  // count `newpage` inside code
  // known issue: a `newpage` starting in a newline inside a multiline comment will also be counted
  return code.match(/^\s*newpage\s?.*$/gm)?.length + 1 || 1;
}

function updatePaginatorSelection() {
  const paginator = document.getElementById("paginator");
  const index = document.appData.index;
  if (index === undefined || paginator.childNodes.length <= index) {
    for (const node of paginator.childNodes) {
      node.checked = false;
    }
  } else {
    paginator.childNodes[index].checked = true;
  }
}

const updatePaginator = (function() {
  function updateNumberOfPagingElements(paginator, pages) {
    // remove elements (buttons) if there are to many
    while (paginator.childElementCount > pages) {
      paginator.removeChild(paginator.lastChild)
    }
    // add elements (buttons) if there are to less
    while (paginator.childElementCount < pages) {
      const radioBtn = document.createElement("input");
      radioBtn.name = "paginator";
      radioBtn.type = "radio";
      radioBtn.value = paginator.childElementCount;
      radioBtn.addEventListener("click", (event) => {
        sendMessage({
          sender: "paginator",
          data: { index: event.target.value },
          synchronize: true,
        });
      });
      paginator.appendChild(radioBtn);
    }
  }
  return function() {
    const paginator = document.getElementById("paginator");
    const pages = document.appData.numberOfDiagramPages;
    if (pages > 1) {
      updateNumberOfPagingElements(paginator, pages);
      setVisibility(paginator, true);
    } else {
      setVisibility(paginator, false);
    }
  };
})();

function initializePaginator() {
  if (document.appData.numberOfDiagramPages > 1) {
    updatePaginator();
    updatePaginatorSelection();
  }
}
