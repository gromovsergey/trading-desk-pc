<%@ page contentType="text/html" %>
<%@ page pageEncoding="UTF-8" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<ui:pageHeadingByTitle/>

<ad:requestContext var="publisherContext"/>
<c:set var="reporting" value="${_context}/reporting"/>
<ui:section>
    <span class="groupOfLinks">
        <c:if test="${ad:isPermitted('Report.run', 'publisher')}">
            <a href="${reporting}/publisher/options.action${ad:accountParam('?accountId', publisherContext.accountId)}">
                <fmt:message key="reports.publisherReport"/>
            </a>
        </c:if>
        <c:if test="${ad:isPermitted('Report.InventoryEstimation.run', publisherContext.accountId)}">
            <a href="${reporting}/inventoryEstimation/options.action${ad:accountParam('?accountId', publisherContext.accountId)}"><fmt:message
                    key="reports.inventoryEstimationReport"/></a>
        </c:if>
        <c:if test="${ad:isPermitted('Report.PubAdvertisingReport.run', publisherContext.accountId)}">
            <a href="../reporting/pubAdvertising/options.action${ad:accountParam('?accountId', publisherContext.accountId)}"><fmt:message key="reports.pubAdvertisingReport"/></a>
        </c:if>
        <c:if test="${ad:isPermitted('Report.ReferrerReport.run', publisherContext.accountId)}">
            <a href="${reporting}/referrer/options.action${ad:accountParam('?accountId', publisherContext.accountId)}"><fmt:message key="reports.referrerReport"/></a>
        </c:if>
    </span>
</ui:section>