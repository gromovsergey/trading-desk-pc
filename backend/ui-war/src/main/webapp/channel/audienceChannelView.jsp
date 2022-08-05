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
        <c:if test="${ad:isPermitted('AuditLog.view', channel)}">
            <ui:button message="form.viewLog" href="/admin/auditLog/view.action?type=56&id=${channel.id}&contextName=${contextName}"/>
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

<ui:section>
    <table class="grouping fieldGroups">
        <tr>
            <td>
                <ui:fieldGroup>
                    <s:set var="makePublicAction" value="'AudienceChannel/makePublic'"/>
                    <s:set var="makePrivateAction" value="'AudienceChannel/makePrivate'"/>

                    <s:url var="activateUrl" action="AudienceChannel/activate"/>
                    <s:url var="inactivateUrl" action="AudienceChannel/inactivate"/>
                    <s:url var="deleteUrl" action="AudienceChannel/delete"/>
                    <s:url var="undeleteUrl" action="AudienceChannel/undelete"/>

                    <%@ include file="commonChannelView.jspf" %>
                </ui:fieldGroup>
            </td>
            <td>
                <%@ include file="commonChannelStats.jspf" %>
            </td>
        </tr>
    </table>
</ui:section>

<ui:errorsBlock>
    <s:fielderror><s:param value="'version'"/></s:fielderror>
    <s:fielderror><s:param value="'name'"/></s:fielderror>
</ui:errorsBlock>
<div id="popupVersionError"></div>

<%@ include file="channelStatsWrapper.jsp" %>
<%@ include file="campaignAssociationsView.jsp"%>
<%@ include file="expressionAssociationsView.jsp" %>