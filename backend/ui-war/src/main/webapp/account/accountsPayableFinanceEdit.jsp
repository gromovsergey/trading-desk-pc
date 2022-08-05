<%@ page import="com.foros.model.security.PaymentMethod" %>
<%@page contentType="text/html" %>
<%@page pageEncoding="UTF-8" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="ad" uri="/ad/serverUI" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<c:set var="handlingFeeLabel">
    <fmt:message key="account.handlingFee"/> (%)
</c:set>

<c:choose>
    <c:when test="${contextName == 'global.menu.publishers'}">
        <s:set var="saveActionName" value="'updateFinance'" />
        <s:set var="viewActionName" value="'view.action'" />
    </c:when>
    <c:when test="${contextName == 'global.menu.isps'}">
        <s:set var="saveActionName" value="'updateFinance'" />
        <s:set var="viewActionName" value="'view.action'" />
    </c:when>
    <c:when test="${contextName == 'global.menu.cmps'}">
        <s:set var="saveActionName" value="'updateFinance'" />
        <s:set var="viewActionName" value="'view.action'" />
    </c:when>
</c:choose>
<c:set var="paymentMethodBACS" value="<%=PaymentMethod.BACS%>" />
<c:set var="paymentMethodSwift" value="<%=PaymentMethod.Swift%>" />
<script type="text/javascript">
    function updatePaymentMethod() {
        var selectedCountry = $('#countryCode');
        var paymentMethod = '';

        if ("GB" == selectedCountry.val()) {
            // selected country is UK
            // set payment method to UK specif
            paymentMethod = '${paymentMethodBACS}';
            $('#bankBicCodeRow, #bankAccountIbanRow').hide();
            $('#bankSortCodeRow').show();
            $('#bankBicCode, #bankAccountIban').val('');
        } else {
            // selected country is different than UK
            // change payment method
            paymentMethod = '${paymentMethodSwift}';
            $('#bankBicCodeRow, #bankAccountIbanRow').show();
            $('#bankSortCodeRow').hide();
            $('#bankSortCode').val('');
        }

        $('#paymentMethodName').val(paymentMethod);
        $('#paymentMethodText').text(paymentMethod);
    }

    $().ready(function(){
        updatePaymentMethod();
    });
</script>
<s:form action="%{#attr.moduleName}/%{#saveActionName}" id="AccountFinanceFormId">
    <s:hidden name="accountId"/>
    <s:hidden name="version"/>
    <s:hidden name="data.version"/>
    <ui:errorsBlock>
        <s:fielderror><s:param value="'version'"/></s:fielderror>
    </ui:errorsBlock>

<ui:pageHeadingByTitle/>
<ui:section>
    <ui:fieldGroup id="finPubISP">
        <ui:field labelForId="countryCode" labelKey="account.bankCountry" required="true" errors="bankCountry">
            <s:select name="bankCountry.countryCode" id="countryCode" cssClass="middleLengthText"
                  list="countries" value="bankCountry.countryCode"
                  listKey="id" listValue="getText('global.country.' + id + '.name')"
                  onchange="updatePaymentMethod();" >
            </s:select>
        </ui:field>

        <ui:field id="paymentMethodTextId" labelKey="account.paymentMethod" labelForId="paymentMethodText">
            <ui:text text="${paymentMethod}" id="paymentMethodText"/>
        </ui:field>

        <ui:field labelKey="account.bankName" labelForId="bankName" required="true" errors="bankName">
            <s:textfield id="bankName" name="bankName" cssClass="middleLengthText" maxLength="80"/>
        </ui:field>

        <ui:field labelForId="bankCurrency" labelKey="account.bankCurrency" required="true" errors="bankCurrency">
            <s:select name="bankCurrency.id" id="bankCurrency" cssClass="middleLengthText"
                  headerValue="%{getText('form.select.pleaseSelect')}" headerKey=""
                  list="currencies" value="bankCurrency.id"
                  listKey="id" listValue="getText('global.currency.' + name + '.name')">
            </s:select>
        </ui:field>

        <ui:field labelForId="bankBranchName" required="true" labelKey="account.bankBranchName" errors="bankBranchName">
            <s:textfield id="bankBranchName" name="bankBranchName" cssClass="middleLengthText" maxLength="80"/>
        </ui:field>

        <ui:field id="bankSortCodeRow" labelForId="bankSortCode" required="true" labelKey="account.bankSortCode" errors="bankSortCode">
            <s:textfield id="bankSortCode" name="bankSortCode" cssClass="middleLengthText" maxLength="15"/>
        </ui:field>

        <ui:field id="bankBicCodeRow" labelForId="bankBicCode" required="true" labelKey="account.bankBicCode" errors="bankBicCode">
            <s:textfield id="bankBicCode" name="bankBicCode" cssClass="middleLengthText" maxLength="30"/>
        </ui:field>

        <ui:field labelForId="bankAccountName" labelKey="account.bankAccountName" required="true" errors="bankAccountName">
            <s:textfield id="bankAccountName" name="bankAccountName" cssClass="middleLengthText" maxLength="80"/>
        </ui:field>

        <ui:field labelForId="bankAccountNumber" labelKey="account.bankAccountNumber" required="true" errors="bankAccountNumber">
            <s:textfield id="bankAccountNumber" name="bankAccountNumber" cssClass="middleLengthText" maxLength="100"/>
        </ui:field>

        <ui:field id="bankAccountIbanRow" labelForId="bankAccountIban" labelKey="account.bankAccountIban" errors="bankAccountIban">
            <s:textfield id="bankAccountIban" name="bankAccountIban" cssClass="middleLengthText" maxLength="50"/>
        </ui:field>

        <ui:field labelForId="accountBillingContact" labelKey="account.accountBillingContact" required="true">
            <s:select name="defaultBillToUser.id" id="accountBillingContact" cssClass="middleLengthText"
                      list="accountBillToUsers"
                      listKey="id" listValue="name" value="defaultBillToUser.id" >
            </s:select>
        </ui:field>
        <c:choose>
            <c:when test="${existingAccount.country.vatNumberInputEnabled}">
                <ui:field id="taxName" labelKey="account.VATNumber" labelForId="taxNumber" errors="taxNumber">
                    <s:textfield name="taxNumber" id="taxNumber" styleClass="middleLengthText" maxlength="20"/>
                </ui:field>
            </c:when>
            <c:when test="${existingAccount.country.vatEnabled and not empty taxNumber}">
                <ui:simpleField labelKey="account.VATNumber" value="${taxNumber}"/>
            </c:when>
        </c:choose>
        
        <s:if test="existingAccount.role.name == 'Publisher'">
            <ui:field id="handlingFeeRow" labelForId="commission" label="${handlingFeeLabel}" errors="commissionPercent,commission" required="true">
                <s:textfield name="commissionPercent" id="commission" cssClass="smallLengthText1" maxLength="6"/>
            </ui:field>
       </s:if>
    </ui:fieldGroup>
</ui:section>

<div class="wrapper">
    <ui:button message="form.save" type="submit"/>

    <s:if test="!isInternal()">
        <ui:button message="form.cancel" onclick="location='myAccountView.action'" type="button" />
    </s:if>
    <s:else>
        <ui:button message="form.cancel" onclick="location='${viewActionName}?id=${accountId}'" type="button" />
    </s:else>
</div>
</s:form>
