<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib prefix="display" uri="http://displaytag.sf.net/el" %>

<ui:pageHeadingByTitle/>

<ui:section>
    <ui:fieldGroup>
        <ui:field labelKey="CampaignCredit.type">
            <fmt:message key="enums.CampaignCreditPurpose.${purpose}"/>
        </ui:field>
        <ui:field labelKey="CampaignCredit.description">
            <c:out value="${description}"/>
        </ui:field>
        <ui:field labelKey="CampaignCredit.campaignCreditAmount">
            ${ad:formatCurrency(amount, account.currency.currencyCode)}
        </ui:field>
        <ui:field labelKey="CampaignCredit.spentAmount">
            <table class="fieldAndAccessories">
                <tr>
                    <td class="withField">${ad:formatCurrency(campaignCreditStats.spentAmount, account.currency.currencyCode)}</td>
                    <td class="withTip">
                        <ui:hint>
                            <fmt:message key="CampaignCredit.spentAmount.tip"/>
                        </ui:hint>
                    </td>
                </tr>
            </table>
        </ui:field>
        <ui:field labelKey="CampaignCredit.availableAmount">
            <table class="fieldAndAccessories">
                <tr>
                    <td class="withField">${ad:formatCurrency(campaignCreditStats.availableAmount, account.currency.currencyCode)}</td>
                    <td class="withTip">
                        <ui:hint>
                            <fmt:message key="CampaignCredit.availableAmount.tip"/>
                        </ui:hint>
                    </td>
                </tr>
            </table>
        </ui:field>
        <ui:field labelKey="CampaignCredit.unallocatedAmount">
            <table class="fieldAndAccessories">
                <tr>
                    <td class="withField">${ad:formatCurrency(campaignCreditStats.unallocatedAmount, account.currency.currencyCode)}</td>
                    <td class="withTip">
                        <ui:hint>
                            <fmt:message key="CampaignCredit.unallocatedAmount.tip"/>
                        </ui:hint>
                    </td>
                </tr>
            </table>
        </ui:field>
        
    </ui:fieldGroup>
</ui:section>

<c:if test="${ad:isPermitted('CampaignCredit.editAllocations', model)}">
    <c:set var="allowEditAllocations" value="true"/>
</c:if>
<c:if test="${allowEditAllocations || allowViewLog}">
    <ui:header>
        <c:if test="${allowEditAllocations}">
            <ui:button message="CampaignCreditAllocation.newCreditAllocation" href="${_context}/campaignCreditAllocation/new.action?campaignCreditId=${id}"/>
        </c:if>
    </ui:header>
</c:if>

<display:table name="allocations" class="dataView" id="allocation">
    <display:setProperty name="basic.msg.empty_list">
        <div class="wrapper">
            <fmt:message key="CampaignCreditAllocation.emptyList"/>
        </div>
    </display:setProperty>
    <display:column titleKey="CampaignCredit.lastUpdated" class="date">
        <fmt:formatDate value="${allocation.version}" type="both" timeStyle="short" dateStyle="short"/>
    </display:column>
    <display:column titleKey="CampaignCreditAllocation.advertiser">
        <ui:displayStatus displayStatus="${allocation.campaign.account.displayStatus}" testFlag="${allocation.campaign.account.testFlag}">
            <c:out value="${allocation.campaign.account.name}"/>
        </ui:displayStatus>
    </display:column>
    <display:column titleKey="CampaignCreditAllocation.campaign">
        <ui:displayStatus displayStatus="${allocation.campaign.displayStatus}">
            <c:choose>
                <c:when test="${ad:isPermitted('AdvertiserEntity.view', allocation.campaign)}">
                    <a class="preText" href="${_context}/campaign/view.action?id=${allocation.campaign.id}"><c:out value="${allocation.campaign.name}"/></a>
                </c:when>
                <c:otherwise>
                    <span class="preText"><c:out value="${allocation.campaign.name}"/></span>
                </c:otherwise>
            </c:choose>
        </ui:displayStatus>
    </display:column>
    <display:column titleKey="CampaignCreditAllocation.allocation" class="number">
        <ad:wrap>
            ${ad:formatCurrency(allocation.allocatedAmount, account.currency.currencyCode)}
            <c:if test="${allowEditAllocations}">
                <ui:button message="form.edit" href="${_context}/campaignCreditAllocation/edit.action?id=${allocation.id}"/>
            </c:if>
        </ad:wrap>
    </display:column>
    <display:column titleKey="CampaignCreditAllocation.availableAmount" class="number">
        ${ad:formatCurrency(allocation.availableAmount, account.currency.currencyCode)}
    </display:column>
</display:table>
