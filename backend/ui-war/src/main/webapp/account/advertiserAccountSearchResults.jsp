<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>

<s:if test="entities != null">
    <h2><s:text name="advertiserAccount.searchResults"/></h2>

    <display:table name="entities" class="dataView" id="account" varTotals="totals">
        <display:setProperty name="basic.msg.empty_list" >
            <div class="wrapper">
                <fmt:message key="nothing.found.to.display"/>
            </div>
        </display:setProperty>
        <display:column titleKey="advertiserAccount.account">
            <ui:displayStatus displayStatus="${account.displayStatus}" testFlag="${account.testFlag}">
                <a href="<s:url action="%{#attr.moduleName}/selectAccount" />?accountId=${account.id}"><c:out value="${account.name}"/></a>
            </ui:displayStatus>
        </display:column>
        
        <display:column property="impressions" titleKey="advertiserAccount.impressions" format="{0,number,integer}" total="true"  class="number" />
        <display:column property="clicks" titleKey="advertiserAccount.clicks" format="{0,number,integer}" total="true" class="number"/>
        <display:column property="ctr" titleKey="advertiserAccount.ctr" format="{0,number,0.00%}" total="true" class="number"/>
        <c:if test="${availableCreditUsed}">
            <display:column titleKey="advertiserAccount.creditUsed" value="${ad:formatCurrency(account.creditUsed, account.currencyCode)}" total="true" class="number" />
        </c:if>
        <display:column titleKey="advertiserAccount.revenue" value="${ad:formatCurrency(account.revenue, account.currencyCode)}" total="true" class="number" />
    </display:table>
    
</s:if>
