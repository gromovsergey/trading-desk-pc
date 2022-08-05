<%@ page contentType="text/html" %>
<%@ page pageEncoding="UTF-8" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ui" %>
<%pageContext.setAttribute("linefeed", "\r\n"); %>

<ui:header>
    <ui:pageHeadingByTitle/>
    <c:if test="${ad:isPermitted0('FraudConditions.update')}">
        <table class="grouping groupOfButtons">
            <tr>
                <td>
                    <ui:button message="form.edit"
                               href="/admin/FraudConditions/edit.action"/>
                </td>
            </tr>
        </table>
    </c:if>
    <ui:button message="form.viewLog" href="/admin/auditLog/view.action?type=38" />
</ui:header>

<ui:section>
    <ui:fieldGroup>
        <ui:field labelKey="fraud.userInactivityTimeout">
            <ui:text text="${userInactivityTimeout}"/>
            <fmt:message key="fraud.timeoutUnits"/>
        </ui:field>
    </ui:fieldGroup>
</ui:section>

<ui:section titleKey="fraud.fraudConditions">
    <div class="list">
        <s:iterator value="fraudConditions" status="status">
            <c:set var="fraudCondition" value="${fraudConditions[status.index]}"/>
            <%@ include file="fraudCondition.jspf" %>
        </s:iterator>
    </div>
</ui:section>
