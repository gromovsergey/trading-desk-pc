<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>

<ui:header>
    <ui:pageHeadingByTitle/>
    <c:if test="${ad:isPermitted('AdvertisingChannel.create', account)}">
        <ui:button message="channel.buttons.createBehavioral" action="BehavioralChannel/new${ad:accountParam('?accountId', account.id)}"/>
        <ui:button message="channel.buttons.createExpression" action="ExpressionChannel/new${ad:accountParam('?accountId', account.id)}"/>
    </c:if>
</ui:header>
<%@ include file="searchList.jsp"%>
