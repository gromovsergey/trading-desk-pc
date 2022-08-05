<%@page contentType="text/html" %>
<%@page pageEncoding="UTF-8" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>


<ui:reportParameters data="${data}"/>

<s:if test="data">

    <h2><fmt:message key="reports.channelTriggersReport.urls"/></h2>
    <%@include file="urlsDetails.jsp" %>

    <h2><fmt:message key="reports.channelTriggersReport.pageKeywords"/></h2>
    <%@include file="pageKeywordsDetails.jsp" %>

    <h2><fmt:message key="reports.channelTriggersReport.searchKeywords"/></h2>
    <%@include file="searchKeywordsDetails.jsp" %>

    <h2><fmt:message key="reports.channelTriggersReport.urlKeywords"/></h2>
    <%@include file="urlKeywordsDetails.jsp" %>

    <c:if test="${not empty data.urls.rows or not empty data.pageKeywords.rows or not empty data.searchKeywords.rows or not empty data.urlKeywords.rows}">
        <ui:reportExportLinks formats="Excel"/>
    </c:if>

</s:if>
