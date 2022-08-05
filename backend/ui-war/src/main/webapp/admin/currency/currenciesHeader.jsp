<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:if test="%{hasCurrenciesForCreate()}">
    <ui:header>
        <ui:pageHeadingByTitle />
        <s:url action="%{#attr.moduleName}/%{#attr.entityName}/edit" var="url"/>
        <ui:button message="form.createNew" href="${url}" />
    </ui:header>
</s:if>
<s:else>
    <ui:pageHeadingByTitle />
    <span class="errors"><s:text name="currency.nomore"/></span>
</s:else>
