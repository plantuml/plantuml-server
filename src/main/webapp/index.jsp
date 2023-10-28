<%@ page info="index" contentType="text/html; charset=utf-8" pageEncoding="utf-8" session="false" %>
<%
  // diagram sources
  String encoded = request.getAttribute("encoded").toString();
  String decoded = request.getAttribute("decoded").toString();
  String index = request.getAttribute("index").toString();
  String diagramUrl = ((index.isEmpty()) ? "" : index + "/") + encoded;
  // map for diagram source if necessary
  String map = request.getAttribute("map").toString();
  boolean hasMap = !map.isEmpty();
  // properties
  boolean showSocialButtons = (boolean)request.getAttribute("showSocialButtons");
  boolean showGithubRibbon = (boolean)request.getAttribute("showGithubRibbon");
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html lang="en" xmlns="http://www.w3.org/1999/xhtml">
<head>
  <%@ include file="/components/app-head.jsp" %>
  <title>PlantUML Server</title>
</head>
<body>
  <div class="app flex-rows">
    <div class="header">
      <%@ include file="/components/header/header.jsp" %>
    </div>
    <div class="main flex-main flex-columns">
      <%@ include file="/components/editor/editor.jsp" %>
      <div id="previewer-main-container" class="previewer flex-main">
        <%@ include file="/components/preview/preview.jsp" %>
      </div>
    </div>
    <div class="footer">
      <%@ include file="/components/footer/footer.jsp" %>
    </div>
    <!-- editor modals -->
    <%@ include file="/components/modals/diagram-import/diagram-import.jsp" %>
    <%@ include file="/components/modals/diagram-export/diagram-export.jsp" %>
  </div>
</body>
</html>
