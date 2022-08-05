<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ui" %>

<c:set var="headerText">
    <fmt:message key="CampaignCredit.entityName.manage"/>:
    <c:out value="${id}"/>
</c:set>

<title>
    <ui:windowTitle attributeName="${headerText}" isSimpleText="true"/>
</title>