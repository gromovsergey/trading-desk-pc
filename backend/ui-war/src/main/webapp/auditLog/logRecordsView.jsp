<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>

<c:if test="${not empty logRecords}">
    <ui:pages pageSize="${pageSize}"
        total="${total}"
        selectedNumber="${page}"
        visiblePagesCount="10"
        handler="goToPage"
        displayHeader="true"/>

    <c:forEach var="logRecord" items="${logRecords}">
        <%@ include file="logRecordBody.jsp"%>
    </c:forEach>

    <tiles:insertTemplate template="/auditLog/logDetails.jsp">
        <tiles:putAttribute name="container" value="#result"/>
    </tiles:insertTemplate>

    <ui:pages pageSize="${pageSize}"
        total="${total}"
        selectedNumber="${page}"
        visiblePagesCount="10"
        handler="goToPage"
        displayHeader="true"/>
</c:if>
<c:if test="${empty logRecords}">
    <fmt:message key="auditLog.noLog"/>
</c:if>
