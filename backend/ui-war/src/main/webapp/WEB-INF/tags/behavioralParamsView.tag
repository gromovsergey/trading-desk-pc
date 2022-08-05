<%@ tag language="java" body-content="empty" description="Renders behavioral parameters view section" %>

<%@ tag import="com.foros.model.channel.trigger.TriggerType" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ui" %>

<%@ attribute name="isChannelBehaviouralParamList" required="false" type="java.lang.Boolean" %>

<c:set var="URL_TRIGGER_TYPE" value="<%=TriggerType.URL.getLetter()%>"/>
<c:set var="SEARCH_KW_TRIGGER_TYPE" value="<%=TriggerType.SEARCH_KEYWORD.getLetter()%>"/>
<c:set var="PAGE_KW_TRIGGER_TYPE" value="<%=TriggerType.PAGE_KEYWORD.getLetter()%>"/>

<c:choose>
    <c:when test="${empty behavioralParameters}">
        <p class="rtext"><fmt:message key="channel.parametersAreNotDefined"/></p>
    </c:when>
    <c:otherwise>
        <c:forEach var="bparam" items="${behavioralParameters}">
            <ui:fieldGroup id="" cssClass="">
                <c:choose>
                    <c:when test="${bparam.triggerType==URL_TRIGGER_TYPE}">
                        <c:set var="labelTextKey" value="channel.params.urls"/>
                    </c:when>
                    <c:when test="${bparam.triggerType==SEARCH_KW_TRIGGER_TYPE}">
                        <c:set var="labelTextKey" value="channel.params.searchKeywords"/>
                    </c:when>
                    <c:when test="${bparam.triggerType==PAGE_KW_TRIGGER_TYPE}">
                        <c:set var="labelTextKey" value="channel.params.pageKeywords"/>
                    </c:when>
                    <c:otherwise>
                        <c:set var="labelTextKey">Unknown type ['<c:out value="${bparam.triggerType}"/>']</c:set>
                    </c:otherwise>
                </c:choose>
                <ui:field labelKey="${labelTextKey}">
                    <ui:behavioralParamView bparam="${bparam}" isChannelBehaviouralParamList="${isChannelBehaviouralParamList}"/>
                </ui:field>
            </ui:fieldGroup>
        </c:forEach>

    </c:otherwise>
</c:choose>
