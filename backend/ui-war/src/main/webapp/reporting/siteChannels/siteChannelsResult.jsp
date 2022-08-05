<%@page contentType="text/html" %>
<%@page pageEncoding="UTF-8" %>

<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>

<ui:reportParameters data="${data}"/>

<s:if test="data">
    <ui:report id="result" data="${data}"/>
    <s:if test="!data.rows.empty">
        <ui:reportExportLinks/>
    </s:if>
</s:if>
