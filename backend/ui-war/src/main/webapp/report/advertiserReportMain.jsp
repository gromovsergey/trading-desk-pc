<%@ page contentType="text/html"%>
<%@ page pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>

<ui:pageHeadingByTitle/>

<ad:requestContext var="advertiserContext"/>
<ad:sessionContext var="sessionContexts"/>
<c:set var="sessionContext" value="${sessionContexts.advertiserContext}"/>
<c:set var="reporting" value="${_context}/reporting"/>
<ui:section>
    <span class="groupOfLinks">
        <c:if test="${ad:isPermitted('Report.runGeneralAdvertiserReport', advertiserContext.accountId)}">
            <a href="${reporting}/generalAdvertising/options.action${ad:accountParam('?accountId', advertiserContext.accountId)}">
                <fmt:message key="reports.generalAdvertisingReport"/>
            </a>
        </c:if>
        <c:if test="${ad:isPermitted('Report.runAdvertiserReport', advertiserContext.accountId)}">
            <a href="${reporting}/displayAdvertising/options.action${ad:accountParam('?accountId', advertiserContext.accountId)}">
                <fmt:message key="reports.displayAdvertisingReports"/>
            </a>
        </c:if>
        <c:if test="${ad:isPermitted('Report.runTextAdvertisingReport', advertiserContext.accountId)}">
            <a href="${reporting}/textAdvertising/options.action${ad:accountParam('?accountId', advertiserContext.accountId)}">
                <fmt:message key="reports.textAdvertisingReports"/>
            </a>
        </c:if>
        <c:if test="${ad:isPermitted('Report.runVideoAdvertisingReport', advertiserContext.accountId)}">
            <a href="${reporting}/videoAdvertising/options.action${ad:accountParam('?accountId', advertiserContext.accountId)}">
                <fmt:message key="reports.videoAdvertisingReports"/>
            </a>
        </c:if>
        <c:if test="${ad:isPermitted('Report.run', 'conversions')}">
            <c:set var="conversionsParameters" value="${ad:accountParam('?accountId', advertiserContext.advertiserSet ? advertiserContext.advertiserId : advertiserContext.accountId)}"/>
            <a href="${reporting}/conversions/options.action${conversionsParameters}">
                <fmt:message key="reports.conversionsReport"/>
            </a>
        </c:if>
        <c:if test="${ad:isPermitted('Report.run', 'conversionPixels')}">
            <c:set var="conversionPixelsParameters" value="${ad:accountParam('?accountId', advertiserContext.advertiserSet ? advertiserContext.advertiserId : advertiserContext.accountId)}"/>
            <a href="${reporting}/conversionPixels/options.action${conversionPixelsParameters}">
                <fmt:message key="reports.conversionPixelsReport"/>
            </a>
        </c:if>
        <c:if test="${ad:isPermitted('Report.run', 'channelTriggers')}">
            <a href="${_context}/reporting/channelTriggers/options.action${ad:accountParam('?accountId', advertiserContext.accountId)}"><fmt:message key="reports.channelTriggersReport"/></a>
        </c:if>
        <c:if test="${ad:isPermitted('Report.run', 'channel')}">
            <a href="${_context}/reporting/channel/options.action${ad:accountParam('?accountId', advertiserContext.accountId)}"><fmt:message key="reports.channelReport"/></a>
        </c:if>
        <c:if test="${ad:isPermitted('Report.run', 'channelInventory')}">
            <a href="${_context}/reporting/channelInventory/options.action${ad:accountParam('?accountId', advertiserContext.accountId)}"><fmt:message key="reports.channelInventoryReport"/></a>
        </c:if>
    </span>
</ui:section>
