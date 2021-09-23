<%@ page info="index" contentType="text/html; charset=utf-8" pageEncoding="utf-8" session="false" %>

<%@ page import="java.util.Properties" %>
<%@ page import="net.sourceforge.plantuml.servlet.utility.Configuration" %>

<%
    Properties cfg = Configuration.get();
    boolean showSocialButtons = cfg.getProperty("SHOW_SOCIAL_BUTTONS") == "on";
    boolean showGithubRibbon = cfg.getProperty("SHOW_GITHUB_RIBBON") == "on";

    String contextroot = request.getContextPath();
    String port = "";
    if (
        (request.getScheme() == "http" && request.getServerPort() != 80)
        ||
        (request.getScheme() == "https" && request.getServerPort() != 443)
    ) {
        port = ":" + request.getServerPort();
    }
    String scheme = request.getScheme();
    if (request.getHeader("x-forwarded-proto") != null && request.getHeader("x-forwarded-proto") != "") {
        scheme = request.getHeader("x-forwarded-proto");
    }
    String hostpath = scheme + "://" + request.getServerName() + port + contextroot;

    String encoded = (request.getAttribute("encoded") == null) ? "" : request.getAttribute("encoded").toString();
    String decoded = (request.getAttribute("decoded") == null) ? "" : request.getAttribute("decoded").toString();

    String imgurl = hostpath + "/png/" + encoded;
    String svgurl = hostpath + "/svg/" + encoded;
    String txturl = hostpath + "/txt/" + encoded;
    String mapurl = hostpath + "/map/" + encoded;

    boolean hasImg = encoded != null && encoded != "";
    boolean hasMap = request.getAttribute("mapneeded") != null && request.getAttribute("mapneeded").toString() != "";
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
    <%-- <link rel="stylesheet" href="https://codemirror.net/lib/codemirror.css" /> --%>
    <%-- <script src="https://codemirror.net/lib/codemirror.js"></script> --%>
    <script>
        window.onload = function() {
            document.myCodeMirror = CodeMirror.fromTextArea(
                document.getElementById("text"), 
                { lineNumbers: true }
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
            <p>
                <textarea id="text" name="text" cols="120" rows="10"><%= decoded %></textarea>
                <input type="submit" />
            </p>
        </form>
        <hr/>
        <p>You can enter here a previously generated URL:</p>
        <form method="post" action="<%= hostpath %>/form">
            <p>
                <input name="url" type="text" size="150" value="<%= imgurl %>" />
                <br/>
                <input type="submit"/>
            </p>
        </form>
        <% if (hasImg) { %>
            <hr/>
            <a href="<%= svgurl %>">View as SVG</a>&nbsp;
            <a href="<%= txturl %>">View as ASCII Art</a>&nbsp;
            <% if (hasMap) { %>
                <a href="<%= mapurl %>">View Map Data</a>
            <% } %>
            <% if (showSocialButtons) { %>
                <%@ include file="resource/socialbuttons2.jspf" %>
            <% } %>
            <p id="diagram">
                <img src="<%= imgurl %>" alt="PlantUML diagram" />
                <% if (hasMap) { %>
                    <%= (request.getAttribute("map") == null) ? "" : request.getAttribute("map").toString() %>
                <% } %>
            </p>
        <% } %>
    </div>
    <%-- FOOTER --%>
    <%@ include file="footer.jspf" %> 
</body>
</html>
