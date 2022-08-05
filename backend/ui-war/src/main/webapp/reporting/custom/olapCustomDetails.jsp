<%@page contentType="text/html" %>
<%@page pageEncoding="UTF-8" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>

<div id="reportSortDiv">
    <ui:reportSummary data="${data}"/>

    <h2><fmt:message key="report.details"/></h2>

    <ui:report id="reportData"
               data="${data}"
               sort="true"
               sortId="reportSortDiv"
               sortUrl="cancellableRun.action?detailsOnly=true"/>
</div>