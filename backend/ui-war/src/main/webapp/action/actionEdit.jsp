<%@page contentType="text/html" %>
<%@page pageEncoding="UTF-8" %>

<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>

<ui:pageHeadingByTitle/>

<s:form action="%{#request.moduleName}/%{#request.entityName}/%{#attr.isCreatePage?'create':'update'}" id="actionSave">
    <s:hidden name="id"/>
    <s:hidden name="version"/>
    <s:hidden name="account.id"/>
    <s:hidden name="account.currency.currencyCode"/>
    
    <div class="wrapper">
        <s:fielderror><s:param value="'version'"/></s:fielderror>
    </div>
    <s:actionerror/>

    <ui:section>
        <ui:fieldGroup>
            <ui:field labelKey="Action.name" labelForId="name" required="true" errors="name">
                <s:textfield name="name" cssClass="middleLengthText" maxlength="100"/>
            </ui:field>

            <ui:field labelKey="Action.conversionCategory" labelForId="conversionCategoryId" tipKey="Action.conversionCategory.hint" errors="conversionCategory">
                <s:select name="conversionCategoryId" id="conversionCategoryId" cssClass="middleLengthText" list="conversionCategories"
                          listKey="id" listValue="getText(nameKey)" value="conversionCategory.id"/>
            </ui:field>

            <c:set var="currencySymbol" value="${ad:currencySymbol(account.currency.currencyCode)}"/>
            <fmt:message key="Action.value" var="actionValue"/>
            <ui:field label="${actionValue} (${currencySymbol})" labelForId="value" errors="value" tipKey="Action.value.hint" required="true" >
                <s:textfield name="value" cssClass="smallLengthText" maxlength="15"/>
            </ui:field>

            <ui:field labelKey="Action.url" labelForId="url" errors="url" tipKey="Action.url.hint">
                <s:textfield name="url" cssClass="middleLengthText" maxlength="2000"/>
            </ui:field>

            <ui:field labelKey="Action.impWindow" labelForId="impWindow" tipKey="Action.impWindow.hint" required="true" errors="impWindow">
                <s:textfield name="impWindow" cssClass="smallLengthText" maxlength="2"/>
            </ui:field>

            <ui:field labelKey="Action.clickWindow" labelForId="clickWindow" tipKey="Action.clickWindow.hint" required="true" errors="clickWindow">
                <s:textfield name="clickWindow" cssClass="smallLengthText" maxlength="2"/>
            </ui:field>
        </ui:fieldGroup>
    </ui:section>

    <s:set var="advertiserId" value="account.id"/>
    <s:include value="/templates/formFooter.jsp">
        <s:param name="createUrl">list.action${ad:accountParam("?advertiserId", advertiserId)}</s:param>
    </s:include>
</s:form>
