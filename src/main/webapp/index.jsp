<%@ page info="index" contentType="text/html; charset=utf-8" pageEncoding="utf-8" session="false" %>

<%
    // diagram sources
    String decoded = request.getAttribute("decoded").toString();
    // properties
    boolean showSocialButtons = (boolean)request.getAttribute("showSocialButtons");
    boolean showGithubRibbon = (boolean)request.getAttribute("showGithubRibbon");
    // image URLs
    boolean hasImg = (boolean)request.getAttribute("hasImg");
    String imgurl = request.getAttribute("imgurl").toString();
    String svgurl = request.getAttribute("svgurl").toString();
    String txturl = request.getAttribute("txturl").toString();
    String pdfurl = request.getAttribute("pdfurl").toString();
    String mapurl = request.getAttribute("mapurl").toString();
    // map for diagram source if necessary
    boolean hasMap = (boolean)request.getAttribute("hasMap");
    String map = request.getAttribute("map").toString();
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <base href="<%= request.getContextPath() %>/" />
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <meta http-equiv="expires" content="0" />
    <meta http-equiv="pragma" content="no-cache" />
    <meta http-equiv="cache-control" content="no-cache, must-revalidate" />
    <link rel="icon" href="favicon.ico" type="image/x-icon"/> 
    <link rel="shortcut icon" href="favicon.ico" type="image/x-icon"/>
    <link rel="stylesheet" href="plantuml.css" />
    <link rel="stylesheet" href="webjars/codemirror/5.63.0/lib/codemirror.css" />
    <script src="plantuml.js"></script>
    <script src="webjars/codemirror/5.63.0/lib/codemirror.js"></script>
    <title>PlantUMLServer</title>
</head>
<body>
    <div id="header">
        <%-- PAGE TITLE --%>
        <h1>PlantUML Server</h1>
        <% if (showSocialButtons) { %>
            <%@ include file="resource/socialbuttons1.html" %>
        <% } %>
        <% if (showGithubRibbon) { %>
            <%@ include file="resource/githubribbon.html" %>
        <% } %>
        <p>Create your <a href="https://plantuml.com">PlantUML</a> diagrams directly in your browser!</p>
    </div>
    <div id="content">
        <%-- CONTENT --%>
        <form method="post" accept-charset="utf-8"  action="form">
            <p> <label for="text">UML Editor Content</label>
                <textarea id="text" name="text" cols="120" rows="10"><%= net.sourceforge.plantuml.servlet.PlantUmlServlet.stringToHTMLString(decoded) %></textarea>
                <input type="submit" value="Submit" title="Submit Code and generate diagram"/>&nbsp;
                <input type="submit" value="Copy Content to Clipboard" title="Copy Content to the clipboard" onclick="copyToClipboard('text','Content');return false; ">
            </p>
        </form>
        <hr/>
        <p>You can enter here a previously generated URL:</p>
        <form method="post" action="form">
            <p> <label for="url">previously generated URL</label>
                <input id="url" name="url" type="text" size="150" value="<%= imgurl %>" />
                <br/>
                <input type="submit" value="Decode URL" title="Decode URL and show code and diagram"/>&nbsp;
                <input type="submit" value="Copy URL to Clipboard" title="Copy URL to the clipboard" onclick="copyToClipboard('url','URL');return false; ">
            </p>
        </form>
        <% if (hasImg) { %>
            <hr/>
            <a href="<%= imgurl %>" title="View diagram as PNG">View as PNG</a>&nbsp;
            <a href="<%= svgurl %>" title="View diagram as SVG">View as SVG</a>&nbsp;
            <a href="<%= txturl %>" title="View diagram as ASCII Art">View as ASCII Art</a>&nbsp;
            <a href="<%= pdfurl %>" title="View diagram as PDF">View as PDF</a>&nbsp;
            <% if (hasMap) { %>
                <a href="<%= mapurl %>">View Map Data</a>
            <% } %>
            <% if (showSocialButtons) { %>
                <%@ include file="resource/socialbuttons2.jspf" %>
            <% } %>
            <p id="diagram">
                <% if (!hasMap) { %>
                    <img src="<%= imgurl %>" alt="PlantUML diagram" />
                <% } else { %>
                    <img src="<%= imgurl %>" alt="PlantUML diagram" usemap="#plantuml_map" />
                    <%= map %>
                <% } %>
            </p>
        <% } %>
    </div>
    <%-- FOOTER --%>
    <%@ include file="footer.jspf" %> 
</body>
</html>