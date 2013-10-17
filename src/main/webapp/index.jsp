<%@ page info="index" contentType="text/html; charset=utf-8" pageEncoding="utf-8" session="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:set var="contextroot" value="${pageContext.request.contextPath}" />
<c:set var="hostpath" value="http://${pageContext.request.serverName}:${pageContext.request.serverPort}${contextroot}" />
<c:if test="${!empty encoded}">
    <c:set var="imgurl" value="${hostpath}/img/${encoded}" />
    <c:set var="svgurl" value="${hostpath}/svg/${encoded}" />
    <c:set var="txturl" value="${hostpath}/txt/${encoded}" />
    <c:if test="${!empty mapneeded}">
        <c:set var="mapurl" value="${hostpath}/map/${encoded}" />
    </c:if>
</c:if>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <meta http-equiv="expires" content="0" />
    <meta http-equiv="pragma" content="no-cache" />
    <meta http-equiv="cache-control" content="no-cache, must-revalidate" />
    <link rel="stylesheet" href="${contextroot}/plantuml.css" type="text/css"/>
    <link rel="icon" href="${contextroot}/favicon.ico" type="image/x-icon"/> 
    <link rel="shortcut icon" href="${contextroot}/favicon.ico" type="image/x-icon"/>
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
    <form method="post" accept-charset="UTF-8"  action="${contextroot}/form">
        <p>
            <textarea name="text" cols="120" rows="10"><c:out value="${decoded}"/></textarea>
            <br/>
            <input type="submit" />
        </p>
    </form>
    <hr/>
    You can enter here a previously generated URL:
    <form method="post" action="${contextroot}/form">
        <p>
            <input name="url" type="text" size="150" value="${imgurl}" />
            <br/>
            <input type="submit"/>
        </p>
    </form>
    <c:if test="${!empty imgurl}">
        <hr/>
        <a href="${svgurl}">View as SVG</a>&nbsp;
        <a href="${txturl}">View as ASCII Art</a>&nbsp;
        <c:if test="${!empty mapurl}">
            <a href="${mapurl}">View Map Data</a>
        </c:if>
        <p id="diagram">
            <c:choose>
            <c:when test="${!empty mapurl}">
                <img src="${imgurl}" alt="PlantUML diagram" usemap="#umlmap" />
                <map name="umlmap">
                    <c:import url="${mapurl}" />
                </map>
            </c:when>
            <c:otherwise>
                <img src="${imgurl}" alt="PlantUML diagram" />
            </c:otherwise>
            </c:choose>
        </p>
    </c:if>
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
