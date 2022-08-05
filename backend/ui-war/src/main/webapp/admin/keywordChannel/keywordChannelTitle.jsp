<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ui" %>

<title>
    <ui:windowTitleEntity entityName="KeywordChannel" isViewPage="${isViewPage}" name="${ad:shortString(name, 80)}" id="${id}" colorStatus="${displayStatus}"/>
</title>
