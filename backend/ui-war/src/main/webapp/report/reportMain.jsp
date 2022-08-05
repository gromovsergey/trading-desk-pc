<%@ page contentType="text/html"%>
<%@ page pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<%@include file="reportPermissions.jsp"%>

<c:set var="predefinedReports">
    <c:if test="${canRunCustomReport}">
        <a href="../reporting/custom/options.action"><fmt:message key="reports.customReport"/></a>
    </c:if>
    <c:if test="${canRunGeneralAdvertiserReport}">
        <a href="../reporting/generalAdvertising/options.action"><fmt:message key="reports.generalAdvertisingReport"/></a>
    </c:if>
    <c:if test="${canRunAdvertiserReport}">
        <a href="../reporting/displayAdvertising/options.action"><fmt:message key="reports.displayAdvertisingReports"/></a>
    </c:if>
    <c:if test="${canRunTextAdvertisingReport}">
        <a href="../reporting/textAdvertising/options.action"><fmt:message key="reports.textAdvertisingReports"/></a>
    </c:if>
    <c:if test="${canRunPublisherReport}">
        <a href="../reporting/publisher/options.action"><fmt:message key="reports.publisherReport"/></a>
    </c:if>
    <c:if test="${canRunISPReport}">
        <a href="../reporting/isp/options.action"><fmt:message key="reports.ISPReport"/></a>
    </c:if>
    <c:if test="${canRunInvitationsReport}">
        <a href="../reporting/invitations/options.action"><fmt:message key="reports.invitationsReport"/></a>
    </c:if>
    <c:if test="${canRunWebwiseReport}">
        <a href="../reporting/webwise/options.action"><fmt:message key="reports.webwiseReport"/></a>
    </c:if>
    <c:if test="${canRunInventoryEstimationReport}">
        <a href="../reporting/inventoryEstimation/options.action"><fmt:message key="reports.inventoryEstimationReport"/></a>
    </c:if>
    <c:if test="${canRunAuditReport}">
        <a href="audit.action"><fmt:message key="reports.auditReport"/></a>
    </c:if>
    <c:if test="${canRunChannelTriggersReport}">
         <a href="${_context}/reporting/channelTriggers/options.action"><fmt:message key="reports.channelTriggersReport"/></a>
    </c:if>
    <c:if test="${canRunSiteChannelsReport}">
        <a href="${_context}/reporting/siteChannels/options.action"><fmt:message key="reports.siteChannelsReport"/></a>
    </c:if>
    <c:if test="${canRunChannelSitesReport}">
        <a href="${_context}/reporting/channelSites/options.action"><fmt:message key="reports.channelSitesReport"/></a>
    </c:if>
    <c:if test="${canRunChannelUsageReport}">
        <a href="${_context}/reporting/channelUsage/options.action"><fmt:message key="reports.channelUsageReport"/></a>
    </c:if>
    <c:if test="${canRunChannelReport}">
        <a href="${_context}/reporting/channel/options.action"><fmt:message key="reports.channelReport"/></a>
    </c:if>
    <c:if test="${canRunChannelInventoryReport}">
        <a href="${_context}/reporting/channelInventory/options.action"><fmt:message key="reports.channelInventoryReport"/></a>
    </c:if>
    <c:if test="${canRunUserAgentsReport}">
        <a href="../reporting/userAgents/options.action"><fmt:message key="reports.userAgentsReport"/></a>
    </c:if>
</c:set>
<c:if test="${not empty predefinedReports}">
    <ui:pageHeading attributeName="reports.predefinedReports"/>
    <ui:section>
        <span class="groupOfLinks">${predefinedReports}</span>
    </ui:section>
</c:if>
<!--  BIRT REPORTS  -->
<c:if test="${ad:isPermitted0('BirtReport.view')}">
    <c:set var="canCreate" value="${ad:isPermitted0('BirtReport.create')}"/>
    <c:if test="${canCreate or not empty birtReports}">
        <ui:header>
            <ui:pageHeading attributeName="reports.birtReports" />
            <c:if test="${canCreate}">
                <ui:button message="form.createNew" href="birtReport/new.action" />
            </c:if>
            <c:if test="${ad:isPermitted0('BirtReport.viewAuditLog')}">
                <ui:button message="form.viewLog" href="/admin/auditLog/view.action?type=43"/>
            </c:if>
        </ui:header>
        <c:if test="${not empty birtReports}">
            <display:table name="birtReports" class="dataView" id="birtReport">
                <display:column titleKey="birtReports.report" >
                    <c:choose>
                        <c:when test="${birtReport.viewable}">
                            <a target="_blank" href="/birt/reports/run/${birtReport.id}/">
                                <c:out value="${birtReport.name}"/>
                            </a>
                        </c:when>
                        <c:otherwise>
                            <c:out value="${birtReport.name}"/>
                        </c:otherwise>
                    </c:choose>
                </display:column>
                <display:column titleKey="birtReports.actions" class="withInnerButton">
                    <c:if test="${birtReport.updatable}">
                        <ui:button message="form.edit" href="birtReport/edit.action?id=${birtReport.id}" />
                        <ui:postButton message="form.delete" href="birtReport/delete.action" entityId="${birtReport.id}"
                            onclick="if (!confirm('${ad:formatMessage('confirmDelete')}')) {return false;}"/>
                        <ui:button message="form.viewLog" href="/admin/auditLog/view.action?type=43&id=${birtReport.id}" />
                    </c:if>
              </display:column>
            </display:table>
        </c:if>
    </c:if>
    <c:if test="${empty birtReports and empty predefinedReports}">
        <div class="wrapper">
            <fmt:message key="nothing.found.to.display"/>
        </div>
    </c:if>
</c:if>
