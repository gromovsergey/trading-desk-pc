<%@page contentType="text/html" %>
<%@page pageEncoding="UTF-8" %>

<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>

<ui:report id="reportDataPageKeywords"
           data="${data.pageKeywords}"
           sort="true"
           sortUrl="sortPageKeywords.action"
           sortProperty="pageKeywordsSortColumn"/>
