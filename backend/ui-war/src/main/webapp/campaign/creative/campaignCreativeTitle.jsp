<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ui" %>
<title>
    <ui:windowTitleEntity entityName="CampaignCreativeLink" isViewPage="${isViewPage}" name="${creative.name}" id="${id}" colorStatus="${displayStatus}"/>
</title>
