<%@page contentType="text/html" %>
<%@page pageEncoding="UTF-8" %>

<%@ taglib uri="/struts-tags" prefix="s" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ui" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core"  prefix="c" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>

<script type="text/javascript">
    function deviceFormSubmit(){
        $('#setDiv input:checked').each(function() {
            $('<input>').attr({type: 'hidden', id: 'setIds', name: 'setIds'}).val($(this).val()).appendTo('#device');
        });

        $('#addDiv input:checked').each(function() {
            $('<input>').attr({type: 'hidden', id: 'addIds', name: 'addIds'}).val($(this).val()).appendTo('#device');
        });

        $('#removeDiv input:checked').each(function() {
            $('<input>').attr({type: 'hidden', id: 'removeIds', name: 'removeIds'}).val($(this).val()).appendTo('#device');
        });
    }

    function dtChildrenChange(jqThis, isChecked) {
        var aChildren = jqThis.data('children').split(';') || [],
        jqTabParent = jqThis.closest('.ui-tabs-panel'),
        sIdPrefix = jqThis.attr('id').split('_')[0]+'_';

        for (i in aChildren) {
            if (aChildren[i]) {
                var jqChild = jqTabParent.find('[id*='+sIdPrefix+aChildren[i]+']:eq(0)');
                if (!jqChild.prop('disabled')) {
                    jqChild.prop('checked',isChecked);
                }
                dtChildrenChange(jqChild, isChecked);
            }
        }
    }

    function dtParentChange(jqThis, isChecked) {
        if (jqThis.data('parent') !== undefined && jqThis.data('parent')) {

            if ($('#browsersSelectedChannels').length && $('#browsersSelectedChannels').val() == jqThis.data('parent')) {
                var sId = 'browsersSelectedChannels';
            } else if ($('#applicationsSelectedChannels').length && $('#applicationsSelectedChannels').val() == jqThis.data('parent')) {
                sId = 'applicationsSelectedChannels';
            } else {
                sId = jqThis.attr('id').split('_')[0]+'_' + jqThis.data('parent');
            }

            var jqTabParent = jqThis.closest('.ui-tabs-panel'),
            jqParent = jqTabParent.find('[id*='+sId+']:eq(0)');

            if (jqParent.length && jqParent.prop('disabled') === false) {
                if (jqTabParent.attr('id') === 'removeDiv') {
                    jqParent.prop('checked', isChecked);
                } else {
                    jqParent.prop('checked', isChecked && isAllChildrenChecked(jqParent));
                }
            }
            dtParentChange(jqParent, isChecked);
        }
    }

    function isAllChildrenChecked(jqParent) {
        var aChildren = $(jqParent).data('children').split(';') || [],
        jqTabParent = jqParent.closest('.ui-tabs-panel'),
        sIdPrefix = jqParent.attr('id').split('_')[0]+'_';

        for (i in aChildren) {
            if (aChildren[i]) {
                if (jqTabParent.find('[id*='+sIdPrefix+aChildren[i]+']').eq(0).prop('checked') === false ) {
                    return false;
                }
            }
        }
        return true;
    }

    function isBrowsersRootChannel(id) {
        var result = false;
        $('#hiddenBrowsersRootChannelsData > input').each(function () {
            if ($(this).data('id') == id) {
                result = true;
            }
        });
        return result;
    }

    $(function() {
        $('#stButtons').tabs({
            select: function( event, ui ) {
                $('#editModeId').val($(ui.tab).parent('li').data('id'));
            }
        });
        $("#stButtons").tabs("select", <s:property value="%{editMode.ordinal()}"/>);
        $("li[data-id='Set'] > a, li[data-id='Add'] > a, li[data-id='Remove'] > a").click(function() {
            $('#error-block').remove();
        });

        $('#setDiv [id*=SelectedChannels], #addDiv [id*=SelectedChannels], #removeDiv [id*=SelectedChannels]').change(function() {
            $('#error-block').remove();

            var dialogId = this.id.replace(/.*SelectedChannels_/, '');
            dialogId = dialogId.replace('SelectedChannels', '');
            if ($(this).attr('id').indexOf('_') === -1 || isBrowsersRootChannel(dialogId)) {
                if (!$(this).prop('checked')) {
                    if (window.confirm( $('#' + dialogId + '-dialog-confirm').text() )) {
                        dtChildrenChange($(this), false);
                        dtParentChange($(this), false);
                    } else {
                        $(this).prop('checked', true);
                    }
                } else {
                    dtChildrenChange($(this), true);
                    dtParentChange($(this), true);
                }
            } else {
                dtChildrenChange($(this), $(this).prop('checked'));
                dtParentChange($(this), $(this).prop('checked'));
            }
        });
    });
</script>

