<%@page contentType="text/html" %>
<%@page pageEncoding="UTF-8" %>
<%@ page import="com.foros.model.Status" %>
<%@ page import="com.foros.model.site.Tag" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ui" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>

<ad:requestContext var="publisherContext"/>
<c:set var="accountBean" value="${publisherContext.account}"/>
<c:if test="${ad:isPermitted('PublisherEntity.view', accountBean)}">
    <c:set var="siteView" value="true"/>
</c:if>


<c:forEach var="rowdata" items="${result}">
    <tr>
        <td class="withCollapsingButton"/>
        <td>
            <c:set var="status" value="${rowdata.status}" scope="request"/>
            <c:set var="displayStatus" value="<%=Tag.getDisplayStatus(Status.valueOf(request.getAttribute("status").toString().charAt(0)))%>"/>
            <ui:displayStatus displayStatus="${displayStatus}">
                <c:choose>
                    <c:when test="${siteView}">
                        <a href="${_context}/tag/view.action?id=${rowdata.tagId}"><c:out value="${rowdata.tagName}"/></a>
                    </c:when>
                    <c:otherwise>
                        <c:out value="${rowdata.tagName}"/>
                    </c:otherwise>
                </c:choose>
            </ui:displayStatus>
        </td>
        <td>
            <c:out value="${rowdata.tagSizeName}"/>
        </td>
        <td style="text-align:right; vertical-align:top;">
            <fmt:formatNumber value="${rowdata.requests}"/>
        </td>
        <td style="text-align:right; vertical-align:top;">
            <fmt:formatNumber value="${rowdata.imps}"/>
        </td>
        <c:if test="${showCreditedImps}">
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
</c:forEach>

