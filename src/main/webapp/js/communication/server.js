/***********************
* Server Communication *
************************/

function makeRequest(
  method,
  url,
  {
    data = null,
    headers = { "Content-Type": "text/plain" },
    responseType = "text",
    baseUrl = "",
  } = {}
) {
  return PlantUmlLanguageFeatures.makeRequest(
    method,
    url,
    { data, headers, responseType, baseUrl }
  );
}
