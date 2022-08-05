<%@page contentType="text/html" %>
<%@page pageEncoding="UTF-8" %>

<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<ui:reportParameters data="${data}"/>

<s:if test="data">
	<ui:reportSummary data="${data}"/>

    <h2><fmt:message key="report.details"/></h2>

    <ui:report id="result" data="${data}"/>
    <s:if test="!data.rows.empty">
        <ui:reportExportLinks/>
    </s:if>
</s:if>
