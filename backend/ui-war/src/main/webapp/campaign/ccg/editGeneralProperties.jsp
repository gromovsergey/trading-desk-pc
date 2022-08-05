<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<script type="text/javascript">

    var timeZoneId = '${timeZone.ID}';
    
    // see also
    //   DeliveryPacingEdit.jsp
    //   editBidStrategy.jsp
    $(document).ready(function () {
        manageEndDate(true);
        showHideDailyBudget();
        checkRotationCriteria();
        
        $('input[type=radio][name=sequentialAdservingFlag]').click(function() {
            checkRotationCriteria();
        });

        $('input[type=radio][name=groupLinkedToCampaignEndDateFlag]').click(function() {
            manageEndDate(false);
            showHideDailyBudget();
        });

        $('input[type=radio][name=deliveryPacing]').click(function() {
            showHideDailyBudget();
        });

        $('#budgetInput,#dateStartDisplay,#timeStartDisplay,#dateEndDisplay,#timeEndDisplay').change(deliveryPacingFactorsChangedHandler);

        $('#dailyBudget').textChanged(checkDailyBudget);

        $('#dateStartDisplay,#timeStartDisplay').change(function() {
            $('#dateStartHidden').val($('#dateStartDisplay').val() + ' ' + $('#timeStartDisplay').val() + ' ' + timeZoneId);
        });
        $('#dateEndDisplay,#timeEndDisplay').change(function() {
            $('#dateEndHidden').val($('#dateEndDisplay').val() + ' ' + $('#timeEndDisplay').val() + ' ' + timeZoneId);
        });
    });

    function manageEndDate(isOnLoad) {
        var endDate;
        var endTime;
        var isLinkedToCampaignDateEnd = $('input[type=radio][name=groupLinkedToCampaignEndDateFlag]:checked').val();

        if(!isOnLoad){
            $('#dateToTr .withError').hide();
        }

        if (isLinkedToCampaignDateEnd == 'true') {
            endDate = '<c:out value="${campaignDateEnd.datePart}"/>';
            endTime = '<c:out value="${campaignDateEnd.timePart}"/>';

            $('#dateEndDisplay').data({lastVal : $('#dateEndDisplay').val()});
            $('#dateEndDisplay').prop({disabled : true}).val(endDate);
            $('#dateEndDisplay').datepicker('disable');

            $('#timeEndDisplay').data({lastVal : $('#timeEndDisplay').val()});
            $('#timeEndDisplay').prop({disabled : true}).val(endTime);
            $('#dateEndHidden').val('');

        } else if (isLinkedToCampaignDateEnd == 'false') {
            $('#dateEndDisplay').datepicker('enable');

            if ($('#dateEndDisplay').data('lastVal')) {
                endDate = $('#dateEndDisplay').data('lastVal');
                endTime = $('#timeEndDisplay').data('lastVal');
            } else {
                endDate = '<c:out value="${groupDateEnd.datePart}"/>';
                endTime = '<c:out value="${ad:escapeJavaScript(groupDateEnd.timePart)}"/>';
                if (endDate == null || $.trim(endDate).length == 0) {
                    endDate = '<c:out value="${defaultDateEnd.datePart}"/>';
                    endTime = '<c:out value="${defaultDateEnd.timePart}"/>';
                }
            }

            $('#dateEndDisplay').prop({disabled : false}).val(endDate);
            $('#timeEndDisplay').prop({disabled : false}).val(endTime);
            $('#dateEndHidden').prop({disabled : false}).val(endDate + ' ' + endTime + ' ' + timeZoneId);
        } else {  // undefined (for new CCG)
            $('#dateEndHidden').val('');
            $('#dateEndDisplay').prop({disabled : true}).val('');
            $('#dateEndDisplay').datepicker('disable');
            $('#timeEndDisplay').prop({disabled : true}).val('');
        }
        showHideDynamicBudget();
    }
</script>

<s:hidden name="id"/>
<s:hidden name="version"/>
<s:hidden name="campaignId"/>
<s:hidden name="tgtType"/>
<s:hidden name="ccgType"/>

<ui:pageHeadingByTitle/>

<c:if test="${wizardFunctionalityEnabled}">
    <h2><fmt:message key="campaign.creative.groupsWizard.step2"/></h2>
    <p><fmt:message key="campaign.creative.groupsWizard.hint2"/></p>
    <s:fielderror fieldName="name"/>
    <s:fielderror fieldName="channelTarget"/>
    <p>&nbsp;</p>
