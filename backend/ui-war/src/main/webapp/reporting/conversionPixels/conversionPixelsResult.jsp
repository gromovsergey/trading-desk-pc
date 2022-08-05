<%@page contentType="text/html" %>
<%@page pageEncoding="UTF-8" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<ui:reportParameters data="${data}"/>

<s:if test="data">

    <ui:report id="reportData" data="${data}"/>

    <c:if test="${not empty data.rows}">
        <ui:reportExportLinks/>
    </c:if>

</s:if>

