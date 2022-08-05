<%@ tag description="UI Field" body-content="empty" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core"      prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt"       prefix="fmt" %>
<%@ taglib uri="/struts-tags"                           prefix="s" %>
<%@ taglib prefix="ad" uri="/ad/serverUI" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ui" %>

<%@ attribute name="id" %>
<%@ attribute name="cssClass" %>
<%@ attribute name="cssValueClass" %>
<%@ attribute name="labelKey" %>
<%@ attribute name="label" %>
<%@ attribute name="value" %>
<%@ attribute name="valueKey" %>
<%@ attribute name="escapeXml" type="java.lang.Boolean" %>

<c:if test="${escapeXml == null}"><c:set var="escapeXml" value="${true}" /></c:if>

<c:set var="message">
    <c:choose>
        <c:when test="${not empty pageScope.valueKey}">
            <fmt:message key="${pageScope.valueKey}"/>
        </c:when>
        <c:otherwise>
           ${(not empty pageScope.value) ? pageScope.value : ''}
        </c:otherwise>
    </c:choose>
</c:set>

<c:if test="${not empty pageScope.message}">
    <ui:field
            id="${pageScope.id}"
            cssClass="${pageScope.cssClass}"
            labelKey="${pageScope.labelKey}"
            label="${pageScope.label}"
            escapeXml="${escapeXml}"
            >
        <ui:text text="${pageScope.message}" subClass="${pageScope.cssValueClass}" escapeXml="${escapeXml}"/>
    </ui:field>
</c:if>
