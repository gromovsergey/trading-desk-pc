<%@ page contentType="text/html" %>
<%@ page pageEncoding="UTF-8" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="ad" uri="/ad/serverUI" %>
<%@ taglib prefix="display" uri="http://displaytag.sf.net/el" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>

<ui:header>
    <ui:pageHeadingByTitle/>
    <c:if test="${ad:isPermitted('AuctionSettings.update', account)}">
        <ui:button message="form.edit" href="editAuctionSettings.action?id=${id}"/>
    </c:if>
</ui:header>

<ui:section titleKey="AuctionSettings.allocationsByType">
    <ui:fieldGroup>
        <ui:field labelKey="AuctionSettings.maximumECPM">
            <fmt:formatNumber value="${maxEcpmShare}"/>%
        </ui:field>
        <ui:field labelKey="AuctionSettings.proportionalProbability">
            <fmt:formatNumber value="${propProbabilityShare}"/>%
        </ui:field>
        <ui:field labelKey="AuctionSettings.random">
            <fmt:formatNumber value="${randomShare}"/>%
        </ui:field>
    </ui:fieldGroup>
</ui:section>

<ui:section titleKey="AuctionSettings.randomAuctionSettings">
    <ui:fieldGroup>
        <ui:field labelKey="AuctionSettings.maximumECPM">
            ${ad:formatCurrency(maxRandomCpm, internalAccount.currency.currencyCode)}
        </ui:field>
    </ui:fieldGroup>
</ui:section>

<ui:header styleClass="level2">
    <h2><fmt:message key="AuctionSettings.tagsWithNonDefault"/></h2>
</ui:header>

<display:table name="tagsAuctionSettings" class="dataView" id="row">
    <display:setProperty name="basic.msg.empty_list">
        <div class="wrapper"><fmt:message key="nothing.found.to.display"/></div>
    </display:setProperty>
    <display:column titleKey="roles.Publisher">
        <ui:displayStatus displayStatus="${row.tag.site.account.displayStatus}">
            <a href="/admin/publisher/account/view.action?id=${row.tag.site.account.id}">${row.tag.site.account.name}</a>
        </ui:displayStatus>
    </display:column>
    <display:column titleKey="Site.entityName">
        <ui:displayStatus displayStatus="${row.tag.site.displayStatus}">
            <a href="/admin/site/view.action?id=${row.tag.site.id}">${row.tag.site.name}</a>
        </ui:displayStatus>
    </display:column>
    <display:column titleKey="Tag.entityName">
        <ui:displayStatus displayStatus="${row.tag.displayStatus}">
            <a href="/admin/tag/view.action?id=${row.tag.id}">${row.tag.name}</a>
        </ui:displayStatus>
    </display:column>
    <display:column titleKey="AuctionSettings.maximumECPM">
        <fmt:formatNumber value="${row.maxEcpmShare}"/>%
    </display:column>
    <display:column titleKey="AuctionSettings.proportionalProbability">
        <fmt:formatNumber value="${row.propProbabilityShare}"/>%
    </display:column>
    <display:column titleKey="AuctionSettings.random">
        <fmt:formatNumber value="${row.randomShare}"/>%
    </display:column>
</display:table>
