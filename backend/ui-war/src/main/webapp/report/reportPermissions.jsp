<%@ page contentType="text/html"%>
<%@ page pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>

<c:set var="canRunCustomReport" value="#{ad:isPermitted('Report.run', 'custom')}"/>
<c:set var="canRunGeneralAdvertiserReport" value="#{ad:isPermitted('Report.run', 'generalAdvertising')}"/>
<c:set var="canRunAdvertiserReport" value="#{ad:isPermitted('Report.run', 'advertiser')}"/>
<c:set var="canRunTextAdvertisingReport" value="#{ad:isPermitted('Report.run', 'textAdvertising')}"/>
<c:set var="canRunPublisherReport" value="#{ad:isPermitted('Report.run', 'publisher')}"/>
<c:set var="canRunISPReport" value="#{ad:isPermitted('Report.run', 'ISP')}"/>
<c:set var="canRunInvitationsReport" value="#{ad:isPermitted('Report.run', 'invitations')}"/>
<c:set var="canRunWebwiseReport" value="#{ad:isPermitted('Report.run', 'webwise')}"/>
<c:set var="canRunInventoryEstimationReport" value="#{ad:isPermitted('Report.run', 'inventoryEstimation')}"/>
<c:set var="canRunAuditReport" value="#{ad:isPermitted('Report.run', 'audit')}"/>
<c:set var="canRunChannelTriggersReport" value="#{ad:isPermitted('Report.run', 'channelTriggers')}"/>
<c:set var="canRunSiteChannelsReport" value="#{ad:isPermitted('Report.run', 'siteChannels')}"/>
<c:set var="canRunChannelSitesReport" value="#{ad:isPermitted('Report.run', 'channelSites')}"/>
<c:set var="canRunConversionPixelsReport" value="#{ad:isPermitted('Report.run', 'conversionPixels')}"/>
<c:set var="canRunChannelUsageReport" value="#{ad:isPermitted('Report.run', 'channelUsage')}"/>
<c:set var="canRunChannelReport" value="#{ad:isPermitted('Report.run', 'channel')}"/>
<c:set var="canRunChannelInventoryReport" value="#{ad:isPermitted('Report.run', 'channelInventory')}"/>
<c:set var="canRunGeotargetingReport" value="#{ad:isPermitted('Report.run', 'geotargeting')}"/>
<c:set var="canRunUserAgentsReport" value="#{ad:isPermitted('Report.run', 'userAgents')}"/>

<c:set var="ispReportsAvailable" value="#{canRunISPReport or canRunInvitationsReport or canRunWebwiseReport}"/>
<c:set var="publisherReportsAvailable" value="#{canRunPublisherReport or canRunInventoryEstimationReport}"/>
<c:set var="cmpReportsAvailable" value="#{canRunChannelUsageReport or canRunChannelTriggersReport or canRunChannelReport}"/>
<c:set var="advertiserReportsAvailable" value="#{canRunAdvertiserReport or canRunConversionPixelsReport or canRunChannelTriggersReport or canRunChannelReport or canRunTextAdvertisingReport or canRunGeneralAdvertiserReport}"/>
