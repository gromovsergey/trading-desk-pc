<%@ page contentType="text/html" %>
<%@ page pageEncoding="UTF-8" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ui" %>

<c:set var="isInternal" value="${ad:isInternal()}"/>
<c:set var="isAdvertiser" value="${ad:isAdvertiser()}"/>
<c:set var="isAgency" value="${ad:isAgency()}"/>

<c:set var="isShowCampaignAssociations" value="${isInternal or isAdvertiser or isAgency}"/>

<c:if test="${isShowCampaignAssociations}">

<form id="associationsForm" style="display:none;">

<table class="dataViewSection">

    <tr class="controlsZone">
        <td>
            <table class="grouping">
                <tr>
                    <td>
                        <h2><fmt:message key="campaign.associations.title"/></h2>
                    </td>
                    <td class="filterZone">
                        <ui:daterange idSuffix="CCGS" options="Y T WTD MTD LW LM"
                                      fastChangeId="Y" onChange="loadCampaignAssociations();"
                                      timeZoneAccountId="${accountId}"/>
                    </td>
                </tr>
            </table>
        </td>
    </tr>
    <tr class="bodyZone">
        <td>
            <div class="logicalBlock" id="campaignAssociationsDiv">
                <%@ include file="campaignAssociationsDiv.jsp"%>
            </div>
        </td>
    </tr>
</table>
</form>

<script type="text/javascript">
    $(function() {
        loadCampaignAssociations(function() {
            if ($('#associationsTable').size() > 0) {
                $('#associationsForm').show();
            }
        });
    });
    function loadCampaignAssociations(callback) {
            $('#campaignAssociationsDiv')
                .html('<h3 class="level1">${ad:formatMessage("form.loading.resources")}</h3>')
                .load("${_context}/channel/campaignAssociations.action?id=${id}",
                    $('#associationsForm').serializeArray(), callback);
    }
</script>

</c:if>
