<%@ page contentType="text/html" %>
<%@ page pageEncoding="UTF-8" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">
    <head profile="http://www.w3.org/2005/10/profile">
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link rel="icon" href="/images/logo.png"/>
        <link rel="shortcut icon" href="/images/logo.png"/>
        <script type="text/javascript" src="/thirdparty/jQuery/1.8.2/jquery.min.js"></script>
        <script type="text/javascript" src="/scripts/${timestampVersion}/cookie.js"></script>
        <link rel="stylesheet" href="/styles/${timestampVersion}/common.css">
        <c:if test="${hieroglyphSupport}">
            <link rel="stylesheet" href="/styles/hieroglyph.css">
        </c:if>
    </head>
    <body>
        <table id="root">
            <tr id="header">
                <td class="rootCell">
                    <div id="headContainer">
                        <div id="applicationLogo">Target RTB</div>
                    </div>
                </td>
            </tr>
            <tr id="breadCrumbs">
                <td class="rootCell">
                    <div class="contentBody"></div>
                </td>
            </tr>
            <tr id="content">
                <td class="rootCell">
                    <div class="contentBody">
                        <c:choose>
                            <c:when test="${errorCode == '404'}">
                                Status ${errorCode}
                                <span class="errors">Resource not found</span>
                            </c:when>
                            <c:otherwise>
                                <c:if test="${errorCode != null && errorCode !=''}"> Status <c:out value="${errorCode}"/> </c:if>
                                <span class="errors">Unexpected error</span>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </td>
            </tr>
            <tr id="footer">
                <td>
                    <div id="copyright">
                        <fmt:message key="form.copyright"/>
                    </div>
                </td>
            </tr>
        </table>
    </body>
</html>
