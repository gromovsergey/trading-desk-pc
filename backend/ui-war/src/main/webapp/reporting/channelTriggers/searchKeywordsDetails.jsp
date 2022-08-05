<%@page contentType="text/html" %>
<%@page pageEncoding="UTF-8" %>

<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>

<ui:report id="reportDataSearchKeywords"
           data="${data.searchKeywords}"
           sort="true"
           sortUrl="sortSearchKeywords.action"
           sortProperty="searchKeywordsSortColumn"/>
