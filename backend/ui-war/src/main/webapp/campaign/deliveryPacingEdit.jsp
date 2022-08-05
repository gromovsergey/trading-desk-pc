<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<s:set var="includedInCampaign">${param.includedInCampaign}</s:set>
<s:set var="type">${param.type}</s:set>
<s:set var="currencySymbol">${param.currencySymbol}</s:set>

<c:choose>
<c:when test="${includedInCampaign || type == 'D'}">
<ui:field id="deliveryPacingFieldId" labelKey="ccg.deliveryPacing" required="true" cssClass="valignFix">
    <table class="fieldAndAccessories">
        <tr>
            <td class="withField">
                <label class="withInput">
                    <s:radio name="deliveryPacing" list="'UNRESTRICTED'" template="justradio"/><fmt:message
                        key="ccg.deliveryPacing.unrestricted"/>
                </label>
            </td>
            <td class="withTip">
                <ui:hint>
                    <fmt:message key="ccg.deliveryPacing.hint.unrestricted"/>
                </ui:hint>
            </td>
        </tr>
    </table>
</ui:field>
<ui:field id="dailyBudgetFieldId" required="true" errors="dailyBudget" cssClass="valignFix">
    <table class="fieldAndAccessories">
        <tr>
            <td class="withField">
                <label class="withInput">
                    <s:radio name="deliveryPacing" list="'FIXED'" template="justradio"/><fmt:message
                        key="ccg.deliveryPacing.fixed"/> (${currencySymbol})
                </label>
            </td>
            <td class="withTip">
                <ui:hint>
                    <fmt:message key="ccg.deliveryPacing.hint.fixed"/>
                </ui:hint>
            </td>
            <td>
                <table class="fieldAndAccessories" id="dailyBudgetInput">
                    <tr>
                        <td class="withField">
                            <s:textfield name="dailyBudget" id="dailyBudget" cssClass="smallLengthText1"
                                         maxlength="15"/>
                        </td>
                    </tr>
                </table>
            </td>
        </tr>
    </table>
</ui:field>
<ui:field id="dailyBudgetWarningTR" cssClass="hide">
    <c:choose>
        <c:when test="${includedInCampaign}">
            <fmt:message key="campaign.daily.budget.low"/>
        </c:when>
        <c:otherwise>
            <fmt:message key="ccg.daily.budget.low"/>
        </c:otherwise>
    </c:choose>
</ui:field>
<ui:field id="dynamicBudgetTR" errors="dynamicDailyBudget" cssClass="valignFix">
    <table class="fieldAndAccessories">
        <tr>
            <td>
                <table class="fieldAndAccessories">
                    <tr>
                        <td class="withField"><label class="withInput"> <s:radio
                                name="deliveryPacing" list="'DYNAMIC'" id="dynamicDailyBudgetRadio"
                                template="justradio"/><fmt:message
                                key="ccg.deliveryPacing.dynamic"/> </label></td>
                        <td class="withTip"><ui:hint>
                            <fmt:message key="ccg.deliveryPacing.hint.dynamic"/>
                        </ui:hint></td>
                    </tr>
                </table>
            </td>
        </tr>
        <tr id="dynamicDailyBudgetInfo" class="hide">
            <td><span id="dynamicDailyBudget"></span></td>
        </tr>
    </table>
</ui:field>
</c:when>
<c:otherwise>
    <c:set var="dailyBudgetLabel">
        <fmt:message key="ccg.daily.budget.input"/> (${currencySymbol})
    </c:set>
    <ui:field label="${dailyBudgetLabel}" id="dailyBudgetInput" labelForId="dailyBudget" required="true"
              errors="dailyBudget">
        <table class="fieldAndAccessories">
            <tr>
                <td class="withField">
                    <s:textfield name="dailyBudget" id="dailyBudget" cssClass="smallLengthText1" maxlength="15"/>
                </td>
            </tr>
        </table>
    </ui:field>
    <ui:field id="dailyBudgetWarningTR" cssClass="hide">
        <c:choose>
            <c:when test="${includedInCampaign}">
                <fmt:message key="campaign.daily.budget.low"/>
            </c:when>
            <c:otherwise>
                <fmt:message key="ccg.daily.budget.low"/>
            </c:otherwise>
        </c:choose>
    </ui:field>
</c:otherwise>
</c:choose>
<ui:field id="deliveryPacingErrorsId" errors="deliveryPacing"/>

