<%@ page contentType="text/html" %>
<%@ page pageEncoding="UTF-8" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib uri="/struts-tags" prefix="s" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ui" %>

<s:set var="channel" value="model"/>
<s:set var="account" value="#channel.account"/>

<c:if test="${not empty language}">
    <c:set var="channelLanguage"><ad:resolveGlobal resource="language" id="${model.language}"/></c:set>
</c:if>
<c:if test="${empty language}">
    <c:set var="channelLanguage"><fmt:message key="form.select.notSpecified"/></c:set>
</c:if>

<%@ include file="/channel/discoverChannelPopupToLinkJS.jsp" %>
<script type="text/javascript">
    $(document).ready(function() {
        $('#linkChannelButton, #linkDifferentChannelButton').click(function(event) {
            var channels = [{id: ${model.id}, version: '${model.version.time}/${model.version.nanos}'}];
        <c:if test="${empty channelList}">
            var channelListId = null;
        </c:if>

        <c:if test="${not empty channelList}">
            var channelListId = ${channelList.id};
        </c:if>
            popupLinkToDiscoverChannelList(channelListId, channels, false, event);
        });

        return false;
    });
</script>


<ui:header>
    <ui:pageHeadingByTitle/>
    <c:if test="${ad:isPermitted('DiscoverChannel.update', model)}">
        <ui:button message="form.edit" href="edit.action?id=${model.id}"/>
    </c:if>

    <c:if test="${ad:isPermitted('DiscoverChannel.createCopy', model)}">
        <ui:postButton message="form.createCopy" href="createCopy.action" entityId="${model.id}"
                       onclick="return UI.Util.confirmCopy(this);" />
    </c:if>
    <c:if test="${ad:isPermitted0('TriggerQA.view')}">
        <c:set var="triggersURL"><c:url value="/admin/Triggers/main.action">
            <c:param name="searchParams.type" value="D"/>
            <c:param name="searchParams.countryCode" value="${channel.country.countryCode}"/>
            <c:param name="searchParams.discoverAccountId" value="${channel.account.id}"/>
            <c:param name="searchParams.discoverChannelId" value="${channel.id}"/>
            <c:param name="searchParams.discoverChannelName" value="${channel.name}"/>
        </c:url></c:set>
        <ui:button message="admin.triggersApproval" href="${triggersURL}"/>
    </c:if>
    <c:if test="${ad:isPermitted('Entity.viewLog', channel)}">
        <ui:button message="form.viewLog" href="/admin/auditLog/view.action?type=19&id=${channel.id}&contextName=${contextName}"/>
    </c:if>
</ui:header>

<ui:errorsBlock>
    <s:actionerror/>
    <s:fielderror fieldName="version"/>
    <s:fielderror fieldName="channelList"/>
    <s:fielderror fieldName="baseKeyword"/>
    <s:fielderror fieldName="keywords"/>
    <s:fielderror fieldName="name"/>
    <c:if test="${param.error == 'version'}">
        <span class="errors"><fmt:message key="errors.version"/></span>
    </c:if>
</ui:errorsBlock>

<ui:section>
    <ui:fieldGroup>
        <ui:field labelKey="channel.account">
            <ui:accountLink id="${account.id}" role="${account.role}" name="${account.name}" displayStatus="${account.displayStatus}"/>
        </ui:field>

        <ui:simpleField labelKey="channel.country"
                        value="${ad:resolveGlobal('country', model.country.countryCode, false)}"/>

        <ui:simpleField labelKey="channel.language" value="${channelLanguage}"/>

        <ui:simpleField labelKey="channel.discoverQuery" value="${model.discoverQuery}"/>

        <ui:simpleField labelKey="channel.discoverAnnotation" value="${model.discoverAnnotation}"/>

        <ui:field labelKey="channel.status">
            <ui:statusButtonGroup
                descriptionKey="${model.displayStatus.description}"
                entity="${model}" restrictionEntity="DiscoverChannel"
                activatePage="activate.action" inactivatePage="inactivate.action"
                deletePage="delete.action" undeletePage="undelete.action"
                >
                <c:if test="${channel.qaStatus.letter == 'D'}">
                    <ui:qaStatusButons entity="${model}" restrictionEntity="DiscoverChannel"/>
                </c:if>
            </ui:statusButtonGroup>
        </ui:field>

        <ui:field labelKey="channel.params">
            <c:choose>
                <c:when test="${not empty model.behavParamsList.id}">
                    <a href="/admin/behavioralParameters/view.action?id=${model.behavParamsList.id}"><c:out value="${model.behavParamsList.name}"/></a>
                </c:when>
                <c:otherwise>
                    <ui:text textKey="form.select.none"/>
                </c:otherwise>
            </c:choose>
        </ui:field>

        <c:if test="${empty channelList}">
            <c:if test="${ad:isPermitted('DiscoverChannel.update', model)}">
                <ui:field labelKey="DiscoverChannelList.entityName">
                    <ui:button id="linkChannelButton" message="DiscoverChannel.link" type="link" />
                </ui:field>
            </c:if>
        </c:if>

        <c:if test="${not empty channelList}">
            <ui:field labelKey="DiscoverChannelList.entityName">
                <table class="fieldAndAccessories">
                    <tr>
                        <td class="withField">
                            <a href="/admin/DiscoverChannelList/view.action?id=${model.channelList.id}"><c:out value="${model.channelList.name}"/></a>
                        </td>
                        <c:if test="${ad:isPermitted('DiscoverChannel.update', model)}">
                            <td class="withButton">
                                <ui:postButton href="unlink.action?id=${model.id}"
                                               onclick="if (!confirm('${ad:formatMessage('confirmUnlink')}')) {return false;}"
                                               message="DiscoverChannel.unlink"/>
                            </td>
                            <td class="withButton">
                                <ui:button id="linkDifferentChannelButton" message="DiscoverChannel.linkDifferent" type="link" />
                            </td>
                        </c:if>
                    </tr>
                </table>
            </ui:field>
        </c:if>

        <c:if test="${not empty model.description}">
            <ui:field labelKey="channel.description">
                <s:textarea name="description" cssClass="middleLengthText" readonly="true" rows="3"
                          cssStyle="height: 50px"/>
            </ui:field>
        </c:if>

        <%@include file="../../channel/channelCategoriesView.jsp" %>
    </ui:fieldGroup>
</ui:section>

<s:set var="behavioralParameters" value="#channel.behavParamsList.behavioralParameters"/>
<%@include file="/channel/triggersView.jsp"%>

<c:set var="channelForm" value="${model}"/>
<%@ include file="/channel/channelStatsWrapper.jsp" %>
