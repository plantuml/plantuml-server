/************
* Modals JS *
*************/

const { registerModalListener, openModal, closeModal } = (function() {
  const modalListener = {};
  return {
    registerModalListener: (id, fnOpen=undefined, fnClose=undefined) => {
      modalListener[id] = { fnOpen, fnClose };
    },
    openModal: (id, ...args) => {
      const fnOpen = modalListener[id]?.fnOpen;
      if (fnOpen) {
        fnOpen(...args);
      } else {
        setVisibility(document.getElementById(id), true, true);
      }
    },
    closeModal: (id, ...args) => {
      const fnClose = modalListener[id]?.fnClose;
      if (fnClose) {
        fnClose(...args);
      } else {
        setVisibility(document.getElementById(id), false);
      }
    },
  };
})();

function initModals(view) {
  function onModalKeydown(event) {
    if (event.key === "Escape" || event.key === "Esc") {
      event.preventDefault();
      closeModal(event.target.closest(".modal").id);
    } else if (event.key === "Enter") {
      event.preventDefault();
      const modal = event.target.closest(".modal");
      const okBtn = modal.querySelector('input.ok[type="button"]');
      if (okBtn && !okBtn.disabled) {
        okBtn.click();
      }
    }
  }
  document.querySelectorAll(".modal").forEach(modal => {
    modal.addEventListener("keydown", onModalKeydown, false);
  });
  // init modals
  initSettings();
  if (view !== "previewer") {
    initDiagramExport();
    initDiagramImport();
  }
}

function isModalOpen(id) {
  return isVisible(document.getElementById(id));
}

function closeAllModals() {
  document.querySelectorAll(".modal").forEach(modal => closeModal(modal.id));
}
