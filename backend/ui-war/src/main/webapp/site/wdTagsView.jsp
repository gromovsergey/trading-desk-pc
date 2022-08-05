<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="ad" uri="/ad/serverUI" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<ui:header>
    <ui:pageHeadingByTitle />
    <s:set var="entityBean" value="%{entity}"/>
    <c:if test="${ad:isPermitted('PublisherEntity.update', entityBean)}">
        <s:url var="buttUrl" action="%{#attr.moduleName}/%{#attr.entityName}/edit" includeParams="get"/>
        <ui:button message="form.edit" href="${buttUrl}" />
    </c:if>
    <%@ include file="../auditLog/viewLogButton.jspf" %>
</ui:header>

<c:set var="wdContextPath" value="${_context}/site/WDTag"/>
<ui:section titleKey="form.main">
    <ui:fieldGroup>
        <ui:field labelKey="wdtag.status">
            <c:set var="statusDescriptionKey">enums.Status.${status}</c:set>
            <ui:statusButtonGroup
                descriptionKey="${statusDescriptionKey}"
                entity="${entityBean}" restrictionEntity="PublisherEntity" 
                deletePage="delete.action"
                undeletePage="undelete.action"/>
        </ui:field>
        <c:if test="${not empty width}">
            <ui:field labelKey="wdtag.width">
                <fmt:formatNumber value="${width}"/>
            </ui:field>
        </c:if>
        <c:if test="${not empty height}">
            <ui:field labelKey="wdtag.height">
                <fmt:formatNumber value="${height}"/>
            </ui:field>
        </c:if>
        <ui:field labelKey="wdtag.template">
            <ui:displayStatus displayStatus="${template.displayStatus}">
                <c:choose>
                    <c:when test="${ad:isPermitted0('Template.view')}">
                        <a href="${_context}/DiscoverTemplate/view.action?id=${template.id}">
                            <c:out value="${ad:localizeNameWithStatus(template.name, template.status)}"/>
                        </a>
                    </c:when>
                    <c:otherwise>
                        ${ad:localizeNameWithStatus(template.name, template.status)}
                    </c:otherwise>
                </c:choose>
            </ui:displayStatus>
        </ui:field>
    </ui:fieldGroup>
</ui:section>

<ui:section titleKey="wdtag.lookAndFeelPreview">
    <div style="padding-bottom:5px;"><fmt:message key="wdtag.lookAndFeelComment"/></div>
    <c:url var="previewUrl" value="${wdContextPath}/preview/content.action?id=${id}"/>
    <ui:wdTagPreview id="previewFrame" width="${width}" height="${height}" src="${previewUrl}"/>
</ui:section>

<ui:section titleKey="wdtag.code">
    <ui:fieldGroup>
        <ui:field labelKey="wdtag.tagHtmlCode" errors="tagHtmlCode">
            <s:textarea name="tagHtmlCode" readonly="true" cols="70" rows="7" cssClass="middleLengthText"/>
        </ui:field>
    </ui:fieldGroup>
</ui:section>
