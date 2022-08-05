<%@ page contentType="text/html"%>
<%@ page pageEncoding="UTF-8"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="ad" uri="/ad/serverUI" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>

<script type="text/javascript">

    $().ready(function() {
        showTreeFilterByCampaign();
    });

     function showRequiredAlertTreeFilterByCampaign() {
        alert('${ad:formatMessage("errors.advertiserReport.filters.required")}');
    }
    
    function getLevelsTreeFilterByCampaign() {
        return ['campaignAdvertiserIds', 'campaignIds', 'groupIds', 'creativeIds'];
    } 
     
    function getSendingDataTreeFilterByCampaign() {
        return {
            entityFilterMessageKey : "report.conversionPixels.select.byCampaign"
        }; 
     }
    
    function showTreeFilterByCampaign() {
        var isAdvertiser = ${isAdvertiser};
        if (isAdvertiser) {
            getOptionsTreeFilterByCampaign(${accountId}, 'campaigns', 'treeFilterByCampaign', true);
        } else {
            getOptionsTreeFilterByCampaign(${accountId}, 'advertisersByCampaigns', 'treeFilterByCampaign', true);
        }
        $('#treeFilterByCampaign').show();
    }

</script>

<ui:treeFilter selectedIds="${byCampaignSelectedIds}" treeId="treeFilterByCampaign"/>

