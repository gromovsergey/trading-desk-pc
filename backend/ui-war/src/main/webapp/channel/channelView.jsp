<%@ page contentType="text/html" %>
<%@ page pageEncoding="UTF-8" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ui" %>
<%@ taglib uri="/struts-tags" prefix="s" %>

<s:set var="channel" value="model"/>
<s:set var="account" value="#channel.account"/>

<ui:header>
    <ui:pageHeadingByTitle/>
    <div class="groupOfButtons">
        <%@include file="editButton.jspf"%>
        <c:if test="${ad:isPermitted('AdvertisingChannel.createCopy', channel)}">
            <ui:postButton message="form.createCopy" href="createCopy.action" entityId="${channel.id}"
                           onclick="return UI.Util.confirmCopy(this);" />
        </c:if>
        <c:if test="${ad:isPermitted('AdvertisingChannel.contactCMPChannelUser', channel)}">
            <ui:button message="form.contact.channel.users" action="contactChannelUsers?id=${channel.id}"/>
        </c:if>
        <c:if test="${ad:isPermitted0('TriggerQA.view')}">
            <c:set var="triggersURL"><c:url value="/admin/Triggers/main.action">
                <c:param name="searchParams.type" value="A"/>
                <c:param name="searchParams.countryCode" value="${channel.country.countryCode}"/>
                <c:param name="searchParams.filterBy" value="CHANNEL"/>
                <c:param name="searchParams.channelAccountId" value="${channel.account.id}"/>
                <c:param name="searchParams.channelId" value="${channel.id}"/>
                <c:param name="searchParams.channelName" value="${channel.name}"/>
            </c:url></c:set>
            <ui:button message="admin.triggersApproval" href="${triggersURL}"/>
        </c:if>
        <c:if test="${ad:isPermitted('AuditLog.view', channel)}">
            <ui:button message="form.viewLog" href="/admin/auditLog/view.action?type=20&id=${channel.id}&contextName=${contextName}"/>
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
    <s:actionerror/>
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

                    <s:set var="makePublicAction" value="'BehavioralChannel/makePublic'"/>
                    <s:set var="makePrivateAction" value="'BehavioralChannel/makePrivate'"/>
                    <s:url var="submitToCmpUrl" action="BehavioralChannel/submitCmp"/>

                    <s:url var="activateUrl" action="BehavioralChannel/activate"/>
                    <s:url var="inactivateUrl" action="BehavioralChannel/inactivate"/>
                    <s:url var="deleteUrl" action="BehavioralChannel/delete"/>
                    <s:url var="undeleteUrl" action="BehavioralChannel/undelete"/>

                    <%@ include file="commonChannelView.jspf" %>

                </ui:fieldGroup>
            </td>
            <td>
                <%@ include file="commonChannelStats.jspf" %>
            </td>
        </tr>
    </table>
</ui:section>

<c:if test="${ad:isPermitted('AdvertisingChannel.viewContent', channel)}">
    <s:set var="behavioralParameters" value="#channel.behavioralParameters"/>
    <%@include file="/channel/triggersView.jsp"%>
</c:if>

<c:if test="${ad:isPermitted('AdvertisingChannel.viewStats', channel)}">
    <%@ include file="channelStatsWrapper.jsp" %>
</c:if>

<%@ include file="campaignAssociationsView.jsp" %>
<%@ include file="expressionAssociationsView.jsp" %>