</c:if>

<ui:errorsBlock>
    <s:fielderror><s:param value="'version'"/></s:fielderror>
</ui:errorsBlock>

<c:if test="${not wizardFunctionalityEnabled}">
    <ui:section>
        <ui:fieldGroup>
            <ui:field labelKey="ccg.name" labelForId="name" required="true" errors="name">
                <s:textfield name="name" id="name" cssClass="middleLengthText" maxlength="100"/>
            </ui:field>
      </ui:fieldGroup>
    </ui:section>
</c:if>

<c:set var="currencySymbol" value="${ad:currencySymbol(existingGroup.account.currency.currencyCode)}"/>

<ui:section id="goalsArea" titleKey="ccg.rates.goals">
    <ui:fieldGroup>
        <ui:field labelKey="ccg.dateStart" labelForId="dateStartDisplay" required="true" errors="dateStart">
            <s:hidden name="dateStart" id="dateStartHidden"/>
            <s:textfield size="11" id="dateStartDisplay" name="groupDateStart.datePart" readonly="true" />
            <s:textfield size="8" id="timeStartDisplay" name="groupDateStart.timePart" maxlength="8"/>
        </ui:field>

        <ui:field labelKey="ccg.dateEnd" required="true" cssClass="valignFix">
            <label class="withInput">
                <s:radio name="groupLinkedToCampaignEndDateFlag" list="true" template="justradio"/><fmt:message key="ccg.delivery.linked.to.campaign.end.date"/>
            </label>
            <label class="withInput">
                <s:radio name="groupLinkedToCampaignEndDateFlag" list="false" template="justradio"/><fmt:message key="ccg.delivery.deactivate.on"/>
            </label>
        </ui:field>
    
    <ui:field id="dateToTr" errors="dateEnd">
        <s:hidden name="dateEnd" id="dateEndHidden"/>
        <s:textfield size="11" id="dateEndDisplay" name="groupDateEnd.datePart" readonly="true"/>
        <s:textfield size="8" id="timeEndDisplay" name="groupDateEnd.timePart" maxlength="8"/>
    </ui:field>

    <fmt:message key="ccg.ccgRate" var="ccgRateLabel"/>
    <ui:field id="ccgRate" label="${ccgRateLabel} (${currencySymbol})" required="true" errors="ccgRate.rateTypeOrdinal,ccgRate.cpa,ccgRate.cpc,ccgRate.cpm,ccgRate">
        <table class="fieldAndAccessories">
            <tr>
                <td class="withField">
                    <s:textfield id="CPARateInput" name="ccgRate.cpa" cssClass="smallLengthText1" maxlength="20" style="display:none;"/>
                    <s:textfield id="CPCRateInput" name="ccgRate.cpc" cssClass="smallLengthText1" maxlength="20" style="display:none;"/>
                    <s:textfield id="CPMRateInput" name="ccgRate.cpm" cssClass="smallLengthText1" maxlength="20" style="display:none;"/>
                </td>
                <td class="withField">
                    <s:select id="rateTypeOrdinal" name="ccgRate.rateType" list="rateTypes" listKey="ordinal()" listValue="name()" value="ccgRate.rateType.ordinal()"/>
                </td>
            </tr>
        </table>
    </ui:field>

    <s:include value="ccg/editBidStrategy.jsp"/>

    <fmt:message key="ccg.budget" var="ccgBudget"/>
    <ui:field label="${ccgBudget} (${currencySymbol})" labelForId="budgetInput" required="${ccgType.letter == 'D'}" errors="budget">
        <s:textfield id="budgetInput" name="budget" cssClass="smallLengthText1" maxlength="15"/>
    </ui:field>

    <ui:field labelKey="ccg.goal">
        <table class="fieldAndAccessories">
            <tr>
                <td class="withField">
                    <span id="goalValue"></span>
                </td>
                <td class="withField">
                    <ui:text id="goalMeasure" textKey="ccg.impressions"/>
                </td>
            </tr>
        </table>
    </ui:field>

    <s:include value="deliveryPacingEdit.jsp">
        <s:param name="includedInCampaign" value="%{false}"/>
        <s:param name="type" value="ccgType.letter"/>
        <s:param name="currencySymbol" value="%{#attr.currencySymbol}"/>
    </s:include>

  </ui:fieldGroup>
</ui:section>
