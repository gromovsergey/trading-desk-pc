<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<c:set var="bulkChangeAvailable" value="false"/>
<c:set var="isCampaignUpdatePermitted" value="${ad:isPermitted('AdvertiserEntity.update', model)}"/>

<c:if test="${isCampaignUpdatePermitted}">
    <c:set var="bulkChangeAvailable" value="true"/>
</c:if>
<c:if test="${ad:isPermitted('AdvertiserEntity.approveChildren', model)}">
    <c:set var="bulkChangeAvailable" value="true"/>
</c:if>

<c:if test="${campaignType == 'TEXT'}" >
    <ui:ccgStatsTable groups="${groups}"
                      showUniqueUsers="${showUniqueUsers}"
                      showPostClickConv="${showPostClickConv}"
                      showPostImpConv="${showPostImpConv}"
                      ccgType="Text"
                      bulkChangeAvailable="${bulkChangeAvailable}"
                      showChannelTarget="${hasChannelTargetedGroup}"/>
</c:if>

<c:if test="${campaignType == 'DISPLAY'}" >
    <ui:ccgStatsTable groups="${groups}"
                      showUniqueUsers="${showUniqueUsers}"
                      showPostClickConv="${showPostClickConv}"
                      showPostImpConv="${showPostImpConv}"
                      ccgType="Display"
                      bulkChangeAvailable="${bulkChangeAvailable}"
                      showChannelTarget="true"/>
</c:if>
