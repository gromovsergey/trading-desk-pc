<%@page contentType="text/html" %>
<%@page pageEncoding="UTF-8" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<ui:reportParameters data="${data}"/>

<s:if test="data">

    <ui:reportSummary data="${data}"/>

    <c:if test="${not empty data.summary}">
        <div class="wrapper">
            <fmt:message key="report.comment.isp"/>
        </div>
    </c:if>

    <h2><fmt:message key="report.details"/></h2>

    <ui:report id="reportData" data="${data}"/>

    <c:if test="${not empty data.rows}">
        <ui:reportExportLinks/>
    </c:if>

</s:if>

