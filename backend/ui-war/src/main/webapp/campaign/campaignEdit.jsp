<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ui" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<script type="text/javascript">
    var timeZoneId = '${timeZone.ID}';

    // see also editDeliveryPacing.jsp
    $(function() {

        initDateFields();
        showHideDailyBudget();
        UI.Util.assignSubmitForFields();

        $('input[type=radio][name=dateEndSet]').change(function() {
            showHideEndDate();
        });

        <s:if test="dateEndSet">
            var selectedDateEnd = '<c:out value="${selectedDateEnd.datePart}"/>';
            var selectedTimeEnd = '<c:out value="${ad:escapeJavaScript(selectedDateEnd.timePart)}"/>';
            setEndDate(selectedDateEnd, selectedTimeEnd);
        </s:if>
        <s:else>
            var defaultDateEnd = '<c:out value="${defaultDateEnd.datePart}"/>';
            var defaultTimeEnd = '<c:out value="${defaultDateEnd.timePart}"/>';
            setEndDate(defaultDateEnd, defaultTimeEnd);
        </s:else>

        showHideEndDate();

        $('input[type=radio][name=deliveryPacing],input[type=radio][name=dateEndSet]').click(showHideDailyBudget);

        $('#budgetInput,#dateStartDisplay,#timeStartDisplay,#dateEndDisplay,#timeEndDisplay,input[name=budgetType]').change(deliveryPacingFactorsChangedHandler);
        $('input[name=budgetType], #budgetInput').on('change keyup',budgetChangedHandler);
        $('#dailyBudget').textChanged(checkDailyBudget);

        $('#dateStartDisplay,#timeStartDisplay').change(function() {
            $('#dateStartHidden').val($('#dateStartDisplay').val() + ' ' + $('#timeStartDisplay').val() + ' ' + timeZoneId);
        });
        $('#dateEndDisplay,#timeEndDisplay').change(function() {
            $('#dateEndHidden').val($('#dateEndDisplay').val() + ' ' + $('#timeEndDisplay').val() + ' ' + timeZoneId);
        });
    });

    function submitForm() {
        if ($('input[type=radio][name=deliverySchedule]:checked').val() == "true" && $('#id').val() != "") {
            checkCCGAffected($('#id').val(), $('input[name=scheduleSet]').val());
        } else {
            $('#campaignForm').submit();
            return false;
        }
    }

    function initDateFields() {
        $("#dateStartDisplay, #dateEndDisplay").datepicker()
    }

    function showHideEndDate(){
        if (!endDateSpecified()) {
            $('#dateEndDisplay, #timeEndDisplay, #dateEndHidden').prop({disabled : true});
            $('#dateEndField').hide();
            $('#dateEndField .withError').hide();
        } else {
            $('#dateEndField').show();
            $('#dateEndDisplay, #timeEndDisplay, #dateEndHidden').prop({disabled : false});
        }
        showHideDynamicBudget();
    }

    function setEndDate(endDate, endTime){
        $('#dateEndDisplay').val(endDate);
        $('#timeEndDisplay').val(endTime);
        $('#dateEndHidden').val(endDate + ' ' + endTime + ' ' + timeZoneId);
    }

    function budgetChangedHandler() {
        if (isBudgetUnlimited()) {
            if (getDeliveryPacingValue() === 'DYNAMIC') {
                setDeliveryPacingValue('UNRESTRICTED');
            }
        }
        showHideDynamicBudget();
    }

    function popupCCGList(ccgs, formData) {
    $.ajax({
        url: '${_context}/campaign/popupAffectedCCGs.action',
        type: 'POST',
        data: formData,
        dataType: 'html',
        success: function(data) {
                var dialog = UI.Dialog.createDialog(data)
                     .attr('id', 'popup')
                     .focus()
                     .keyup(function(e) {
                             if (e.keyCode == 27) {
                                 UI.Dialog.removeAllDialogs();
                             }
                     });
                $('#campaignCreativeGroups', dialog).val(ccgs);
                $('input:text:visible:first', dialog).focus();

            }
    });
    }

    function checkCCGAffected(campaignId, schedule) {
         UI.Data.get(
            'affectedCCGForDelivery',
            {
                id : campaignId,
                scheduleSet : schedule
            },
            function (data) {
                var val = $('result', data).text();
                if ($.trim(val).length > 0) {
                    popupCCGList(val, $('#campaignForm').serialize());
                } else {
                    $('#campaignForm').submit();
                }
            }
        );
    }


