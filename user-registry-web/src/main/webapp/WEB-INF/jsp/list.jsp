<%-- 
    Document   : list
    Created on : 15.03.2018, 10:20:15
    Author     : vasil
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Личный кабинет</title>       
        <link href="css/main.css" rel="stylesheet" type="text/css"/>
    </head>
    <body>
        <div class="header">
            HEADER
        </div>
        <div class="main">
            <div class="left_panel">
                <c:forEach items="${list}" var="item">
                    ${item.id}
                </c:forEach>
            </div>

        </div>

        <div id="footer">низ сайта</div>
    </body>
</html>
