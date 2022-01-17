<%@ page isErrorPage="true" contentType="text/html; charset=utf-8" pageEncoding="utf-8" session="false" %>

<%
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
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <meta http-equiv="expires" content="0" />
    <meta http-equiv="pragma" content="no-cache" />
    <meta http-equiv="cache-control" content="no-cache, must-revalidate" />
    <link rel="stylesheet" href="<%= hostpath %>/plantuml.css" type="text/css"/>
    <link rel="icon" href="<%= hostpath %>/favicon.ico" type="image/x-icon"/> 
    <link rel="shortcut icon" href="<%= hostpath %>/favicon.ico" type="image/x-icon"/>
    <title>PlantUMLServer Error</title>
</head>
<body>
    <p>
        Sorry, but things didn't work out as planned.
    </p>
    <hr/>
    <jsp:useBean id="now" class="java.util.Date" />
    <ul>
        <li><%= now.toString() %></li>
        <li>Request that failed: <%= pageContext.getErrorData().getRequestURI() %></li>
        <li>Status code: <%= pageContext.getErrorData().getStatusCode() %></li>
        <li>Exception: <%= pageContext.getErrorData().getThrowable() %></li>
    </ul>
    <hr/>
</body>
</html>
