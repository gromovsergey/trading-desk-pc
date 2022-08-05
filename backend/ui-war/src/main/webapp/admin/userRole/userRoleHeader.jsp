<%@page contentType="text/html" pageEncoding="UTF-8" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="ad" uri="/ad/serverUI" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>


<ui:header>

    <ui:pageHeadingByTitle/>

    <c:if test="${ad:isPermitted0('UserRole.update')}">
        <c:url var="url" value="/admin/UserRole/edit.action">
            <c:param name="id" value="${entity.id}"/>
        </c:url>
        <ui:button message="form.edit" href="${url}"/>
    </c:if>

    <c:url var="url" value="/admin/auditLog/view.action">
        <c:param name="type" value="${ad:getObjectType(entity)}"/>
        <c:param name="id" value="${entity.id}"/>
    </c:url>
    <ui:button message="form.viewLog" href="${url}" />

</ui:header>
