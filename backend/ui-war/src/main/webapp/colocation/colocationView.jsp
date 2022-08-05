<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ui" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:set name="colocation" value="model"/>

<ui:header>
    <ui:pageHeadingByTitle/>
     <c:if test="${ad:isPermitted('Colocation.update', colocation)}">
         <ui:button message="form.edit" href="edit.action?id=${colocation.id}" />
     </c:if>
    <c:if test="${ad:isPermitted('Entity.viewLog', colocation)}">
        <ui:button message="form.viewLog" href="/admin/auditLog/view.action?type=3&id=${colocation.id}" />
     </c:if>
</ui:header>

<ui:errorsBlock>
    <s:fielderror><s:param value="'version'"/></s:fielderror>
</ui:errorsBlock>

<ui:section titleKey="form.main">
    <ui:fieldGroup>
        
        <ui:simpleField labelKey="colocation.colocationId" value="${colocation.id}"/>
        
        <ui:field labelKey="colocation.status">
            <ui:statusButtonGroup
                descriptionKey="${colocation.displayStatus.description}"
                entity="${colocation}" restrictionEntity="Colocation" 
                deletePage="delete.action"
                undeletePage="undelete.action"/>
        </ui:field>

        <c:if test="${not empty colocationRate.revenueShareInPercent}">
            <ui:field labelKey="colocation.revenueShare">
                <s:property value="colocationRate.revenueShareInPercent"/>%
            </ui:field>
        </c:if>

        <c:choose>
            <c:when test="${colocation.optOutServing eq 'ALL'}">
                <fmt:message var="nonOptedInUserServingMessage" key="colocation.nonOptedInUserServing.all"/>
            </c:when>
            <c:when test="${colocation.optOutServing eq 'NON_OPTOUT'}">
                <fmt:message var="nonOptedInUserServingMessage" key="colocation.nonOptedInUserServing.nonOptOut"/>
            </c:when>
            <c:when test="${colocation.optOutServing eq 'OPTIN_ONLY'}">
                <fmt:message var="nonOptedInUserServingMessage" key="colocation.nonOptedInUserServing.optInOnly"/>
            </c:when>
            <c:when test="${colocation.optOutServing eq 'NONE'}">
                <fmt:message var="nonOptedInUserServingMessage" key="colocation.nonOptedInUserServing.none"/>
            </c:when>
        </c:choose>

        <ui:simpleField labelKey="colocation.nonOptedInUserServing"
                        value="${nonOptedInUserServingMessage}"/>

    </ui:fieldGroup>
</ui:section>
