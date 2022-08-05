<%@page contentType="text/html" %>
<%@page pageEncoding="UTF-8" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="ad" uri="/ad/serverUI" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:set var="editableFieldsPresent" value="false" />
<s:set var="saveActionName" value="'advertiserUpdateFinance'" />
<s:set var="viewActionName" value="'advertiserView.action'" />
<s:if test="!existingAccount.standalone">
    <s:set var="saveActionName" value="'agencyAdvertiserUpdateFinance'" />
    <s:set var="viewActionName" value="'agencyAdvertiserView.action'" />
</s:if>

<c:set var="canUpdateFinance" value="${ad:isPermitted('AdvertisingAccount.updateRestrictedFinanceFields', existingAccount)}"/>

<s:if test="#attr.contextName == 'global.menu.advertisers'">
        <s:set var="saveActionName" value="'advertiserUpdateFinance'" />
        <s:set var="viewActionName" value="'advertiserView.action'" />
    <s:if test="!existingAccount.standalone">
        <s:set var="saveActionName" value="'agencyAdvertiserUpdateFinance'" />
        <s:set var="viewActionName" value="'agencyAdvertiserView.action'" />
    </s:if>
</s:if>

<script type="text/javascript">
    $().ready(function() {
        var buttons = $('input[type=radio][name=paymentType]');
        
        buttons.click(function() {
            if (this.value == 'PREPAY') {
                $("#creditLimitTR,#creditLimitTipTR").hide();
                $('#creditLimit').prop({disabled : true});
            } else {
                $("#creditLimitTR,#creditLimitTipTR").show();
                $('#creditLimit').prop({disabled : false});
            }
        });

        buttons.filter(':checked').click();
        
        $('#AccountFinanceFormId').submit(function(){
            var currForm = this;
            <s:if test="#attr.canUpdateFinance">
                var requireValidatePrepaidAmount = true;
            </s:if>
            <s:else>
                var requireValidatePrepaidAmount = false;
            </s:else>
                
            if (requireValidatePrepaidAmount) {
                validateOnCreditLimit(currForm);
                return false;
            }
            return true;
        });

        function validateOnCreditLimit(currForm) {
            UI.Util.disableButtons($(currForm));
            UI.Data.get(
                    'creditLimitCheck',
                    {accountId: '<c:out value="${accountId}"/>', creditLimit: $('#creditLimit').val()},
                    function(data) {
                        var status = $('status', data).text();

                        if (status == 'OK') {
                            currForm.submit();
                        } else {
                            // show popup
                            var maxCreditLimit = $('maxCreditLimit', data).text();
                            var confirmText = $('message', data).text();
                            confirmText += '\n\n' + '${ad:formatMessage("account.creditLimit.confirmation1")}' + ' ' + maxCreditLimit;
                            confirmText += ' ' + '${ad:formatMessage("account.creditLimit.confirmation2")}';
                            if(confirm(confirmText)) {
                                $('#creditLimit').val(maxCreditLimit);
                                currForm.submit();
                            } else {
                                UI.Util.enableButtons($(currForm));
                            }
                        }
                    }, 
                    null
            );
        }
    });

    function correctInputValue(val){
        var val = val || '';
        var thousandSeparator = '\\' + '${thousandSeparator}';
        var decimalSeparator = '\\' + '${decimalSeparator}';

        return +val.replace(new RegExp(thousandSeparator, 'g'), '')
            .replace(new RegExp(decimalSeparator, 'g'), '.');
    }
</script>

<s:form action="%{#attr.moduleName}/%{#saveActionName}" id="AccountFinanceFormId">
<s:hidden name="accountId"/>
<s:hidden name="version"/>
<s:hidden name="data.version"/>
<ui:errorsBlock>
    <s:fielderror><s:param value="'version'"/></s:fielderror>
</ui:errorsBlock>

<ui:pageHeadingByTitle/>
<c:set var="creditLimitLabel">
    <fmt:message key="account.creditLimit"/> (${ad:currencySymbol(existingAccount.currency.currencyCode)})
