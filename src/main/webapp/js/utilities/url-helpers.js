/**************
* URL Helpers *
***************/

function resolvePath(path) {
  return PlantUmlLanguageFeatures.absolutePath(path);
}

function prepareUrl(url) {
  if (!(url instanceof URL)) {
    url = new URL(resolvePath(url));
  }
  // pathname excluding context path
  let base = new URL((document.querySelector("base") || {}).href || window.location.origin).pathname;
  if (base.slice(-1) === "/") base = base.slice(0, -1);
  const pathname = url.pathname.startsWith(base) ? url.pathname.slice(base.length) : url.pathname;
  // same as `UrlDataExtractor.URL_PATTERN`
  // regex = /\/\w+(?:\/(?<idx>\d+))?(?:\/(?<encoded>[^\/]+))?\/?$/gm;
  const regex = /\/\w+(?:\/(\d+))?(?:\/([^/]+))?\/?$/gm;
  const match = regex.exec(pathname);
  return [ url, pathname, { idx: match[1], encoded: match[2] } ];
}

function analyseUrl(url) {
  let _, idx, encoded;
  [url, _, { idx, encoded }] = prepareUrl(url);
  return {
    index: idx,
    encodedDiagram: encoded || url.searchParams.get("url"),
  };
}

function replaceUrl(url, encodedDiagram, index) {
  let oldPathname, encoded;
  [url, oldPathname, { encoded }] = prepareUrl(url);
  let pathname = oldPathname.slice(1);
  pathname = pathname.slice(0, pathname.indexOf("/"));
  if (index && index >= 0) pathname += "/" + index;
  if (encoded) pathname += "/" + encodedDiagram;
  if (oldPathname.slice(-1) === "/") pathname += "/";
  url.pathname = new URL(resolvePath(pathname)).pathname;
  if (url.searchParams.get("url")) {
    url.searchParams.set("url", encodedDiagram);
  }
  return { url, pathname };
}

function buildUrl(serletpath, encodedDiagram, index) {
  let pathname = serletpath;
  if (index && index >= 0) pathname += "/" + index;
  pathname += "/" + encodedDiagram;
  return pathname;
}
