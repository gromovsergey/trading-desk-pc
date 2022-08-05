<%@ taglib tagdir="/WEB-INF/tags" prefix="ui" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<%@ include file="ccg/editHeader.jsp"%>

<s:set value="#attr.isCreatePage?'createDisplay':'updateDisplay'" var="saveActionName"/>

<s:form action="%{#attr.moduleName}/%{#attr.entityName!=null?('create'+#attr.entityName):#saveActionName}" id="CCGForm">
<%@ include file="ccg/groupWizardSettings.jsp"%>
<%@ include file="ccg/editGeneralProperties.jsp"%>
<%@ include file="ccg/editDeliveryPeriod.jsp"%>

<ui:frequencyCapEdit fcPropertyName="frequencyCap"/>

<c:if test="${ad:isInternal()}">
    <%@ include file="ccg/editOptInStatusTargeting.jsp"%>
    <%@ include file="ccg/editISPColocationTargeting.jsp"%>
</c:if>

<%@ include file="ccg/editSitesTargeting.jsp"%>
<%@ include file="ccg/editConversionsTracking.jsp"%>
<%@ include file="ccg/editOtherSection.jsp"%>

<%@ include file="ccg/editFooter.jsp"%>
</s:form>
