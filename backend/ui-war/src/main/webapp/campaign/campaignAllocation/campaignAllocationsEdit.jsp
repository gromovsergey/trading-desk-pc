<%@page contentType="text/html" %>
<%@page pageEncoding="UTF-8" %>
<%@ page import="com.foros.session.campaignAllocation.CampaignAllocationsValidations" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ui" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<script type="text/javascript">
    function addAllocation() {
        var callback = function(responseText) {
            $('#allocationsTable tbody:eq(0)').empty();
            $('#allocationsTable tbody:eq(0)').append(responseText);
            checkAllocations();
        };
        loadRow('${_context}/campaign/allocation/add.action', callback);
    }

    function deleteAllocation(index) {
        $('#currentAllocationIndexId').val(index);
        var callback = function(responseText) {
            $('#allocationsTable tbody:eq(0)').empty();
            $('#allocationsTable tbody:eq(0)').append(responseText);
            checkAllocations();
        };
        loadRow('${_context}/campaign/allocation/delete.action', callback);
    }

    function changeOpportunity(index) {
        $('#currentAllocationIndexId').val(index);
        var callback = function(responseText) {
            $('#allocation' + index).replaceWith(responseText);
        };
        $('#allocation' + index).prop({disabled : true});
        loadRow('${_context}/campaign/allocation/changeOpportunity.action', callback);
    }

    function loadRow(action, callback) {
        UI.Data.getUrl(action, 'html', UI.Data.serializeForm('#allocationForm'), callback);
    }

    function checkAllocations() {
        if ($('tr[id^=allocation]').length > 0) {
            $('#noAllocation').hide();
            $('#tableHeader').show();
        } else {
            $('#noAllocation').show();
            $('#tableHeader').hide();
        }
        if ($('tr[id^=allocation]').length < <%=CampaignAllocationsValidations.MAX_ALLOCATIONS_COUNT%>) {
            $('#addAllocationButton').show();
        } else {
            $('#addAllocationButton').hide();
        }
    }

    $(document).ready(function () {
        checkAllocations();
    });
</script>

<ui:pageHeadingByTitle/>

<s:form action="update" id="allocationForm">
    <s:hidden name="id"/>
    <s:hidden name="campaign.id"/>
    <s:hidden name="campaign.version"/>
    <s:hidden name="campaign.name"/>
    <s:hidden name="campaign.account.id"/>
    <s:hidden name="currentAllocationIndex" id="currentAllocationIndexId"/>

    <ui:errorsBlock>
        <s:actionerror/>
        <s:fielderror><s:param value="'version'"/></s:fielderror>
        <s:fielderror><s:param value="'campaignAllocations.size'"/></s:fielderror>
    </ui:errorsBlock>

    <ui:fieldGroup>
        <ui:field>
            <ui:text textKey="campaignAllocation.emptyList" id="noAllocation"/>
            <table id="allocationsTable" class="dataView">
                <thead>
                <tr id="tableHeader">
                    <th><s:text name="campaignAllocation.id"/></th>
                    <th><s:text name="campaignAllocation.activity"/></th>
                    <th><s:text name="campaignAllocation.amount"/></th>
                    <th><s:text name="campaignAllocation.spentAmount"/></th>
                    <th><s:text name="campaignAllocation.availableAmount"/></th>
                    <th><s:text name="campaignAllocation.unallocatedAmount"/></th>
                    <th><s:text name="campaignAllocation.allocationAmount"/></th>
                    <th><s:text name="campaignAllocation.utilizedAmount"/></th>
                    <th><s:text name="campaignAllocation.order"/></th>
                </tr>
                </thead>
                <tbody>
                    <%@include file="allCampaignAllocations.jsp" %>
                </tbody>
            </table>
        </ui:field>
        <ui:field>
            <ui:button id="addAllocationButton" onclick="addAllocation(); return false;"
                       message="campaignAllocation.add"/>
        </ui:field>
    </ui:fieldGroup>

    <div class="wrapper">
        <ui:button message="form.save" type="submit"/>
        <ui:button message="form.cancel"
                   href="${_context}/campaign/view.action?id=${id}#campaignAllocation" type="button"/>
    </div>
</s:form>
