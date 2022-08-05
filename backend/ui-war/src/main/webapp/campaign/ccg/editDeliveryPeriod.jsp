<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ui" %>

<script type="text/javascript">
    $(function() {
        $('#CCGForm').on('submit', function(e){
            $('#groupCountries option').prop('selected', true);
            <s:if test="isInternal() || isAdvertiser()">$('#groupSites option').prop('selected', true);</s:if>
        });
        
        var schdlChange = function(e){
            if ($('[name=deliveryScheduleFlag]:checked', '#schdl_flag').val() == 'true'){
                $('#ccgBlock').show();
                $('#scheduleSet').data('weekRange') && $('#scheduleSet').data('weekRange').refresh();
                $('#campaignBlock').hide();
                $('#deliveryScheduleLinkedToCampaignInfo').hide();
                $('#errorDiv').show();
            } else {
                $('#ccgBlock').hide();
                $('#errorDiv').hide();
                <s:if test="campaignScheduleSet.schedules.empty">
                    $('#campaignBlock').hide();
                    $('#deliveryScheduleLinkedToCampaignInfo').show();
                </s:if>
                <s:else>
                    $('#deliveryScheduleLinkedToCampaignInfo').hide();
                    $('#campaignBlock').show();
                    $('#campaignScheduleSet').data('weekRange') && $('#campaignScheduleSet').data('weekRange').refresh();
                    <s:set var="campaignNotRunningKey" value="'deliverySchedule.campaign.not.running'"/>
                </s:else>
            }
        };
        
        $('#schdl_flag').on('change', 'input', schdlChange);
        schdlChange();
    });
</script>

<c:set var="legend">
    <fmt:message key="deliverySchedule.label"/> (<fmt:message key="deliverySchedule.timeZone"/>: <ad:resolveGlobal resource="timezone" id="${existingGroup.account.timezone.key}" prepare="true"/>)
</c:set>

<ui:section title="${legend}">
    <ui:fieldGroup>
        <ui:field id="schdl_flag">
            <s:radio name="deliveryScheduleFlag" list="false" template="justradio"/>
            <label style="margin:0;display:inline;" for="CCGForm_deliveryScheduleFlagfalse">
                <fmt:message key="deliverySchedule.ccg.default"/>
            </label>
            <s:radio name="deliveryScheduleFlag" list="true" template="justradio"/>
            <label style="margin:0;display:inline;" for="CCGForm_deliveryScheduleFlagtrue">
                <fmt:message key="deliverySchedule.selectLabel"/>
            </label>
            <div id="errorDiv">
                <s:fielderror><s:param value="'deliverySchedule'"/></s:fielderror>
                <s:fielderror><s:param value="'conflictedDeliverySchedule'"/></s:fielderror>
            </div>
        </ui:field>

        <ui:field id="deliveryScheduleLinkedToCampaignInfo" cssClass="hide">
           <span class="infos"><fmt:message key="deliverySchedule.ccg.link.toCampaign.info"/></span>
        </ui:field>
        <s:if test="outsideCampaignSchedule">
            <s:set var="conflictedKey" value="'deliverySchedule.conflicted'"/>
        </s:if>
        <ui:field id="deliverySection">
            <div id="ccgBlock">
                <ui:weekRange name="scheduleSet" id="scheduleSet"
                              editableRangesName="campaignScheduleSet"
                              iconRunningKey="deliverySchedule.ccg.running"
                              iconNotRunningKey="${campaignNotRunningKey}"
                              iconAvailableKey="deliverySchedule.available.periods"
                              iconConflictedKey="${conflictedKey}"/>
            </div>

            <div id="campaignBlock">
                <ui:weekRange name="campaignScheduleSet" id="campaignScheduleSet"
                              editableRangesName="campaignScheduleSet"
                              iconRunningKey="deliverySchedule.ccg.running"
                              iconNotRunningKey="deliverySchedule.campaign.not.running"
                              readonly="true"/>
            </div>
        </ui:field>
    </ui:fieldGroup>
</ui:section>
