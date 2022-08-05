<%@page contentType="text/html" %>
<%@page pageEncoding="UTF-8" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>

<ui:header>
    <s:if test="model">
        <c:set var="entity" value="${model}"/>
    </s:if>
    <ui:pageHeadingByTitle/>
    <c:set var="restriction" value="${entityName}.update"/>
    <c:if test="${not empty updateEntityRestriction}">
        <c:set var="restriction" value="${updateEntityRestriction}"/>
    </c:if>

    <c:choose>
        <c:when test="${onEntityRestriction}">
            <c:set var="isUpdatePermitted" value="${ad:isPermitted(restriction, entity)}"/>
        </c:when>
        <c:otherwise>
            <c:set var="isUpdatePermitted" value="${ad:isPermitted0(restriction)}"/>
        </c:otherwise>
    </c:choose>

    <c:set var="isNotTextTemplate" value="${entity.defaultName ne 'Text'}"/>
    <c:if test="${isUpdatePermitted and isNotTextTemplate}">
        <c:set var="entityId" value="${id}"/>
        <s:url action="%{#attr.moduleName}/%{#attr.entityName}/edit" var="url">
            <s:param name="id" value="%{#attr.entityId}" />
        </s:url>
        <ui:button message="form.edit" href="${url}"/>
   </c:if>

    <s:if test="#attr.hasAuditLog">
        <s:url var="url" value="/admin/auditLog/view.action" >
            <s:param name="type">${ad:getObjectType(entity)}</s:param>
            <s:param name="id">${entity.id}</s:param>
        </s:url>
        <ui:button message="form.viewLog" href="${url}" />
    </s:if>
</ui:header>
