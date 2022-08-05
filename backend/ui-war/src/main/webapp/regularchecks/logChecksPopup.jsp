<%@ page contentType="text/html" %>
<%@ page pageEncoding="UTF-8" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/struts-tags" prefix="s" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ui" %>

<s:set var="hasValidationErrors" value="%{hasErrors()}"/>
<s:set var="hasVersionError" value="%{fieldErrors['version'] != null}" />
<s:set var="entityPath" value="%{entityName == 'group'? 'campaign/group': 'channel/' + entityName}" />

<script type="text/javascript">
    var hasValidationErrors = ${hasValidationErrors};
    var hasVersionError = ${hasVersionError};
    var inProgress = false;
    var successLocation = '<s:property value="successLocation"/>';

    function initForm() {
        $('#popup_cancel').click(function(e) {
            UI.Dialog.removeAllDialogs();
            e.preventDefault();
        });
        
        $('#logChecksPopupForm').submit(function(e){
            submitHandler();
            e.preventDefault();
        });
    }

    function toggleDisabledForInputs(disabled) {
        $("#logChecksPopupForm input[type='button']").prop({disabled : !!disabled});
    }

    function submitHandler(event) {
        $('#popupAlert').empty();
        if (!$('#confirmation').is(":checked")) {
             $('<span></span>').addClass('errors').text('<fmt:message key="checks.errors.confirmationNotChecked"/>').appendTo('#popupAlert');
        }
        if (inProgress || !$('#popupAlert').is(':empty')) {
            return;
        }
        inProgress = true;
        $("#popup").css('z-index', 0);
        toggleDisabledForInputs(true);
        $.ajax({
            url: '${_context}/${entityPath}/logChecks.action',
            data: $('#logChecksPopupForm').serialize() ,
            success: submitSuccess,
            error : submitVersion,
            type: "POST"
        });
    }

    function submitVersion(data) {
        UI.Dialog.removeAllDialogs();
        document.location.hash = 'error:version';
        document.location.reload();
    }

    function submitSuccess(data) {
        $('#popup').html(data);
        if (!hasValidationErrors) {
            UI.Dialog.removeAllDialogs();
            document.location = successLocation;
        } else {
            if (hasVersionError) {
                submitVersion();
            }
            toggleDisabledForInputs(false);
            inProgress = false;
        }
    }
    
    $(function(){
        initForm();
    });
</script>

<s:form id="logChecksPopupForm">
    <s:hidden name="id"/>
    <s:hidden name="entityName"/>
    <s:hidden name="version"/>
    <s:hidden name="existingInterval"/>
    <s:hidden name="account.id"/>

    <ui:pageHeading attributeName="checks.logCheckTitle" isSimpleText="false"/>

    <ui:fieldGroup>
        <ui:simpleField labelKey="checks.user" value="${userName}"/>
        <ui:field labelKey="checks.confirmation">
            <s:checkbox id="confirmation" name="confirmation"/><fmt:message key="checks.checksCompletedConfirmation"/>
        </ui:field>
        <ui:field labelKey="checks.notes" errors="checkNotes">
            <s:textarea id="checkNotes" name="checkNotes" rows="5" cssClass="middleLengthText1" cssStyle="height: 50px"/>
        </ui:field>
        <ui:field labelKey="checks.nextCheckIn">
            <s:select list="availableIntervals" name="interval" id="interval" cssClass="middleLengthText" value="1"/>
        </ui:field>
    </ui:fieldGroup>

    <ui:errorsBlock>
        <s:fielderror><s:param value="'version'"/></s:fielderror>
        <s:fielderror><s:param value="'name'"/></s:fielderror>
        <s:actionerror/>
    </ui:errorsBlock>

    <div id="popupAlert" class="wrapper"></div>
    <div>
        <ui:button message="checks.form.log" id="popup_save" type="submit"/>
        <ui:button message="form.cancel" id="popup_cancel" type="button"/>
    </div>
</s:form>