<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<ui:header>
    <ui:pageHeadingByTitle />
    <c:if test="${ad:isPermitted0('GlobalParams.update')}">
    <s:url action="%{#attr.moduleName}/%{#attr.entityName}/edit" var="url"/>
    <ui:button message="form.edit" href="${url}" />
    </c:if>
</ui:header>

<ui:section titleKey="GlobalParam.discover">
    <ui:fieldGroup>
        <ui:field labelKey="GlobalParam.wdTagMapping">
            <c:set var="textVal">
                <s:if test="name != null">
                    <s:property value="name"/>
                </s:if>
                <s:else>
                    <s:text name="GlobalParam.wdTagMapping.notSpecified"/>
                </s:else>
            </c:set>
            <ui:text text="${pageScope.textVal}"/>
        </ui:field>
    </ui:fieldGroup>
</ui:section>

<ui:section titleKey="GlobalParam.currency">
    <ui:fieldGroup>
        <ui:field labelKey="GlobalParam.currency.exchangeRateUpdate">
            <fmt:message key="${exchangeRateSource.resourceKey}"/>
        </ui:field>
    </ui:fieldGroup>
</ui:section>
