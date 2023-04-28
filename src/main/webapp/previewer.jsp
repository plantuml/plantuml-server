<%@ page info="index" contentType="text/html; charset=utf-8" pageEncoding="utf-8" session="false" %>
<%
  // diagram sources
  String encoded = request.getAttribute("encoded").toString();
  String index = request.getAttribute("index").toString();
  String diagramUrl = ((index.isEmpty()) ? "" : index + "/") + encoded;
  // map for diagram source if necessary
  String map = request.getAttribute("map").toString();
  boolean hasMap = !map.isEmpty();
  // properties
  boolean showSocialButtons = (boolean)request.getAttribute("showSocialButtons");
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html lang="en" xmlns="http://www.w3.org/1999/xhtml">
<head>
  <%@ include file="resource/htmlheadbase.jsp" %>
  <title>PlantUML Server</title>
</head>
<body>
  <div class="content viewer-content">
    <%-- Preview --%>
    <%@ include file="resource/preview.jsp" %>
  </div>
</body>
</html>