</script>

<s:set value="#attr.isCreatePage?'create':'update'" var="saveActionName"/>

<s:form action="%{#attr.moduleName}/%{#saveActionName}" name="campaignForm" id="campaignForm">
    <ui:pageHeadingByTitle/>

    <s:hidden name="id" id="id"/>
    <s:hidden name="version"/>
    <s:hidden name="account.id"/>
    <s:hidden name="type"/>
    <s:hidden name="editBudget"/>

    <ui:errorsBlock>
        <s:actionerror/>
        <s:fielderror><s:param value="'version'"/></s:fielderror>
    </ui:errorsBlock>

    <ui:section>
        <ui:fieldGroup>
            <ui:field labelKey="campaign.name" labelForId="name" required="true" errors="name">
                <s:textfield name="name" cssClass="middleLengthText" maxlength="100"/>
            </ui:field>

            <c:set var="currencySymbol" value="${ad:currencySymbol(existingAccount.currency.currencyCode)}"/>
            <s:if test="editBudget">
                <fmt:message key="campaign.budget" var="campaignBudget"/>
                <s:if test="type == 'T'">
                  <ui:field id="rowWithCurrencySymbol" label="${campaignBudget} (${currencySymbol})" required="true" errors="budget,allocatedAmount">
                    <s:textfield name="budget" id="budgetInput" cssClass="middleLengthText" maxlength="15"/>
                  </ui:field>
                </s:if>
                <s:else>
                    <ui:field id="rowWithCurrencySymbol" label="${campaignBudget} (${currencySymbol})" required="true" errors="budget,allocatedAmount">
                        <label>
                            <input type="radio" name="budgetType" value="UNLIMITED"${budgetType == 'UNLIMITED' ? 'checked' : ''}/>
                            <fmt:message key="campaign.budget.unlimited"/>
                        </label>
                        <table class="grouping">
                            <tr>
                                <td>
                                    <label>
                                        <input type="radio" name="budgetType" id="budgetTypeRadio" value="LIMITED" ${budgetType == 'LIMITED' ? 'checked' : ''}/>
                                    </label>
                                </td>
                                <td>
                                    <s:textfield name="budget" id="budgetInput" cssClass="middleLengthText" maxlength="15" onclick="$('#budgetTypeRadio').attr({checked:'checked'})" />
                                </td>
                            </tr>
                        </table>
                    </ui:field>
                </s:else>
            </s:if>

            <s:if test="isInternal()">
                <c:if test="${ad:isPermitted0('AdvertiserEntity.advanced')}">
                    <fmt:message key="campaign.maxPubShare" var="campaignMaxPubShareLabel"/>
                    <ui:field label="${campaignMaxPubShareLabel}" required="true" errors="maxPubShare">
                        <s:select name="maxPubShare" value="maxPubShare.stripTrailingZeros()"
                                  list="#{1B:'100%',0.9B:'90%',0.8B:'80%',0.7B:'70%',0.6B:'60%',0.5B:'50%',0.4B:'40%',0.3B:'30%',0.2B:'20%',0.1B:'10%'}"/>
                    </ui:field>
                </c:if>
            </s:if>

            <ui:field labelKey="campaign.dateStart" labelForId="dateStartDisplay" required="true" errors="dateStart">
                <s:hidden name="dateStart" id="dateStartHidden" />
                <s:textfield size="11" id="dateStartDisplay" name="selectedDateStart.datePart" readonly="true" />
                <s:textfield size="8" id="timeStartDisplay" name="selectedDateStart.timePart" maxlength="8"/>
            </ui:field>

            <ui:field labelKey="campaign.dateEnd" required="true" errors="dateEndSet">
                <label class="withInput">
                    <s:radio name="dateEndSet" list="false" template="justradio" disabled="%{!endDateCleanAllowed}"/><fmt:message key="campaign.notSet"/>
                </label>
                <label class="withInput">
                    <s:radio name="dateEndSet" list="true" template="justradio"/><fmt:message key="campaign.deactivate.on"/>
                </label>
            </ui:field>

            <ui:field id="dateEndField" errors="dateEnd">
                <s:hidden name="dateEnd" id="dateEndHidden"/>
                <s:textfield size="11" id="dateEndDisplay" name="selectedDateEnd.datePart" readonly="true"/>
                <s:textfield size="8" id="timeEndDisplay" name="selectedDateEnd.timePart" maxlength="8"/>
            </ui:field>

            <s:include value="deliveryPacingEdit.jsp">
                <s:param name="includedInCampaign" value="%{true}" />
                <s:param name="type" value="%{type}" />
                <s:param name="currencySymbol" value="%{#attr.currencySymbol}"/>
            </s:include>

            <s:if test="isInternal()">
                <ui:field labelKey="ccg.bidStrategy" id="bidStrategy" errors="bidStrategy">
                    <s:select
                            name="bidStrategy"
                            list="@com.foros.model.campaign.CampaignBidStrategy@values()"
                            listKey="name()" listValue="getText('campaign.bidStrategy.' + name())"
                            />
                </ui:field>
            </s:if>

            <fmt:message var="excludedChannelsTip1" key="campaign.excludedChannels.tipLine1"/>
            <fmt:message var="excludedChannelsTip2" key="campaign.excludedChannels.tipLine2"/>
            <fmt:message var="excludedChannelsTip3" key="campaign.excludedChannels.tipLine3"/>
            <fmt:message var="excludedChannelsTip4" key="campaign.excludedChannels.tipLine4"/>
            <c:set var="excludedChannelsTip">
                <div><c:out value="${excludedChannelsTip1}"/></div>
                <div>&nbsp;</div>
                <div><c:out value="${excludedChannelsTip2}"/></div>
                <div><c:out value="${excludedChannelsTip3}"/></div>
                <div><c:out value="${excludedChannelsTip4}"/></div>
            </c:set>
            <ui:field labelKey="campaign.excludedChannels" tipText="${excludedChannelsTip}" escapeXml="${false}" errors="excludedChannels">
                <s:textarea rows="3" cols="70" wrap="off" name="excludedChannels"/>
            </ui:field>

        </ui:fieldGroup>
    </ui:section>

    <s:include value="editDeliveryPeriod.jsp"/>

    <s:if test="walledGardenEnabled">
        <ui:section titleKey="campaign.walledGarden" mandatory="true">
            <ui:fieldGroup>
                <ui:field>
                    <label class="withInput">
                        <s:checkbox name="marketplace.inWG"/><fmt:message key="WalledGarden.agency.marketplace.WG"/>
                    </label>
                </ui:field>
                
                <ui:field errors="marketplaceType">
                    <table class="fieldAndAccessories">
                        <tr>
                            <td class="withField">
                                <label class="withInput">
                                    <s:checkbox name="marketplace.inFOROS"/><fmt:message key="WalledGarden.agency.marketplace.FOROS"/>
                                </label>
                            </td>
                            <td class="withTip">
                                <ui:hint>
                                    <fmt:message key="WalledGarden.agency.marketplace.FOROS.tip"/>
                                </ui:hint>
                            </td>
                        </tr>
                    </table>
                </ui:field>
                
            </ui:fieldGroup>
        </ui:section>
    </s:if>

    <ui:frequencyCapEdit fcPropertyName="frequencyCap"/>

    <div class="wrapper">
        <ui:button message="form.save" type="button" onclick="submitForm();"/>
        <s:if test="id == null">
            <ui:button message="form.cancel" onclick="location='main.action${ad:accountParam('?advertiserId', account.id)}';" type="button"/>
        </s:if>
        <s:else>
            <ui:button message="form.cancel" onclick="location='view.action?id=${id}';" type="button"/>
        </s:else>
    </div>
</s:form>
