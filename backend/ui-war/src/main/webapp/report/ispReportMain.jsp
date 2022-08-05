<%@ page contentType="text/html"%>
<%@ page pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:set var="reporting" value="${_context}/reporting"/>

<ui:pageHeadingByTitle/>

<ad:requestContext var="ispContext"/>
<ui:section>
    <span class="groupOfLinks">
        <c:if test="${ad:isPermitted('Report.run', 'ISP')}">
            <a href="${reporting}/isp/options.action${ad:accountParam('?accountId', ispContext.accountId)}"><fmt:message key="reports.ISPReport"/></a>
        </c:if>
        <c:if test="${ad:isPermitted('Report.run', 'invitations')}">
            <a href="../reporting/invitations/options.action${ad:accountParam('?account.id', ispContext.accountId)}"><fmt:message key="reports.invitationsReport"/></a>
        </c:if>
        <c:if test="${ad:isPermitted('Report.run', 'webwise')}">
            <a href="../reporting/webwise/options.action${ad:accountParam('?accountId', ispContext.accountId)}"><fmt:message key="reports.webwiseReport"/></a>
        </c:if>

        <c:if test="${ad:isPermitted('Report.AdvancedISPReports.run', ispContext.accountId)}">

            <a href="../reporting/activeAdvertisers/options.action${ad:accountParam('?accountId', ispContext.accountId)}"><fmt:message key="reports.activeAdvertisers"/></a>

            <a href="../reporting/campaignOverview/options.action${ad:accountParam('?accountId', ispContext.accountId)}"><fmt:message key="reports.campaignOverview"/></a>

            <a href="../reporting/publisherOverview/options.action${ad:accountParam('?accountId', ispContext.accountId)}"><fmt:message key="reports.publisherOverview"/></a>

            <a href="../reporting/profiling/options.action${ad:accountParam('?accountId', ispContext.accountId)}"><fmt:message key="reports.profiling"/></a>

        </c:if>

    </span>
</ui:section>
