<%@ page contentType="text/html" %>
<%@ page pageEncoding="UTF-8" %>

<%@ page import="com.foros.model.channel.trigger.TriggerType" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ui" %>
<%@ taglib uri="/struts-tags" prefix="s" %>

<c:set var="URL_TRIGGER_TYPE" value="<%=TriggerType.URL.getLetter()%>"/>
<c:set var="SEARCH_KW_TRIGGER_TYPE" value="<%=TriggerType.SEARCH_KEYWORD.getLetter()%>"/>
<c:set var="PAGE_KW_TRIGGER_TYPE" value="<%=TriggerType.PAGE_KEYWORD.getLetter()%>"/>
<c:set var="bpTypes" value="${PAGE_KW_TRIGGER_TYPE},${SEARCH_KW_TRIGGER_TYPE},${URL_TRIGGER_TYPE}"/>

<c:forTokens var="bpType" items="${bpTypes}" delims=",">
    <div id="bpRow[${bpType}]" class="bpRow">

    <s:set var="currentParamPath" value="#attr.bpPropertyName + '(' + quote(#attr.bpType) + ')'"/>
    <s:set var="currentParam" value="#currentParamPath(#root)"/>
    <c:set var="bpLabelText"><fmt:message key="channel.params.matchingCriteria"/></c:set>
    <c:choose>
        <c:when test="${bpType==URL_TRIGGER_TYPE}">
            <c:set var="sectionTitle"><fmt:message key="channel.params.urls"/></c:set>
            <c:set var="positiveText"><fmt:message key="channel.urls.positive"/></c:set>
            <c:set var="negativeText"><fmt:message key="channel.urls.negative"/></c:set>
            <c:set var="negativeTipKey"><fmt:message key="channel.urls.negativeTooltip"/></c:set>
        </c:when>
        <c:when test="${bpType==SEARCH_KW_TRIGGER_TYPE}">
            <c:set var="sectionTitle"><fmt:message key="channel.params.searchKeywords"/></c:set>
            <c:set var="positiveText"><fmt:message key="channel.keywords.positive"/></c:set>
            <c:set var="negativeText"><fmt:message key="channel.keywords.negative"/></c:set>
            <c:set var="negativeTipKey"><fmt:message key="channel.searchKeywords.negativeTooltip"/></c:set>
        </c:when>
        <c:when test="${bpType==PAGE_KW_TRIGGER_TYPE}">
            <c:set var="sectionTitle"><fmt:message key="channel.params.pageKeywords"/></c:set>
            <c:set var="positiveText"><fmt:message key="channel.keywords.positive"/></c:set>
            <c:set var="negativeText"><fmt:message key="channel.keywords.negative"/></c:set>
            <c:set var="negativeTipKey"><fmt:message key="channel.pageKeywords.negativeTooltip"/></c:set>
        </c:when>
        <c:otherwise>
        </c:otherwise>
    </c:choose>

    <ui:section title="${sectionTitle}" errors="behavioralParameters">
        <c:choose>
            <c:when test="${bpType==URL_TRIGGER_TYPE}">
                <s:set var="triggersProperty" value="'urls'"/>
            </c:when>
            <c:when test="${bpType==SEARCH_KW_TRIGGER_TYPE}">
                <s:set var="triggersProperty" value="'searchKeywords'"/>
            </c:when>
            <c:when test="${bpType==PAGE_KW_TRIGGER_TYPE}">
                <s:set var="triggersProperty" value="'pageKeywords'"/>
            </c:when>
        </c:choose>

        <s:fielderror><s:param value="%{#triggersProperty}"/></s:fielderror>

        <div class="wrapper">
            <table class="grouping fieldsets">
                <tr>
                    <td class="singleFieldset">
                        <ui:header styleClass="level2 withTip"><h4>${positiveText}</h4></ui:header>
                        <s:fielderror><s:param value="%{#triggersProperty + '.positive'}"/></s:fielderror>
                        <s:textarea name="%{#triggersProperty}" id="%{#triggersProperty}" wrap="off" cssClass="middleLengthText1"/>
                    </td>
                    <td class="singleFieldset">
                        <ui:header styleClass="level2 withTip">
                            <h4>${negativeText}</h4>
                            <ui:hint>${negativeTipKey}</ui:hint>
                        </ui:header>
                        <s:fielderror><s:param value="%{#triggersProperty + '.negative'}"/></s:fielderror>
                        <s:textarea name="%{#triggersProperty}Negative" id="%{#triggersProperty}Negative" wrap="off" cssClass="middleLengthText1"/>
                    </td>
                </tr>
            </table>
        </div>
    </ui:section>
</c:forTokens>