<script type="text/javascript">
    function getDeliveryPacingValue() {
    <c:choose>
    <c:when test="${type != 'T' || includedInCampaign}">
        return $('input[type=radio][name=deliveryPacing]:checked').val();
    </c:when>
    <c:otherwise>
        return 'FIXED';
    </c:otherwise>
    </c:choose>
    }

    function setDeliveryPacingValue(value) {
    <c:if test="${includedInCampaign}">
        $('input[type=radio][name=deliveryPacing]:checked').prop({checked : false});
        $('input[type=radio][name=deliveryPacing][value=' + value + ']').prop({checked : true});
    </c:if>
    }

    function endDateSpecified() {
    <c:choose>
    <c:when test="${includedInCampaign}">
        return $('input[type=radio][name=dateEndSet]:checked').val() === 'true';
    </c:when>
    <c:otherwise>
        return $('input[type=radio][name=groupLinkedToCampaignEndDateFlag]:checked').val() === 'false' || '<c:out value="${campaignDateEnd.datePart}"/>' != '';
    </c:otherwise>
    </c:choose>
    }

    function showHideDailyBudget() {
        var checkedFlag = getDeliveryPacingValue();
        $('#dynamicDailyBudgetInfo, #dailyBudgetInput, #dailyBudgetWarningTR').hide();

        if (checkedFlag == 'FIXED') {
            $('#dailyBudgetInput').show();
            $('#dailyBudget').prop({disabled : false});
            checkDailyBudget();
        } else if (checkedFlag == 'DYNAMIC') {
            $('#dynamicDailyBudgetInfo').show();
            $('#dailyBudget').prop({disabled : true}).val('');
            updateDynamicBudget();
        } else if (checkedFlag == 'UNRESTRICTED') {
            $('#dailyBudget').prop({disabled : true}).val('');
        }
    }

    function showHideDynamicBudget() {
        if (endDateSpecified() && !isBudgetUnlimited()) {
            showDynamicBudget();
        } else {
            hideDynamicBudget();

            if (getDeliveryPacingValue() == 'DYNAMIC') {
                setDeliveryPacingValue('UNRESTRICTED');
            }
        }
    }

    function showDynamicBudget() {
        $('#dynamicBudgetTR').show();
        $('#dynamicDailyBudgetRadio').prop({disabled : false});
    }

    function hideDynamicBudget() {
        $('#dynamicBudgetTR').hide();
        $('#dynamicDailyBudgetRadio').prop({disabled : true});
    }

    function isBudgetUnlimited() {
    <c:choose>
    <c:when test="${includedInCampaign}">
        return getTotalBudget() == null;
    </c:when>
    <c:otherwise>
        return false;
    </c:otherwise>
    </c:choose>
    }

    function deliveryPacingFactorsChangedHandler() {
        var checkedDeliveryPacing = getDeliveryPacingValue();
        if (checkedDeliveryPacing == 'FIXED') {
            checkDailyBudget();
        } else if (checkedDeliveryPacing == 'DYNAMIC') {
            updateDynamicBudget();
        }
    }

    function checkDailyBudget() {
        var data = createCommonData();
        data.totalBudget = getTotalBudget();
        data.dailyBudget = $('#dailyBudget').val();

        if (data.totalBudget == null || data.totalBudget === "" || data.dailyBudget === "") {
            return;
        }

        $.ajax({
            type: 'GET',
            url: '${_context}/campaign/group/checkDailyBudget.action',
            dataType: 'text',
            data: data,
            success : checkDailyBudgetSuccess,
            error: checkDailyBudgetError
        });
    }

    function createCommonData() {
        <c:choose>
        <c:when test="${includedInCampaign}">
        var ccgId;
        var campaignId = '${id}';
        </c:when>
        <c:otherwise>
        var ccgId = '${id}';
        var campaignId = '${existingGroup.campaign.id}';
        </c:otherwise>
        </c:choose>
        var accountId = '${existingAccount.id}';
        var startDate = $('#dateStartDisplay').val();
        var startTime = $('#timeStartDisplay').val();
        var endDate;
        var endTime;

        if (endDateSpecified()) {
            endDate = $('#dateEndDisplay').val();
            endTime = $('#timeEndDisplay').val();
        } else {
            endDate = '';
            endTime = '';
        }

        return {ccgId: ccgId, campaignId: campaignId, accountId: accountId,
            startDate: startDate, startTime: startTime, endDate: endDate, endTime: endTime};
    }

    function checkDailyBudgetSuccess(data, textStatus) {
        if (data == 'false') {
            $('#dailyBudgetWarningTR').show();
        } else {
            $('#dailyBudgetWarningTR').hide();
        }
    }

    function checkDailyBudgetError() {
        $('#dailyBudgetWarningTR').hide();
    }

    function getTotalBudget() {
    <c:choose>
        <c:when test="${(includedInCampaign && !existingAccount.accountType.ioManagement) || !includedInCampaign}">
            var budgetType = $('input[name=budgetType]:checked').val();
            var manualBudget = UI.Localization.parseFloat($('#budgetInput').val());
            if (budgetType == 'UNLIMITED' || !$.isNumeric(manualBudget) || manualBudget <=0) {
                return null;
            } else {
                return $('#budgetInput').val();
            }
        </c:when>
        <c:otherwise>
            return ${totalBudget != null ? totalBudget : 0};
        </c:otherwise>
    </c:choose>
    }

    function updateDynamicBudget() {
        var data = createCommonData();
        data.totalBudget = getTotalBudget();

        if (data.totalBudget == null || data.totalBudget === "") {
            return;
        }
        
        $.ajax({
            type: 'GET',
            url: '${_context}/campaign/group/updateDynamicBudget.action',
            dataType: 'text',
            data: data,
            success : updateDynamicBudgetSuccess,
            error: updateDynamicBudgetError
        });
    }

    function updateDynamicBudgetSuccess(data, textStatus) {
        $('#dynamicDailyBudget').html(data);
    }

    function updateDynamicBudgetError() {
        $('#dynamicDailyBudget').html('');
    }

    $('#dailyBudgetFieldId, #deliveryPacingFieldId').on('change', function() {
        $('#deliveryPacingErrorsId').hide();
    });

</script>
