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
    <script src="<%= hostpath %>/ascii-encoder.js"></script>
    <script src="<%= hostpath %>/webjars/codemirror/5.63.0/lib/codemirror.js"></script>
    <script src="<%= hostpath %>/webjars/zopfli.js/1.0.0/bin/zopfli.raw.min.js"></script>
    <script>
        var lastUpdate = Date.now();
        var lastContent = "";
        var syncTimer = null;

        function updateTimestamp() {
            lastUpdate = Date.now();
        }

        function updateLink(umlContent) {
        }

        function syncContent() {
            var timeDelta = Date.now() - lastUpdate;
            var content = document.myCodeMirror.getValue();
            if (timeDelta > 1000 && content != lastContent) {
                lastContent = content;
                var compressedContent = compress(content);
                var target = document.getElementById("theimg");
                target.src = target.src.substring(0, target.src.lastIndexOf('/') + 1) + compressedContent;

                target  = document.getElementById("url");
                target.value = target.value.substring(0, target.value.lastIndexOf('/') + 1) + compressedContent;

                target  = document.getElementById("urlpng");
                target.href = target.href.substring(0, target.href.lastIndexOf('/') + 1) + compressedContent;

                target  = document.getElementById("urlsvg");
                target.href = target.href.substring(0, target.href.lastIndexOf('/') + 1) + compressedContent;

                target  = document.getElementById("urltxt");
                target.href = target.href.substring(0, target.href.lastIndexOf('/') + 1) + compressedContent;

                target  = window.location;
                var newHref = target.href.substring(0, target.href.lastIndexOf('/') + 1) + compressedContent;
                history.replaceState(history.stat, document.title, newHref);
            }
        }

        function compress(content) {
            content = unescape(encodeURIComponent(content));
            var contentArray = [];
            for (var i = 0; i < content.length; ++i) {
                contentArray.push(content.charCodeAt(i));
            }
            var compressor = new Zopfli.RawDeflate(contentArray);
            var compressed = compressor.compress();
            return AsciiEncoder.encode(compressed);
        }

        function switchAutoRefresh() {
            var autoRefresh = document.getElementById("autorefresh");
            if (autoRefresh.className == "on") {
                autoRefresh.className = "off";
                clearInterval(syncTimer);
            } else {
                autoRefresh.className = "on";
                syncTimer = setInterval(syncContent, 900);
            }
        }
        window.onload = function() {
            document.myCodeMirror = CodeMirror.fromTextArea(
                document.getElementById("text"), 
                { lineNumbers: true }
            );
            document.myCodeMirror.on("change", updateTimestamp);
            lastContent = document.myCodeMirror.getValue();
            switchAutoRefresh();
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
        <form id="formuml" method="post" accept-charset="utf-8"  action="<%= hostpath %>/form"></form>
        <div class="code-block">
            <textarea id="text" form="formuml" name="text" cols="120" rows="10"><%= net.sourceforge.plantuml.servlet.PlantUmlServlet.stringToHTMLString(decoded) %></textarea>
        </div>
        <form method="post" action="<%= hostpath %>/form">
            <div class="inline-form">
                <div class="inline-input-wrapper">
                    <input id="url" name="url" type="text" title="You can enter here a previously generated URL" value="<%= imgurl %>" />
                </div>
                <div>
                    <input type="submit" value="Decode URL"/>
                </div>
            </div>
        </form>
        <img style="margin-right:10px;" id="autorefresh" title="Auto refresh" onclick="switchAutoRefresh()" class="off" src="<%= hostpath %>/svgrepo-refresh.svg" width="20" height="20">
        <input form="formuml" type="submit" value="Submit" style="margin-right:10px;vertical-align:top;" />
        <% if (hasImg) { %>
            <a id="urlpng" href="<%= imgurl %>">PNG</a>&nbsp;
            <a id="urlsvg" href="<%= svgurl %>">SVG</a>&nbsp;
            <a id="urltxt" href="<%= txturl %>">ASCII Art</a>&nbsp;
            <% if (hasMap) { %>
                <a href="<%= mapurl %>">View Map Data</a>
            <% } %>
            <% if (showSocialButtons) { %>
                <%@ include file="resource/socialbuttons2.jspf" %>
            <% } %>
            <p id="diagram">
                <img id="theimg" src="<%= imgurl %>" alt="PlantUML diagram" />
                <%= map %>
            </p>
        <% } %>
    </div>
    <%-- FOOTER --%>
    <%@ include file="footer.jspf" %> 
</body>
</html>
