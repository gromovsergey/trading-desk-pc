<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@ page import="com.foros.util.CurrencyHelper" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<ui:pageHeadingByTitle/>

<s:form action="admin/GlobalParam/save">
<c:set var="currencySymbol"><%=CurrencyHelper.getCurrencySymbol("USD")%></c:set>

<div class="wrapper">
    <s:fielderror cssClass="errors"><s:param value="'version'"/></s:fielderror>
</div>

<ui:section titleKey="GlobalParam.discover">
    <ui:fieldGroup>
        <ui:field labelKey="GlobalParam.wdTagMapping">
            <s:hidden name="wdTagMapping.version"/>
            <s:select name="wdTagMapping.value" list="wdTagMappings" cssClass="middleLengthText"
                      listKey="id" listValue="name" headerKey="" headerValue="%{getText('GlobalParam.wdTagMapping.notSpecified')}" />
        </ui:field>
    </ui:fieldGroup>
</ui:section>

<c:if test="${ad:isPermitted0('Currency.update')}">

<ui:section titleKey="GlobalParam.currency">
    <ui:fieldGroup>
        <ui:field labelKey="GlobalParam.currency.exchangeRateUpdate" cssClass="valignFix">
            <s:hidden name="exchangeRateUpdate.version"/>
            <s:radio cssClass="withInput" name="exchangeRateUpdate.value" 
                list="sourceValues" listValue="getText(resourceKey)" />
        </ui:field>
    </ui:fieldGroup>
</ui:section>

</c:if>

<div class="wrapper">
    <ui:button message="form.save" type="submit"/>
    <ui:button message="form.cancel" onclick="location='view.action';" type="button" />
</div>
</s:form>
