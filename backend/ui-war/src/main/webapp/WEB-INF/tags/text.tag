<%@ tag description="UI Text" body-content="empty" %>

<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core"   prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt"    prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>

<%@ attribute name="textKey" %>
<%@ attribute name="text" %>
<%@ attribute name="maxLength" %>
<%@ attribute name="subClass" %>
<%@ attribute name="id" %>
<%@ attribute name="escapeXml" type="java.lang.Boolean" %>

<c:if test="${escapeXml == null}"><c:set var="escapeXml" value="${true}" /></c:if>

<c:set var="message">
    <c:choose>
        <c:when test="${not empty pageScope.textKey}">
            <fmt:message key="${pageScope.textKey}"/>
        </c:when>
        <c:otherwise>${pageScope.text}</c:otherwise>
    </c:choose>
</c:set>
<c:set var="messageEscaped">
    <c:out value="${pageScope.message}" escapeXml="${escapeXml}"/>
</c:set>

<c:set var="textAttr"/>
<c:set var="toCutText" value="${false}"/>

<c:if test="${not empty pageScope.maxLength}">
    <c:set var="textAttr">
        text="${pageScope.messageEscaped}"
    </c:set>
    <c:if test="${fn:length(pageScope.message) > pageScope.maxLength}">
        <c:set var="toCutText" value="${true}"/>
    </c:if>
</c:if>

<span class="simpleText ${pageScope.subClass}"
        id="${pageScope.id}" ${pageScope.textAttr} >
    <c:choose>
        <c:when test="${toCutText}">
            <span class="text"><c:out value="${ad:shortString(pageScope.message, pageScope.maxLength)}" escapeXml="${escapeXml}"/></span>

            <ui:toolTip>
                ${pageScope.messageEscaped}
            </ui:toolTip>
        </c:when>
        <c:otherwise>
            <span class="text">${pageScope.messageEscaped}</span>
        </c:otherwise>
    </c:choose>
</span>
