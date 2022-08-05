<%@page contentType="text/html" %>
<%@page pageEncoding="UTF-8" %>
<%@ page import="com.foros.model.account.Account" %>
<%@ page import="com.foros.model.creative.Creative" %>

<%@ page import="java.util.Map" %>
<%@ page import="java.util.List" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ui" %>

<c:set var="creativeUrls">${_context}/creative/main.action</c:set>
<c:set var="creativePUStatus"><%=Creative.getDisplayStatusPA_User().getId()%></c:set>
<c:set var="creativePOStatus"><%=Creative.getDisplayStatusPA_Foros().getId()%></c:set>
<c:set var="creativeGroupUrls">${_context}/campaign/campaignsList.action</c:set>
<ad:requestContext var="advertiserContext"/>
<c:set var="accountBean" value="${advertiserContext.account}"/>
<c:set var="isAdvertiserEntityViewPermitted" value="${ad:isPermitted('AdvertiserEntity.view', accountBean)}"/>

<display:table name="result" class="dataView" uid="row" id="rowdata" requestURI="" varTotals="totals">
    <display:setProperty name="basic.empty.showtable" value="true"/>
    <display:setProperty name="basic.msg.empty_list_row">
        <tr>
            <td colspan="100">
                <fmt:message key="nothing.found.to.display"/>
            </td>
        </tr>
    </display:setProperty>
    <display:column titleKey="report.output.field.adv" style="text-align:left">
        <c:set var="displayStatusId" value="${rowdata.advertiserDisplayStatusId}" scope="request"/>
        <c:set var="displayStatus" value="<%=Account.getDisplayStatus(new Long(request.getAttribute("displayStatusId").toString()))%>"/>
        <ui:displayStatus displayStatus="${displayStatus}"  testFlag="${accountBean.testFlag}">
            <a class="preText" href="switch.action?advertiserId=${rowdata.advertiserId}" onclick="showAdvertiserDash(this, '${rowdata.advertiserId}','${ad:escapeJavaScriptInTag(rowdata.advertiserName)}'); return false;"><c:out value="${rowdata.advertiserName}"/></a>
        </ui:displayStatus>
    </display:column>

    <c:set var="creativeCount">${rowdata.creativesPendingUser}</c:set>
    <c:set var="creativeForosCount">${rowdata.creativesPendingForos}</c:set>
    <c:set var="creativeGroupCount">${rowdata.ccgsPendingUser}</c:set>
    <c:if test="${isAdvertiserEntityViewPermitted}">
        <display:column titleKey="report.account.alerts">
            <c:if test="${creativeCount != 0}">
                <fmt:message key="report.dashboard.creative">
                    <fmt:param value="<a href='${creativeUrls}?advertiserId=${rowdata.advertiserId}&displayStatusId=${creativePUStatus}'>${creativeCount}"/>
                    <fmt:param value="</a>"/></fmt:message> <br/>
            </c:if>
            <c:if test="${creativeForosCount != 0}">
                <fmt:message key="report.dashboard.creative.foros">
                    <fmt:param value="<a href='${creativeUrls}?advertiserId=${rowdata.advertiserId}&displayStatusId=${creativePOStatus}'>${creativeForosCount}"/>
                    <fmt:param value="</a>"/></fmt:message> <br/>
            </c:if>
            <c:if test="${creativeGroupCount != 0}">
                <fmt:message key="report.dashboard.creativeGroup">
                    <fmt:param value="<a href='${creativeGroupUrls}?advertiserId=${rowdata.advertiserId}'>${creativeGroupCount}"/>
                    <fmt:param value="</a>"/>
                </fmt:message>
            </c:if>
        </display:column>
    </c:if>

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
                <c:if test="${isAdvertiserEntityViewPermitted}">
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
                    <td>
                        <fmt:message key="notAvailable"/>
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
