<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>
<%@ taglib uri="/struts-tags" prefix="s" %>

<s:set var="opportunity" value="model"/>

<ui:header>
    <ui:pageHeadingByTitle />
</ui:header>

<%@include file="opportunityViewFields.jsp"%>

<ui:header styleClass="level2">
    <h2>
        <fmt:message key="opportunity.campaignAssociations"/>
    </h2>
</ui:header>

<display:table name="campaignAssociations" class="dataView" id="campaignAssociation">
    <display:setProperty name="basic.msg.empty_list" >
        <div class="wrapper">
            <fmt:message key="nothing.found.to.display"/>
        </div>
    </display:setProperty>
    <display:column titleKey="opportunity.campaignAssociations.campaign">
       <a href="${_context}/campaign/view.action?id=${campaignAssociation.campaign.id}"><c:out value="${campaignAssociation.campaign.name}"/></a>
    </display:column>
    <display:column titleKey="opportunity.campaignAssociations.allocation" class="number">
        <c:out value="${ad:formatCurrency(campaignAssociation.amount, opportunity.account.currency.currencyCode)}"/>
    </display:column>
    <display:column titleKey="opportunity.campaignAssociations.utilised" class="number">
        <c:out value="${ad:formatCurrency(campaignAssociation.utilizedAmount, opportunity.account.currency.currencyCode)}"/>
    </display:column>
</display:table>