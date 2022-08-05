<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<s:set var="deliveryPacingLetter">${param.deliveryPacingLetter}</s:set>
<s:set var="formattedDailyBudget">${param.formattedDailyBudget}</s:set>
<s:set var="type">${param.type}</s:set>
<s:set var="includedInCampaign">${param.includedInCampaign}</s:set>

<c:if test="${includedInCampaign || type != 'T'}">
    <ui:field labelKey="ccg.deliveryPacing">
        <c:choose>
            <c:when test="${deliveryPacingLetter == 'U'}">
                <ui:text textKey="ccg.deliveryPacing.unrestricted"/>
            </c:when>
            <c:when test="${deliveryPacingLetter == 'F'}">
                <ui:text textKey="ccg.deliveryPacing.fixed"/>
            </c:when>
            <c:when test="${deliveryPacingLetter == 'D'}">
                <ui:text textKey="ccg.deliveryPacing.dynamic"/>
            </c:when>
        </c:choose>
    </ui:field>
</c:if>
<c:if test="${deliveryPacingLetter != 'U'}">
    <ui:field labelKey="ccg.daily.budget">
        <ui:text text="${formattedDailyBudget}"/>
    </ui:field>
</c:if>
