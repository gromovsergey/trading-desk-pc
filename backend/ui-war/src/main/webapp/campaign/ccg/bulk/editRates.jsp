<%@page contentType="text/html" %>
<%@page pageEncoding="UTF-8" %>

<%@ taglib uri="/struts-tags" prefix="s" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ui" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core"  prefix="c" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>

<s:form action="save">

<s:hidden name="campaignId"/>

<jsp:useBean id="account" scope="request" type="com.foros.model.account.AdvertiserAccount"/>
<c:set var="currencySymbol" value="${ad:currencySymbol(account.currency.currencyCode)}"/>
<fmt:message key="ccg.ccgRate" var="ccgRateLabel"/>

<%@ include file="bulkGroupErrors.jsp"%>

<ui:fieldGroup>
    <ui:field id="ccgRate" label="${ccgRateLabel} (${currencySymbol})" required="true" errors="rate,rateValue">
        <table class="fieldAndAccessories">
            <tr>
                <td class="withField">
                    <s:textfield id="ccgRateInput" name="rateValue" cssClass="smallLengthText1" maxlength="20"/>
                </td>
                <td class="withField">
                    <s:select id="rateTypeSelect" name="rateType" list="rateTypes" listKey="name()" listValue="name()"/>
                </td>
            </tr>
        </table>
    </ui:field>
</ui:fieldGroup>

</s:form>