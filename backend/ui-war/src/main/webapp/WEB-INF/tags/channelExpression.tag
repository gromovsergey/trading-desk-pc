<%@ tag import="com.foros.model.DisplayStatus" %>
<%@ tag description="Expression Channel's Expression" %>

<%@ attribute name="expression" required="true" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ui" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib uri="/struts-tags" prefix="s" %>

<c:set var="replacedIds" value=""/>
<c:set var="formattedExpression" value="${pageScope.expression}"/>
<c:set var="formattedExpression" value="${fn:replace(formattedExpression, '&', ' <span class=\"b-expicon b-expicon__and\"></span> ')}"/>
<c:set var="formattedExpression" value="${fn:replace(formattedExpression, '^', ' <span class=\"b-expicon b-expicon__andnot\"></span> ')}"/>
<c:set var="formattedExpression" value="${fn:replace(formattedExpression, '|', ' <span class=\"b-expicon b-expicon__or\"></span> ')}"/>
<c:set var="formattedExpression" value="${fn:replace(formattedExpression, '(', ' <span class=\"b-expicon b-expicon__lb\"></span> ')}"/>
<c:set var="formattedExpression" value="${fn:replace(formattedExpression, ')', ' <span class=\"b-expicon b-expicon__rb\"></span> ')}"/>
<c:set var="LiveEnumValue" value='<%=DisplayStatus.Major.LIVE%>'/>

<c:forTokens items="${pageScope.expression}" delims="(|&^)" var="channelId">
    <s:set var="ch" value="usedChannel(#attr.channelId)"/>

    <c:choose>
        <c:when test="${ad:isPermitted('AdvertisingChannel.view', ch)}">
            <c:set var="channelLink">
                <ui:displayStatus displayStatus="${ch.account.displayStatus.major != LiveEnumValue ? ch.account.displayStatus : ch.displayStatus}" cssClass="withDisplayStatusInline">
                    <a href="${_context}/channel/view.action?id=${ch.id}"><c:out value="${ch.fullName}" /></a>
                </ui:displayStatus>
            </c:set>
        </c:when>
        <c:otherwise>
            <c:set var="channelLink" value="${channelName} "/>
        </c:otherwise>
    </c:choose>

    <c:if test="${!fn:contains(replacedIds, channelId)}">
        <c:set var="formattedExpression" value="${fn:replace(formattedExpression, channelId, channelLink)}"/>
        <c:set var="replacedIds" value="${replacedIds}:${channelId}"/>
    </c:if>
</c:forTokens>

<span class="simpleText">${formattedExpression}</span>
