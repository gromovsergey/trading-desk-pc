<%@ page contentType="text/html" %>
<%@ page pageEncoding="UTF-8" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ui" %>

<c:set var="isInternal" value="${ad:isInternal()}"/>
<c:set var="isAdvertiser" value="${ad:isAdvertiser()}"/>
<c:set var="isAgency" value="${ad:isAgency()}"/>

<c:set var="isShowCampaignAssociationAccounts" value="${isInternal}"/>
<c:set var="isShowCampaignAssociationAdvertisers" value="${isInternal or isAgency}"/>
<c:set var="canViewAccounts" value="${ad:isPermitted('Account.view', 'Advertiser') || ad:isPermitted('Account.view', 'Agency')}"/>
<c:set var="canCampainsCCGs" value="${ad:isPermitted0('AdvertiserEntity.view')}"/>

<display:table name="campaignAssociations" class="dataView" id="ca" htmlId="associationsTable">
    <display:setProperty name="basic.msg.empty_list">
        <div class="wrapper">
            <fmt:message key="nothing.found.to.display"/>
        </div>
    </display:setProperty>
    <c:if test="${isShowCampaignAssociationAccounts}">
        <display:column titleKey="campaign.associations.account">
            <ui:displayStatus displayStatus="${ca.account.displayStatus}" testFlag="${ca.account.testFlag}">
                <c:choose>
                    <c:when test="${canViewAccounts}">
                        <a href="/admin/advertiser/account/advertiserView.action?id=${ca.account.id}">
                            <ui:nameWithStatus entityStatus="${ca.account.status}" entityName="${ca.account.name}"/>
                        </a>
                     </c:when>
                    <c:otherwise>
                        <ui:nameWithStatus entityStatus="${ca.account.status}" entityName="${ca.account.name}"/>
                    </c:otherwise>
                </c:choose>
            </ui:displayStatus>
        </display:column>
    </c:if>
    <c:if test="${isShowCampaignAssociationAdvertisers}">
        <display:column titleKey="campaign.associations.advertiser">
            <ui:nameWithStatus entityStatus="${ca.advertiser.status}" entityName="${ca.advertiser.name}"/>
        </display:column>
    </c:if>
    <display:column titleKey="campaign.associations.campaign">
        <ui:displayStatus displayStatus="${ca.campaign.displayStatus}">
            <c:choose>
                <c:when test="${canCampainsCCGs}">
                    <a href="${_context}/campaign/view.action?id=${ca.campaign.id}">
                        <ui:nameWithStatus entityStatus="${ca.campaign.status}" entityName="${ca.campaign.name}"/>
                    </a>
                </c:when>
                <c:otherwise>
                    <ui:nameWithStatus entityStatus="${ca.campaign.status}" entityName="${ca.campaign.name}"/>
                </c:otherwise>
            </c:choose>
        </ui:displayStatus>
    </display:column>
    <display:column titleKey="campaign.associations.ccg">
        <ui:displayStatus displayStatus="${ca.group.displayStatus}">
            <c:choose>
                <c:when test="${canCampainsCCGs}">
                    <a href="${_context}/campaign/group/view.action?id=${ca.group.id}">
                        <ui:nameWithStatus entityStatus="${ca.group.status}" entityName="${ca.group.name}"/>
                    </a>
                </c:when>
                <c:otherwise>
                    <ui:nameWithStatus entityStatus="${ca.group.status}" entityName="${ca.group.name}"/>
                </c:otherwise>
            </c:choose>
        </ui:displayStatus>
    </display:column>
    <display:column titleKey="channel.stats.impressions" class="number">
        <fmt:formatNumber value="${ca.impressions}" groupingUsed="true"/>
    </display:column>
    <display:column titleKey="channel.stats.clicks" class="number">
        <fmt:formatNumber value="${ca.clicks}" groupingUsed="true"/>
    </display:column>
    <display:column titleKey="channel.stats.ctr" class="number">
        <fmt:formatNumber value="${ca.ctr}" groupingUsed="false" minFractionDigits="2" maxFractionDigits="2"/>%
    </display:column>
</display:table>
