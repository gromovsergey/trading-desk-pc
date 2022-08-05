<%@page contentType="text/html" %>
<%@page pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="/struts-tags" prefix="s"%>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ui" %>
<tiles:importAttribute ignore="true" scope="request" name="accountModuleName"/>

<script type="text/javascript">
    function showAdvertiserDash(link, advId, advName){
        $('#advertiserId').val(advId);
        $('#mainForm').attr("action", link.href);
        $('#mainForm').submit();
    }

    function loadAdvertiserDashboard() {
        var activity = $('#showZeroStat').prop('checked');
        $('#withActivityOnly').val(!activity);

        $('#advertiserDashboardDiv').html('<h3 class="level1">${ad:formatMessage("form.loading.resources")}</h3>');
        UI.Data.getUrl(
            "${_context}/dashboard/advertiserStats.action",
            "html",
            $('#mainForm').serializeArray(),
            function(data) {
                $('#advertiserDashboardDiv').html(data);
            }
        );
    }

</script>
<ad:requestContext var="advertiserContext"/>
<c:set var="showTable" value="${!showZeroStat or not empty result}"/>
<c:set var="accountBean" value="${advertiserContext.account}"/>



<jsp:include page="/admin/notices/noticesSnapshot.jsp" />


<ui:header>
    <ui:pageHeadingByTitle/>
    <c:if test="${ad:isPermitted('AgencyAdvertiserAccount.create', accountBean)}">
        <ui:button message="avertisers.createNew" href="${accountModuleName}/agencyAdvertiserNew.action${ad:accountParam('?agency.id',accountBean.id)}" />
    </c:if>
</ui:header>

<form id="mainForm" >
    <input type="hidden" name="advertiserId" id="advertiserId" value="${advertiserContext.accountId}"/>
    <c:if test="${!showTable}">
        <div class="wrapper">
            <span class="infos"><fmt:message key="campaign.dashboard.agency.empty"/></span>
        </div>
    </c:if>
    <table class="dataViewSection">
        <tr class="controlsZone">
            <td>
                <table class="grouping">
                    <tr>
                        <td class="withButtons">&nbsp;</td>
                        <c:if test="${showTable}">
                            <td class="filterZone">
                                <s:set var="fastChangeId"><s:property value="%{#request.fastChangeId}" escapeHtml="true"/></s:set>
                                <c:set var="accountId" value="${advertiserContext.accountId}"/>
                                <ui:daterange options="Y T WTD MTD QTD YTD LW LM LQ LY" fastChangeId="${fastChangeId}"
                                    onChange="loadAdvertiserDashboard();" timeZoneAccountId="${accountId}" fromDateFieldName="dateRange.begin" toDateFieldName="dateRange.end"/>
                            </td>
                        </c:if>
                    </tr>
                </table>
            </td>
        </tr>
    <c:if test="${showTable}">
        <tr class="bodyZone">
            <td>
                <div class="logicalBlock" id="advertiserDashboardDiv">
                    <%@ include file="advertiserStats.jsp" %>
                </div>
                <label class="withInput">
                    <s:checkbox id="showZeroStat" onclick="loadAdvertiserDashboard();"  name="showZeroStat" /><fmt:message key="report.display.advertiser.noactivity"/>
                    <input type="hidden" id="withActivityOnly" name="withActivityOnly" value="${!showZeroStat}" >
                </label>
            </td>
        </tr>
    </c:if>
    </table>
</form>






