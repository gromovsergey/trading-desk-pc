<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@ taglib prefix="display" uri="http://displaytag.sf.net/el" %>
<%@ taglib prefix="ad" uri="/ad/serverUI" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<ui:header>
    <ui:pageHeadingByTitle />
    <c:if test="${ad:isPermitted('Account.create', 'Internal')}">
        <ui:button message="InternalAccount.createNew" href="/admin/internal/account/new.action" />
    </c:if>
</ui:header>

<display:table name="accounts" class="dataView" id="account">
	<display:setProperty name="basic.msg.empty_list">
		<div class="wrapper"><fmt:message key="nothing.found.to.display" /></div>
	</display:setProperty>
	<display:column titleKey="InternalAccount.accountName">
	<ui:displayStatus displayStatus="${account.displayStatus}" testFlag="${account.testFlag}">
            <a href="view.action?id=${account.id}"><c:out value="${account.name}"/></a>
        </ui:displayStatus>
	</display:column>
	<display:column titleKey="InternalAccount.country">
		<ad:resolveGlobal resource="country" id="${account.countryCode}" />
	</display:column>
</display:table>
