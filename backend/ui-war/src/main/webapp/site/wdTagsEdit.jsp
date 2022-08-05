<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="ad" uri="/ad/serverUI" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<script type="text/javascript">

    function copyContent() {
        $('#optedOutUrls').val($('#optedInUrls').val());
    }

    $(function() {
        if ($("#saveForm_optedInOptionA").prop('checked')) {
            $("#optedInUrls").prop({disabled : true});
        }
        $("#saveForm_optedInOptionA").click(function() {
            $("#optedInUrls").prop({disabled : true});
        });
        $("#saveForm_optedInOptionS").click(function() {
            $("#optedInUrls").prop({disabled : false});
        });

        if ($("#saveForm_optedOutOptionA").prop('checked')) {
            $('#optedOutUrls, #saveForm_copy, #passbackUrl').prop({disabled : true});
        }
        if ($("#saveForm_optedOutOptionS").prop('checked')) {
            $('#passbackUrl').prop({disabled : true});
        }
        if ($("#saveForm_optedOutOptionP").prop('checked')) {
            $('#optedOutUrls, #saveForm_copy').prop({disabled : true});
        }
        $("#saveForm_optedOutOptionA").click(function() {
            $('#optedOutUrls, #saveForm_copy, #passbackUrl').prop({disabled : true});
        });
        $("#saveForm_optedOutOptionS").click(function() {
            $('#optedOutUrls, #saveForm_copy').prop({disabled : false});
            $('#passbackUrl').prop({disabled : true});
        });
        $("#saveForm_optedOutOptionP").click(function() {
            $('#optedOutUrls, #saveForm_copy').prop({disabled : true});
            $('#passbackUrl').prop({disabled : false});
        });

        $('#width, #height').liveChange(updateLookAndFeel);

        $('#templateId').change(function(){
            changeTemplate();
        });
        
        assignLiveChange();
        tokenNames2Data($('#optionsDiv'));
    });
    
    function assignLiveChange(){
        $(':input', '#options, #optionsDiv').liveChange(updateLookAndFeel);
    }

    function changeTemplate() {
        if ($('#templateId').val()) {
            $('#optionsDiv').show();
            var params = $('#saveForm').serializeArray(),
            tokenVals   = $('#optionsDiv').formKeeper({'key':'optionsDivWD','useDataNames':true}).formKeeper('serialize'),
            storedVals  = $.parseJSON(sessionStorage.getItem('optionsDivWD'));

            if (storedVals) {
                storedVals  = $.extend(storedVals, tokenVals);
            } else {
                storedVals  = tokenVals;
            }
            sessionStorage.setItem('optionsDivWD', JSON.stringify(storedVals));

            $('#optionsDiv')
                .html('<h3 class="level1">${ad:formatMessage("site.wdTag.options.loading")}</h3>')
                .load('${_context}/site/WDTag/changeTemplate.action',
                    params,
                    function() {
                        assignLiveChange();
                        updateLookAndFeel();
                        $('.collapsible').collapsible();
                        tokenNames2Data($(this));
                        $(this).formKeeper('restore');
                    });
        } else {
            $('#optionsDiv').hide();
        }
    }

    var targetElementId = null;
    
    function openFileBrowser(id, fileTypes) {
        targetElementId = id;
        
        var cdStr   = '';
        if (document.getElementById(targetElementId) && document.getElementById(targetElementId).value !== ''){
            var path    = document.getElementById(targetElementId).value;
            path    = path.substring(path[0] !== undefined && path[0] === '/' ? 1 : 0, path.lastIndexOf("/")+1);
            cdStr   = (path !== '' && path !== '/') ? '&currDirStr='+encodeURIComponent(path):'';
        }
        
        var account = '&accountId=' + '${accountId}';
        window.open('${_context}/fileman/fileManager.action?id='
                + targetElementId + '&mode=publisher' + account + '&fileTypes=' + fileTypes+cdStr,'filebrowser','width=820,height=600,resizable=yes,scrollbars=yes');
    }
    
    function updateLookAndFeel() {
        $.ajax({
            type: 'POST',
            url: 'preview/livePreview.action',
            dataType: 'text',
            data: $('#saveForm').serialize(),
            success : previewSuccess,
            error: previewError,
            waitHolder: $('#previewSection')
        });
    }

    function previewError() {
        $('#previewFrame').hide();
        $('#previewSection > .legend').after('<span class="errors" id="previewFrameError"><fmt:message key="wdtag.previewNotAvailable"/></span>');
    }

    function previewSuccess(data, textStatus) {
        var frame = $('#previewFrame');
        var w = UI.Localization.parseInt($('#width').val());
        var h = UI.Localization.parseInt($('#height').val());

        if (w && w > 0) {
            frame.width(w);
        }

        if (h && h > 0) {
            frame.height(h);
        }

        frame.show();
        try {
            var frameDoc = frame[0].contentWindow.document;
            frameDoc.write(data);
            frameDoc.close();
        } catch (e) {}
    }

    function tokenNames2Data(jqContainer) {
        jqContainer.find('.formFields tr').each(function(){
            var tokenName   = $.trim($(this).children('td.fieldName').text());
            if (tokenName != '') {
                $(this).children('td.field').find(':input:visible').not(':disabled').attr('data-name',tokenName);
            }
        });
    }
</script>

