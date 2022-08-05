<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@ page import="com.foros.model.opportunity.Probability" %>

<%@ taglib uri="/struts-tags" prefix="s" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ui" %>

<ui:header>
    <ui:pageHeadingByTitle/>
    <ad:requestContext var="advertiserContext"/>
    <c:set var="accountBean" value="${advertiserContext.advertiser}"/>
    <c:if test="${ad:isPermitted('Opportunity.create', accountBean)}">
        <ui:button message="opportunity.entityName.new"
                   href="new.action?advertiserId=${advertiserContext.advertiserId}"/>
    </c:if>
</ui:header>

<c:set var="beginStatus" value="<%=Probability.IO_SIGNED.ordinal()%>"/>
<c:set var="endStatus" value="<%=Probability.LIVE.ordinal()%>"/>

<display:table name="opportunities" class="dataView" id="opportunity">
    <display:setProperty name="basic.msg.empty_list" >
        <div class="wrapper">
            <fmt:message key="nothing.found.to.display"/>
        </div>
    </display:setProperty>
    <display:column titleKey="opportunity.subTitle">
       <a href="${_context}/opportunity/view.action?id=${opportunity.id}"><c:out value="${opportunity.name}"/></a>
    </display:column>
    <display:column titleKey="opportunity.amount">
        <c:out value="${ad:formatCurrency(opportunity.amount, opportunity.account.currency.currencyCode)}"/>
    </display:column>
    <display:column titleKey="opportunity.probability">
        <c:choose>
            <c:when test="${opportunity.account.accountType.ioManagement &&
            beginStatus <= opportunity.probability.ordinal() && opportunity.probability.ordinal() <= endStatus}">
                <a href="${_context}/insertionOrder/view.action?id=${opportunity.id}"><fmt:message key="enum.opportunity.probability.${opportunity.probability}"/></a>
            </c:when>
            <c:otherwise>
                <fmt:message key="enum.opportunity.probability.${opportunity.probability}"/>
            </c:otherwise>
        </c:choose>
    </display:column>
    <display:column titleKey="opportunity.lastUpdated" style="white-space:nowrap;">
        <fmt:formatDate value="${opportunity.version}" type="both" timeStyle="short" dateStyle="short"
                        timeZone="${_userSettings.timeZone}"/>
    </display:column>
</display:table>
