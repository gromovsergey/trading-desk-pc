<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@ page import="com.foros.model.creative.Creative" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ui" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<tiles:importAttribute ignore="true" scope="request" name="contextModule"/>
<tiles:importAttribute ignore="true" scope="request" name="contextPath"/>

<ad:requestContext var="advertiserContext"/>

<script type="text/javascript">
    function showAdvertiserDash(action, accountId){
        if ($('#mainForm').length) {
            var activity = $('#showZeroStat').prop('checked');
            $("#advertiserId").val(accountId);
            $('#mainForm').attr("action", action);
            $('#mainForm').submit();
            return false;
        } else {
            return true;
        }
    }

    function setActivity(){
        var activity = $('#showZeroStat').prop('checked');
        loadCampaignDashboard();
    }

    function loadCampaignDashboard() {
        $('#campaignDashboardDiv').html('<h3 class="level1">${ad:formatMessage("form.loading.resources")}</h3>');
        UI.Data.getUrl(
            "${_context}/dashboard/campaignStats.action",
            "html",
            $('#mainForm').serializeArray(),
            function(data) {
                $('#campaignDashboardDiv').html(data);
            }
        );
    }

    function submitCampaignExportForm(format, tgtType) {
        $('.errors').remove();
        $(document).off('submit.preventDoubleSubmit');
        $('#mainForm').attr("action", "export.action?format=" + format + "&tgtType=" + tgtType).attr("method", "post").submit();
    }

    function getSelectedIds() {
        var campaignIds = [];
        $('[name=selectedCampaignIds]').each(function () {
            if (this.checked) {
                campaignIds.push(this.value);
            }
        });
        return campaignIds;
    }

    function submitCampaignBulkAction(action, url) {
        var campaignIds = getSelectedIds();
        if(campaignIds.length == 0 ) {
            return;
        }

        UI.Data.post('CampaignBatchStatusActionCheckXMLAction', {action: action, ids: campaignIds}, function(data) {
            var result = $('result', data).text();
            var confirmed = true;
            if(result != 'true') {
                confirmed = confirm('${ad:formatMessage('campaign.bulkchanges.batchAction.incomplete.confirm')}');
            }
            if(confirmed) {
                if (action == 'DELETE' && !confirm('<fmt:message key="confirmDelete"/>')) {
                    return;
                }

                var params = [];
                var campaignIds = getSelectedIds();
                $.each(campaignIds, function(i, id) {
                    params.push({name: "selectedCampaignIds", value: id});
                });
                params.push({name: "PWSToken", value: "${sessionScope.PWSToken}"});
                params.push({name: "advertiserId", value: "${advertiserContext.advertiserId}"});
                $.post(url, $.param(params, true), function(data) {
                    loadCampaignDashboard();
                }, 'html');
            }
        });
    }

    function toggleAllGroups(header) {
        $('[name=selectedCampaignIds]').prop({checked : header.checked});
    }

    $().ready(function() {
        $('#bulkchanges').menubutton();
        $('#export').menubutton();
    });
</script>

<jsp:include page="/admin/notices/noticesSnapshot.jsp" />

<c:set var="creativeUrls">${contextModule}/creative/main.action</c:set>
<c:set var="creativePUStatus"><%=Creative.getDisplayStatusPA_User().getId()%></c:set>
<c:set var="creativePOStatus"><%=Creative.getDisplayStatusPA_Foros().getId()%></c:set>
<c:set var="creativeGroupUrls">${contextModule}/campaign/campaignsList.action</c:set>

<ui:section cssStyle="display:none" titleKey="report.account.alerts" id="accountAlerts" >
    <ui:fieldGroup>
        <ui:field id="creativesToApprove">
            <fmt:message key="report.dashboard.creative">
                <fmt:param value="<a href='${creativeUrls}?displayStatusId=${creativePUStatus}${ad:accountParam('&advertiserId',advertiserContext.advertiserId)}'><span id='creativesToApproveCount'></span>"/>
                <fmt:param value="</a>"/>
            </fmt:message>
        </ui:field>
        <ui:field id="creativesToApproveFOROS">
            <fmt:message key="report.dashboard.creative.foros">
                <fmt:param value="<a href='${creativeUrls}?displayStatusId=${creativePOStatus}${ad:accountParam('&advertiserId',advertiserContext.advertiserId)}'><span id='creativesToApproveFOROSCount'></span>"/>
                <fmt:param value="</a>"/>
            </fmt:message>
        </ui:field>
        <ui:field id="campaignsToApprove">
            <fmt:message key="report.dashboard.creativeGroup">
                <fmt:param value="<a href='${creativeGroupUrls}${ad:accountParam('?advertiserId',advertiserContext.advertiserId)}'><span id='campaignsToApproveCount'></span> "/>
                <fmt:param value="</a>"/>
            </fmt:message>
        </ui:field>
    </ui:fieldGroup>
</ui:section>

<ui:header>
    <c:if test="${advertiserContext.agencyContext}">
        <h1><fmt:message key="report.input.field.advertiser" />: <c:out value="${account.name}"/></h1>
        <a href="#" onclick="return showAdvertiserDash('advertisers.action','${advertiserContext.accountId}');" href="#" class="button">
            <fmt:message key="report.showall" />
        </a>
    </c:if>
</ui:header>

<c:set var="accountBean" value="${advertiserContext.advertiser}"/>
<c:set var="accountIdParam" value="${ad:accountParam('?advertiserId',accountBean.id)}"/>
<c:set var="addParam" value="?"/>
<c:if test="${not empty accountIdParam}">
    <c:set var="addParam" value="&"/>
