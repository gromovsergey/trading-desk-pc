<%@page contentType="text/html" %>
<%@page pageEncoding="UTF-8" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>

<ui:header>
    <ui:pageHeadingByTitle/>
    <c:if test="${ad:isPermitted0('CreativeSize.update')}">
        <ui:button message="form.edit" href="edit.action?id=${id}"/>
    </c:if>
    <ui:button message="form.viewLog" href="/admin/auditLog/view.action?type=55&id=${id}" />
</ui:header>
