<%@page contentType="text/html" %>
<%@page pageEncoding="UTF-8" %>

<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>

<ui:report id="reportDataUrlKeywords"
           data="${data.urlKeywords}"
           sort="true"
           sortUrl="sortUrlKeywords.action"
           sortProperty="urlKeywordsSortColumn"/>