<s:form action="save" id="device">
    <s:hidden name="campaignId"/>
    <s:hidden name="editMode" id="editModeId"/>

    <%@ include file="bulkGroupErrors.jsp"%>

    <div id="stButtons">
        <ul>
            <li data-id="Set"><ui:button message="ccg.bulk.device.setTo" href="#setDiv"/></li>
            <li data-id="Add"><ui:button message="ccg.bulk.device.add" href="#addDiv"/></li>
            <li data-id="Remove"><ui:button message="ccg.bulk.device.remove" href="#removeDiv"/></li>
        </ul>

        <div id="hiddenBrowsersRootChannelsData" class="hide">
            <c:forEach items="${deviceHelper.browsersRootChannels}" var="idName">
                <input type="hidden" data-id="${idName.id}" data-name="${idName.name}">
            </c:forEach>
        </div>
        <div id="applications-dialog-confirm" class="hide"><fmt:message key="ccg.devices.applications.confirm" /></div>
        <div id="browsers-dialog-confirm" class="hide"><fmt:message key="ccg.devices.browsers.confirm" /></div>
        <c:forEach items="${deviceHelper.browsersRootChannels}" var="idName">
            <div id="${idName.id}-dialog-confirm" class="hide"><fmt:message key="ccg.devices.${idName.name}.confirm" /></div>
        </c:forEach>

        <div id="setDiv" style="overflow: scroll;">
            <ui:fieldGroup>
                <ui:field id="deviceTargetingOptions" labelForId="deviceTargeting">
                    <c:if test="${not empty errors.deviceTargetingOptions}">
                        <s:fielderror>
                            <s:param value="%{#attr.errors.deviceTargetingOptions.trim()}"/>
                        </s:fielderror>
                    </c:if>
                    <tiles:insertTemplate template="/admin/accountType/deviceTargetingTree.jsp">
                        <tiles:putAttribute  name="prefix" value="browsers"/>
                        <tiles:putAttribute name="tree" value="${deviceHelper.browsersTreeRoot}"/>
                        <tiles:putAttribute name="rootChannels" value="${deviceHelper.browsersRootChannels}"/>
                    </tiles:insertTemplate>
                    <tiles:insertTemplate template="/admin/accountType/deviceTargetingTree.jsp">
                        <tiles:putAttribute name="prefix" value="applications"/>
                        <tiles:putAttribute name="tree" value="${deviceHelper.applicationsTreeRoot}"/>
                        <tiles:putAttribute name="rootChannels" value="${deviceHelper.browsersRootChannels}"/>
                    </tiles:insertTemplate>
                </ui:field>
            </ui:fieldGroup>
        </div>
        <div id="addDiv" style="overflow: scroll;">
            <ui:fieldGroup>
                <ui:field id="deviceTargetingOptions" labelForId="deviceTargeting">
                    <c:if test="${not empty errors.deviceTargetingOptions}">
                        <s:fielderror>
                            <s:param value="%{#attr.errors.deviceTargetingOptions.trim()}"/>
                        </s:fielderror>
                    </c:if>
                    <tiles:insertTemplate template="/admin/accountType/deviceTargetingTree.jsp">
                        <tiles:putAttribute  name="prefix" value="browsers"/>
                        <tiles:putAttribute name="tree" value="${deviceHelper.browsersTreeRoot}"/>
                        <tiles:putAttribute name="rootChannels" value="${deviceHelper.browsersRootChannels}"/>
                    </tiles:insertTemplate>
                    <tiles:insertTemplate template="/admin/accountType/deviceTargetingTree.jsp">
                        <tiles:putAttribute name="prefix" value="applications"/>
                        <tiles:putAttribute name="tree" value="${deviceHelper.applicationsTreeRoot}"/>
                        <tiles:putAttribute name="rootChannels" value="${deviceHelper.browsersRootChannels}"/>
                    </tiles:insertTemplate>
                </ui:field>
            </ui:fieldGroup>
        </div>
        <div id="removeDiv" style="overflow: scroll;">
            <ui:fieldGroup>
                <ui:field id="deviceTargetingOptions" labelForId="deviceTargeting">
                    <c:if test="${not empty errors.deviceTargetingOptions}">
                        <s:fielderror>
                            <s:param value="%{#attr.errors.deviceTargetingOptions.trim()}"/>
                        </s:fielderror>
                    </c:if>
                    <tiles:insertTemplate template="/admin/accountType/deviceTargetingTree.jsp">
                        <tiles:putAttribute  name="prefix" value="browsers"/>
                        <tiles:putAttribute name="tree" value="${deviceHelper.browsersTreeRoot}"/>
                        <tiles:putAttribute name="rootChannels" value="${deviceHelper.browsersRootChannels}"/>
                    </tiles:insertTemplate>
                    <tiles:insertTemplate template="/admin/accountType/deviceTargetingTree.jsp">
                        <tiles:putAttribute name="prefix" value="applications"/>
                        <tiles:putAttribute name="tree" value="${deviceHelper.applicationsTreeRoot}"/>
                        <tiles:putAttribute name="rootChannels" value="${deviceHelper.browsersRootChannels}"/>
                    </tiles:insertTemplate>
                </ui:field>
            </ui:fieldGroup>
        </div>
    </div>
</s:form>