<s:form id="saveForm" action="%{#request.moduleName}/%{#request.entityName}/%{#attr.isCreatePage?'saveNew':'save'}">
    <s:hidden name="id" />
    <s:hidden name="version"/>
    <s:hidden name="site.id" />
    <s:hidden name="site.name" />
    <s:hidden name="site.account.id" />
    <s:hidden name="accountId" />

    <div class="wrapper">
        <s:fielderror><s:param value="'version'"/></s:fielderror>
        <s:fielderror><s:param value="'options'"/></s:fielderror>
    </div>
    <ui:section titleKey="form.main">
        <ui:fieldGroup>
            <ui:field labelKey="wdtag.name" labelForId="name" required="true" errors="name">
                <s:textfield name="name" id="name" cssClass="middleLengthText" maxLength="100"/>
            </ui:field>
            <ui:field labelKey="wdtag.width" labelForId="width" required="true" errors="width">
                <s:textfield name="width" id="width" cssClass="smallLengthText" maxLength="5" />
            </ui:field>
            <ui:field labelKey="wdtag.height" labelForId="height" required="true" errors="height">
                <s:textfield name="height" id="height" cssClass="smallLengthText" maxLength="5"/>
            </ui:field>
            <ui:field labelKey="wdtag.template" labelForId="template" required="true" errors="template">
                <s:select name="template.id" list="templates" cssClass="middleLengthText" id="templateId"
                          listKey="id" listValue="name"
                          headerKey="" headerValue="%{getText('form.select.pleaseSelect')}"/>
            </ui:field>
        </ui:fieldGroup>
    </ui:section>

    <div class="logicalBlock" id="optionsDiv">
        <s:hidden name="template.version"/>
        <s:set var="groups" value="template.publisherOptionGroups"/>
        <c:set var="optionTitleKey" value="page.title.Option"/>
        <%@ include file="/admin/option/optionValuesEdit.jsp"%>
    </div>

    <ui:section titleKey="wdtag.lookAndFeelPreview" id="previewSection">
        <div style="padding-bottom:5px;"><fmt:message key="wdtag.lookAndFeelComment"/></div>
        <c:url var="previewUrl" value="${_context}/site/WDTag/preview/content.action"/>
        <s:set var="previewWidth" value="previewWidth"/>
        <s:set var="previewHeight" value="previewHeight"/>
        <ui:wdTagPreview
                id="previewFrame"
                width="${previewWidth}"
                height="${previewHeight}"
                src="${previewUrl}?id=${id}"
        />
    </ui:section>

    <ui:section titleKey="wdtag.contentFilter">
        <table>
            <tr>
                <td style="vertical-align:top;">
                    <b><fmt:message key="wdtag.cfComment1"/></b>
                    <table class="formFields">
                        <tr>
                            <td class="field">
                                <s:radio cssClass="withInput"  list="'A'" listValue="getText('wdtag.useAll')" name="optedInOption"/>
                            </td>
                        </tr>
                        <tr>
                            <td class="field">
                                <s:radio cssClass="withInput"  list="'S'" listValue="getText('wdtag.useCustom')" name="optedInOption"/>
                            </td>
                        </tr>
                        <tr>
                            <td>
                                <fmt:message key="wdtag.cfComment3"/>
                            </td>
                        </tr>
                        <tr>
                            <td>
                                <s:fielderror><s:param value="'optedInUrls'"/></s:fielderror>
                                <s:textarea name="optedInUrls" id="optedInUrls" cssClass="middleLengthText" rows="3" cols="50"/>
                            </td>
                        </tr>
                    </table>
                </td>
                <td>
                    <b><fmt:message key="wdtag.cfComment2"/></b>
                    <table class="formFields">
                        <tr>
                            <td class="field">
                                <s:radio cssClass="withInput"  list="'A'" listValue="getText('wdtag.useLastest')" name="optedOutOption"/>
                            </td>
                        </tr>
                        <tr>
                            <td class="field">
                                <s:radio cssClass="withInput"  list="'S'" listValue="getText('wdtag.useCustom1')" name="optedOutOption"/>
                            </td>
                        </tr>
                        <tr>
                            <td>
                                <fmt:message key="wdtag.cfComment3"/>
                            </td>
                        </tr>
                        <tr>
                            <td>
                                <table class="fieldAndAccessories">
                                    <tr>
                                        <td class="withTopButton">
                                            <ui:button id="saveForm_copy" message="wdtag.copy" onclick="copyContent();" type="button" />
                                        </td>
                                    </tr>
                                    <tr>
                                        <td class="withField">
                                            <s:fielderror><s:param value="'optedOutUrls'"/></s:fielderror>
                                            <s:textarea name="optedOutUrls" id="optedOutUrls" cssClass="middleLengthText" rows="3" cols="50"/>
                                        </td>
                                    </tr>
                                </table>
                            </td>
                        </tr>
                        <tr>
                            <td class="field">
                                <s:radio cssClass="withInput"  list="'P'" listValue="getText('wdtag.usePassback')" name="optedOutOption"/>
                            </td>
                        </tr>
                        <tr>
                            <td>
                                <table class="formFields">
                                    <tr>
                                        <td class="field">
                                            <fmt:message key="wdtag.URL"/>&nbsp;
                                            <s:textfield name="passbackUrl" id="passbackUrl" cssClass="middleLengthText" maxLength="2000"/>
                                        </td>
                                        <td class="withError">
                                            <s:fielderror><s:param value="'passbackUrl'"/></s:fielderror>
                                        </td>
                                    </tr>
                                </table>
                            </td>
                        </tr>
                    </table>
                </td>
            </tr>
        </table>
    </ui:section>  
    <div class="wrapper">
        <ui:button message="form.save" type="submit" />
         <c:choose>
            <c:when test="${empty id}">
                <ui:button message="form.cancel" onclick="location='${_context}/site/view.action?id=${site.id}';" type="button" />
            </c:when>
            <c:otherwise>
                <ui:button message="form.cancel" onclick="location='view.action?id=${id}';" type="button" />
            </c:otherwise>
        </c:choose>
    </div>
</s:form>
