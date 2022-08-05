<%@ page contentType="text/html" %>
<%@ page pageEncoding="UTF-8" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/struts-tags" prefix="s" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ui" %>

<script type="text/javascript">

    $(document).ready(initForm);

    function initForm() {
        $('#popup_cancel').click(function() {
            UI.Dialog.removeAllDialogs();
            return false;
        });
    }

    function submitCampaignForm() {
        UI.Dialog.removeAllDialogs();
        document.forms["campaignForm"].submit();
    }

</script>


<s:form>

    <ui:fieldGroup id="ccgList">
        <ui:field>
            <fmt:message key="campaign.affectCCGDeliverySchedule.message1"/>
            <br/><fmt:message key="campaign.affectCCGDeliverySchedule.message2"/>
        </ui:field>

        <ui:field>
            <s:textarea id="campaignCreativeGroups" wrap="off" cssClass="middleLengthText1" cssStyle="height: 80px" readonly="true"/>
        </ui:field>
    </ui:fieldGroup>

    <div>
        <ui:button message="form.ok" id="popup_ok" type="button" onclick="submitCampaignForm();"/>
        <ui:button message="form.cancel" id="popup_cancel" type="button"/>
    </div>
</s:form>
