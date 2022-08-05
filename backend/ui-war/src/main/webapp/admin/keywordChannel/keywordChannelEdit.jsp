<%@ page contentType="text/html" %>
<%@ page pageEncoding="UTF-8" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/struts-tags" prefix="s" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ui" %>

<s:form action="admin/KeywordChannel/save" styleId="channelSave">

<s:hidden name="id"/>
<s:hidden name="version"/>

<ui:pageHeadingByTitle/>

<ui:errorsBlock>
    <s:fielderror><s:param value="'version'"/></s:fielderror>
</ui:errorsBlock>

<ui:section>
    <ui:fieldGroup>

        <ui:simpleField labelKey="account.internalAccount" value="${account.name}"/>
        <s:hidden name="account.id"/>
        <s:hidden name="account.name"/>

        <ui:simpleField labelKey="channel.keywordChannel.keyword" value="${name}"/>
        <s:hidden name="name"/>

        <ui:field labelKey="channel.keywordChannel.type">
            <fmt:message key="enums.KeywordTriggerType.${triggerType.name}"/>
            <s:hidden name="triggerType"/>
        </ui:field>

        <ui:field labelKey="channel.country" labelForId="countryCode" id="countryElem" errors="country.countryCode">
            <fmt:message key="global.country.${country.countryCode}.name"/>
            <s:hidden name="country.countryCode"/>
        </ui:field>
    </ui:fieldGroup>
</ui:section>

<ui:keywordBehavioralParamsEdit bpTypes="${triggerType.letter}" />

<ui:frequencyCapEdit fcPropertyName="frequencyCap"/>

<div class="wrapper">
  <ui:button message="form.save" type="submit"/>
  <c:choose>
        <c:when test="${standalone == true}">
            <ui:button message="form.cancel" onclick="closeWindow(); return false;" type="button" />
        </c:when>
        <c:otherwise>
            <ui:button message="form.cancel" onclick="location='view.action?id=${id}'" type="button" />
    </c:otherwise>
  </c:choose>
</div>

</s:form>
