<%@page contentType="text/html" %>
<%@page pageEncoding="UTF-8" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>

<ui:reportParameters data="${data}"/>

<s:if test="data">
    <ui:reportSummary data="${data}"/>

    <h2><fmt:message key="report.details"/></h2>

    <ui:report id="reportData" data="${data}"/>

    <c:if test="${not empty data.rows}">
        <ui:reportExportLinks/>
    </c:if>
</s:if>
