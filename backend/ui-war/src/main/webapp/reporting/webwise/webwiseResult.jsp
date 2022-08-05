<%@page contentType="text/html" %>
<%@page pageEncoding="UTF-8" %>

<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>


<ui:reportParameters data="${data}"/>

<s:if test="data">

    <ui:reportSummary data="${data}"/>

    <div class="wrapper">
        <s:text name="report.comment.webwise.note"/>
    </div>

    <h2><fmt:message key="report.details"/></h2>


    <ui:report id="result" data="${data}"/>

    <s:if test="!data.rows.empty">
        <ui:reportExportLinks/>
    </s:if>
</s:if>
