<%@ tag description="UI Select" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core"   prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ui" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<%@ attribute name="id" %>
<%@ attribute name="name" %>
<%@ attribute name="cssClass" %>
<%@ attribute name="size" %>
<%@ attribute name="onchange" %>
<%@ attribute name="labelKey" %>
<%@ attribute name="labelText" %>
<%@ attribute name="required" type="java.lang.Boolean" %>

<table class="grouping withTip">
    <c:if test="${not empty pageScope.labelKey or not empty pageScope.labelText}">
        <tr>
            <td>
                <label class="${pageScope.required ? 'mandatory' : ''}">
                    <c:choose>
                        <c:when test="${not empty pageScope.labelKey}">
                            <fmt:message key="${pageScope.labelKey}" />
                        </c:when>
                        <c:when test="${not empty pageScope.labelText}">
                            <c:out value="${pageScope.labelText}" />
                        </c:when>
                    </c:choose>
                </label>
            </td>
        </tr>
    </c:if>
    <tr>
        <td>
            <select multiple="multiple" class="${pageScope.cssClass}"
                <c:if test="${not empty pageScope.id}">id="${pageScope.id}"</c:if>
                <c:if test="${not empty pageScope.name}">name="${pageScope.name}"</c:if>
                <c:if test="${not empty pageScope.size}">size="${pageScope.size}"</c:if>
                <c:if test="${not empty pageScope.onchange}">onchange="${pageScope.onchange}"</c:if>
            >
                <jsp:doBody />
            </select>
        </td>
    </tr>
    <tr>
        <td>
            <div class="tipContainer">
                <div class="selTip"></div>
            </div>
        </td>
    </tr>
</table>

