<%@page contentType="text/html" %>
<%@page pageEncoding="UTF-8" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>

<ui:header>
    <ui:pageHeadingByTitle/>
    <c:set var="isUpdatePermitted" value="${ad:isPermitted('Template.update', model)}"/>
    <c:if test="${isUpdatePermitted}">
        <ui:button message="form.edit" href="edit.action?id=${id}"/>
        <ui:postButton message="form.createCopy" href="createCopy.action"
                       onclick="return UI.Util.confirmCopy(this);"
                       entityId="${id}" />
   </c:if>
   <ui:button message="form.viewLog" href="/admin/auditLog/view.action?type=34&id=${id}" />
</ui:header>
