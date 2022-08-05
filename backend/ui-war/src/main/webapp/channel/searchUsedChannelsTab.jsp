<%@ page contentType="text/html" %>
<%@ page pageEncoding="UTF-8" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ui" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<%@include file="searchTabTips.jsp"%>

<c:set var="usedChannelForms" scope="request" value="${usedChannels}"/>
<c:set var="canViewGroup" value="${ad:isPermitted0('AdvertiserEntity.view')}"/>

<fmt:message key="channel.usedChannels.tab.text"/>
<div id="usedChannelsResultsDiv" class="dataViewInnerSection">

    <c:set var="messageKey" value="channel.usedChannels.notFound"/>
    <c:set var="accountId" value="${account.id}"/>
    <c:set var="noUsedChannelsMessage">
        <%@include file="notFoundMessage.jsp"%>
    </c:set>

    <display:table name="usedChannelForms" class="dataView" id="usedChannelRow">
        <display:setProperty name="basic.msg.empty_list" value="${noUsedChannelsMessage}"/>
        <display:column class="hide" property="accountId" titleKey="channel.authorId" headerClass="hide"/>
        <display:column titleKey="channel.name">
            <ui:displayStatus displayStatus="${usedChannelRow.displayStatus}">
                <c:if test="${accountId != usedChannelRow.accountId}">
                    <c:out value="${usedChannelRow.accountName}"/> /
                </c:if>
                <a href="${_context}/channel/view.action?id=${usedChannelRow.id}" target="_blank">
                    <c:out value="${usedChannelRow.name}"/>
                </a>
            </ui:displayStatus>
        </display:column>
        <display:column titleKey="channel.creativeGroups" style="width:250px;">
            <c:set var="ccgsSize" value="${fn:length(usedChannelRow.ccgs)}"/>
            <c:set var="count" value="${0}"/>
            <c:forEach var="ccg" items="${usedChannelRow.ccgs}">
                <c:set var="count" value="${count + 1}"/>
                <c:choose>
                    <c:when test="${canViewGroup}">
                        <a href="${_context}/campaign/group/view.action?id=${ccg.id}" target="_blank"><c:out value="${ccg.name}"/></a>
                    </c:when>
                    <c:otherwise>
                        <c:out value="${ccg.name}"/>
                    </c:otherwise>
                </c:choose>
                <c:if test="${count < ccgsSize}">,&nbsp;</c:if>
            </c:forEach>
        </display:column>
        <display:column title="${impressionTip}" style="text-align:right;">
            <fmt:formatNumber value="${usedChannelRow.imps}" maxFractionDigits="0"  groupingUsed="true"/>
        </display:column>
        <display:column title="${reuseTip}" style="text-align:right;">
            <c:choose>
                <c:when test="${not empty usedChannelRow.reuse}">
                    <fmt:formatNumber value="${usedChannelRow.reuse}" maxFractionDigits="0" groupingUsed="true"/>
                </c:when>
                <c:otherwise>
                     <fmt:message key="notAvailable"/>
                </c:otherwise>
            </c:choose>
        </display:column>
        <display:column title="${lastUseTip}" style="text-align:right;">
            <c:choose>
                <c:when test="${not empty usedChannelRow.lastUse}">
                    <fmt:formatDate value="${usedChannelRow.lastUse}" dateStyle="short" timeZone="GMT"/>
                </c:when>
                <c:otherwise>
                    <fmt:message key="notAvailable"/>
                </c:otherwise>
            </c:choose>
        </display:column>
        <display:column title="${populationTip}" style="text-align:right;">
            <fmt:formatNumber value="${usedChannelRow.userCount}" maxFractionDigits="0" groupingUsed="true"/>
        </display:column>
        <display:column titleKey="channel.table.actions" style="width:100px;text-align:center">
            <div>
            <table class="grouping" align="center">
                <tr>
                    <td>
                        <ui:button message="form.${param.channelAction}" onclick="${param.channelAction}Channel({
                                id: '${ad:escapeJavaScriptInTag(usedChannelRow.id)}',
                                name: '${ad:escapeJavaScriptInTag(usedChannelRow.name)}',
                                account : {
                                    id: '${ad:escapeJavaScriptInTag(usedChannelRow.accountId)}',
                                    name: '${ad:escapeJavaScriptInTag(usedChannelRow.accountName)}'
                                }
                             }); return false;"
                         />
                    </td>
                </tr>
            </table>
            </div>
        </display:column>
    </display:table>
</div>
