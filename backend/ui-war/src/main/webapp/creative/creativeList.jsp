<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>
<%@ taglib prefix="ad" uri="/ad/serverUI" %>

<script type="text/javascript">
function check(){
    $('#errorsDiv').remove();
    $('#update').attr("action", "updates.action");

    if ($('[name=displayCreativeIds]:checked').length == 0
            && $('[name=textCreativeIds]:checked').length == 0){
        alert('${ad:formatMessage("creative.link.nothingSelected")}');
        return false;
    }

    if ($('[name=displayCreativeIds]:checked').length == 0){
        $('#LINK_TO_DISPLAY').hide();
    } else {
        $('#LINK_TO_DISPLAY').show();
    }
    
    if ($('[name=textCreativeIds]:checked').length == 0){
        $('#LINK_TO_TEXT').hide();
    } else {
        $('#LINK_TO_TEXT').show();
    }
    
    return true;
}


function toggleAllCreatives(header) {
    $('[name=displayCreativeIds]').prop({checked : header.checked});
    $('[name=textCreativeIds]').prop({checked : header.checked});
}

function getSelectdIdsByAction(action) {
    var creativeIds = [];
    if (action == "LINK_TO_TEXT"){
        $('[name=textCreativeIds]').each(function () {
            if (this.checked) {
                creativeIds.push(this.value);
            }
        });
    } else if (action == "LINK_TO_DISPLAY"){
        $('[name=displayCreativeIds]').each(function () {
            if (this.checked) {
                creativeIds.push(this.value);
            }
        });
    }

    return creativeIds;
}

function getDeclineReason() {
    var declinationReason = '';
    do {
        declinationReason = prompt("<fmt:message key="decline.reason"/>", "");
        if (declinationReason == null || declinationReason.length == 0) return;
        if (declinationReason.length > 500) {
            alert("<fmt:message key="decline.too.long"/>");
        }
    } while (declinationReason.length > 500);
    return declinationReason;
}

function checkCreativeBatchActionAndProceed(action) {
    var creatives = getAllSelectedIds();
    if (creatives.length == 0) {
        
        return;
    }

    if (action == "LINK_TO_DISPLAY" || action == "LINK_TO_TEXT") {
        doLink(action);
        return;
    }

 
    UI.Data.get('CreativeBatchStatusActionCheck', {action: action, ids: creatives}, function(data) {
        var result = $('result', data).text();
        var confirmed;
        if(result == 'true') {
            confirmed = (action == 'DECLINE') || (creatives.length == 1) || confirm('${ad:formatMessage('confirmBulkChange')}');
        } else {
            confirmed = confirm('${ad:formatMessage('creative.batchAction.incomplete.confirm')}');
        }
        if(confirmed) {
            if (action == 'DECLINE') {
                var declineReason = getDeclineReason();
                if(!declineReason) {
                    return;
                }
                $('#declineReason').val(declineReason);
            }
            $('#changeType').val(action);
            var frm = $('#update');
            frm.submit();
        }
    });
}

function getAllSelectedIds() {
    var creativeIds = [];
    $('[name$=CreativeIds]').each(function () {
        if (this.checked) {
            creativeIds.push(this.value);
        }
    });
    return creativeIds;
}

function doClicks() {
    var creatives = getAllSelectedIds();
    if (creatives.length == 0) {
        return;
    }

    $.get('clickUrls.action', {advertiserId:'${account.id}'}, function(data){
        $('#ccgDialog').html(data).dialog({
            'title': "${ad:formatMessage("creative.clickUrls")}",
            'buttons': [
                {
                    id: 'ccgDialogSubmit',
                    text: '${ad:formatMessage("creative.clickUrls.btn")}',
                    click: function() {
                        if (confirm('${ad:formatMessage("ccg.bulk.confirm")}')) {
                            $('#waiter').show();
                            var self = $(this);
                            var params = self.find(':input').serializeArray();
                            var creativeIds = getAllSelectedIds();
                            $.each(creativeIds, function(i, id) {
                                params.push({name: "creativeIds", value: id});
                            });
                            params.push({name: "PWSToken", value: $("[name='PWSToken']").val()});
                            params.push({name: "advertiserId", value: ${account.id}});
                            $('#ccgDialogSubmit, #ccgDialogCancel').button("disable");
                            $.post("saveclickUrls.action", $.param(params, true), function(data) {
                                self.html(data);
                                $('#ccgDialogSubmit, #ccgDialogCancel').button("enable");
                            }, 'html');
                        }
                    }
                },
                {
                    id: 'ccgDialogCancel',
                    text: '${ad:formatMessage("form.cancel")}',
                    click: function(){ $(this).dialog('close');  }
                },
                {
                    id: 'ccgDialogOk',
                    text: '${ad:formatMessage("form.ok")}',
                    click: function(){ $(this).dialog('close'); window.location.href = window.location.pathname + window.location.search;}
                }
            ],
            'width': 670,
            'resizable': false,
            'modal': true,
            'open': function() { 
                $('#ccgDialogOk').hide();
            }
        }).on('keypress', ':input', function(e){
            e.stopPropagation();
            if (e.keyCode === 13) {
                e.preventDefault();
                $('#ccgDialogSubmit:enabled').trigger('click');
            }
        });
    }, 'html');
}

