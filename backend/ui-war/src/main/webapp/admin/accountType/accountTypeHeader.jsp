<%@page contentType="text/html" pageEncoding="UTF-8" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="ad" uri="/ad/serverUI" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<ui:header>

    <ui:pageHeadingByTitle/>

    <c:set var="canUpdate" value="${ad:isPermitted0('AccountType.update')}"/>

    <s:if test="#attr.canUpdate">

        <s:url var="url" value="/admin/AccountType/edit.action">
            <s:param name="id">${entity.id}</s:param>
        </s:url>
        <ui:button message="form.edit" href="${url}"/>

    </s:if>

    <s:url var="url" value="/admin/auditLog/view.action" >
        <s:param name="type">${ad:getObjectType(entity)}</s:param>
        <s:param name="id">${entity.id}</s:param>
    </s:url>
    <ui:button message="form.viewLog" href="${url}" />

</ui:header>
