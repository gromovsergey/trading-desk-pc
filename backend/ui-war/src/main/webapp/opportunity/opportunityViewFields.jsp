<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>
<%@ taglib uri="/struts-tags" prefix="s" %>

<s:set var="opportunity" value="model"/>

<ui:section>
    <ui:fieldGroup>
        <ui:simpleField labelKey="opportunity.amount" value="${ad:formatCurrency(opportunity.amount, model.account.currency.currencyCode)}"/>
        <ui:simpleField labelKey="opportunity.spentAmount" value="${ad:formatCurrency(spentAmount, model.account.currency.currencyCode)}"/>
        <ui:simpleField labelKey="opportunity.availableAmount" value="${ad:formatCurrency(availableAmount, model.account.currency.currencyCode)}"/>
        <ui:simpleField labelKey="opportunity.unallocatedAmount" value="${ad:formatCurrency(unallocatedAmount, model.account.currency.currencyCode)}"/>
        <fmt:message key="enum.opportunity.probability.${opportunity.probability}" var="probabilityName"/>
        <ui:simpleField labelKey="opportunity.probability" value="${probabilityName}"/>
        <ui:simpleField labelKey="opportunity.notes" value="${opportunity.notes}"/>
        <c:if test="${opportunity.poNumber != null}">
            <ui:simpleField labelKey="opportunity.poNumber" value="${opportunity.poNumber}"/>
        </c:if>
        <c:if test="${opportunity.ioNumber != null}">
            <ui:simpleField labelKey="opportunity.ioNumber" value="${opportunity.ioNumber}"/>
        </c:if>
    </ui:fieldGroup>
</ui:section>

<c:if test="${not empty existingFiles}">
    <ui:section titleKey="opportunity.ioFiles">
        <s:iterator value="existingFiles" var="existingFile">
            <ui:button messageText="${existingFile}"
                       onclick="viewFile('${existingFile}')"
                       id="browseFile"/>
        </s:iterator>
    </ui:section>
</c:if>

<script type="text/javascript">
    function viewFile(fileName) {
        var url             = "${_context}/opportunity/viewFile.action?fileName=" + fileName + "&id=" + ${id};
        var options         = "resizable=yes,menubar=no,status=no,toolbar=no,scrollbars=no,location=0,dialog=no,height=350,width=350,left=100,top=100,titlebar=no";
        var win             = window.open(url, "printpop", options);
        win.location.href   = url;
        win.focus();
    }
</script>
