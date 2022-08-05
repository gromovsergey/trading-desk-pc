<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ui" %>
<c:set var="pageExt" scope="request" value="${existingGroup.ccgType.pageExtension}"/>

<c:if test="${existingGroup.tgtType.letter == 'C' && existingGroup.ccgType.letter == 'T'}">
    <c:set var="pageExt" value="Channel${pageExt}"/>
</c:if>

<title>
    <ui:windowTitleEntity entityName="${pageExt}CampaignCreativeGroup" isViewPage="${isViewPage}" id="${id}" name="${name}" colorStatus="${existingGroup.displayStatus}"/>
</title>
