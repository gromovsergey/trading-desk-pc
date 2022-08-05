<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ui" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<script type="text/javascript">
    $(function(){
        $.datepicker.setDefaults({
            'showOn': 'both',
            'mandatory': true,
            'numberOfMonths': 2,
            'buttonImageOnly': true,
            'buttonImage': '<s:url value="/images/calendar.gif"/>'
        });

        $("#statusId").on('change', function() {
            HideClosedDateDisplay($("#statusId").val());
        }).change();

        $("#invoiceEmailDateDisplay, #closedDateDisplay").datepicker({
            'onSelect': function(date, inst){
                var timeField = inst.input[0].id;
                timeField = timeField.replace("Date", "Time");
                UI.Data.get('dateInfo', {accountId: <s:property value="existingInvoice.account.id"/>}, function(data) {
                    if (!$('#'+timeField).val() || $.trim($('#'+timeField).val()) == '') {
                        $('#'+timeField).val($("timePart", data).text()).change();
                    }
                });
            }
        });
        
        $('#dueDateDisplay').datepicker({
            'onSelect': function(dateText){
                $('#dueDateHidden').val(dateText);
            }
        });
        $('#dueDateHidden').val($('#dueDateDisplay').val());

        var timeZoneId = '${timeZone.ID}';
        $('#invoiceEmailDateDisplay, #invoiceEmailTimeDisplay').on('change', function(){
            var str = $('#invoiceEmailDateDisplay').val() + ' ' + $('#invoiceEmailTimeDisplay').val();
            if ($.trim(str) == '') {
                str = '';
            } else {
                str += ' ' + timeZoneId;
            }
            $('#invoiceEmailDateHidden').val(str);
        });

        $('#closedDateDisplay, #closedTimeDisplay').on('change', function() {
            $('#closedDateHidden').val($('#closedDateDisplay').val() + ' ' + $('#closedTimeDisplay').val() + ' ' + timeZoneId);
        });

        $(document).on('submit', '#invoicesForm', function(e){
            if (!confirm('${ad:formatMessage("account.invoice.confirmSave")}')){
                e.preventDefault();
                UI.Util.enableButtons($(this));
            }
        });
    });

    function HideClosedDateDisplay(statusValue){
        var isHide = statusValue == 'OPEN';
        $('#closedDateDisplayId')[isHide ? 'hide' : 'show']();

        if(statusValue == 'CLOSED') {
            UI.Data.get('dateInfo', {accountId: <s:property value="existingInvoice.account.id"/>}, function(data) {
                $('#closedTimeDisplay').val($("timePart", data).text());
                $('#closedDateDisplay').val($("datePart", data).text());
                $('#closedDateDisplay').change();
            })
        }
    }
</script>

<s:form action="%{#attr.moduleName}/invoiceUpdate" id="invoicesForm">
<s:hidden name="id"/>
<s:hidden name="version"/>

<ui:pageHeadingByTitle/>

<ui:errorsBlock>
    <s:fielderror><s:param value="'version'"/></s:fielderror>
</ui:errorsBlock>

<c:set var="currencySymbol" value="${ad:currencySymbol(existingInvoice.account.currency.currencyCode)}"/>

<c:set var="invoiceAmountDueAndCurrencySymbol">
    <fmt:message key="account.invoice.totalAmountPayable"/> (${currencySymbol})
</c:set>

<c:set var="paidAmountAndCurrencySymbol">
    <fmt:message key="account.invoice.paid"/> (${currencySymbol})
</c:set>

<c:set var="deductFromPrepaidAmountAndCurrencySymbol">
    <fmt:message key="account.invoice.deductFromPrepaidAmount"/> (${currencySymbol})
</c:set>

<c:set var="creditNoteNegotiatedSettlementAndCurrencySymbol">
    <fmt:message key="account.invoice.creditNoteNegotiatedSettlement"/> (${currencySymbol})
</c:set>

<s:if test="existingInvoice.account.accountType.invoiceCommissionFlag">
    <fmt:message key="account.inputAmountsIncludeCommission.tip" var="amountsCommissionTip"/>
</s:if>
<s:else>
    <fmt:message key="account.inputAmountsExcludeCommission.tip" var="amountsCommissionTip"/>
</s:else>

