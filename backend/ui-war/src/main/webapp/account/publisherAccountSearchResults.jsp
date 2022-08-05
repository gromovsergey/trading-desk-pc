<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>

<script type="text/javascript">

function submitSitesExportForm() {
    $.ajax(
        {
            type: 'POST',
            url: '/admin/publisher/prepareExportSites.action',
            dataType: 'text',
            data: prepareData(),
            success : previewSuccess,
            waitHolder: $('#mainForm')
        }
    );
}

function prepareData(){
    return $('#mainForm').find("[name=accountIds]:checked").serialize();
}

function previewSuccess(data, textStatus) {
    if (data.indexOf("resultId") >= 0) {
        location.href="/admin/publisher/exportSites.action?" + data;
    } else {
        alert(data);
    }
}

    function toggleAllAccounts(header) {
        $('[name=accountIds]').prop({checked : header.checked});
    }
</script>

<s:if test="entities != null">
    <div class="pageHeading">
        <h2><s:text name="publisherAccount.searchResults"/></h2>
        <c:if test="${ad:isPermitted0('PublisherEntity.view')}">
            <ui:button message="site.list.download"  href="#" onclick="submitSitesExportForm(); return false;"/>
        </c:if>
    </div>
    <c:set var="creditedImpressionsTitle">
        <div class="textWithHint">
            <fmt:message key="publisherAccount.creditedImpressions"/>
            <ui:hint><fmt:message key="publisherAccount.creditedImpressions.tip"/></ui:hint>
        </div>
    </c:set>
    <form id="mainForm">
        <display:table name="entities" class="dataView" id="account" varTotals="totals" >
            <display:setProperty name="basic.msg.empty_list" >
                <div class="wrapper">
                    <fmt:message key="nothing.found.to.display"/>
                </div>
            </display:setProperty>
            <display:column title="<input type='checkbox' onclick='toggleAllAccounts(this)'/>"
                            style="text-align:center;width:24px;">
                <input type="checkbox" name="accountIds" value="${account.id}"/>
            </display:column>
            <display:column titleKey="publisherAccount.account">
                <ui:displayStatus displayStatus="${account.displayStatus}" testFlag="${account.testFlag}">
                    <a href="<s:url action="%{#attr.moduleName}/selectAccount"/>?accountId=${account.id}"><c:out value="${account.name}"/></a>
                </ui:displayStatus>
            </display:column>
            <display:column property="requests" titleKey="publisherAccount.requests" format="{0,number,integer}" total="true"  class="number" />
            <display:column property="impressions" titleKey="publisherAccount.impressions" format="{0,number,integer}" total="true" class="number"/>
            <c:if test="${availableCreditedImps}">
                <display:column property="creditedImpressions" title="${creditedImpressionsTitle}" format="{0,number,integer}" total="true" class="number"/>
            </c:if>
            <display:column titleKey="publisherAccount.cost" value="${ad:formatCurrency(account.cost, account.currencyCode)}" total="true" class="number" />
        </display:table>
    </form>
</s:if>
