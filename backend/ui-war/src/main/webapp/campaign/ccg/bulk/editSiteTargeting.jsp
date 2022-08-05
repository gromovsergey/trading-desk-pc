<%@page contentType="text/html" %>
<%@page pageEncoding="UTF-8" %>

<%@ taglib uri="/struts-tags" prefix="s" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ui" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core"  prefix="c" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>

<script type="text/javascript">
    function getSites(request, response) {
        var availableSites = [
            <ad:commaWriter var="site" items="${availableSites}">{label : '${ad:escapeJavaScript(site.name)}', value: ${site.id}}</ad:commaWriter>
        ],
        regexp  = new RegExp(UI.Util.escapeRegexp(request.term), "i"),
        res     = $.grep(availableSites, function(opt) {
            return opt.label.search(regexp) >= 0
        });
        response(res);
    }

    $(function(){
        $('#stButtons').tabs({
            activate: function( event, ui ) {
                $('#editModeId').val(ui.newTab.data('id'));
            },
            active: <s:property value="%{editMode.ordinal()}"/>
        });
    });
</script>
<s:form action="save" id="siteTargeting">

<s:hidden name="campaignId"/>
<s:hidden name="editMode" id="editModeId"/>

<%@ include file="bulkGroupErrors.jsp"%>

<div id="stButtons">
    <ul>
        <li data-id="Set"><ui:button message="ccg.bulk.setTo" href="#setDiv" /></li>
        <li data-id="Add"><ui:button message="ccg.bulk.add.sites" href="#addDiv" /></li>
        <li data-id="Remove"><ui:button message="ccg.bulk.remove.sites" href="#removeDiv" /></li>
    </ul>

    <div id="setDiv">
        <%@ include file="/campaign/ccg/editSitesTargeting.jsp"%>
    </div>

    <div id="addDiv">
        <s:fielderror><s:param value="'addSites'"/></s:fielderror>
        <ui:field labelKey="ccg.bulk.add.sites.label">
            <ui:autocomplete
                id="addIds"
                source="getSites"
                selectedItems="${addSites}"
                selectedNameKey="name"
                selectedValueKey="id"
                cssClass="bigLengthText"
                isMultiSelect="true"
                minLength="1" />
        </ui:field>
    </div>
    <div id="removeDiv">
        <s:fielderror><s:param value="'removeSites'"/></s:fielderror>
        <ui:field labelKey="ccg.bulk.remove.sites.label">
            <ui:autocomplete
            id="removeIds"
            source="getSites"
            selectedItems="${removeSites}"
            selectedNameKey="name"
            selectedValueKey="id"
            cssClass="bigLengthText"
            isMultiSelect="true"
            minLength="1" />
        </ui:field>
    </div>
</div>

</s:form>
