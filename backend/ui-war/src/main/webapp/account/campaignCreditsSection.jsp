<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="ad" uri="/ad/serverUI" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>

<c:if test="${ad:isPermitted('CampaignCredit.edit', model)}">
    <c:set var="allowEditCampaignCredits" value="true"/>
</c:if>

<ui:header styleClass="level2">
    <h2><fmt:message key="CampaignCredit.plural"/></h2>
    <c:if test="${allowEditCampaignCredits}">
        <ui:button message="form.add" href="${_context}/campaignCredit/new.action?accountId=${id}"/>
    </c:if>
    <c:if test="${ad:isPermitted('Entity.viewLog', model)}">
        <ui:button message="form.viewLog" href="/admin/auditLog/view.action?type=50&id=${id}" />
    </c:if>
</ui:header>

<display:table name="campaignCredits" class="dataView" id="campaignCredit">
    <display:setProperty name="basic.msg.empty_list">
        <div class="wrapper">
            <fmt:message key="campaignCredit.emptyList"/>
        </div>
    </display:setProperty>
    <display:column titleKey="CampaignCredit.lastUpdated" class="date">
        <fmt:formatDate value="${campaignCredit.version}" type="both" timeStyle="short" dateStyle="short"/>
    </display:column>
    <display:column titleKey="CampaignCredit.type">
        <fmt:message key="enums.CampaignCreditPurpose.${campaignCredit.purpose}"/>
    </display:column>
    <display:column titleKey="CampaignCredit.description">
        <c:out value="${campaignCredit.description}"/>
    </display:column>
    <display:column titleKey="CampaignCredit.advertiser">
        <c:choose>
            <c:when test="${campaignCredit.advertiser.id != null}">
                <c:choose>
                    <c:when test="${ad:isInternal()}">
                        <c:set var="viewAdvertiserUrl" value="/admin/advertiser/account/agencyAdvertiserView.action?id=${campaignCredit.advertiser.id}"/>
                    </c:when>
                    <c:otherwise>
                        <c:set var="viewAdvertiserUrl" value="/advertiser/myAccount/agencyAdvertiserView.action?id=${campaignCredit.advertiser.id}"/>
                    </c:otherwise>
                </c:choose>
                <a href="${viewAdvertiserUrl}">
                    <c:out value="${campaignCredit.advertiser.name}"/>
                </a>
            </c:when>
            <c:otherwise>
                <fmt:message key="CampaignCredit.advertiser.any"/>
            </c:otherwise>
        </c:choose>
    </display:column>
    <display:column titleKey="CampaignCredit.amount" class="number">
        ${ad:formatCurrency(campaignCredit.amount, currency.currencyCode)}
    </display:column>
    <display:column titleKey="CampaignCredit.balance" class="number">
        ${ad:formatCurrency(campaignCredit.balance, currency.currencyCode)}
    </display:column>
    <display:column titleKey="form.actions">
        <ui:button message="CampaignCredit.manage" href="${_context}/campaignCredit/view.action?id=${campaignCredit.id}"/>

        <c:if test="${allowEditCampaignCredits}">
            <ui:button message="form.edit" href="${_context}/campaignCredit/edit.action?id=${campaignCredit.id}"/>
        </c:if>

        <c:if test="${allowEditCampaignCredits && !campaignCredit.hasAllocations}">
            <ui:postButton message="form.delete" href="${_context}/campaignCredit/delete.action?accountId=${id}" entityId="${campaignCredit.id}"
                onclick="if (!confirm('${ad:formatMessage('CampaignCredit.confirmDeletion')}')) {return false;}" />
        </c:if>
    </display:column>
</display:table>
