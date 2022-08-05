<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="ad" uri="/ad/serverUI"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="display" uri="http://displaytag.sf.net/el"%>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>

<script type="text/javascript">

    function getBrowsersRootChannels() {
        var aData = [];
        $('#hiddenBrowsersRootChannelsData > input').each(function () {
            aData.push({'id': $(this).data('id'), 'name': $(this).data('name')});
        });
        return aData;
    }

    $(function() {

        function dtGetChildrenList(element) {
            if ($(element).data('children') != undefined
                    && $(element).data('children')) {
                var aData = $(element).data('children').split(';');
                var aChildren = [];
                for (i in aData) {
                    if (aData[i] != "") {
                        aChildren.push(parseInt(aData[i], 10));
                    }
                }
                return aChildren;
            } else {
                return false;
            }
        }

        function dtChildrenChange(element, isChecked, selectedChannels) {
            var aChildren = dtGetChildrenList(element);
            if (aChildren.length) {
                for (i in aChildren) {
                    var elSub = document.getElementById(selectedChannels
                            + aChildren[i]);
                    if (!elSub.disabled) {
                        if (isChecked) {
                            $(elSub).attr({
                                checked : 'checked'
                            });
                        } else {
                            $(elSub).removeAttr('checked');
                        }
                    }
                    dtChildrenChange(elSub, isChecked, selectedChannels);
                }
            }
        }

        function dtParentChange(element, isChecked, selectedChannels) {
            if ($(element).data('parent') != undefined
                    && $(element).data('parent')) {

                if ($('#applicationsSelectedChannels').length
                        && $('#applicationsSelectedChannels').val() == $(element)
                                .data('parent')) {
                    var sId = 'applicationsSelectedChannels';
                } else if ($('#browsersSelectedChannels').length
                        && $('#browsersSelectedChannels').val() == $(element)
                                .data('parent')) {
                    sId = 'browsersSelectedChannels';
                } else {
                    sId = selectedChannels + $(element).data('parent');
                }

                var elParent = document.getElementById(sId);
                if (elParent && !elParent.disabled) {
                    if (isChecked
                            && isAllChildrenChecked(elParent, selectedChannels)) {
                        $(elParent).attr({
                            checked : 'checked'
                        });
                    } else {
                        $(elParent).removeAttr('checked');
                    }
                }
                dtParentChange(elParent, isChecked, selectedChannels);
            }
        }

        function isAllChildrenChecked(element, selectedChannels) {
            var aChildren = dtGetChildrenList(element);
            for (i in aChildren) {
                var elSub = document.getElementById(selectedChannels
                        + aChildren[i]);
                if (!elSub.checked) {
                    return false;
                }
            }
            return true;
        }

        function isParent(id, element) {
            if ($(element).data('parent') == undefined || !$(element).data('parent')) {
                return false;
            }

            var parentId = $(element).data('parent');
            if (parentId == id) {
                return true;
            }

            return isParent(id, document.getElementById("browsersSelectedChannels_" + parentId));
        }

        function browsersSelectedChannelsChange(dialogObject) {
            if (!$(this).prop('checked')) {
                var dialogId = this.id.replace(/.*SelectedChannels_/, '');
                dialogId = dialogId.replace('SelectedChannels', '');
                if (window.confirm( $('#' + dialogId + '-dialog-confirm').text() )) {
                    dtChildrenChange(this, false, 'browsersSelectedChannels_');
                } else {
                    $(this).prop('checked', true);
                }
            }
            <c:if test="${param.isDeviceTargetingEditPage}">
                else {
                    dtChildrenChange(this, true, 'browsersSelectedChannels_');
                }
            </c:if>
        }
        
        $('#browsersSelectedChannels').on('change', browsersSelectedChannelsChange);
        var browsersRootChannels=getBrowsersRootChannels();
        for (i in browsersRootChannels) {
            $('#browsersSelectedChannels_' + browsersRootChannels[i].id).on('change', browsersSelectedChannelsChange);
        }
        
        $('#applicationsSelectedChannels').change(function(){
            if (!$(this).prop('checked')) {
                if (window.confirm( $('#applications-dialog-confirm').text() )) {
                    dtChildrenChange(this, false, 'applicationsSelectedChannels_');
                } else {
                    $(this).prop('checked', true);
                }
            }
            <c:if test="${param.isDeviceTargetingEditPage}">
                else {
                    dtChildrenChange(this, true, 'applicationsSelectedChannels_');
                }
            </c:if>
        });
        

        <c:if test="${not param.isDeviceTargetingEditPage}">
            $('[id*=browsersSelectedChannels_]').change(function(e) {
                if (this.checked) {
                    $('[id=browsersSelectedChannels]').prop('checked', true);
                    var browsersRootChannels=getBrowsersRootChannels();
                    for (i in browsersRootChannels) {
                        if (isParent(browsersRootChannels[i].id, this)) {
                            $('#browsersSelectedChannels_' + browsersRootChannels[i].id).prop('checked', true);
                            break;
                        }
                    }
                }
            });

            $('[id*=applicationsSelectedChannels_]').change(function(e) {
                if (this.checked) {
                    $('[id=applicationsSelectedChannels]').prop('checked', true);
                }
            });
        </c:if>
        
        <c:if test="${param.isDeviceTargetingEditPage}">
        
            $('[id*=browsersSelectedChannels]:checked').each(function(){
                dtParentChange(this, this.checked, 'browsersSelectedChannels_');
                dtChildrenChange(this, this.checked, 'browsersSelectedChannels_');
            });

            $('#deviceTargetingOptions [id*=browsersSelectedChannels_]').change(function(e) {
                dtChildrenChange(this, this.checked, 'browsersSelectedChannels_');
                dtParentChange(this, this.checked, 'browsersSelectedChannels_');
            });

            var browsersRootChannels=getBrowsersRootChannels();
            for (i in browsersRootChannels) {
                var id = '[id*=browsersSelectedChannels_' + browsersRootChannels[i].id + ']:checked'
                $(id).each(function(){
                    dtChildrenChange(this, this.checked, 'browsersSelectedChannels_');
                });
            }

            $('[id*=applicationsSelectedChannels]:checked').each(function(){
                dtParentChange(this, this.checked, 'applicationsSelectedChannels_');
                dtChildrenChange(this, this.checked, 'applicationsSelectedChannels_');
            });

            $('#deviceTargetingOptions [id*=applicationsSelectedChannels_]').change(function(e) {
                dtChildrenChange(this, this.checked, 'applicationsSelectedChannels_');
                dtParentChange(this, this.checked, 'applicationsSelectedChannels_');
            });

        
        </c:if>
    });
</script>
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
<ui:fieldGroup>

    <ui:field id="deviceTargetingOptions" labelForId="deviceTargeting">
        
        <c:if test="${not empty errors.deviceTargetingOptions}">
            <s:fielderror>
                <s:param value="%{#attr.errors.deviceTargetingOptions.trim()}"/>
            </s:fielderror>
        </c:if>
        
        <tiles:insertTemplate template="deviceTargetingTree.jsp">
            <tiles:putAttribute  name="prefix" value="browsers"/>
            <tiles:putAttribute name="tree" value="${deviceHelper.browsersTreeRoot}"/>
            <tiles:putAttribute name="rootChannels" value="${deviceHelper.browsersRootChannels}"/>
        </tiles:insertTemplate>

        <tiles:insertTemplate template="deviceTargetingTree.jsp">
            <tiles:putAttribute name="prefix" value="applications"/>
            <tiles:putAttribute name="tree" value="${deviceHelper.applicationsTreeRoot}"/>
        </tiles:insertTemplate>
    </ui:field>
</ui:fieldGroup>



