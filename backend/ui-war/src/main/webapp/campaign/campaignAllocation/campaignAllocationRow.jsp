<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>

<s:set var="allocation" value="%{campaignAllocations[#attr.index]}"/>

<c:if test="${not empty allocation}">
    <tr id="allocation${index}">
        <td>
            <s:if test="#allocation.id != null">
                <s:hidden name="campaignAllocations[%{#attr.index}].id"/>
                <s:hidden name="campaignAllocations[%{#attr.index}].version"/>
                <s:hidden name="campaignAllocations[%{#attr.index}].opportunity.id"/>
                <s:hidden name="campaignAllocations[%{#attr.index}].opportunity.ioNumber"/>

                <a href="/admin/insertionOrder/view.action?id=${allocation.opportunity.id}&campaignId=${id}">
                    <fmt:message key="campaignAllocation.ioNumber">
                        <fmt:param value="${allocation.opportunity.ioNumber}"/>
                    </fmt:message>
                </a>
            </s:if>
            <s:else>
                <table class="fieldAndAccessories">
                    <tr>
                        <td class="withField">
                            <s:select name="campaignAllocations[%{#attr.index}].opportunity.id" cssClass="smallLengthText1"
                                      headerValue="%{getText('campaignAllocation.ioNumber.pleaseSelect')}" headerKey=""
                                      list="availableOpportunities"
                                      listKey="id" listValue="getText('campaignAllocation.ioNumber', '', ioNumber)"
                                      onchange="%{'changeOpportunity(' + #attr.index + ')'}"/>
                        </td>
                        <td class="withError">
                            <s:fielderror><s:param value="'campaignAllocations[' + #attr.index + '].opportunity'"/></s:fielderror>
                        </td>
                    </tr>
                </table>
            </s:else>
        </td>
        <td>
            <c:out value="${opportunitiesMap[allocation.opportunity.id].name}"/>
            <c:if test="${opportunitiesMap[allocation.opportunity.id].notes != null}">
                <ui:button message="campaignAllocation.notes" onclick="$('#opportunityNotes${index}').dialog('open');"/>
                <div id="opportunityNotes${index}" style="display:none;">
                    <c:out value="${opportunitiesMap[allocation.opportunity.id].notes}"/>
                </div>
            </c:if>
        </td>
        <td class="number">${ad:formatCurrency(opportunitiesMap[allocation.opportunity.id].amount, existingAccount.currency.currencyCode)}</td>
        <td class="number">${ad:formatCurrency(opportunitiesMap[allocation.opportunity.id].spentAmount, existingAccount.currency.currencyCode)}</td>
        <td class="number">${ad:formatCurrency(opportunitiesMap[allocation.opportunity.id].availableAmount, existingAccount.currency.currencyCode)}</td>
        <td class="number">${ad:formatCurrency(opportunitiesMap[allocation.opportunity.id].unallocatedAmount, existingAccount.currency.currencyCode)}</td>
        <td>
            <table class="fieldAndAccessories">
                <tr>
                    <td class="withField mandatory">
                        <s:textfield name="campaignAllocations[%{#attr.index}].amount" cssClass="smallLengthText1" maxlength="16"/>
                    </td>
                    <td class="withError">
                        <s:fielderror><s:param value="'campaignAllocations[' + #attr.index + '].amount'"/></s:fielderror>
                        <s:fielderror><s:param value="'allocatedAmount'"/></s:fielderror>
                    </td>
                </tr>
            </table>
        </td>
        <td class="number">
            <s:hidden name="campaignAllocations[%{#attr.index}].utilizedAmount"/>
            <s:if test="#allocation.utilizedAmount != null">
                ${ad:formatCurrency(allocation.utilizedAmount, existingAccount.currency.currencyCode)}
            </s:if>
            <s:else>
                ${ad:formatCurrency(0, existingAccount.currency.currencyCode)}
            </s:else>
        </td>
        <td class="number">
            <s:if test="#allocation.id != null">
                <s:hidden name="campaignAllocations[%{#attr.index}].order"/>
                <s:if test="campaignCreditAllocation != null">
                    <s:property value="#allocation.order + 1"/>
                </s:if>
                <s:else>
                    <s:property value="#allocation.order"/>
                </s:else>
            </s:if>
            <s:else>
                <table class="fieldAndAccessories">
                    <tr>
                        <td class="withField">
                            <select name="campaignAllocations[${index}].order" class="smallLengthText1">
                                <c:set var="selectedOrder">
                                    ${allocation.order != null && allocation.order < campaignAllocations.size() ? allocation.order : campaignAllocations.size()}
                                </c:set>
                                <s:iterator begin="1" end="%{campaignAllocations.size}" var="idx">
                                    <option value="${idx}"<c:if test="${idx == selectedOrder}">selected="selected"</c:if>>
                                            ${campaignCreditAllocation != null ? idx + 1 : idx}
                                    </option>
                                </s:iterator>
                            </select>
                        </td>
                        <td class="withError">
                            <s:fielderror><s:param value="'campaignAllocations[' + #attr.index + '].order'"/></s:fielderror>
                        </td>
                    </tr>
                </table>
            </s:else>
        </td>
        <td class="withButton">
            <s:if test="%{#allocation.id == null || removableAllocationIds.contains(#allocation.id)}">
                <ui:button onclick="deleteAllocation(${index}); return false;" message="form.delete"/>
            </s:if>
        </td>
    </tr>
</c:if>

<script type="text/javascript">
	$(function(){
		$('#opportunityNotes${index}').dialog({
			autoOpen: false,
			width: 600
		});
	});
</script>