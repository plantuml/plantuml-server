<%@ page info="index" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>

<%
String contextRoot = request.getContextPath();
String host = "http://" + request.getServerName() + ":" + request.getServerPort();
String encoded = "";
String umltext = "";
String imgurl = "";
Object encodedAttribute = request.getAttribute("net.sourceforge.plantuml.servlet.encoded");
if (encodedAttribute != null) {
    encoded = encodedAttribute.toString();
    if (!encoded.isEmpty()) {
	    imgurl = host + contextRoot + "/img/" + encoded;
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
    <h1>PlantUMLServer</h1>
    <p>This application provides a servlet which serves images created by <a href="http://plantuml.sourceforge.net">PlantUML</a>.</p>
</div>
<div id="content">
    <%-- CONTENT --%>
    <form method="post" action="<%=contextRoot %>/form">
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
    <% if ( !imgurl.isEmpty()) { %>
    <hr/>
    <p>You can use the following URL:
        <br/>
        <a href="<%=imgurl %>"><code>&lt;img src="<%=imgurl %>" /&gt;</code></a>
        <br/><br/>
        <img id="diagram" src="<%=imgurl %>" alt="PlantUML diagram"/>
    </p>
    <% } //endif %>
</div>

<%-- FOOTER 
<%@ include file="util/footer.jspf" %> --%>
</body>
</html>
