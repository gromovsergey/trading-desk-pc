<%@page contentType="text/html" %>
<%@page pageEncoding="UTF-8" %>

<%@ taglib tagdir="/WEB-INF/tags" prefix="ui" %>

<ui:report id="expressionPerformanceReport"
           data="${reportData}"
           sort="true"
           sortUrl="${_context}/campaign/group/expressionPerformanceStats.action?id=${id}"
           sortProperty="sortColumn"/>
