<%@page contentType="text/html" %>
<%@page pageEncoding="UTF-8" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>

<ui:header>
    <ui:pageHeadingByTitle/>
    <c:if test="${ad:isPermitted('CreativeSize.update', model)}">
        <ui:button message="form.edit" href="edit.action?id=${id}"/>
    </c:if>
    <c:if test="${ad:isPermitted('CreativeSize.createCopy', model)}">
        <ui:postButton message="form.createCopy" href="createCopy.action"
                       onclick="return UI.Util.confirmCopy(this);"
                       entityId="${id}" />
   </c:if>
   <ui:button message="form.viewLog" href="/admin/auditLog/view.action?type=33&id=${id}" />
</ui:header>
