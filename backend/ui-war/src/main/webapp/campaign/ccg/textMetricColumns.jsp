<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>


<td class="number">
    <fmt:formatNumber value="${to.impressions}" groupingUsed="true"/>
</td>
<td class="number">
    <fmt:formatNumber value="${to.clicks}" groupingUsed="true"/>
</td>
<td class="number">
    <fmt:formatNumber value="${to.ctr}" groupingUsed="false" maxFractionDigits="2"/>%
</td>
<c:if test="${showPostImpConv}">
    <td class="number">
        <fmt:formatNumber value="${to.postImpConv}" groupingUsed="true"/>
    </td>
    <td class="number">
        <fmt:formatNumber value="${to.postImpConvCr}" groupingUsed="false" maxFractionDigits="2"/>%
    </td>
</c:if>
<c:if test="${showPostClickConv}">
    <td class="number">
        <fmt:formatNumber value="${to.postClickConv}" groupingUsed="true"/>
    </td>
    <td class="number">
        <fmt:formatNumber value="${to.postClickConvCr}" groupingUsed="false" maxFractionDigits="2"/>%
    </td>
</c:if>
<c:if test="${showUniqueUsers}">
    <td class="number">
        <c:choose>
            <c:when test="${to.class.simpleName == 'CreativeSetTO'}">
                <fmt:message key="notAvailable"/>
            </c:when>
            <c:otherwise>
                <fmt:formatNumber value="${to.uniqueUsers}" groupingUsed="true"/>
            </c:otherwise>
        </c:choose>
    </td>
</c:if>

<c:if test="${ad:isInternal()}">
    <c:if test="${availableCreditUsed}">
        <td class="currency">
                ${ad:formatCurrency(to.creditUsed, account.currency.currencyCode)}
        </td>
    </c:if>
    <td class="currency">
            ${ad:formatCurrency(to.totalCost, account.currency.currencyCode)}
    </td>
    <c:if test="${availableCreditUsed}">
        <td class="currency">
                ${ad:formatCurrency(to.totalValue, account.currency.currencyCode)}
        </td>
    </c:if>

    <td class="currency">
            ${ad:formatCurrency(to.ecpm, account.currency.currencyCode)}
    </td>
</c:if>