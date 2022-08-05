<%@ page contentType="text/html" %>
<%@ page pageEncoding="UTF-8" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ui" %>

<c:set var="isInternal" value="${ad:isInternal()}"/>
<c:set var="showExpressionAssociationAccounts" value="${isInternal}"/>

<c:if test="${not empty expressionAssociations}">
<h2><fmt:message key="expression.associations.title"/></h2>
<display:table name="expressionAssociations" class="dataView" id="ea">
    <display:setProperty name="basic.msg.empty_list">
        <div class="wrapper">
            <fmt:message key="nothing.found.to.display"/>
        </div>
    </display:setProperty>
    <c:if test="${showExpressionAssociationAccounts}">
        <display:column titleKey="expression.associations.account">
            <ui:displayStatus displayStatus="${ea.account.displayStatus}" testFlag="${ea.account.testFlag}">
                <c:choose>
                    <c:when test="${ad:isPermitted('Account.view', ea.account)}">
                        <a href="/admin/account/view.action?id=${ea.account.id}">
                            <ui:nameWithStatus entityStatus="${ea.account.status}" entityName="${ea.account.name}"/>
                        </a>
                    </c:when>
                    <c:otherwise>
                        <ui:nameWithStatus entityStatus="${ea.account.status}" entityName="${ea.account.name}"/>
                    </c:otherwise>
                </c:choose>
            </ui:displayStatus>
        </display:column>
    </c:if>
    <display:column titleKey="expression.associations.expression">
        <ui:displayStatus displayStatus="${ea.expression.displayStatus}">
            <c:choose>
                <c:when test="${ad:isPermitted('AdvertisingChannel.view', ea.expression)}">
                    <a href="${_context}/channel/view.action?id=${ea.expression.id}">
                        <ui:nameWithStatus entityStatus="${ea.expression.status}" entityName="${ea.expression.name}"/>
                    </a>
                </c:when>
                <c:otherwise>
                    <ui:nameWithStatus entityStatus="${ea.expression.status}" entityName="${ea.expression.name}"/>
                </c:otherwise>
            </c:choose>
        </ui:displayStatus>
    </display:column>
</display:table>
</c:if>
