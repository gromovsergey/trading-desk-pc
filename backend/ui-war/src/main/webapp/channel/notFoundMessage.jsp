<%@ page contentType="text/html" %>
<%@ page pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:choose>
    <c:when test="${ad:isPermitted('AdvertisingChannel.create', account)}">
        <fmt:message key="${messageKey}">
            <fmt:param>
                <a href="${_context}/channel/BehavioralChannel/new.action${ad:accountParam('?accountId', accountId)}" target="_blank">
                    <fmt:message key="channel.createBehavioral"/>
                </a>
            </fmt:param>
            <fmt:param>
                <a href="${_context}/channel/ExpressionChannel/new.action${ad:accountParam('?accountId', accountId)}" target="_blank">
                    <fmt:message key="channel.createExpression"/>
                </a>
            </fmt:param>
        </fmt:message>
    </c:when>
    <c:otherwise>
        <fmt:message key="${messageKey}.withoutLinks"/>
    </c:otherwise>
</c:choose>