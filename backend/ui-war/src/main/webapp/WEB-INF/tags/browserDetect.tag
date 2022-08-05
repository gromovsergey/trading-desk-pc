<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core"   prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt"    prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>

<%@ attribute name="type" required="true" %>

<%
String userAgent    = request.getHeader("user-agent");
if (userAgent.indexOf("Firefox") > -1) { %>
    <c:if test="${type=='firefox'}">
        <jsp:doBody />
    </c:if>
<% } else if (userAgent.indexOf("Trident") > -1) { %>
    <c:if test="${type=='ie'}">
        <jsp:doBody />
    </c:if>
<% } else if (userAgent.indexOf("Chrome") > -1) { %>
    <c:if test="${type=='chrome'}">
        <jsp:doBody />
    </c:if>
<% } else if (userAgent.indexOf("Opera") > -1) { %>
    <c:if test="${type=='opera'}">
        <jsp:doBody />
    </c:if>
<% } else { %>
    <c:if test="${type=='safari'}">
        <jsp:doBody />
    </c:if>
<% } %>