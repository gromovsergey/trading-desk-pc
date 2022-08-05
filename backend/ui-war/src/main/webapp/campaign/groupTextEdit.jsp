<%@ taglib prefix="s" uri="/struts-tags" %>

<%@ include file="ccg/editHeader.jsp"%>

<s:set value="#attr.isCreatePage?'createText':'updateText'" var="saveActionName"/>

<s:form action="%{#attr.moduleName}/%{#attr.entityName!=null?('create'+#attr.entityName):#saveActionName}" id="CCGForm">
  <%@ include file="ccg/groupWizardSettings.jsp"%>
  <%@ include file="ccg/editGeneralProperties.jsp"%>
  <%@ include file="ccg/editDeliveryPeriod.jsp"%>

  <ui:frequencyCapEdit fcPropertyName="frequencyCap"/>

  <c:if test="${ad:isInternal()}">
    <%@ include file="ccg/editOptInStatusTargeting.jsp"%>
    <%@ include file="ccg/editISPColocationTargeting.jsp"%>
  </c:if>

  <s:if test="existingGroup.tgtType.letter == 'C' && existingGroup.account.accountType.allowTextChannelAdvertisingFlag">
      <%@ include file="ccg/editSitesTargeting.jsp"%>
  </s:if>

  <%@ include file="ccg/editConversionsTracking.jsp"%>

  <%@ include file="ccg/editOtherSection.jsp"%>

  <%@ include file="ccg/editFooter.jsp"%>
</s:form>