function doLink(action) {
    $.get('createLinks.action', {advertiserId:'${account.id}', display:(action=='LINK_TO_DISPLAY' ? true : false)}, function(data){
        $('#ccgDialog').html(data).dialog({
            'title': "${ad:formatMessage("creative.linkToCreativeGroups")}",
            'buttons': [
                {
                    id: 'ccgDialogSubmit',
                    text: '${ad:formatMessage("creative.linkToCreativeGroups")}',
                    click: function(){
                        if ($('#treeFilterByCampaign').find('input[type="checkbox"]').length > 0
                                && $('#treeFilterByCampaign').find('input[type="checkbox"]:checked').length == 0) {
                            showRequiredAlertTreeFilterByCampaign();
                            return false;
                        }
                        
                        if (confirm('${ad:formatMessage("ccg.bulk.confirm")}')) {
                            $('#waiter').show();
                            var self = $(this);
                            var params = self.find(':input').serializeArray();
                            var creativeIds = getSelectdIdsByAction(action);
                            $.each(creativeIds, function(i, id) {
                                params.push({name: "creativeIds", value: id});
                            });
                            params.push({name: "PWSToken", value: $("[name='PWSToken']").val()});
                            params.push({name: "advertiserId", value: ${account.id}});
                            params.push({name: "display", value: (action=='LINK_TO_DISPLAY' ? true : false)});
                            $('#ccgDialogSubmit, #ccgDialogCancel').button("disable");
                            $.post("saveLinks.action", $.param(params, true), function(data){
                                self.html(data);
                                $('#ccgDialogSubmit, #ccgDialogCancel').button("enable");
                            }, 'html');
                        }
                    }
                },
                {
                    id: 'ccgDialogCancel',
                    text: '${ad:formatMessage("form.cancel")}',
                    click: function(){ $(this).dialog('close');  }
                },
                {
                    id: 'ccgDialogOk',
                    text: '${ad:formatMessage("form.ok")}',
                    click: function(){ $(this).dialog('close'); window.location.href = window.location.pathname + window.location.search;}
                }
            ],
            'width': 670,
            'resizable': false,
            'modal': true,
            'open': function() { 
                $('#ccgDialogOk').hide();
            }
        }).on('keypress', ':input', function(e){
            e.stopPropagation();
            if (e.keyCode === 13) {
                e.preventDefault();
                $('#ccgDialogSubmit:enabled').trigger('click');
            }
        });
    }, 'html');
}

$(function(){
    $('#bulk_btn').menubutton({
        'beforeclick': function(){
            return check();
        }
    });

    $('#export').menubutton()
    
    $('#creativeList').on('click', '.b-cpreview', function(e){
        e.preventDefault(); 
        UI.Util.togglePreview($(this).data('cid'));
    });
});

function submitCreativeExportForm(format, filter) {
    $('#errorsDiv').remove();
    var creativeIds = [];
    if (filter == 'selected') {
        var creativeIds = getAllSelectedIds();
        if (creativeIds.length == 0) {
            return;
        }
    }

    $('#hiddenCreativeIds').remove();
    var frm = $('#update');
    frm.unbind('submit.preventDoubleSubmit');
    var sHtml = '<div id="hiddenCreativeIds">';
    for (var i = 0; i < creativeIds.length; i++) {
        sHtml += '<input type="hidden" name="creativeIds" value="' + creativeIds[i] + '" />';
    }
    sHtml += '</div>';
    $(sHtml).appendTo(frm);
    frm.attr("action", "export.action?format=" + format).attr("method", "post").submit();
}

