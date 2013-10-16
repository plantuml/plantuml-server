<%@ page info="index" contentType="text/html; charset=utf-8" pageEncoding="utf-8" session="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%
String contextRoot = request.getContextPath();
String host = "http://" + request.getServerName() + ":" + request.getServerPort();
String encoded = "";
String umltext = "";
String imgurl = "";
String svgurl = "";
String txturl = "";
Object mapNeeded = request.getAttribute("net.sourceforge.plantuml.servlet.mapneeded");
Object encodedAttribute = request.getAttribute("net.sourceforge.plantuml.servlet.encoded");
if (encodedAttribute != null) {
    encoded = encodedAttribute.toString();
    if (!encoded.isEmpty()) {
	    imgurl = host + contextRoot + "/img/" + encoded;
	    svgurl = host + contextRoot + "/svg/" + encoded;
	    txturl = host + contextRoot + "/txt/" + encoded;
        if (mapNeeded != null) {
            pageContext.setAttribute("mapurl", host + contextRoot + "/map/" + encoded);
        }
	}
}
Object decodedAttribute = request.getAttribute("net.sourceforge.plantuml.servlet.decoded");
if (decodedAttribute != null) {
	umltext = decodedAttribute.toString();
}
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <meta http-equiv="expires" content="0" />
    <meta http-equiv="pragma" content="no-cache" />
    <meta http-equiv="cache-control" content="no-cache, must-revalidate" />
    <link rel="stylesheet" href="<%=contextRoot %>/plantuml.css" type="text/css"/>
    <link rel="icon" href="<%=contextRoot %>/favicon.ico" type="image/x-icon"/> 
    <link rel="shortcut icon" href="<%=contextRoot %>/favicon.ico" type="image/x-icon"/>
    <title>PlantUMLServer</title>
</head>
<body>
<div id="header">
    <%-- PAGE TITLE --%>
    <h1>PlantUML Server</h1>
    <p>This application provides a servlet which serves images created by <a href="http://plantuml.sourceforge.net">PlantUML</a>.</p>
</div>
<div id="content">
    <%-- CONTENT --%>
    <form method="post" accept-charset="UTF-8"  action="<%=contextRoot %>/form">
        <p>
            <textarea name="text" cols="120" rows="10"><%=umltext %></textarea>
            <br/>
            <input type="submit" />
        </p>
    </form>
    <hr/>
    You can enter here a previously generated URL:
    <form method="post" action="<%=contextRoot %>/form">
        <p>
            <input name="url" type="text" size="150" value="<%=imgurl %>" />
            <br/>
            <input type="submit"/>
        </p>
    </form>
    <% if (!imgurl.isEmpty()) { %>
    <hr/>
    <a href="<%=svgurl%>">View as SVG</a>&nbsp;
    <a href="<%=txturl%>">View as ASCII Art</a>&nbsp;
    <% if (mapNeeded != null) { %>
    <a href="<c:out value="${mapurl}"/>">View Map Data</a>
    <% } //endif %>
    <p id="diagram">
        <% if (mapNeeded != null) { %>
        <img src="<%=imgurl %>" alt="PlantUML diagram" usemap="#umlmap" />
        <map name="umlmap">
            <c:import url="${mapurl}" />
        </map>
        <% } else { %>
        <img src="<%=imgurl %>" alt="PlantUML diagram" />
        <% } %>
    </p>
    <% } //endif %>
</div>
<!-- This comment is used by the TestProxy class  
@startuml
Bob -> Alice : hello
@enduml
-->
<%-- FOOTER --%>
<%@ include file="footer.jspf" %> 
</body>
</html>
