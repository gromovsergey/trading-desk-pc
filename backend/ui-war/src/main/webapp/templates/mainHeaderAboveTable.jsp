<%@page contentType="text/html" %>
<%@page pageEncoding="UTF-8" %>

<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="ad" uri="/ad/serverUI" %>

<ui:header>
    <ui:pageHeadingByTitle/>
    <c:set var="restriction" value="${entityName}.create"/>
    <c:if test="${not empty createEntityRestriction}">
        <c:set var="restriction" value="${createEntityRestriction}"/>
    </c:if>
    <c:if test="${ad:isPermitted0(restriction)}">
        <s:url var="createNewUrl" action="%{#attr.moduleName}/%{#attr.entityName}/create"/>
        <ui:button message="form.createNew" href="${createNewUrl}"/>
    </c:if>

</ui:header>

