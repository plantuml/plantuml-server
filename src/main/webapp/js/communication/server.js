/***********************
* Server Communication *
************************/

function makeRequest(
  method,
  url,
  {
    data = null,
    headers = {
      "Content-Type": "text/plain",
      "X-Preferred-Color-Mapper": document.appConfig.umlColorMapper,
    },
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
