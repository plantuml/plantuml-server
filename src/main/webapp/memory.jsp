<%@ page info="index" contentType="text/html; charset=utf-8" pageEncoding="utf-8" session="false" %>

<%
String contextRoot = request.getContextPath();
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <meta http-equiv="expires" content="0" />
    <meta http-equiv="pragma" content="no-cache" />
    <meta http-equiv="cache-control" content="no-cache, must-revalidate" />
    <link rel="icon" href="<%=contextRoot %>/favicon.ico" type="image/x-icon"/> 
    <link rel="shortcut icon" href="<%=contextRoot %>/favicon.ico" type="image/x-icon"/>
    <!-- SimpleTabs -->
	<link rel="stylesheet" media="screen" type="text/css" href="<%=contextRoot %>/simpletabs.css" />
	<script type="text/javascript" src="<%=contextRoot %>/simpletabs_1.3.packed.js"></script>
    <title>PlantUML memory</title>
</head>
<body>

<h3>Memory</h3>
<% net.sourceforge.plantuml.pstat.Stats.getInstance().memories(200, 12, 12).printSvg(out); %>
<p><hr>
<h3>Threads</h3>
<% net.sourceforge.plantuml.pstat.Stats.getInstance().threads(200, 12, 12).printSvg(out); %>


</body>
</html>
