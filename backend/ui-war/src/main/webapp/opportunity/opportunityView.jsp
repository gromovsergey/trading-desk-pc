<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>
<%@ taglib uri="/struts-tags" prefix="s" %>

<s:set var="opportunity" value="model"/>

<ui:header>
    <ui:pageHeadingByTitle />
    <c:if test="${ad:isPermitted('Opportunity.update', opportunity)}">
        <ui:button message="form.edit" href="edit.action?id=${id}"/>
    </c:if>

    <ui:button message="form.viewLog" href="/admin/auditLog/view.action?type=45&id=${id}"/>
</ui:header>

<%@include file="opportunityViewFields.jsp"%>