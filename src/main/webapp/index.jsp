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
  <%@ include file="resource/htmlheadbase.jsp" %>
  <title>PlantUML Server</title>
</head>
<body>
  <div class="app flex-rows">
    <div class="header">
      <h1>PlantUML Server</h1>
      <% if (showSocialButtons) { %>
        <%@ include file="resource/socialbuttons1.html" %>
      <% } %>
      <% if (showGithubRibbon) { %>
        <%@ include file="resource/githubribbon.html" %>
      <% } %>
      <p>Create your <a href="https://plantuml.com">PlantUML</a> diagrams directly in your browser!</p>
    </div>
    <div class="main flex-main flex-columns">
      <div id="editor-main-container" class="editor flex-main flex-rows">
        <div>
          <div class="btn-input">
            <input id="url" type="text" name="url" value="png/<%= diagramUrl %>" />
            <input type="image" alt="copy" src="assets/actions/copy.svg" onclick="copyUrlToClipboard()" />
          </div>
        </div>
        <div class="flex-main monaco-editor-container">
          <textarea id="initCode" name="initCode" style="display: none;"><%= net.sourceforge.plantuml.servlet.PlantUmlServlet.stringToHTMLString(decoded) %></textarea>
          <div id="monaco-editor"></div>
          <div class="editor-menu" tabindex="-1">
            <div class="menu-kebab">
              <div class="kebab-circle"></div>
              <div class="kebab-circle"></div>
              <div class="kebab-circle"></div>
              <div class="kebab-circle"></div>
              <div class="kebab-circle"></div>
            </div>
            <div class="menu-items">
              <input class="menu-item" type="image" alt="copy" title="Copy code" src="assets/actions/copy.svg" onclick="copyCodeToClipboard()" />
              <input class="menu-item" type="image" alt="import" title="Import diagram" src="assets/actions/upload.svg" onclick="openModal('diagram-import')" />
              <input class="menu-item" type="image" alt="export" title="Export diagram" src="assets/actions/download.svg" onclick="openModal('diagram-export')" />
            </div>
          </div>
        </div>
      </div>
      <div id="previewer-main-container" class="previewer flex-main">
        <%@ include file="resource/preview.jsp" %>
      </div>
    </div>
    <div class="footer">
      <%@ include file="resource/footer.jsp" %>
    </div>
    <!-- editor modals -->
    <%@ include file="resource/diagram-import.jsp" %>
    <%@ include file="resource/diagram-export.jsp" %>
  </div>
</body>
</html>
