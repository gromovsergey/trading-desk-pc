<%@ page contentType="text/html" %>
<%@ page pageEncoding="UTF-8" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/struts-tags" prefix="s" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ui" %>

<s:set var="hasValidationErrors" value="%{hasErrors()}"/>
<s:set var="hasVersionError" value="%{fieldErrors['version'] != null}" />
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
        
        $('#discoverChannelPopupForm').submit(function(e){
            submitHandler();
            e.preventDefault();
        });
        
        $(document).off('.preventDoubleSubmit');
    }

    function toggleDisabledForInputs(disabled) {
        $("#discoverChannelPopupForm input[type='button']").prop({disabled : !!disabled});
    }

    function submitHandler(event) {
        if (inProgress) {
            return;
        }
        inProgress = true;
        if (!$('#discoverChannelListId').val()) {
            var errorTd = $('#discoverChannelListId').closest('.field').find('.withError');
            errorTd.html('<span class="errors"><fmt:message key="errors.field.required"/></span>');
            inProgress = false;
            return;
        }
        $("#popup").css('z-index', 0);
        toggleDisabledForInputs(true);
        $.ajax({
            url: '/admin/DiscoverChannel/linkDiscoverChannel.action',
            data: $('#discoverChannelPopupForm').serialize() ,
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
            $('#discoverChannelPopupForm').append(channelsHTML);
            toggleDisabledForInputs(false);
            inProgress = false;
            $('#discoverChannelListId').val( $('#discoverChannelListLastKey').val() );
            $('#discoverChannelListId').siblings('input[name="discoverChannelListId"]').val( $('#discoverChannelListLastValue').val() );
        }
    }
    
    $(function(){
        initForm();
    });
</script>

<s:form action="linkDiscoverChannel" id="discoverChannelPopupForm">
    <s:hidden name="fromDiscoverChannelList"/>

    <input type="hidden" name="lastInputKey" id="discoverChannelListLastKey" value="${param.undefined_text}"/>
    <input type="hidden" name="lastInputValue" id="discoverChannelListLastValue" value="${param.discoverChannelListId}"/>

    <ui:pageHeading attributeName="DiscoverChannel.link.title" isSimpleText="false"/>

    <ui:errorsBlock>
        <s:fielderror><s:param value="'version'"/></s:fielderror>
        <s:fielderror><s:param value="'name'"/></s:fielderror>
        <s:actionerror/>
        <s:if test="!single">
            <s:fielderror><s:param value="'keywords'"/></s:fielderror>
        </s:if>
    </ui:errorsBlock>
    
    <ui:fieldGroup>
        <ui:field labelKey="DiscoverChannelList.entityName" labelForId="discoverChannelListId" required="true" errors="discoverChannelListId">
            <ui:autocomplete
                id="discoverChannelListId"
                source="/xml/discoverChannelListsByDiscoverChannel.action"
                requestDataCb="Autocomplete.discoverChannelListId.getListParams"
                cssClass="middleLengthText"
            >
                    <script type="text/javascript">
                        Autocomplete.discoverChannelListId.getListParams = function(query){
                            return $.extend({discoverChannelId:${discoverChannels[0].id}}, {currentChannelListId: $('#currentChannelListId').val()}, {query : query});
                        }
                    </script>
            </ui:autocomplete>
        </ui:field>
    </ui:fieldGroup>

    <s:if test="single">
        <ui:section titleKey="channel.keywords" errors="keywords,singleBaseKeyword,baseKeyword">
            <s:textarea name="singleBaseKeyword" id="keywords" wrap="off" cssClass="middleLengthText1" cssStyle="height: 80px"/>
            <div style="width:450px">
                <fmt:message key="channel.linkedChannelKeywordsNote"/>
            </div>
        </ui:section>
    </s:if>

    <c:if test="${not empty discoverChannels[0].urls}">
        <div class="wrapper">
            <fmt:message key="channel.linkedChannelUrlsNote"/>
        </div>
    </c:if>

    <div>
        <ui:button message="form.save" id="popup_save" type="submit"/>
        <ui:button message="form.cancel" id="popup_cancel" type="button"/>
    </div>
</s:form>