</c:if>
<c:set var="displayCampaignCreateLink" value="${contextModule}/campaign/new.action${accountIdParam}${addParam}type=D"/>
<c:set var="textCampaignCreateLink" value="${contextModule}/campaign/new.action${accountIdParam}${addParam}type=T"/>

<ui:header>
    <ui:pageHeadingByTitle/>
    <c:if test="${ad:isPermitted('BulkTextCampaignUpload.upload', accountBean)}">
        <ui:button message="TextAd.upload.bulkUpload" href="${contextModule}/campaign/upload/main.action${ad:accountParam('?advertiserId',accountBean.id)}" />
    </c:if>
    <c:if test="${ad:isPermitted('AdvertiserEntity.create', accountBean)}">
        <c:if test="${ad:isPermitted('AdvertiserEntity.accessDisplayAd', accountBean)}">
            <ui:button message="campaign.createNewDisplay" href="${displayCampaignCreateLink}" />
        </c:if>
        <c:if test="${ad:isPermitted('AdvertiserEntity.accessTextAd', accountBean)}">
            <ui:button message="campaign.createNewText" href="${textCampaignCreateLink}" />
        </c:if>
    </c:if>
</ui:header>

<s:actionerror/>

<form id="mainForm">
    <input type="hidden" name="advertiserId" id="advertiserId" value="${advertiserContext.advertiserId}"/>
<c:choose>
    <c:when test="${showZeroStat and empty result}">
        <div class="wrapper">
            <span class="infos"><fmt:message key="campaign.dashboard.avertiser.empty"/></span>
        </div>
    </c:when>
    <c:otherwise>
        <table class="dataViewSection">
            <tr class="controlsZone">
                <td>
                    <table class="grouping">
                        <tr>
                            <td class="withButtons">
                                <c:if test="${ad:isPermitted('AdvertiserEntity.update', accountBean)}">
                                    <div style="display: inline-block; margin-right: 10px">
                                        <ui:button id="bulkchanges" message="campaign.bulkchanges" type="link"/>
                                        <ul class="hide">
                                            <li><a href="#" onclick="submitCampaignBulkAction('ACTIVATE', 'bulkactivate.action'); return false;">
                                                <fmt:message key="campaign.bulkchanges.activate"/>
                                            </a></li>
                                            <li><a href="#" onclick="submitCampaignBulkAction('INACTIVATE', 'bulkinactivate.action'); return false;">
                                                <fmt:message key="campaign.bulkchanges.deactivate"/>
                                            </a></li>
                                            <li><a href="#" onclick="submitCampaignBulkAction('DELETE', 'bulkdelete.action'); return false;">
                                                <fmt:message key="campaign.bulkchanges.delete"/>
                                            </a></li>
                                        </ul>
                                    </div>
                                </c:if>
                                <c:if test="${ad:isPermitted('BulkTextCampaignUpload.export', accountBean)}">
                                    <div style="display: inline-block; margin-right: 10px">
                                    <ui:button id="export" message="campaign.export" type="link"/>
                                    <ul class="hide">
                                        <c:if test="${account.accountType.allowTextKeywordAdvertisingFlag}">
                                            <li><a href="#" onclick="submitCampaignExportForm('CSV', 'KEYWORD'); return false;">
                                                <fmt:message key="campaign.export.TextKT.CSV"/>
                                            </a></li>
                                            <li><a href="#" onclick="submitCampaignExportForm('TAB', 'KEYWORD'); return false;">
                                                <fmt:message key="campaign.export.TextKT.TAB"/>
                                            </a></li>
                                            <li><a href="#" onclick="submitCampaignExportForm('XLSX', 'KEYWORD'); return false;">
                                                <fmt:message key="campaign.export.TextKT.XLSX"/>
                                            </a></li>
                                        </c:if>
                                        <c:if test="${account.accountType.allowTextChannelAdvertisingFlag}">
                                            <li><a href="#" onclick="submitCampaignExportForm('CSV', 'CHANNEL'); return false;">
                                                <fmt:message key="campaign.export.TextCT.CSV"/>
                                            </a></li>
                                            <li><a href="#" onclick="submitCampaignExportForm('TAB', 'CHANNEL'); return false;">
                                                <fmt:message key="campaign.export.TextCT.TAB"/>
                                            </a></li>
                                            <li><a href="#" onclick="submitCampaignExportForm('XLSX', 'CHANNEL'); return false;">
                                                <fmt:message key="campaign.export.TextCT.XLSX"/>
                                            </a></li>
                                        </c:if>
                                    </ul>
                                    </div>
                                    <c:set var="exportEnabled" value="true"/>
                                </c:if>
                                &nbsp;
                            </td>
                            <td class="filterZone">
                                <s:set var="fastChangeId"><s:property value="%{#request.fastChangeId}" escapeHtml="true"/></s:set>
                                <c:set var="accountId" value="${advertiserContext.accountId}"/>
                                <ui:daterange options="Y T WTD MTD QTD YTD LW LM LQ LY" fastChangeId="${fastChangeId}"
                                    onChange="loadCampaignDashboard();" timeZoneAccountId="${accountId}" fromDateFieldName="dateRange.begin" toDateFieldName="dateRange.end"/>
                            </td>
                        </tr>
                    </table>
                </td>
            </tr>
            <tr class="bodyZone">
                <td>
                    <div class="logicalBlock" id="campaignDashboardDiv">
                        <%@ include file="campaignStats.jsp" %>
                    </div>
                    <label class="withInput">
                        <s:checkbox id="showZeroStat" onclick="setActivity();"  name="showZeroStat" /><fmt:message key="report.display.campaign.noactivity"/>
                    </label>
                </td>
            </tr>
        </table>
    </c:otherwise>
</c:choose>

</form>