<ui:section>
    <ui:fieldGroup>
        <ui:field labelKey="account.invoice.invoiceLegalNumber" required="true" errors="invoiceLegalNumber">
              <s:textfield name="invoiceLegalNumber" maxlength="20"/>
        </ui:field>
        
        <ui:field labelKey="account.invoice.status" errors="status">
            <s:select name="status" id="statusId"
                      list="availableStatuses"
                      listKey="name()" listValue="getText('enums.FinanceStatus.' + name())">
            </s:select>
        </ui:field>
        
        <ui:field labelKey="account.invoice.emailedDate" labelForId="invoiceEmailDateDisplay" errors="invoiceEmailDate">
              <s:hidden name="invoiceEmailDate" id="invoiceEmailDateHidden" />
              <s:textfield size="11" id="invoiceEmailDateDisplay" name="invoiceEmailDateDisplay"/>
              <s:textfield size="8" id="invoiceEmailTimeDisplay" name="invoiceEmailTimeDisplay"/>
        </ui:field>
        
        <ui:field labelKey="account.invoice.dueDate" labelForId="dueDateDisplay" required="true" errors="dueDate">
              <s:hidden name="dueDate" id="dueDateHidden" />
              <s:textfield size="11" id="dueDateDisplay" name="dueDateDisplay"/>
        </ui:field>
        
        <ui:field id="closedDateDisplayId" labelKey="account.invoice.closedDate" labelForId="closedDateDisplay" required="true" errors="closedDate">
              <s:hidden name="closedDate" id="closedDateHidden" />
              <s:textfield size="11" id="closedDateDisplay" name="closedDateDisplay"/>
              <s:textfield size="8" id="closedTimeDisplay" name="closedTimeDisplay"/>
        </ui:field>
        
        <ui:field label="${paidAmountAndCurrencySymbol}" required="true" errors="paidAmount">
              <s:textfield name="paidAmount" maxlength="16"/>
        </ui:field>
        <ui:field>
            <span style="font-style: italic;">${amountsCommissionTip}</span>
        </ui:field>

        <s:if test="existingInvoice.account.financialFieldsPresent">
            <s:set value="existingInvoice.account.financialSettings.data.prepaidAmount" var="accountPrepaidAmount"/>
        </s:if>
        <s:else>
            <s:set value="existingInvoice.account.agency.financialSettings.data.prepaidAmount" var="accountPrepaidAmount"/>
        </s:else>

        <s:if test="(#accountPrepaidAmount != null && #accountPrepaidAmount > 0) || (deductFromPrepaidAmount != null && deductFromPrepaidAmount > 0)">
            <ui:simpleField labelKey="account.invoice.accountPrepaidAmount"
                            value="${ad:formatCurrency(accountPrepaidAmount, existingInvoice.account.currency.currencyCode)}"/>
            <ui:field label="${deductFromPrepaidAmountAndCurrencySymbol}" errors="deductFromPrepaidAmount" tipKey="account.invoice.deductFromPrepaidAmount.tip">
                <s:textfield name="deductFromPrepaidAmount" maxlength="16"/>
            </ui:field>
            <ui:field>
                <span style="font-style: italic;">${amountsCommissionTip}</span>
            </ui:field>
        </s:if>

        <ui:field label="${creditNoteNegotiatedSettlementAndCurrencySymbol}" required="true" errors="creditSettlement"
                  tipKey="account.invoice.creditNoteNegotiatedSettlement.tip">
            <s:textfield name="creditSettlement" maxlength="16"/>
        </ui:field>
        <ui:field>
            <span style="font-style: italic;">${amountsCommissionTip}</span>
        </ui:field>

        <c:set var="textForTip">
            <fmt:message key="account.invoice.validateTip">
                <fmt:param><fmt:formatNumber value="1500" groupingUsed="true"/></fmt:param>
            </fmt:message>
        </c:set>

        <ui:field label="${invoiceAmountDueAndCurrencySymbol}" required="true" tipText="${textForTip}" errors="totalAmountDue">
            <s:textfield name="totalAmountDue" maxlength="13"/>
            <s:if test="totalAmountDue != null">
                <div id="totalAmountPayableAlertDiv" style="display:none;"><s:if test="getFieldErrors().containsKey('totalAmountDue')"><fmt:message key="errors.invoice.invalidAmountPayableAlert"/></s:if></div>
                <script type="text/javascript">
                    $().ready(function() {
                        var error = $.trim($("#totalAmountPayableAlertDiv").text());
                        error && alert(error);
                    });
                </script>
            </s:if>
        </ui:field>
        <ui:field>
            <span style="font-style: italic;"><fmt:message key="account.invoice.inputTotalAmountPayable.tip"/></span>
        </ui:field>
    </ui:fieldGroup>
</ui:section>

<div class="wrapper">
  <ui:button message="form.save"/>
  <ui:button message="form.cancel" onclick="location='invoiceView.action?id=${id}';" type="button" />
</div>
</s:form>
