<%@ tag description="UI Section" %>

<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core"   prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt"    prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ui" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>

<%@ attribute name="id" %>
<%@ attribute name="titleKey" %>
<%@ attribute name="title" %>
<%@ attribute name="cssClass" %>
<%@ attribute name="cssStyle" %>
<%@ attribute name="tipKey" %>
<%@ attribute name="tipText" %>
<%@ attribute name="mandatory" type="java.lang.Boolean"%>
<%@ attribute name="present" type="java.lang.Boolean"%>
<%@ attribute name="errors" %>
<%@ attribute name="infoText" %>
<%@ attribute name="infoKey" %>
<%@ attribute name="titleInputId" %>
<%@ attribute name="titleInputValue" %>
<%@ attribute name="titleInputChecked" type="java.lang.Boolean"%>

<c:if test="${empty pageScope.id and not empty pageScope.titleKey}">
    <c:set var="id" value="${fn:replace(pageScope.titleKey, '.', '_')}"/>
</c:if>

<c:if test="${empty pageScope.present || pageScope.present == true}">

<c:set var="tip">
    <c:choose>
        <c:when test="${not empty pageScope.tipText}">
            <ui:hint>${pageScope.tipText}</ui:hint>
        </c:when>
        <c:when test="${not empty pageScope.tipKey}">
            <ui:hint><fmt:message key="${pageScope.tipKey}"/></ui:hint>
        </c:when>
        <c:otherwise></c:otherwise>
    </c:choose>
</c:set>

<c:set var="infoMessage">
    <c:choose>
        <c:when test="${not empty pageScope.infoText}">
            <span class="infos">${pageScope.infoText}</span>
        </c:when>
        <c:when test="${not empty pageScope.infoKey}">
            <span class="infos"><fmt:message key="${pageScope.infoKey}"/></span>
        </c:when>
        <c:otherwise></c:otherwise>
    </c:choose>
</c:set>

<c:set var="titleFull">
    <c:choose>
        <c:when test="${not empty pageScope.titleKey}">
            <fmt:message key="${pageScope.titleKey}" />${tip}
        </c:when>
        <c:when test="${not empty pageScope.title}">
            ${pageScope.title}${tip}
        </c:when>
        <c:otherwise>
            ${tip}
        </c:otherwise>
    </c:choose>
</c:set>

<table id="${pageScope.id}" class="fieldset ${pageScope.cssClass}" style="${cssStyle}">
    <tr>
        <td class="fieldsetCell">
            <c:if test="${(not empty pageScope.titleKey) or (not empty pageScope.title)}">
                <div class="legend ${pageScope.mandatory ? 'mandatory' : ''}">
                    <c:if test="${not empty pageScope.titleInputId}">
                        <input class="triggerType" type="checkbox"
                               id="${pageScope.titleInputId}" name="${pageScope.titleInputId}"
                               ${pageScope.titleInputChecked?'checked':''}
                               value="${empty pageScope.titleInputValue?true:pageScope.titleInputValue}"/>
                    </c:if>${titleFull}
                </div>
            </c:if>
            <div class="fixing"></div>
            
            <div class="content">
                <c:if test="${not empty pageScope.errors}">
                    <c:set var="errList" value="${fn:split(pageScope.errors, ', ')}" />
                    <%--Errors for struts 2--%>  
                    <s:fielderror>
                      <c:forEach items="${errList}" var="err">
                        <s:param value="%{#attr.err.trim()}"/>
                      </c:forEach>
                    </s:fielderror>
                </c:if>
                
                ${infoMessage}
                
                <jsp:doBody />
            </div>
        </td>
    </tr>
</table>

</c:if>
