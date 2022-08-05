<%@page contentType="text/html" %>
<%@page pageEncoding="UTF-8" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>

<ui:reportParameters data="${data}"/>

<s:if test="data">
    <%@include file="olapCustomDetails.jsp"%>

    <c:if test="${not empty data.rows}">
        <ui:reportExportLinks formats="CSV, Excel, Excel_noLinks"/>
    </c:if>
</s:if>
