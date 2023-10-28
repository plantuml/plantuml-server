/**************
* DOM Helpers *
***************/

function removeChildren(element) {
  if (element.replaceChildren) {
    element.replaceChildren();
  } else {
    element.innerHTML = "";
  }
}

function isVisible(element) {
  // `offsetParent` returns `null` if the element, or any of its parents,
  // is hidden via the display style property.
  // see: https://developer.mozilla.org/en-US/docs/Web/API/HTMLElement/offsetParent
  return (element.offsetParent !== null);
}

function setVisibility(element, visibility, focus=false) {
  if (visibility) {
    element.style.removeProperty("display");
    if (focus) element.focus();
  } else {
    element.style.display = "none";
  }
}
