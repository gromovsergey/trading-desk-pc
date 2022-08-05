<%@ page import="com.foros.model.VersionHelper" %>
<%@ page contentType="text/html"%>
<%@ page pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ui" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>

<tiles:importAttribute scope="request" name="moduleName"/>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xhtml="true">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <title><ui:windowTitle attributeName="page.title.fileManager"/></title>
    <link rel="icon" href="/images/logo.png" />
    <link rel="shortcut icon" href="/images/logo.png" />
    <ui:stylesheet fileName="common.css" />
    <c:if test="${ad:isActiveLocale('ja') || ad:isActiveLocale('zh')}">
        <ui:stylesheet fileName="hieroglyph.css"/>
    </c:if>
    
    <ui:externalLibrary libName="jquery-css"/>
    <ui:externalLibrary libName="jquery"/>
    <ui:externalLibrary libName="jquery-ui"/>
    
    <ui:javascript fileName="jquery-custom.js"/>
    
    <ui:javascript fileName="common.js"/>
    
    <script type="text/javascript">
    
        $.localize('form.all', '<fmt:message key="form.all"/>');
        $.localize('form.select.wait', '<fmt:message key="form.select.wait"/>');
        $.localize('form.select.none', '<fmt:message key="form.select.none"/>');
        $.localize('form.select.pleaseSelect', '<fmt:message key="form.select.pleaseSelect"/>');
        $.localize('error.ajax', '<fmt:message key="error.ajax"/>');
        $.localize('error.ajax.unauthorized', '<fmt:message key="error.ajax.unauthorized"/>');
        $.localize('form.select.notSpecified', '<fmt:message key="form.select.notSpecified"/>');
        $.localize('report.invalid.output.columns', '<fmt:message key="report.invalid.output.columns"/>');
        $.localize('report.invalid.metrics.columns', '<fmt:message key="report.invalid.metrics.columns"/>');
        $.localize('report.account.required', '<fmt:message key="report.account.required"/>');
        $.localize('selectTip.selected', '<fmt:message key="selectTip.selected"/>');
        $.localize('report.output.field.agency', '<fmt:message key="report.output.field.agency"/>');
        $.localize('report.output.field.adv', '<fmt:message key="report.output.field.adv"/>');
        $.localize('dynamicResources.localize', '<fmt:message key="dynamicResources.localize"/>');
        $.localize('channel.visibility.PUB', '<fmt:message key="channel.visibility.PUB"/>');
        $.localize('channel.visibility.PRI', '<fmt:message key="channel.visibility.PRI"/>');
        $.localize('channel.visibility.CMP', '<fmt:message key="channel.visibility.CMP"/>');
        $.environment('ui.user.account.id', '${_principal.accountId}');

        var targetElementId;
        if (window.opener && !window.opener.closed) {
            window.opener.targetElementId = window.opener.targetElementId || '';
            targetElementId = window.opener.targetElementId;
        }

        function selectFile(fileName) {
            var selectedFile = $('#currDirId').val() + fileName;
            if (targetElementId == '' || !window.opener) return;
            
            if(window.opener){
                var targetElem = window.opener.document.getElementById(targetElementId);
                if(targetElem){
                    window.opener.$(targetElem).val('<c:out value="${filePrefix}"/>' + selectedFile).trigger('input.liveChange');
                }
                window.opener.focus();
            }
            window.close();
        }

        function chdir(form, next) {
            form.nextDir.value = next;
            $('#browseFile').remove();
            $(form).attr({action : "/${moduleName}/fileManagerChDir.action"}).submit();
        }

        function doUpload(){
            var fileToUpload = $('#browseFile').val();
            if ($.trim(fileToUpload).length == 0) {
                alert('${ad:formatMessage("fileman.file.notExist")}')
                return;
            }
            startUpload(fileToUpload, onFileChecked);
        }

        function doDelete(fileName, control) {
            if(!confirm("${ad:formatMessage("fileman.confirmDelete")}" + " '" + fileName + "'?")) return;
            control.value = fileName;
            $('#browseFile').remove();
            $(control.form).attr({action : "/${moduleName}/fileManagerRemove.action"}).submit();
        }

        function doConvert(filename) {
            $.post("convertDialog.action", {
                "sourceFileName": filename,
                "currDirStr": '${currDirStr}',
                "accountId": '${accountId}',
                "mode": '${mode}'
            }, function(data) {
                $('#convertDialog').html(data).dialog({
                    'title': '${ad:formatMessage("fileman.convertSWFDialog.title")}',
                    'buttons': [
                        {
                            id: 'convertDialogSubmit',
                            text: '${ad:formatMessage("form.submit")}',
                            click: function() {
                                params = {
                                    "sourceFileName": $("#sourceFileName").val(),
                                    "targetFileName": $("#targetFileName").val(),
                                    "targetFileNameWithMacro": $("#targetFileNameWithMacro").val(),
                                    "currDirStr": '${currDirStr}',
                                    "accountId": '${accountId}',
                                    "mode": '${mode}'
                                };
                                if ($("#withoutClickUrlMacro").prop("checked")) {
                                    params["withoutClickUrlMacro"] = "true";
                                }
                                if ($("#withClickUrlMacro").prop("checked")) {
                                    params["withClickUrlMacro"] = "true";
                                }

                                $.post("convertCheck.action"
                                    , params
                                    , function(data) {
                                    $("#convertResult").html(data);
                                    if (confirmConversion()) {
                                        prepareConversion();
                                        params = {
                                            "sourceFileName": $("#sourceFileName").val(),
                                            "targetFileName": $("#targetFileName").val(),
                                            "targetFileNameWithMacro": $("#targetFileNameWithMacro").val(),
                                            "clickMacro": $("#clickMacro").val(),
                                            "PWSToken": '${sessionScope.PWSToken}',
                                            "currDirStr": '${currDirStr}',
                                            "accountId": '${accountId}',
                                            "mode": '${mode}'
                                        };
                                        if ($("#withoutClickUrlMacro").prop("checked")) {
                                            params["withoutClickUrlMacro"] = "true";
                                        }
                                        if ($("#withClickUrlMacro").prop("checked")) {
                                            params["withClickUrlMacro"] = "true";
                                        }
                                        $.post(
                                            "convertRun.action",
                                            params,
                                            function(data) {$("#convertDialog").html(data);}
                                        );
                                    }
                                });
                            }
                        },
                        {
                            id: 'convertDialogCancel',
                            text: '${ad:formatMessage("form.cancel")}',
                            click: function() {
                                $(this).dialog('close');
                            }
                        },
                        {
                            id: 'convertDialogClose',
                            text: '${ad:formatMessage("form.close")}',
                            click: function() {
                                window.location.reload();
                            }
                        }
                    ],
                    'open': function(){
                        $('#convertDialogClose').hide();
                    },
                    'width': 380,
                    'resizable': false,
                    'modal': true
                });
            });
        }

        function startUpload(fileToUpload, processor) {
            var params = {
                id : targetElementId,
                fileToUpload : encodeURIComponent(fileToUpload),
                currDirStr : $('#currDirId').val(),
                accountId : '${accountId}',
                mode : '<c:out value="${mode}"/>'
            };
            UI.Data.get("isFileUnique", params, processor);
        }

        function onFileChecked(xmlResp){
            var elem = $('FILE_MANAGER:eq(0)', xmlResp);
            var isFileExists = elem.attr('doesFileExist');
            var isFolderExists = elem.attr('doesFolderExist');
            var confirmMessage = elem.attr('confirmMessage');
            if (isFolderExists == 'true') {
                alert(confirmMessage);
            } else if(isFileExists != 'true' || confirm(confirmMessage)) {
                try {
                    $('#fileManagerChoose')[0].submit();
                } catch (e) {
                    alert('${ad:formatMessage("fileman.file.notExist")}');
                }
            }
        }

        $(function() {
            UI.Hint.initHints();

            $('#browseFile').on('change keydown', function(){
                $('#uploadButton').prop({disabled : !this.value});
            }).trigger('change');

            <s:if test="!fieldErrors['newFolder'].empty">
                $('#newFolderPrompt').show();
                $('#newFolder').focus();
            </s:if>

            $(window.opener).on('unload', function(){
                window.close();
            });
            
            $('.preview_link').on('click', function(e){
                e.preventDefault();
                var jqTr    = $(this).closest('tr').next('tr.preview_image');
                if (jqTr.find('img').length === 0) {
                    jqTr.children('td').eq(0).append( $('<img />').attr('src', $(this).data('preview')).css({'max-height':'600px', 'max-width':'800px'}) );
                }
                jqTr.toggle();
            });
        });
    </script>
