<%@ page contentType="text/html" %>
<%@ page pageEncoding="UTF-8" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ui" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:set var="channel" value="model"/>
<s:set var="account" value="#channel.account"/>

<ui:header>
    <ui:pageHeadingByTitle />
    <div class="groupOfButtons">
        <%@include file="editButton.jspf"%>
        <c:if test="${ad:isPermitted('AdvertisingChannel.contactCMPChannelUser', channel)}">
            <ui:button message="form.contact.channel.users" action="contactChannelUsers?id=${channel.id}"/>
        </c:if>
        <c:if test="${ad:isPermitted('AuditLog.view', channel)}">
            <ui:button message="form.viewLog" href="/admin/auditLog/view.action?type=21&id=${channel.id}&contextName=${contextName}"/>
        </c:if>
    </div>
</ui:header>

<c:if test="${(not empty param['sentSuccessfully']) || (not empty param['exceedsLimit']) || (not empty param['sentSuccessfullyToMe'])}">
    <div class="wrapper">
        <c:if test="${param['sentSuccessfully'] == 'true'}">
            <span class="infos"><fmt:message key="cmp.channel.user.js.mailSuccess"/></span>
        </c:if>
        <c:if test="${param['sentSuccessfully'] == 'false'}">
            <span class="errors"><fmt:message key="cmp.channel.user.js.mailUnSuccess"/></span>
        </c:if>
        <c:if test="${param['exceedsLimit'] == 'true'}">
            <span class="errors"><fmt:message key="cmp.channel.users.exceeds.limit"/></span>
        </c:if>
        <c:if test="${param['sentSuccessfullyToMe'] == 'true'}">
            <span class="infos"><fmt:message key="cmp.user.js.mailSuccess"/></span>
        </c:if>
    </div>
</c:if>

<ui:errorsBlock>
    <s:fielderror><s:param value="'version'"/></s:fielderror>
    <s:fielderror><s:param value="'name'"/></s:fielderror>
</ui:errorsBlock>
<div id="popupVersionError">
</div>

<c:if test="${not ad:isPermitted('AdvertisingChannel.viewContent', channel)}">
    <div class="wrapper">
        <span class="infos"><fmt:message key="channel.CMPChannelRestricted"/></span>
    </div>
</c:if>

<ui:section>
    <table class="grouping fieldGroups">
        <tr>
            <td>
                <ui:fieldGroup>
                    <s:set var="makePublicAction" value="'ExpressionChannel/makePublic'"/>
                    <s:set var="makePrivateAction" value="'ExpressionChannel/makePrivate'"/>
                    <s:url var="submitToCmpUrl" action="ExpressionChannel/submitCmp"/>

                    <s:url var="activateUrl" action="ExpressionChannel/activate"/>
                    <s:url var="inactivateUrl" action="ExpressionChannel/inactivate"/>
                    <s:url var="deleteUrl" action="ExpressionChannel/delete"/>
                    <s:url var="undeleteUrl" action="ExpressionChannel/undelete"/>

                    <%@ include file="commonChannelView.jspf" %>
                    <c:if test="${not empty populatedRate}">
                        <ui:field labelKey="channel.rate" tipKey="channel.rate.tip">
                            <c:out value="${populatedRate}"/>
                        </ui:field>
                    </c:if>
                </ui:fieldGroup>
            </td>
            <td>
                <%@ include file="commonChannelStats.jspf" %>
            </td>
        </tr>
    </table>
</ui:section>

<c:if test="${ad:isPermitted('AdvertisingChannel.viewContent', channel)}">
    <c:if test="${not empty channel.expression}">
        <ui:section titleKey="channel.expression" >
            <ui:fieldGroup>
                <ui:field>
                    <ui:channelExpression expression="${channel.expression}" />
                </ui:field>
            </ui:fieldGroup>
        </ui:section>
    </c:if>
</c:if>

<%@ include file="channelStatsWrapper.jsp" %>
<%@ include file="campaignAssociationsView.jsp"%>
<%@ include file="expressionAssociationsView.jsp"%>
