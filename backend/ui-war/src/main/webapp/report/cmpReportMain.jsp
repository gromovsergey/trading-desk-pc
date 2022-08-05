<%@ page contentType="text/html"%>
<%@ page pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>

<ui:pageHeadingByTitle/>

<ad:requestContext var="cmpContext"/>
<ui:section>
    <span class="groupOfLinks">
        <c:if test="${ad:isPermitted('Report.run', 'channelUsage')}">
            <a href="${_context}/reporting/channelUsage/options.action${ad:accountParam('?accountId', cmpContext.accountId)}"><fmt:message key="reports.channelUsageReport"/></a>
        </c:if>
        <c:if test="${ad:isPermitted('Report.run', 'channelTriggers')}">
            <a href="${_context}/reporting/channelTriggers/options.action${ad:accountParam('?accountId', cmpContext.accountId)}"><fmt:message key="reports.channelTriggersReport"/></a>
        </c:if>
        <c:if test="${ad:isPermitted('Report.run', 'channel')}">
            <a href="${_context}/reporting/channel/options.action${ad:accountParam('?accountId', cmpContext.accountId)}"><fmt:message key="reports.channelReport"/></a>
        </c:if>
        
    </span>
</ui:section>