</head>

<body>
    <table id="root">
        <tr id="header">
            <td class="rootCell">
                <div id="headContainer">
                    <div id="applicationLogo">Target RTB</div>
                </div>
            </td>
        </tr>

        <tr id="content">
            <td class="rootCell">
                <div class="contentBody">
                    <s:form action="%{#attr.moduleName}/fileManager" id="fileManagerChoose" enctype="multipart/form-data" method="post">
                        <s:fielderror><s:param value="'fileToDelete'"/></s:fielderror>
                        <s:fielderror><s:param value="'resourceNotFound'"/></s:fielderror>
                        <s:fielderror><s:param value="'fileManagerError'"/></s:fielderror>
                        <s:hidden name="id"/>
                        <s:hidden name="nextDir"/>
                        <s:hidden id="currDirId" name="currDirStr"/>
                        <s:hidden name="mode"/>
                        <s:hidden name="accountId"/>
                        <s:hidden name="fileTypes"/>

                        <s:if test="hasActionErrors()">
                            <div style="margin-top: 5px; margin-bottom: 5px">
                                <s:actionerror/>
                            </div>
                        </s:if>

                        <ui:section>
                            <ui:fieldGroup>
                                
                                <ui:field errors="fileToUpload">
                                    <table class="fieldAndAccessories">
                                        <tr>
                                            <td class="withField">
                                                <s:hidden name="fileToDelete"/>
                                                <s:file name="fileToUpload" id="browseFile" size="70" />
                                            </td>
                                            <s:if test="!isInternal()">
                                                <td class="withTip">
                                                    <ui:hint>
                                                        <fmt:message key="fileman.instruction.1"><fmt:param><fmt:message key="fileman.upload"/></fmt:param></fmt:message> <br/>
                                                        <fmt:message key="fileman.instruction.2"/>
                                                    </ui:hint>
                                                </td>
                                            </s:if>
                                            <td class="withButton">
                                                <s:url var="url" value="/%{#attr.moduleName}/fileManagerUpload.action">
                                                    <s:param name="PWSToken">${sessionScope.PWSToken}</s:param>
                                                    <s:param name="currDirStr" value="%{currDirStr}"/>
                                                    <s:param name="mode" value="%{mode}"/>
                                                    <s:param name="accountId" value="%{accountId}"/>
                                                </s:url>
                                                <ui:button message="fileman.upload" onclick="$('#fileManagerChoose')[0].action='${url}';doUpload();" id="uploadButton" type="button" />
                                            </td>
                                        </tr>
                                        <tr>
                                    </table>
                                </ui:field>
                                <ui:field>
                                    <c:set var="textVal">
                                        <fmt:message key="fileman.fileSizeLimitMessage">
                                            <fmt:param value="20"/>
                                        </fmt:message>
                                    </c:set>
                                    <ui:text text="${pageScope.textVal}"/>
                                </ui:field>
                                
                            </ui:fieldGroup>
                        </ui:section>
                        
                        <div class="path">
                            <s:if test="!currDir.empty">
                                <a href="#" onclick="chdir($('#fileManagerChoose')[0],'../-1');return false;">[<fmt:message key="fileman.root"/>]</a>
                            </s:if>
                            <s:else>
                                [<fmt:message key="fileman.root"/>]
                            </s:else>
                            <s:if test="!currDir.empty">
                                <c:set var="lastIndex" value="${fn:length(currDir)}"/>
                                <c:forEach items="${currDir}" var="pathElem" varStatus="indexID">
                                    <span class="delimiter">/</span>
                                    <c:choose>
                                        <c:when test="${indexID.count != lastIndex}">
                                            <a href="#" onclick="chdir($('#fileManagerChoose')[0],'../${indexID.count-1}');return false;"><c:out value="${pathElem}"/></a>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="fileName"><c:out value="${pathElem}"/></span>
                                        </c:otherwise>
                                    </c:choose>
                                </c:forEach>
                            </s:if>
                        </div>
                    </s:form>
                    
                    <div class="tableOptions">
                        <s:form id="newFolderForm" method="post" action="%{#attr.moduleName}/fileManagerCreateFolder">
                            <s:hidden name="id"/>
                            <s:hidden name="currDirStr"/>
                            <s:hidden name="mode"/>
                            <s:hidden name="accountId"/>
                            <s:hidden name="fileTypes"/>
    
                            <div>
                                <ui:button message="fileman.newFolder" onclick="$('#newFolderPrompt').show(); $('#newFolder').focus();" type="button" />
                                <s:fielderror><s:param value="'newFolder'"/></s:fielderror>
                                <s:if test="auditObjectId != null">
                                    <ui:button message="form.viewLog" target="_blank" href="/admin/auditLog/view.action?type=54&id=${auditObjectId}" />
                                </s:if>
                            </div>
                            <div id="newFolderPrompt" style="margin-top:5px; display: none;">
                                <s:textfield id="newFolder" name="newFolder" cssClass="middleLengthText" maxlength="100"/>
                                <input type="submit" value="OK"/>
                            </div>
                        </s:form>
                    </div>

                    <table id="filestable" class="dataView filestable">
                        <thead>
                            <tr>
                                <th><span><fmt:message key="fileman.file"/></span></th>
                                <th><span><fmt:message key="fileman.date"/></span></th>
                                <th><span><fmt:message key="fileman.size"/></span></th>
                                <th><span><fmt:message key="fileman.mimeType"/></span></th>
                                <th colspan="4"><span><fmt:message key="fileman.actions"/></span></th>
                            </tr>
                        </thead>
                        <tbody>
                            <s:if test="fileList != null">
                                <c:set var="curDir" value="${currDirStr}"/>
                                <c:set var="accountId" value="${accountId}"/>
                                <c:set var="mode" value="${mode}"/>
                                <c:forEach items="${fileList}" var="file" varStatus="indexID">
                                  <s:if test="#attr.file != null">
                                    <c:choose>
                                      <c:when test="${file.directory}">
                                        <tr>
                                          <td>
                                            <a href="#" class="withIcon folder" onclick="chdir($('#fileManagerChoose')[0],'${ad:escapeJavaScriptInTag(file.name)}'); return false;">
                                                      <c:out value="${file.name}"/>/
                                                    </a>
                                          </td>
                                          <td><c:out value="${file.date}"/></td>
                                          <td>&#160;</td>
                                          <td>&#160;</td>
                                          <td class="selectFile">&nbsp;</td>
                                          <td class="delete">
                                              <a href="#"
                                                 class="withIcon delete"
                                                 onclick="doDelete('${ad:escapeJavaScriptInTag(file.name)}', $('#fileManagerChoose')[0].fileToDelete)">
                                                  <fmt:message key="fileman.deleteFolder.tooltip"/>
                                              </a>
                                          </td>
                                          <td class="download">&nbsp;</td>
                                          <td class="convert">&nbsp;</td>
                                        </tr>
                                      </c:when>
                                      <c:otherwise>
                                        <tr>
                                          <td><span class="withIcon file"><c:out value="${file.name}"/></span></td>
                                          <td><c:out value="${file.date}"/></td>
                                          <td class="size"><fmt:formatNumber value="${file.length}" groupingUsed="true" maxFractionDigits="0"/></td>
                                          <td class="size">
                                            <c:set var="hasPreview" value="${file.filetype == 'image/gif' || file.filetype == 'image/png' || file.filetype == 'image/jpeg' || file.filetype == 'image/bmp' || file.filetype == 'image/x-ms-bmp'}"/>
                                            ${file.filetype}<c:if test="${hasPreview}">,&nbsp;<a href="#" data-preview="download.action?file=${ad:escapeURL(file.name)}&accountId=${accountId}&mode=${mode}&currDirStr=${curDir}" class="preview_link">preview</a></c:if>
                                          </td>
                                          <td class="selectFile">
                                              <c:if test="${file.allowedToSelect}">
                                                <a href="#" onclick="selectFile('${ad:escapeJavaScriptInTag(file.name)}')"
                                                            class="withIcon select">
                                                    <fmt:message key="fileman.select"/>
                                                </a>
                                              </c:if>
                                              <c:if test="${!file.allowedToSelect}">&nbsp;</c:if>
                                          </td>
                                          <td class="delete">
                                              <a href="#"
                                                 class="withIcon delete"
                                                 onclick="doDelete('${ad:escapeJavaScriptInTag(file.name)}', $('#fileManagerChoose')[0].fileToDelete)">
                                                  <fmt:message key="fileman.deleteFile.tooltip"/>
                                              </a>
                                          </td>
                                          <td class="download">
                                              <a href="download.action?file=${ad:escapeURL(file.name)}&accountId=${accountId}&mode=${mode}&currDirStr=${curDir}"
                                                 class="withIcon download">
                                                  <fmt:message key="fileman.download"/>
                                              </a>
                                          </td>
                                          <td class="convert">
                                              <c:if test="${file.filetype == 'application/x-shockwave-flash'}">
                                                  <a href="#" class="withIcon convert"
                                                     onclick="doConvert('${ad:escapeJavaScriptInTag(file.name)}', $('#fileManagerChoose')[0].fileToDelete)">
                                                      <fmt:message key="fileman.convert"/>
                                                  </a>
                                              </c:if>
                                          </td>
                                        </tr>
                                        <c:if test="${hasPreview}">
                                            <tr class="hide preview_image">
                                                <td colspan="7"></td>
                                            </tr>
                                        </c:if>
                                      </c:otherwise>
                                    </c:choose>
                                  </s:if>
                                </c:forEach>
                              </s:if>
                        </tbody>
                    </table>
                </div>
                <div id="convertDialog" class="hide"></div>
            </td>
        </tr>

        <tr id="footer">
            <td>
                <div id="appVersion">
                    <s:if test="isInternal()">
                        <c:set var="versionName"><fmt:message key="form.version"/></c:set>
                        <c:set var="versionValue"><%=application.getAttribute(VersionHelper.VERSION_PROPERTY)%></c:set>
                        <c:out value="${versionName} ${versionValue}"/>
                    </s:if>
                    <s:else>
                            <a href="${_context}/TermsOfUse.action"><fmt:message key="TermsOfUse.termsOfUse"/></a>
                            <a href="${_context}/Contacts.action"><fmt:message key="contacts.contactUs"/></a>
                    </s:else>
                </div>
                <div id="copyright">
                    <fmt:message key="form.copyright"/>
                </div>
            </td>
        </tr>
    </table>
</body>
</html>
