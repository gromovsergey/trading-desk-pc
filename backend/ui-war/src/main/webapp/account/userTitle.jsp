<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<title>
    <s:if test="!#attr.titleProperty.empty">
        <ui:windowTitle attributeName="${titleProperty}"/>
    </s:if>
    <s:else>
        <ui:windowTitleEntity entityName="User" name="${model.firstName} ${model.lastName}" id="${id}" colorStatus="${isViewPage ? model.displayStatus : null}" isViewPage="${isViewPage}"/>
    </s:else>
</title>