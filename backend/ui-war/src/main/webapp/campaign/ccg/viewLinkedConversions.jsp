<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="/ad/serverUI" prefix="ad"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ui"%>

<script type="text/javascript">
    function loadConversionStatsData() {
        $('#conversions_preloader').show();
        var params = $('#conversionForm').serializeArray();
        $('#conversionsStatsDiv')
            .html('<h3 class="level1">${ad:formatMessage("form.loading.resources")}</h3>')
            .load('${_context}/campaign/group/conversionStats.action', params, function() {
                $('#conversions_preloader').hide();
            });
        
    }
</script>


<ui:header styleClass="level2">
    <h2>
        <fmt:message key="ccg.conversions" />
    </h2>
    <c:if test="${ad:isPermitted('Report.run', 'conversions')}">
        <c:set var="reporting" value="${_context}/reporting" />
        <ui:button message="reports.conversionsReport"
            href="${reporting}/conversions/options.action?groupIds=${id}" />
    </c:if>
    <div id="conversions_preloader" style="float: right" class="hide">
        <img src="/images/wait-animation-small.gif">
    </div>
</ui:header>
<table class="dataViewSection">
    <tr class="controlsZone">
        <td>
            <table class="grouping">
                <tr>
                    <td class="withButtons">&nbsp;</td>
                    <td class="filterZone">
                        <form id="conversionForm">
                            <ui:daterange idSuffix="Conversions"
                                options="TOT Y T WTD MTD QTD YTD LW LM LQ LY"
                                fastChangeId="TOT"
                                onChange="loadConversionStatsData();"
                                timeZoneAccountId="${requestContexts.advertiserContext.accountId}" />
                            <s:hidden name="id" />
                        </form>
                    </td>
                </tr>
            </table>
        </td>
    </tr>
    <tr class="bodyZone">
        <td>
            <div class="logicalBlock" id="conversionsStatsDiv">
                <%@ include file="conversionStats.jsp"%>
            </div>
        </td>
    </tr>
</table>
