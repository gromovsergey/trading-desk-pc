<%@ page contentType="text/html"%>
<%@ page pageEncoding="UTF-8"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="ad" uri="/ad/serverUI"%>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="s" uri="/struts-tags" %>

<ad:requestContext var="advertiserContext" />

<table>
    <tr>
        <td><fmt:message key="creative.linkToCreativeGroups.text"/>
        </td>
    </tr>
    <tr>
        <td class="ajax_loader_container">
            <ui:treeFilter selectedIds="${selectedIds}" treeId="treeFilterByCampaign" />
            <div id="waiter" class="ajax_loader hide" />
        </td>
    </tr>
</table>


<script type="text/javascript">

    function showRequiredAlertTreeFilterByCampaign() {
        alert('${ad:formatMessage("errors.advertiserReport.filters.required")}');
    }
    
    function getLevelsTreeFilterByCampaign() {
        return ['campaignIds', 'groupIds'];
    } 
    
    function lastFilterExpandedStatusTreeFilterByCampaign() {
        return true;
    }
    
    function getSendingDataTreeFilterByCampaign() {
        return {
            showDisplay : "${display}"
        }; 
    }
    

    $().ready(function() {
        $('#waiter').show();
        getOptionsTreeFilterByCampaign(${advertiserContext.advertiserId}, 'campaigns', 'treeFilterByCampaign', true, function() {
            $("#treeRootCheckboxTreeFilterByCampaign").removeAttr("checked");
            switchAll($("#treeRootCheckboxTreeFilterByCampaign"));
            $(".collapseButt").remove();
            $('#waiter').hide();
        });
        
        $('#treeFilterByCampaign').show();
    });
</script>