</script>

<s:if test="creatives != null && creatives.size > 0">

<ui:pages pageSize="${searchParams.pageSize}"
          total="${searchParams.total}"
          selectedNumber="${searchParams.page}"
          handler="goToPage"
          visiblePagesCount="10"
          displayHeader="true"/>

<form id="update" action="updates.action" method="POST">
<s:hidden name="searchParams.campaignId"/>
<s:hidden name="searchParams.sizeId"/>
<s:hidden name="searchParams.displayStatusId"/>
<s:hidden name="searchParams.orderBy"/>
<s:hidden name="searchParams.page"/>
<input type="hidden" id="changeType" name="changeType" value=""/>
<input type="hidden" id="declineReason" name="declineReason" value=""/>
<input type="hidden" name="advertiserId" value="${account.id}"/>
<input type="hidden" name="PWSToken" value="${sessionScope.PWSToken}"/>
<div id="hiddenCreativeIds"/>
<table class="dataViewSection">
    <tr class="controlsZone">
        <td>
            <table class="grouping">
                <tr>
                    <td class="withButtons">
                        <table class="fieldAndAccessories">
                            <tr>
                                <td class="withButton">
                                    <ui:button id="bulk_btn" message="ccg.bulk.menu" type="link"/>
                                    <ul id="bulk_menu" class="hide b-menu__bulk">
                                        <c:if test="${ad:isPermitted0('AdvertiserEntity.activate')}">
                                            <li>
                                                <ui:button message="form.activate" onclick="checkCreativeBatchActionAndProceed('ACTIVATE')" />
                                            </li>
                                        </c:if>
                                        <c:if test="${ad:isPermitted0('AdvertiserEntity.approve')}">
                                            <li>
                                                <ui:button message="form.approve" onclick="checkCreativeBatchActionAndProceed('APPROVE')" />
                                             </li>
                                        </c:if>
                                        <c:if test="${ad:isPermitted0('AdvertiserEntity.activate')}">
                                            <li>
                                                <ui:button message="form.deactivate" onclick="checkCreativeBatchActionAndProceed('INACTIVATE')" />
                                             </li>
                                        </c:if>
                                        <c:if test="${ad:isPermitted0('AdvertiserEntity.approve')}">
                                            <li>
                                                <ui:button message="form.decline" onclick="checkCreativeBatchActionAndProceed('DECLINE')" />
                                            </li>
                                        </c:if>
                                        <c:if test="${ad:isPermitted0('AdvertiserEntity.update')}">
                                            <li>
                                                <ui:button message="form.delete" onclick="checkCreativeBatchActionAndProceed('DELETE')" />
                                            </li>
                                        </c:if>
                                        <c:if test="${ad:isPermitted0('AdvertiserEntity.undelete')}">
                                            <li>
                                                <ui:button message="form.undelete" onclick="checkCreativeBatchActionAndProceed('UNDELETE')" />
                                            </li>
                                        </c:if>
                                        <li class="ui-menu-divider">&nbsp;</li>
                                        <li id="LINK_TO_DISPLAY">
                                            <ui:button message="creative.linkToDisplayCreativeGroups" onclick="checkCreativeBatchActionAndProceed('LINK_TO_DISPLAY')" />
                                        </li>
                                        <li id="LINK_TO_TEXT">
                                            <ui:button message="creative.linkToTextCreativeGroups" onclick="checkCreativeBatchActionAndProceed('LINK_TO_TEXT')" />
                                        </li>
                                        
                                        <c:if test="${ad:isPermitted0('AdvertiserEntity.update')}">
                                            <li>
                                                <ui:button message="creative.clickUrls" onclick="doClicks()" />
                                            </li>
                                        </c:if>
                                    </ul>
                                    <c:if test="${ad:isPermitted0('AdvertiserEntity.view')}">
                                        <div style="display: inline-block; margin-right: 10px">
                                            <ui:button id="export" message="creative.export" type="link"/>
                                            <ul class="hide">
                                                <li><a href="#" onclick="submitCreativeExportForm('XLSX', 'all'); return false;">
                                                    <fmt:message key="creative.export.all.excel"/>
                                                </a></li>
                                                <li><a href="#" onclick="submitCreativeExportForm('TAB', 'all'); return false;">
                                                    <fmt:message key="creative.export.all.tab"/>
                                                </a></li>
                                                <li><a href="#" onclick="submitCreativeExportForm('CSV', 'all'); return false;">
                                                    <fmt:message key="creative.export.all.csv"/>
                                                </a></li>
                                                <li><a href="#" onclick="submitCreativeExportForm('XLSX', 'selected'); return false;">
                                                    <fmt:message key="creative.export.selected.excel"/>
                                                </a></li>
                                                <li><a href="#" onclick="submitCreativeExportForm('TAB', 'selected'); return false;">
                                                    <fmt:message key="creative.export.selected.tab"/>
                                                </a></li>
                                                <li><a href="#" onclick="submitCreativeExportForm('CSV', 'selected'); return false;">
                                                    <fmt:message key="creative.export.selected.csv"/>
                                                </a></li>
                                            </ul>
                                        </div>
                                        <c:set var="exportEnabled" value="true"/>
                                    </c:if>
                                    <c:if test="${ad:isPermitted0('AdvertiserEntity.update')}">
                                        <ui:button message="creative.upload.bulkUpload" href="${_context}/creative/upload/main.action?advertiserId=${advertiserId}" />
                                    </c:if>
                                </td>
                            </tr>
                        </table>
                    </td>
                </tr>
            </table>
        </td>
    </tr>
    <tr class="bodyZone">
        <td>
            <table class="dataView" id="creativeList">
                <thead>
                    <tr><th width="1">
                            <input type="checkbox" onclick="toggleAllCreatives(this)"/>
                        </th><th>
                            <s:text name="creative.search.creative"/>
                        </th><th>
                            <s:text name="creative.preview"/>
                        </th><th>
                            <s:text name="creative.search.size"/>
                        </th><th>
                            <s:text name="creative.search.template"/>
                        </th>
                    </tr>
                </thead>
                <tbody>
                    <s:iterator value="creatives" var="creative" status="row">
                    <tr class="creativeRow"><td>
                            <input type="checkbox" name="${creative.textCreative ? 'textCreativeIds' : 'displayCreativeIds'}" value="${creative.id}"/>
                        </td><td>
                            <ui:displayStatus displayStatus="${creative.displayStatus}">
                                <a href="${_context}/creative/view.action?id=${creative.id}"><c:out value="${creative.name}"/></a>
                            </ui:displayStatus>
                        </td><td>
                            <c:set var="previewId" value="${creative.id}"/>
                            <a href="#" class="b-cpreview" data-cid="<c:out value="${previewId}"/>"><fmt:message key="creative.previewCreative"/></a>
                        </td><td>
                            <c:choose>
                                <c:when test="${ad:isPermitted0('CreativeSize.view')}">
                                    <a href="/admin/CreativeSize/view.action?id=${creative.sizeId}"><c:out value="${ad:localizeName(creative.sizeName)}"/></a>
                                </c:when>
                                <c:otherwise>
                                    ${ad:localizeName(creative.sizeName)}
                                </c:otherwise>
                            </c:choose>
                        </td><td>
                            <c:choose>
                                <c:when test="${ad:isPermitted0('Template.view')}">
                                    <a href="/admin/CreativeTemplate/view.action?id=${creative.templateId}"><c:out value="${ad:localizeName(creative.templateName)}"/></a>
                                </c:when>
                                <c:otherwise>
                                    ${ad:localizeName(creative.templateName)}
                                </c:otherwise>
                            </c:choose>
                        </td>
                    </tr>
                    <tr class="hide" id="preview_tr_<c:out value="${previewId}"/>">
                        <td colspan="5">
                            <ui:creativePreview creativeId="${creative.id}" noload="true"/>
                        </td>
                    </tr>
                    </s:iterator>
                </tbody>
            </table>
        </td>
    </tr>
</table>
</form>

<ui:pages pageSize="${searchParams.pageSize}"
          total="${searchParams.total}"
          selectedNumber="${searchParams.page}"
          handler="goToPage"
          visiblePagesCount="10"
          displayHeader="true"/>

</s:if>
<s:else>
    <c:if test="${ad:isPermitted0('AdvertiserEntity.update')}">
        <table class="dataViewSection">
            <tr><td class="withButton">
                <ui:button message="creative.upload.bulkUpload" href="${_context}/creative/upload/main.action?advertiserId=${advertiserId}" />
            </td></tr>
        </table>
    </c:if>
    <div class="wrapper">
        <fmt:message key="${emptyMessage}"/>
    </div>
</s:else>