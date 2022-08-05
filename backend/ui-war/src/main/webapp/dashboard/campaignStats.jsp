<%@page contentType="text/html" %>
<%@page pageEncoding="UTF-8" %>
<%@ page import="com.foros.model.account.Account" %>
<%@ page import="com.foros.model.campaign.Campaign" %>
<%@ page import="com.foros.model.creative.Creative" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.List" %>
<%@ page import="org.apache.commons.lang.StringEscapeUtils" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ui" %>
<%@ taglib uri="/struts-tags" prefix="s"%>

<script type="text/javascript">
<!--
$().ready(function(){
    var ccgsPendingUser = 0;
    var creativesPendingUser = 0;
    var creativesPendingForos = 0;
    <c:if test='${not empty total}'>
        ccgsPendingUser = ${total.ccgsPendingUser};
        creativesPendingUser = ${total.creativesPendingUser};
        creativesPendingForos = ${total.creativesPendingForos};
    </c:if>

    if (ccgsPendingUser > 0 || creativesPendingUser > 0 || creativesPendingForos > 0) {
        $('#accountAlerts').show();
    } else {
        $('#accountAlerts').hide();
    }

    if (ccgsPendingUser > 0 ){
        $('#campaignsToApproveCount').html(ccgsPendingUser)
        $('#campaignsToApprove').show();
    } else {
        $('#campaignsToApprove').hide();
    }

    if (creativesPendingUser > 0 ){
        $('#creativesToApproveCount').html(creativesPendingUser)
        $('#creativesToApprove').show();
    } else {
        $('#creativesToApprove').hide();
    }

    if (creativesPendingForos > 0 ){
        $('#creativesToApproveFOROSCount').html(creativesPendingForos)
        $('#creativesToApproveFOROS').show();
    } else {
        $('#creativesToApproveFOROS').hide();
    }
});
//-->
</script>

<c:if test="${exportEnabled == null}">
    <ad:requestContext var="advertiserContext"/>
    <c:set var="accountBean" value="${advertiserContext.advertiser}"/>
    <c:if test="${ad:isPermitted('BulkTextCampaignUpload.export', accountBean)}">
         <c:set var="exportEnabled" value="true"/>
    </c:if>
</c:if>

<c:if test="${ad:isPermitted('AdvertiserEntity.update', accountBean)}">
    <c:set var="bulkUpdateEnabled" value="true"/>
</c:if>

<display:table name="result" class="dataView" uid="row" id="rowdata" requestURI="" varTotals="totals">
    <display:setProperty name="basic.empty.showtable" value="true"/>
    <display:setProperty name="basic.msg.empty_list_row">
        <tr>
            <td colspan="100">
                <fmt:message key="nothing.found.to.display"/>
            </td>
        </tr>
    </display:setProperty>
    <c:if test="${exportEnabled || bulkUpdateEnabled}">
        <display:column title="<input type='checkbox' onclick='toggleAllGroups(this)'/>"
                        style="text-align:center;width:24px;">
            <input type="checkbox" name="selectedCampaignIds" value="${rowdata.campaignId}"/>
        </display:column>
    </c:if>

    <display:column titleKey="report.output.field.campaign" style="text-align:left">
        <c:set var="displayStatusId" value="${rowdata.campaignDisplayStatusId}" scope="request"/>
        <c:set var="displayStatus" value="<%=Campaign.getDisplayStatus(new Long(request.getAttribute("displayStatusId").toString()))%>"/>
        <ui:displayStatus displayStatus="${displayStatus}">
            <a href="${_context}/campaign/view.action?id=${rowdata.campaignId}"><ui:text subClass="entityName" text="${rowdata.campaignName}"/></a>
        </ui:displayStatus>
    </display:column>

    <display:column titleKey="report.output.field.impressions" class="number"
                    property="imps"
                    format="{0,number,integer}"
                    nulls="true" total="true"/>

    <display:column titleKey="report.output.field.clicks" class="number"
                    property="clicks"
                    format="{0,number,integer}"
                    nulls="true" total="true"/>

    <display:column titleKey="report.output.field.CTR" class="number"
                    property="ctr" 
                    format="{0,number, 0.00'%'}"
                    nulls="true" total="true"/>

    <c:if test="${showUniqueUsers}">
        <display:column titleKey="report.output.field.uniqueUsers" class="number"
                    property="uniqueUsers" 
                    format="{0,number,integer}"
                    nulls="true" total="true"/>
    </c:if>

    <c:if test="${ad:isInternal()}">
        <c:if test="${showCreditUsed}">
            <display:column titleKey="report.output.field.campaignCreditUsed" class="number"
                            value="${ad:formatCurrency(rowdata.campaignCreditUsed, accountBean.currency.currencyCode)}"
                            nulls="true" total="true"/>
        </c:if>

        <display:column titleKey="report.output.field.totalCost" class="number"
                        value="${ad:formatCurrency(rowdata.totalCost, accountBean.currency.currencyCode)}"
                        nulls="true" total="true"/>

        <c:if test="${showCreditUsed}">
            <display:column titleKey="report.output.field.totalValue" class="number"
                            value="${ad:formatCurrency(rowdata.totalValue, accountBean.currency.currencyCode)}"
                            nulls="true" total="true"/>
        </c:if>

        <display:column titleKey="report.output.field.eCPM" class="number"
                        value="${ad:formatCurrency(rowdata.ecpm, accountBean.currency.currencyCode)}"
                        nulls="true" total="true"/>
    </c:if>

    <c:if test="${accountBean.selfServiceFlag}">
        <display:column titleKey="report.output.field.selfServiceCost" class="number"
                        value="${ad:formatCurrency(rowdata.selfServiceCost, accountBean.currency.currencyCode)}"
                        nulls="true" total="true"/>
    </c:if>

    <display:footer>
        <c:if test="${not empty result}">
            <tr class="total">
                <c:if test="${exportEnabled || bulkUpdateEnabled}">
                <td></td>
                </c:if>
                <td class="totalText">
                    <fmt:message key="report.totals"/>
                </td>

                <td class="number">
                    <fmt:formatNumber value="${total.imps}"/>
                </td>
                <td class="number">
                    <fmt:formatNumber value="${total.clicks}"/>
                </td>
                <td class="number">
                    <fmt:formatNumber type="number" pattern="0.00'%'" value="${total.ctr}"/>
                </td>

                <c:if test="${showUniqueUsers}">
                    <td class="number">
                        <fmt:formatNumber value="${total.uniqueUsers}"/>
                    </td>
                </c:if>
                <c:if test="${ad:isInternal()}">
                    <c:if test="${showCreditUsed}">
                        <td class="number">
                            ${ad:formatCurrency(total.campaignCreditUsed, accountBean.currency.currencyCode)}
                        </td>
                    </c:if>

                    <td class="number">
                        ${ad:formatCurrency(total.totalCost, accountBean.currency.currencyCode)}
                    </td>

                    <c:if test="${showCreditUsed}">
                        <td class="number">
                            ${ad:formatCurrency(total.totalValue, accountBean.currency.currencyCode)}
                        </td>
                    </c:if>

                    <td class="number">
                        ${ad:formatCurrency(total.ecpm, accountBean.currency.currencyCode)}
                    </td>
                </c:if>

                <c:if test="${accountBean.selfServiceFlag}">
                    <td class="number">
                            ${ad:formatCurrency(total.selfServiceCost, accountBean.currency.currencyCode)}
                    </td>
                </c:if>

            </tr>
        </c:if> 
    </display:footer>

</display:table>
