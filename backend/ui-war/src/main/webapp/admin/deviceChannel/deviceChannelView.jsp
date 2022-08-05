<%@ page import="com.foros.model.channel.Channel" %>
<%@ page contentType="text/html" %>
<%@ page pageEncoding="UTF-8" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="/struts-tags" prefix="s" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>

<script type="text/javascript">
    $(function(){
        $('#searchForm').pagingAssist({
            action:     '/admin/DeviceChannel/searchAssociatedCampaigns.action',
            message:    '${ad:formatMessage("channel.search.loading")}',
            result:     $('#result')
        });
    });
</script>

<c:set var="channelType" value="<%=Channel.CHANNEL_TYPE_DEVICE%>"/>
<c:set var="entityType" value="8"/>
<c:set var="entityBean" value="${model}"/>

<ui:header>
    <ui:pageHeadingByTitle />
    <c:if test="${ad:isPermittedAny('DeviceChannel.update,DeviceChannel.view')}">
    <table class="grouping groupOfButtons">
        <tr>
            <c:if test="${ad:isPermitted('DeviceChannel.update', model)}">
            <td>
                <ui:button message="form.edit" href="/admin/DeviceChannel/edit.action?id=${model.id}" />
            </td>
            </c:if>
            <c:if test="${ad:isPermitted0('DeviceChannel.view')}">
            <td>
                <%@ include file="../../auditLog/viewLogButton.jspf" %>
            </td>
            </c:if>
        </tr>
    </table>
    </c:if>
</ui:header>

<ui:errorsBlock>
    <s:actionerror/>
    <s:fielderror><s:param value="'version'"/></s:fielderror>
    <s:fielderror fieldName="name"/>
</ui:errorsBlock>

<ui:section>
    <ui:fieldGroup>
        <c:if test="${not empty parentLocations}">
          <ui:field labelKey="channel.deviceChannel.location">
              <div class="path">
                  <c:forEach items="${parentLocations}" var="parentLocation" varStatus="indexId">
                      <c:if test="${indexId.count > 0}">
                          <span class='delimiter'>/</span>
                      </c:if>
                      <a href="/admin/DeviceChannel/view.action?id=${parentLocation.id}"><c:out value="${parentLocation.name}"/></a>
                  </c:forEach>
              </div>
          </ui:field>
        </c:if>

        <ui:field labelKey="channel.status">
            <c:set var="showButtons" value="${status != 'ACTIVE' || fn:length(associatedCampaignCreativeGroups) == 0 && ad:isPermitted('DeviceChannel.inactivate', model) && ad:isPermitted('DeviceChannel.delete', model)}"/>
            <c:if test="${showButtons}">
                <ui:statusButtonGroup
                    descriptionKey="${model.displayStatus.description}"
                    entity="${model}" restrictionEntity="DeviceChannel"
                    activatePage="activate.action" inactivatePage="inactivate.action"
                    deletePage="delete.action" undeletePage="undelete.action"/>
            </c:if>
            <c:if test="${!showButtons}">
                <fmt:message key="${model.displayStatus.description}"/>
            </c:if>
        </ui:field>
    </ui:fieldGroup>
</ui:section>

<ui:section titleKey="channel.deviceChannel.Expression">
  <ui:fieldGroup>
    <ui:field>
      <c:set var="replacedIds" value=""/>
      <c:set var="formatedExpression" value="${model.expression}"/>
      <c:set var="formatedExpression" value="${fn:replace(formatedExpression, '&', ' and ')}"/>
      <c:set var="formatedExpression" value="${fn:replace(formatedExpression, '^', ' and not ')}"/>
      <c:set var="formatedExpression" value="${fn:replace(formatedExpression, '|', ' or ')}"/>
      <c:set var="formatedExpression" value="${fn:replace(formatedExpression, '(', ' ( ')}"/>
      <c:set var="formatedExpression" value="${fn:replace(formatedExpression, ')', ' ) ')}"/>
      <c:forTokens items="${model.expression}" delims="(|&^)" var="platformId">
          <s:set var="platform" value="platformMap(#attr.platformId)"/>
          <c:set var="platformLink" value="<a href='/admin/Platform/view.action?id=${platformId}' target='_blank'>${platform.name}</a>"/>
          <c:if test="${!fn:contains(replacedIds, platformId)}">
              <c:set var="formatedExpression" value="${fn:replace(formatedExpression, platformId, platformLink)}"/>
              <c:set var="replacedIds" value="${replacedIds}:${platformId}"/>
          </c:if>
      </c:forTokens>
      <span class="simpleText">${formatedExpression}</span>
    </ui:field>
  </ui:fieldGroup>
</ui:section>

<c:if test="${ad:isPermitted('AdvertisingChannel.viewStats', channel)}">
    <%@ include file="/channel/channelStatsWrapper.jsp" %>
</c:if>

<ui:header styleClass="level2">
    <h2><fmt:message key="channel.deviceChannel.Associations"/></h2>
</ui:header>

<s:form id="searchForm">
    <s:hidden name="id"/>
</s:form>

<div id="result" class="logicalBlock">
    <%@ include file="deviceChannelCampaignsView.jsp" %>
</div>

<ui:header styleClass="level2">
    <h2><fmt:message key="channel.deviceChannel.children"/></h2>
    <c:if test="${ad:isPermitted('DeviceChannel.create', model)}">
          <a class="button" href="/admin/DeviceChannel/new.action?parentChannelId=${model.id}"><fmt:message key="form.createNew"/></a>
    </c:if>
</ui:header>

<table class="dataViewSection">
        <tr class="bodyZone">
            <td>
                <display:table name="childrenChannels" class="dataView" id="channel">
                    <display:setProperty name="basic.msg.empty_list" >
                      <div class="wrapper">
                          <fmt:message key="nothing.found.to.display"/>
                      </div>
                    </display:setProperty>
                
                    <display:column titleKey="DeviceChannel.entityName">
                        <ui:displayStatus cssClass="indentLevel_${channel.level - 1}" displayStatus="${channel.displayStatus}">
                            <a href="/admin/DeviceChannel/view.action?id=${channel.id}"><c:out value="${channel.name}"/></a>
                        </ui:displayStatus>
                    </display:column>
                </display:table>
            </td>
        </tr>
</table>
