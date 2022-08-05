<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags"%>

<%-- EditSaveAllocationsActionBase --%>
<c:if test="${campaignCreditAllocation != null}">
    <tr id="allocationCredit">
        <td>
            <fmt:message key="CampaignCredit.id">
                <fmt:param value="${campaignCreditAllocation.campaignCredit.id}"/>
            </fmt:message>
        </td>
        <td>
            <c:out value="${campaignCreditAllocation.campaign.name}"/>
            <ui:button message="campaignAllocation.notes" onclick="$('#creditAllocationDescription').dialog('open');"/>
            <div id="creditAllocationDescription" style="display:none;">
                <c:out value="${campaignCreditAllocation.campaignCredit.description}"/>
            </div>
        </td>
        <td class="number">${ad:formatCurrency(campaignCredit.amount, existingAccount.currency.currencyCode)}</td>
        <td class="number">${ad:formatCurrency(campaignCredit.spentAmount, existingAccount.currency.currencyCode)}</td>
        <td class="number">${ad:formatCurrency(campaignCredit.availableAmount, existingAccount.currency.currencyCode)}</td>
        <td class="number">${ad:formatCurrency(campaignCredit.unallocatedAmount, existingAccount.currency.currencyCode)}</td>
        <td class="number">${ad:formatCurrency(campaignCreditAllocation.allocatedAmount, existingAccount.currency.currencyCode)}</td>
        <td class="number">${ad:formatCurrency(campaignCreditAllocation.usedAmount, existingAccount.currency.currencyCode)}</td>
        <td class="number">1</td>
    </tr>
</c:if>

<c:forEach var="allocation" items="${campaignAllocations}" varStatus="i">
    <c:set var="index" value="${i.index}"/>
    <%@include file="campaignAllocationRow.jsp" %>
</c:forEach>

<script type="text/javascript">
    $(function(){
        $('#creditAllocationDescription').dialog({
            autoOpen: false,
            width: 600
        });
    });
</script>