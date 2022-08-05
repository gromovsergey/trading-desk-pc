<%@page contentType="text/html" %>
<%@page pageEncoding="UTF-8" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>

<ui:header>
    <ui:pageHeadingByTitle/>
    <c:if test="${ad:isPermitted('OptionGroup.update', model)}">
        <ui:button message="form.edit" href="edit.action?id=${id}"/>
    </c:if>
    <c:if test="${ad:isPermitted('OptionGroup.delete', model)}">
        <ui:postButton message="form.delete" href="remove.action"
                       onclick="if (!confirm('${ad:formatMessage('confirmDelete')}')) {return false;}"
                       entityId="${id}" />
   </c:if>
</ui:header>
