/*********************
* Preview Diagram JS *
**********************/

async function initializeDiagram(force = false) {
  if (force || document.appConfig.diagramPreviewType !== "png") {
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

  async function requestDiagram(type, encodedDiagram, index, options = {}) {
    return makeRequest("GET", buildUrl(type, encodedDiagram, index), options);
  }
  function setPngDiagram(pngBlob) {
    png.src = URL.createObjectURL(pngBlob);
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
  function setAsciiDiagram(asciiString) {
    function dedent(str) {
      const lines = str.replace(/\r\n/g, '\n').split('\n');
      // Find the minimum indentation among non-blank lines
      let minIndent = Infinity;
      for (const line of lines) {
        if (/^\s*$/.test(line)) continue; // skip blank lines
        const m = line.match(/^(\s*)/);
        const indent = m ? m[1].length : 0;
        if (indent < minIndent) minIndent = indent;
      }
      if (minIndent === Infinity) return str; // all-blank
      // Remove that indentation from each non-blank line
      return lines
        .map(line => {
          if (/^\s*$/.test(line)) return line;
          return line.slice(minIndent);
        })
        .join('\n');
    }
    txt.innerHTML = dedent(asciiString);
  }
  function setPdfDiagram(pdfBlob) {
    pdf.data = URL.createObjectURL(pdfBlob);
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
      // png.src = buildUrl("png", encodedDiagram, index);  // no header support for uml theme
      setPngDiagram(await requestDiagram("png", encodedDiagram, index, {responseType: "blob"}));
      setDiagramMap(await requestDiagram("map", encodedDiagram, index));
    } else if (type === "svg") {
      setSvgDiagram(await requestDiagram("svg", encodedDiagram, index));
    } else if (type === "txt") {
      setAsciiDiagram(await requestDiagram("txt", encodedDiagram, index));
    } else if (type === "pdf") {
      // pdf.data = buildUrl("pdf", encodedDiagram, index);  // no header support for uml theme
      setPdfDiagram(await requestDiagram("pdf", encodedDiagram, index, {responseType: "blob"}));
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
