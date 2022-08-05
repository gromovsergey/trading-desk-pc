<%@ tag description="ui button" %>

<%@ attribute name="id" required="false" %>
<%@ attribute name="href" required="false" %>
<%@ attribute name="action" required="false" %>
<%@ attribute name="message" required="false" %>
<%@ attribute name="messageText" required="false" %>
<%@ attribute name="onclick" required="false" %>
<%@ attribute name="target" required="false" %>
<%@ attribute name="subClass" required="false" %>
<%@ attribute name="type" required="false" %>
<%@ attribute name="disabled" required="false" %>
<%@ attribute name="title" required="false" %>
<%@ attribute name="novalidate" required="false" type="java.lang.Boolean" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib uri="/struts-tags" prefix="s" %>

<c:if test="${not empty pageScope.action}">
    <c:set var="href"><s:url action="%{#attr.action}"/></c:set>
</c:if>

<c:set var="buttonType">
    <c:choose>
        <c:when test="${not empty pageScope.type}">${pageScope.type}</c:when>
        <c:otherwise>
            <c:choose>
                <c:when test="${empty pageScope.href and empty pageScope.onclick}">submit</c:when>
                <c:otherwise>link</c:otherwise>
            </c:choose>
        </c:otherwise>
    </c:choose>
</c:set>

<c:set var="caption">
    <c:choose>
        <c:when test="${not empty pageScope.message}">
            <fmt:message var="unescapedMessage" key="${pageScope.message}"/>
            <c:out value="${unescapedMessage}"/>
        </c:when>
        <c:when test="${not empty pageScope.messageText}">
            ${pageScope.messageText}
        </c:when>
        <c:otherwise>
            <jsp:doBody/>
        </c:otherwise>
    </c:choose>
</c:set>

<c:choose>
    <c:when test="${buttonType == 'link'}">
        <c:set var="hrefText">
            <c:choose>
                <c:when test="${not empty pageScope.href}">
                    ${pageScope.href}
                </c:when>
                <c:otherwise>javascript:void(false);</c:otherwise>
            </c:choose>
        </c:set>
        
        <c:set var="onclickText">
            <c:if test="${not empty pageScope.onclick}">
                ${pageScope.onclick}; return false;
            </c:if>
        </c:set>
        
        <a href="${hrefText}" id="${pageScope.id}" onclick="${onclickText}"
                target="${pageScope.target}" class="button ${pageScope.subClass}">${caption}</a>
    </c:when>
    <c:otherwise>
        <c:if test="${not empty pageScope.href}">
            <c:set var="onclick" value="${pageScope.onclick}; UI.Util.openLink('${pageScope.href}', '${pageScope.target}')"/>
        </c:if>        
        <input type="${buttonType}" name="${pageScope.id}" id="${pageScope.id}" value="${caption}"
                onclick="${pageScope.onclick}" class="${pageScope.subClass}" 
                <c:if test="${not empty pageScope.disabled}">disabled="disabled"</c:if>
                <c:if test="${pageScope.novalidate}">formnovalidate="formnovalidate"</c:if> 
                <c:if test="${not empty pageScope.title}">title="${pageScope.title}"</c:if> />
    </c:otherwise>
</c:choose>