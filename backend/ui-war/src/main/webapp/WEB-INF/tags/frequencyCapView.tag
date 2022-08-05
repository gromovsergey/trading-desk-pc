<%@ tag language="java" body-content="empty" description="Renders frequency cap view section" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ui" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="ad" uri="/ad/serverUI" %>

<%@ attribute name="frequencyCap" type="com.foros.model.FrequencyCap" %>
<%@ attribute name="labelKey"%>

<c:if test="${empty pageScope.labelKey}">
    <c:set var="labelKey" value="frequency.caps"/>
</c:if>

<s:if test="#attr.frequencyCap != null && !#attr.frequencyCap.empty">
    <c:if test="${frequencyCap.lifeCount>0}">
        <c:set var="lifeAvailable" value="true"/>
    </c:if>
    <c:if test="${frequencyCap.windowCount>0 and frequencyCap.windowLength>0}">
        <c:set var="windowAvailable" value="true"/>
    </c:if>
    
    <ui:field labelKey="${labelKey}">
        <c:if test="${frequencyCap.period>0}">
            <fmt:message key="frequency.show.period1per"/>
            <c:choose>
                <c:when test="${frequencyCap.periodSpan.value == 1}">
                    <fmt:message key="${frequencyCap.periodSpan.unit.messageKey}"/><c:if test="${lifeAvailable or windowAvailable}">,</c:if>
                </c:when>
                <c:otherwise>
                    <c:out value="${ad:formatNumber(frequencyCap.periodSpan.value)}"/>
                    <fmt:message key="${frequencyCap.periodSpan.unit.messageKey}s"/><c:if test="${lifeAvailable or windowAvailable}">,</c:if>
                </c:otherwise>
            </c:choose>
        </c:if>
        <c:if test="${lifeAvailable}">
            <c:out value="${ad:formatNumber(frequencyCap.lifeCount)}"/>
            <fmt:message key="frequency.show.lifetotal"/><c:if test="${windowAvailable}">,</c:if>
        </c:if>
        <c:if test="${windowAvailable}">
            <fmt:message key="frequency.show.maximum"/>
            <c:out value="${ad:formatNumber(frequencyCap.windowCount)}"/>
            <fmt:message key="frequency.show.per"/>
            <c:choose>
                <c:when test="${frequencyCap.windowLengthSpan.value == 1}">
                    <fmt:message key="${frequencyCap.windowLengthSpan.unit.messageKey}"/>
                </c:when>
                <c:otherwise>
                    <c:out value="${ad:formatNumber(frequencyCap.windowLengthSpan.value)}"/>
                    <fmt:message key="${frequencyCap.windowLengthSpan.unit.messageKey}s"/>
                </c:otherwise>
            </c:choose>
        </c:if>
    </ui:field>
</s:if>
