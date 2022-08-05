<%@page contentType="text/html" %>
<%@page pageEncoding="UTF-8" %>

<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>

<ui:report id="reportDataUrls"
           data="${data.urls}"
           sort="true"
           sortUrl="sortUrls.action"
           sortProperty="urlsSortColumn"/>
