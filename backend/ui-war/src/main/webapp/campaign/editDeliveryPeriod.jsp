<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ui" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<script type="text/javascript">
     $(document).ready(function () {
         manageTimeControl();

         $('input[type=radio][name=deliverySchedule]').change(function() {
             manageTimeControl();
         });
    });

    function manageTimeControl() {
        if ($('input[type=radio][name=deliverySchedule]:checked').val() == "true" ) {
            $('#deliverySection').show();
        } else {
            $('#deliverySection').hide();
    }
    }
</script>

<s:set var="legend">
    <fmt:message key="deliverySchedule.label"/> (<fmt:message key="deliverySchedule.timeZone"/>: <ad:resolveGlobal resource="timezone" id="${existingAccount.timezone.key}" prepare="true"/>)
</s:set>

<ui:section title="${legend}">
    <ui:fieldGroup>
        <ui:field>
            <s:radio name="deliverySchedule" list="false" template="justradio"/>
            <label style="margin:0;display:inline;" for="campaignForm_deliverySchedulefalse">
                <fmt:message key="deliverySchedule.defaultLabel"/>
            </label>
            <s:radio name="deliverySchedule" list="true" template="justradio"/>
            <label style="margin:0;display:inline;" for="campaignForm_deliveryScheduletrue">
                <fmt:message key="deliverySchedule.selectLabel"/>
            </label>
            <div id="errorDiv"><s:fielderror><s:param value="'deliverySchedule'"/></s:fielderror></div>
        </ui:field>

        <ui:field id="deliverySection">
            <ui:weekRange id="scheduleSet" name="scheduleSet" iconRunningKey="deliverySchedule.campaign.running" iconAvailableKey="deliverySchedule.available.periods" />
        </ui:field>
    </ui:fieldGroup>
</ui:section>
