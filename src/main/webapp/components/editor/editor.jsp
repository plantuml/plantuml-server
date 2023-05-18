<div id="editor-main-container" class="editor flex-main flex-rows">
  <div>
    <%@ include file="/components/editor/url-input/editor-url-input.jsp" %>
  </div>
  <div class="flex-main monaco-editor-container">
    <textarea id="initCode" name="initCode" style="display: none;"><%= net.sourceforge.plantuml.servlet.PlantUmlServlet.stringToHTMLString(decoded) %></textarea>
    <div id="monaco-editor"></div>
    <%@ include file="/components/editor/menu/editor-menu.jsp" %>
  </div>
</div>
