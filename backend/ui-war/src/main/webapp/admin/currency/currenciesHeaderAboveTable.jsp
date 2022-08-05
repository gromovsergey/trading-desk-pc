<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="ad" uri="/ad/serverUI" %>


<s:if test="%{hasCurrenciesForCreate()}">
    <ui:header>
        <ui:pageHeadingByTitle />
        <c:if test="${ad:isPermitted0('Currency.create')}">
            <s:url var="url" action="%{#attr.moduleName}/%{#attr.entityName}/new"/>
            <ui:button message="form.createNew" href="${url}" />
        </c:if>
    </ui:header>
</s:if>
<s:else>
    <ui:pageHeadingByTitle />
    <span class="errors"><s:text name="currency.nomore"/></span>
</s:else>