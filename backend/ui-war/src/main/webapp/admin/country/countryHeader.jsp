<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="ad" uri="/ad/serverUI" %>
<ui:header>
<ui:pageHeadingByTitle/>
<c:if test="${isViewPage}">
    <c:set var="isUpdatePermitted" value="${ad:isPermitted0('Country.update')}"/>
    <c:if test="${isUpdatePermitted}">
        <s:url action="%{#attr.moduleName}/%{#attr.entityName}/edit" var="url">
            <s:param name="id" value="%{#attr.countryCode}" />
        </s:url>
        <ui:button message="form.edit" href="${url}"/>
    </c:if>
    <ui:button message="country.CTRAlgorithmData" href="CTRAlgorithm/view.action?id=${countryCode}" />
    <c:if test="${ad:isPermitted0('PlacementsBlacklist.view')}">
        <ui:button message="admin.placementsBlacklist" href="PlacementsBlacklist/view.action?id=${countryCode}" />
    </c:if>
    <s:url var="url" value="/admin/auditLog/view.action">
        <s:param name="type">${ad:getObjectType(entity)}</s:param>
        <s:param name="id">${countryId}</s:param>
        <s:param name="contextName">${contextName}</s:param>
    </s:url>

    <ui:button message="form.viewLog" href="${url}"/>
</c:if>
</ui:header>
