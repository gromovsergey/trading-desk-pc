<%@ tag language="java" body-content="empty" description="Renders behavioral parameters view section" %>

<%@ tag import="com.foros.model.channel.trigger.TriggerType" %>
<%@ tag import="com.foros.model.channel.BehavioralParametersUnits" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ui" %>

<%@ attribute name="bparam" required="true" type="java.lang.Object" %>
<%@ attribute name="isChannelBehaviouralParamList" required="false" type="java.lang.Boolean" %>

<c:set var="URL_TRIGGER_TYPE" value="<%=TriggerType.URL.getLetter()%>"/>
<c:set var="SEARCH_KW_TRIGGER_TYPE" value="<%=TriggerType.SEARCH_KEYWORD.getLetter()%>"/>
<c:set var="PAGE_KW_TRIGGER_TYPE" value="<%=TriggerType.PAGE_KEYWORD.getLetter()%>"/>

<c:choose>
    <c:when test="${bparam.triggerType==URL_TRIGGER_TYPE}">
        <c:set var="part1Single" value="channel.params.part1.url.one"/>
        <c:set var="part1Plural" value="channel.params.part1.url.plural"/>
        <c:set var="part2TextKey" value="channel.params.part2.url"/>
    </c:when>
    <c:when test="${bparam.triggerType==SEARCH_KW_TRIGGER_TYPE}">
        <c:set var="part1Single" value="channel.params.part1.search.one"/>
        <c:set var="part1Plural" value="channel.params.part1.search.plural"/>
        <c:set var="part2TextKey" value="channel.params.part2.search"/>
    </c:when>
    <c:when test="${bparam.triggerType==PAGE_KW_TRIGGER_TYPE}">
        <c:set var="part1Single" value="channel.params.part1.keyword.one"/>
        <c:set var="part1Plural" value="channel.params.part1.keyword.plural"/>
        <c:set var="part2TextKey" value="channel.params.part2.keyword"/>
    </c:when>
</c:choose>
<c:set var="timeUnit" value="<%=BehavioralParametersUnits.DAYS.getName()%>"/>
<c:set var="multiplier" value="<%=BehavioralParametersUnits.DAYS.getMultiplier()%>"/>
<c:if test="${bparam.timeFrom mod multiplier != 0 or bparam.timeTo mod multiplier != 0}">
    <c:set var="timeUnit" value="<%=BehavioralParametersUnits.HOURS.getName()%>"/>
    <c:set var="multiplier" value="<%=BehavioralParametersUnits.HOURS.getMultiplier()%>"/>
    <c:if test="${bparam.timeFrom mod multiplier != 0 or bparam.timeTo mod multiplier != 0}">
        <c:set var="timeUnit" value="<%=BehavioralParametersUnits.MINUTES.getName()%>"/>
        <c:set var="multiplier" value="<%=BehavioralParametersUnits.MINUTES.getMultiplier()%>"/>
    </c:if>
</c:if>

<c:set var="textVal">
    <c:out value="${bparam.minimumVisits}"/>
    <c:choose>
        <c:when test="${bparam.minimumVisits > 1}">
            <fmt:message key="${part1Plural}"/>
        </c:when>
        <c:otherwise>
            <fmt:message key="${part1Single}"/>
        </c:otherwise>
    </c:choose>
    <c:choose>
        <c:when test="${bparam.timeFrom > 0}">
            <fmt:formatNumber value="${bparam.timeFrom div multiplier}" maxFractionDigits="0"/>
        </c:when>
        <c:otherwise>
            <fmt:message key="channel.params.now"/>
        </c:otherwise>
    </c:choose>
    <fmt:message key="channel.params.and"/>
    <c:choose>
        <c:when test="${bparam.timeTo > 0}">
            <fmt:formatNumber value="${bparam.timeTo div multiplier}" maxFractionDigits="0"/>
        </c:when>
        <c:otherwise>
            <fmt:message key="channel.params.now"/>
        </c:otherwise>
    </c:choose>
    <c:if test="${bparam.timeFrom > 0 or bparam.timeTo > 0}">
        <c:choose>
            <c:when test="${bparam.timeTo > multiplier}">
                <fmt:message key="channel.params.${timeUnit}.plural"/>
            </c:when>
            <c:otherwise>
                <fmt:message key="channel.params.${timeUnit}"/>
            </c:otherwise>
        </c:choose>
        <fmt:message key="channel.params.ago"/>
    </c:if>
    <fmt:message key="${part2TextKey}"/>
    <c:if test="${isChannelBehaviouralParamList}">
        <fmt:message key="channel.withWeight"/>
        <fmt:formatNumber value="${bparam.weight}"/>
    </c:if>
</c:set>

<ui:text text="${pageScope.textVal}"/>