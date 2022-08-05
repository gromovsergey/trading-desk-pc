<%@ page contentType="text/html" %>
<%@ page pageEncoding="UTF-8" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>


<ui:pageHeadingByTitle/>

<s:form id="invitationsForm" action="run" method="post" target="_blank">
    <%@include file="../enableDoubleSubmit.jsp"%>

    <ui:section titleKey="form.filter">
        <ui:fieldGroup>

            <ui:field labelKey="report.input.field.dateRange">
                <ui:daterange
                        fromDateFieldName="dateRange.begin"
                        toDateFieldName="dateRange.end"
                        options="Y T WTD MTD QTD YTD LW LM LQ LY R"
                        fastChangeId="Y"
                        currentPos="1"
                        maxDate="+1d"
                        validateRange="true"/>
            </ui:field>

            <c:choose>
                <c:when test="${not empty accounts}">
                    <ui:field labelKey="report.input.field.objectType.Account" labelForId="accountId">
                        <select id="accountId" name="accountId" class="middleLengthText">
                            <c:forEach var="account" items="${accounts}">
                                <c:choose>
                                    <c:when test="${account.id == accountId}">
                                        <option value="${account.id}" selected="true"><c:out value="${account.name}"/></option>
                                    </c:when>
                                    <c:otherwise>
                                        <option value="${account.id}"><c:out value="${account.name}"/></option>
                                    </c:otherwise>
                                </c:choose>
                            </c:forEach>
                        </select>
                    </ui:field>
                </c:when>
                <c:otherwise>
                    <input type="hidden" name="accountId" id="accountId" value="${account.id}"/>
                </c:otherwise>
            </c:choose>

            <ui:field id="browserBreakdownId" cssClass="valignFix" labelKey="report.input.field.browserBreakdown">
                <label class="withInput">
                    <input type="radio" name="showBrowserFamilies" value="false" checked><fmt:message key="report.hide"/>
                </label>
                <label class="withInput">
                    <input type="radio" name="showBrowserFamilies" value="true"><fmt:message key="report.show"/>
                </label>
            </ui:field>

            <ui:field cssClass="withButton">
                <ui:button id="submitButton" message="report.button.runReport"/>
            </ui:field>
        </ui:fieldGroup>
    </ui:section>

</s:form>
