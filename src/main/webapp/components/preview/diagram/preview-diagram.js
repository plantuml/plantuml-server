/*********************
* Preview Diagram JS *
**********************/

async function initializeDiagram() {
  if (document.appConfig.diagramPreviewType !== "png") {
    // NOTE: "png" is preloaded from the server
    return setDiagram(
      document.appConfig.diagramPreviewType,
      document.appData.encodedDiagram,
      document.appData.index
    );
  }
}

async function setDiagram(type, encodedDiagram, index) {
  const container = document.getElementById("diagram");
  const png = document.getElementById("diagram-png");
  const txt = document.getElementById("diagram-txt");
  const pdf = document.getElementById("diagram-pdf");
  // NOTE: the map and svg elements will be overwitten, hence can not be cached

  async function requestDiagram(type, encodedDiagram, index) {
    return makeRequest("GET", buildUrl(type, encodedDiagram, index));
  }
  function setDiagramMap(mapString) {
    const mapEl = document.getElementById("plantuml_map");
    const mapBtn = document.getElementById("map-diagram-link");
    if (mapString) {
      const div = document.createElement("div");
      div.innerHTML = mapString;
      mapEl.parentNode.replaceChild(div.firstChild, mapEl);
      setVisibility(mapBtn, true);
    } else {
      removeChildren(mapEl);
      setVisibility(mapBtn, false);
    }
  }
  function setSvgDiagram(svgString) {
    const svgEl = document.getElementById("diagram-svg");
    const div = document.createElement("div");
    div.innerHTML = svgString;
    const newSvg = div.querySelector("svg");
    newSvg.id = "diagram-svg";
    newSvg.classList = svgEl.classList;
    newSvg.style.cssText = svgEl.style.cssText;
    svgEl.parentNode.replaceChild(newSvg, svgEl);
  }
  function setDiagramVisibility(type) {
    const map = document.getElementById("plantuml_map");
    const svg = document.getElementById("diagram-svg");
    container.setAttribute("data-diagram-type", type);
    setVisibility(png, type === "png");
    setVisibility(map, type === "png");
    setVisibility(svg, type === "svg");
    setVisibility(txt, type === "txt");
    setVisibility(pdf, type === "pdf");
  }
  // update diagram
  try {
    if (type === "png") {
      png.src = buildUrl("png", encodedDiagram, index);
      const map = await requestDiagram("map", encodedDiagram, index);
      setDiagramMap(map);
    } else if (type === "svg") {
      const svg = await requestDiagram("svg", encodedDiagram, index);
      setSvgDiagram(svg);
    } else if (type === "txt") {
      txt.innerHTML = await requestDiagram("txt", encodedDiagram, index);
    } else if (type === "pdf") {
      pdf.data = buildUrl("pdf", encodedDiagram, index);
    } else {
      const message = "unknown diagram type: " + type;
      (console.error || console.log)(message);
      return Promise.reject(message);
    }
    setDiagramVisibility(type);
  } catch (e) {
    // This should only happen if for example a broken diagram is requested.
    // Therefore, since the error message is already included in the response image, prevent further error messages.
    //(console.error || console.log)(e);
  }
}