</c:set>
<c:set var="prepaidAmountLabel">
    <fmt:message key="account.prepaidAmount"/> (${ad:currencySymbol(existingAccount.currency.currencyCode)})
</c:set>
<c:set var="minInvoiceLabel">
    <fmt:message key="account.minimalInvoice"/> (${ad:currencySymbol(existingAccount.currency.currencyCode)})
</c:set>
<c:set var="commissionLabel">
    <fmt:message key="account.agencyCommission"/> (%)
</c:set>
<c:set var="mediaHandlingFeeLabel">
    <fmt:message key="account.mediaHandlingFee"/> (%)
</c:set>
<c:set var="amountsCommissionTip">
    <s:if test="agencyFlag || !existingAccount.standalone">
        <s:if test="existingAccount.accountType.invoiceCommissionFlag">
            <fmt:message key="account.inputAmountsIncludeCommission.tip"/>
        </s:if>
        <s:else>
            <fmt:message key="account.inputAmountsExcludeCommission.tip"/>
        </s:else>
    </s:if>
    <s:else>
        <fmt:message key="account.bill.inputAmounts.tip"/>
    </s:else>
</c:set>

<ui:section>
    <ui:fieldGroup id="finAdv">
        <s:if test="existingAccount.financialFieldsPresent && existingAccount.country.vatNumberInputEnabled">
            <s:set var="editableFieldsPresent" value="true" />
            <ui:field id="taxName" labelKey="account.VATNumber" labelForId="taxNumber" errors="taxNumber">
               <s:textfield name="taxNumber" id="taxNumber" cssClass="smallLengthText1" maxLength="20"/>
            </ui:field>
        </s:if>

        <s:if test="existingAccount.financialFieldsPresent">
            <s:if test="#attr.canUpdateFinance">
                <s:set var="editableFieldsPresent" value="true" />
                <ui:field labelKey="account.paymentType" errors="paymentType">
                    <s:radio name="paymentType"
                             list="@com.foros.model.account.PaymentOrderType@values()"
                             listKey="name()" listValue="getText('account.paymentType.' + name())"/>
                </ui:field>

                <ui:field id="creditLimitTR" labelForId="creditLimit" label="${creditLimitLabel}" errors="creditLimit">
                    <s:textfield name="creditLimit" cssClass="smallLengthText1" id="creditLimit" maxLength="17"/>
                </ui:field>
                <ui:field id="creditLimitTipTR">
                    <span style="font-style: italic;">${amountsCommissionTip}</span>
                </ui:field>
                <ui:field label="${prepaidAmountLabel}" labelForId="prepaidAmount" errors="data.prepaidAmount">
                    <s:textfield name="data.prepaidAmount" cssClass="smallLengthText1" id="prepaidAmount" maxLength="17"/>
                </ui:field>
                <ui:field>
                    <span style="font-style: italic;">${amountsCommissionTip}</span>
                </ui:field>

                <s:if test="existingAccount.accountType.perCampaignInvoicingFlag">
                    <ui:field labelKey="account.displayCampaignInvoiceGeneration" errors="invoiceGeneration">
                        <s:radio name="invoiceGenerationType"
                                 list="@com.foros.model.account.InvoiceGenerationType@values()"
                                 listKey="name()" listValue="getText('account.InvoiceGenerationType.' + name())"/>
                    </ui:field>
                </s:if>
            </s:if>

            <s:if test="#attr.canUpdateFinance">
                <s:set var="editableFieldsPresent" value="true" />
                <ui:field labelForId="billingFrequency" labelKey="account.billingFrequency" errors="billingFrequency">
                    <s:set var="editableFieldsPresent" value="true" />
                    <s:select name="billingFrequency" id="billingFrequency" cssClass="smallLengthText1"
                              list="@com.foros.model.security.BillingFrequency@values()"
                              listKey="name()" listValue="getText('enums.BillingFrequency.' + name())">
                    </s:select>
                </ui:field>
            </s:if>
            <s:else>
                 <s:if test="existingAccount.financialSettings.billingFrequency != null">
                    <fmt:message key="enums.BillingFrequency.${existingAccount.financialSettings.billingFrequency}" var="billingFrequencyMessage"/>
                    <ui:simpleField labelKey="account.billingFrequency" value="${billingFrequencyMessage}"/>
                </s:if>
            </s:else>

            <s:if test="#attr.canUpdateFinance">
                <s:set var="editableFieldsPresent" value="true" />
                <ui:field labelKey="account.billingFrequencyOffset" labelForId="billingFrequencyOffset" errors="billingFrequencyOffset" required="true">
                    <s:set var="editableFieldsPresent" value="true" />
                    <s:textfield name="billingFrequencyOffset" id="billingFrequencyOffset" cssClass="smallLengthText1" maxLength="2"/>
                </ui:field>

                <ui:field labelKey="account.paymentTermsInDays" labelForId="paymentTerms" errors="paymentTerms">
                    <s:set var="editableFieldsPresent" value="true"/>
                    <s:textfield name="paymentTerms" id="paymentTerms" cssClass="smallLengthText1" maxLength="2"/>
                </ui:field>

                <ui:field label="${minInvoiceLabel}" labelForId="minInvoice" errors="minInvoice" required="true">
                    <s:set var="editableFieldsPresent" value="true" />
                    <s:textfield name="minInvoice" id="minInvoice" cssClass="smallLengthText1" maxLength="15"/>
                </ui:field>
                <ui:field>
                    <span style="font-style: italic;">${amountsCommissionTip}</span>
                </ui:field>
            </s:if>
            <s:else>
                <s:if test="!existingAccount.financialSettings.billingFrequencyOffset.empty">
                    <ui:simpleField labelKey="account.billingFrequencyOffset" value="${existingAccount.financialSettings.billingFrequencyOffset}"/>
                </s:if>

                <s:if test="!existingAccount.financialSettings.paymentTerms.empty">
                    <ui:simpleField labelKey="account.paymentTermsInDays" value="${existingAccount.financialSettings.paymentTerms}"/>
                </s:if>

                <s:if test="existingAccount.financialSettings.minInvoice != null">
                    <ui:simpleField labelKey="account.minimalInvoice"
                                    value="${ad:formatCurrency(existingAccount.financialSettings.minInvoice, existingAccount.currency.currencyCode)}"/>
                </s:if>
            </s:else>

            <s:if test="agencyFlag || !existingAccount.standalone">
                <s:set var="editableFieldsPresent" value="true" />
                <ui:field label="${commissionLabel}" labelForId="commission" errors="commissionPercent,commission" required="true">
                    <s:textfield name="commissionPercent" cssClass="smallLengthText1" id="commission" maxLength="6"/>
                </ui:field>
            </s:if>
        </s:if>

        <s:if test="#attr.canUpdateFinance">
            <s:set var="editableFieldsPresent" value="true" />
            <ui:field label="${mediaHandlingFeeLabel}" labelForId="mediaHandlingFee" errors="mediaHandlingFee,mediaHandlingFeePercent" required="true">
                <s:textfield name="mediaHandlingFeePercent" cssClass="smallLengthText1" id="mediaHandlingFee" maxLength="6"/>
            </ui:field>
        </s:if>
    </ui:fieldGroup>
</ui:section>
<s:if test="editableFieldsPresent">
    <div class="wrapper">

        <ui:button message="form.save" type="submit"/>

        <s:if test="!isInternal()">
            <s:if test="!existingAccount.standalone">
                <ui:button message="form.cancel" type="button" onclick="location='${viewActionName}?id=${accountId}';" />
            </s:if>
            <s:else>
                <ui:button message="form.cancel" type="button" onclick="location='myAccountView.action';" />
            </s:else>
        </s:if>
        <s:else>
            <ui:button message="form.cancel" type="button" onclick="location='${viewActionName}?id=${accountId}';" />
        </s:else>
    </div>
</s:if>
</s:form>
