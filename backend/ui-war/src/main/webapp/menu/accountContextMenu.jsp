<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%@ include file="/auditLog/logHighlightMenu.jsp" %>
<c:set var="forosContext" value="${_context}"/>
<c:choose>
    <c:when test="${contextName == 'global.menu.advertisers' and forosContext=='/admin'}">
        <jsp:include page="/menu/internalAdvertiserMenuBody.jsp"/>
    </c:when>
    <c:when test="${contextName == 'global.menu.publishers' and forosContext=='/admin'}">
        <jsp:include page="/menu/internalPublisherMenuBody.jsp"/>
    </c:when>
    <c:when test="${contextName == 'global.menu.isps' and forosContext=='/admin'}">
        <jsp:include page="/menu/internalISPMenuBody.jsp"/>
    </c:when>
    <c:when test="${contextName == 'global.menu.cmps' and forosContext=='/admin'}">
        <jsp:include page="/menu/internalCMPMenuBody.jsp"/>
    </c:when>
    <c:when test="${forosContext=='/advertiser'}">
        <jsp:include page="/menu/agencyUserMenuBody.jsp"/>
    </c:when>
</c:choose>
