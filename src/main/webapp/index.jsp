<%@ page info="index" contentType="text/html; charset=utf-8" pageEncoding="utf-8" session="false" %>

<%
    // diagram sources
    String decoded = request.getAttribute("decoded").toString();
    // properties
    boolean showSocialButtons = (boolean)request.getAttribute("showSocialButtons");
    boolean showGithubRibbon = (boolean)request.getAttribute("showGithubRibbon");
    // URL base
    String hostpath = request.getAttribute("hostpath").toString();
    // image URLs
    boolean hasImg = (boolean)request.getAttribute("hasImg");
    String imgurl = request.getAttribute("imgurl").toString();
    String svgurl = request.getAttribute("svgurl").toString();
    String txturl = request.getAttribute("txturl").toString();
    String mapurl = request.getAttribute("mapurl").toString();
    // map for diagram source if necessary
    boolean hasMap = (boolean)request.getAttribute("hasMap");
    String map = request.getAttribute("map").toString();
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <meta http-equiv="expires" content="0" />
    <meta http-equiv="pragma" content="no-cache" />
    <meta http-equiv="cache-control" content="no-cache, must-revalidate" />
    <link rel="icon" href="<%= hostpath %>/favicon.ico" type="image/x-icon"/> 
    <link rel="shortcut icon" href="<%= hostpath %>/favicon.ico" type="image/x-icon"/>
    <link rel="stylesheet" href="<%= hostpath %>/plantuml.css" />
    <link rel="stylesheet" href="<%= hostpath %>/webjars/codemirror/5.63.0/lib/codemirror.css" />
    <script src="<%= hostpath %>/webjars/codemirror/5.63.0/lib/codemirror.js"></script>
    <script>
        window.onload = function() {
            document.myCodeMirror = CodeMirror.fromTextArea(
                document.getElementById("text"), 
                { lineNumbers: true,
                  extraKeys: {Tab: false, "Shift-Tab": false} 
                }
            );
            };
    </script>
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
        <form method="post" accept-charset="utf-8"  action="<%= hostpath %>/form">
            <p> <label for="text">UML Editor Content</label>
                <textarea id="text" name="text" cols="120" rows="10"><%= net.sourceforge.plantuml.servlet.PlantUmlServlet.stringToHTMLString(decoded) %></textarea>
                <input type="submit" value="Submit" title="Submit Code and generate diagram"/>&nbsp;
                <input type="submit" value="Copy Content to Clipboard" title="Copy Content to the clipboard" onclick="copyToClipboard('text','Content');return false; ">
            </p>
        </form>
        <hr/>
        <p>You can enter here a previously generated URL:</p>
        <form method="post" action="<%= hostpath %>/form">
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
            <% if (hasMap) { %>
                <a href="<%= mapurl %>">View Map Data</a>
            <% } %>
            <% if (showSocialButtons) { %>
                <%@ include file="resource/socialbuttons2.jspf" %>
            <% } %>
            <p id="diagram">
                <img src="<%= imgurl %>" alt="PlantUML diagram" />
                <%= map %>
            </p>
        <% } %>
    </div>
    <script>
        var clipboard_write=false;
        var clipboard_read=false;
        
        if (navigator.permissions){
        navigator.permissions.query({ name: "clipboard-write" }).then((result) => {
            if (result.state == "granted" || result.state == "prompt") {
                clipboard_write = true;
            }
        });
        navigator.permissions.query({ name: "clipboard-read" }).then((result) => {
            if (result.state == "granted" || result.state == "prompt") {
                clipboard_read = true;
            }
        });
        };

    function copyToClipboard(fieldid, fielddesc) {
        if (clipboard_write == true){
        var copyText = '';
        copyText = document.getElementById(fieldid).value;
            navigator.clipboard.writeText(document.getElementById(fieldid).value)
            alert(fielddesc + " copied to clipboard");
        }
        return false;
    }
</script>
    <%-- FOOTER --%>
    <%@ include file="footer.jspf" %> 
</body>
</html>