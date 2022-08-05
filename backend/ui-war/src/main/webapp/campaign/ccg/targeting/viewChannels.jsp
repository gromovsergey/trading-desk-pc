<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ui" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>

<s:if test="tgtType.letter == 'C'">
<tbody id="ccgTargetingBehaviours">
<tr>
    <td>
        <fmt:message key="ccg.targeting.behaviours"/>
        <c:if test="${ad:isPermitted('CreativeGroup.updateChannelTarget', model)}">
            <ui:button message="form.edit" href="target/edit.action?id=${id}" />
        </c:if>
        <c:if test="${showExpressionPerformance && ad:isPermitted('CreativeGroup.viewExpressionPerformance', model)}">
            <ui:button message="ccg.targeting.performance" href="performance/view.action?id=${id}" />
        </c:if>
    </td>
    <td class="ccg_target">
        <c:choose>
            <c:when test="${not empty channel}">
                <ui:displayStatus displayStatus="${channel.displayStatus}">
                    <c:choose>
                        <c:when test="${ad:isPermitted('AdvertisingChannel.view', channel)}">
                            <a href="${_context}/channel/view.action?id=${channel.id}">
                                <c:out value="${ad:appendStatus(channel.name, channel.displayStatus)}"/>
                            </a>
                        </c:when>
                        <c:otherwise>
                            <c:out value="${channel.name}"/>
                        </c:otherwise>
                    </c:choose>
                </ui:displayStatus>
            </c:when>
            <c:when test="${channelTarget.letter == 'N'}">
                <ui:text textKey="channel.notset"/>
            </c:when>
            <c:otherwise>
                <ui:text textKey="channel.untargeted"/>
            </c:otherwise>
        </c:choose>
    </td>
    <tiles:insertTemplate template="/campaign/ccg/targeting/statsData.jsp">
        <tiles:putAttribute name="data" value="${targetingStats.behaviors}"/>
    </tiles:insertTemplate>
</tr>
</tbody>
</s:if>