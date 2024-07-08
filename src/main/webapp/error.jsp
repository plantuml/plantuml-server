<%@ page isErrorPage="true" contentType="text/html; charset=utf-8" pageEncoding="utf-8" session="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <base href="<%= request.getContextPath() %>/" />
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <meta http-equiv="expires" content="0" />
    <meta http-equiv="pragma" content="no-cache" />
    <meta http-equiv="cache-control" content="no-cache, must-revalidate" />
    <link rel="stylesheet" href="plantuml.css" type="text/css"/>
    <link rel="icon" href="favicon.ico" type="image/x-icon"/> 
    <link rel="shortcut icon" href="favicon.ico" type="image/x-icon"/>
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
        <li>Request that failed: <code><c:out value="${pageContext.errorData.requestURI}"/></code></li>
        <li>Status code: <code><c:out value="${pageContext.errorData.statusCode}"/></code></li>
        <li>Exception: <code><c:out value="${pageContext.errorData.throwable}"/></code></li>
    </ul>
    <hr/>
</body>
</html>
