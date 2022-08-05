<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>

<s:if test="entities != null">
    <h2><s:text name="cmpAccount.searchResults"/></h2>

    <display:table name="entities" class="dataView" id="account" varTotals="totals">
        <display:setProperty name="basic.msg.empty_list" >
            <div class="wrapper">
                <fmt:message key="nothing.found.to.display"/>
            </div>
        </display:setProperty>
        <display:column titleKey="cmpAccount.account">
            <ui:displayStatus displayStatus="${account.displayStatus}" testFlag="${account.testFlag}">
                <a href="<s:url action="%{#attr.moduleName}/selectAccount" />?accountId=${account.id}"><c:out value="${account.name}"/></a>
            </ui:displayStatus>
        </display:column>
        
        <display:column property="impressions" titleKey="cmpAccount.impressions" format="{0,number,integer}" total="true"  class="number" />
        <display:column property="clicks" titleKey="cmpAccount.clicks" format="{0,number,integer}" total="true" class="number"/>
        <display:column titleKey="cmpAccount.cost" value="${ad:formatCurrency(account.cost, account.currencyCode)}" total="true" class="number" />
    </display:table>
    
</s:if>
</div>
