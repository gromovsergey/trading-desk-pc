<%@page contentType="text/html" %>
<%@page pageEncoding="UTF-8" %>
<%@ page import="com.foros.model.site.Site" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ui" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>

<ad:requestContext var="publisherContext"/>
<c:set var="accountBean" value="${publisherContext.account}"/>

<table class="dataView collapsing">
    <thead>
        <tr>
            <th colspan="2"><fmt:message key="site.sites"/> / <fmt:message key="site.tags"/></th>
            <th><fmt:message key="site.url"/> / <fmt:message key="site.tag.size"/></th>
            <th><fmt:message key="report.output.field.requests"/></th>
            <th><fmt:message key="report.output.field.impressions"/></th>
            <c:if test="${availableCreditedImps}">
                <th>
                    <div class="textWithHint">
                        <fmt:message key="report.output.field.creditedImpressions"/>
                        <ui:hint><fmt:message key="publisherAccount.creditedImpressions.tip"/></ui:hint>
                    </div>
                </th>
            </c:if>
            <c:if test="${clicksDataAvailable}">
                <th><fmt:message key="report.output.field.clicks"/></th>
                <th><fmt:message key="report.output.field.CTR"/></th>
            </c:if>
            <th><fmt:message key="report.output.field.eCPM"/></th>
            <th><fmt:message key="report.output.field.revenue"/></th>
        </tr>
    </thead>
    <c:if test="${empty result}">
        <td colspan="100">
                <fmt:message key="nothing.found.to.display"/>
        </td>
    </c:if>
    
    <c:forEach var="rowdata" items="${result}">
        <tbody class="parent">
            <tr>
                <td class="withCollapsingButton">
                    <c:if test="${rowdata.tagExist}">
                        <a href="#" onclick="showHide(${rowdata.siteId}, ${availableCreditedImps}, this); return false;"><fmt:message key="show.tags"/></a>
                    </c:if>
                </td>
                <td>
                    <c:set var="displayStatusId" value="${rowdata.siteDisplayStatusId}" scope="request"/>
                    <c:set var="displayStatus" value="<%=Site.getDisplayStatus(new Long(request.getAttribute("displayStatusId").toString()))%>"/>
                    <ui:displayStatus displayStatus="${displayStatus}">
                        <a href="${_context}/site/view.action?id=${rowdata.siteId}" style="font-weight:bold;">
                            <c:out value="${rowdata.siteName}"/>
                        </a>
                        <c:if test="${rowdata.creativesToApprove > 0 and accountBean.accountType.advExclusionFlag}">
                            <div class="currPendingApproval">
                                <fmt:message key="report.dashboard.creative">
                                    <fmt:param value="<a href='${_context}/site/creativesApproval/main.action?site.id=${rowdata.siteId}&showPending=true' style='font-weight:bold;'>${rowdata.creativesToApprove}"/>
                                    <fmt:param value="</a>"/>
                                </fmt:message>
                            </div>
                        </c:if>
                    </ui:displayStatus>
                </td>
                <td>
                    <a href="<c:out value='${rowdata.siteUrl}'/>" target="blank">
                        <c:out value='${rowdata.siteUrl}'/>
                    </a>
                </td>
                <td style="text-align:right; vertical-align:top;">
                    <fmt:formatNumber value="${rowdata.requests}"/>
                </td>
                <td style="text-align:right; vertical-align:top;">
                    <fmt:formatNumber value="${rowdata.imps}"/>
                </td>
                <c:if test="${availableCreditedImps}">
                    <td style="text-align:right; vertical-align:top;">
                        <fmt:formatNumber value="${rowdata.creditedImps}"/>
                    </td>
                </c:if>
                <c:if test="${clicksDataAvailable}">
                    <td style="text-align:right; vertical-align:top;">
                        <fmt:formatNumber value="${rowdata.clicks}"/>
                    </td>
                    <td style="text-align:right; vertical-align:top;">
                        <fmt:formatNumber type="number" pattern="0.00'%'" value="${rowdata.ctr}"/>
                    </td>
                </c:if>
                <td style="text-align:right; vertical-align:top;" class="number">
                    ${ad:formatCurrency(rowdata.ecpm, accountBean.currency.currencyCode)}
                </td>
                <td style="text-align:right; vertical-align:top;" class="number">
                    ${ad:formatCurrency(rowdata.revenue, accountBean.currency.currencyCode)}
                </td>
            </tr>
        </tbody>
        <c:if test="${rowdata.tagExist}">
            <tbody id="data_${rowdata.siteId}" style="display:none" class="child">
            </tbody>
        </c:if>
    </c:forEach>

    <c:if test="${not empty result}">
        <tbody>
            <tr class="total">
                <td colspan="2"></td>
                <td class="totalText"><fmt:message key="report.totals"/>:</td>
                <td class="number"><fmt:formatNumber value="${total.requests}"/></td>
                <td class="number"><fmt:formatNumber value="${total.imps}"/></td>
                <c:if test="${availableCreditedImps}">
                    <td class="number"><fmt:formatNumber value="${total.creditedImps}"/></td>
                </c:if>
                <c:if test="${clicksDataAvailable}">
                    <td class="number"><fmt:formatNumber value="${total.clicks}"/></td>
                    <td class="number"><fmt:formatNumber type="number" pattern="0.00'%'" value="${total.ctr}"/></td>
                </c:if>
                <td class="currency">${ad:formatCurrency(total.ecpm, accountBean.currency.currencyCode)}</td>
                <td class="currency">${ad:formatCurrency(total.revenue, accountBean.currency.currencyCode)}</td>
            </tr>
        </tbody>
    </c:if>
</table>
