<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ui" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>

<c:set var="channels" value="${excludedChannels}"/>
<c:if test="${not empty channels}">
<tbody id="ccgCampaignExcludedBehaviours">
<tr>
    <td>
        <fmt:message key="ccg.targeting.campaignExcludedBehaviours"/>
    </td>
    <td class="ccg_target">
        <ad:commaWriter var="channel" items="${channels}">
            <c:set var="channelName" value="${ad:appendStatus(channel.name, channel.status)}"/>
            <c:choose>
                <c:when test="${ad:isPermitted('AdvertisingChannel.view', channel)}">
                    <a href="${_context}/channel/view.action?id=${channel.id}"><c:out value="${channelName}"/></a>
                </c:when>
                <c:otherwise>
                    <c:out value="${channelName}"/>
                </c:otherwise>
            </c:choose>
        </ad:commaWriter>
    </td>
    <tiles:insertTemplate template="/campaign/ccg/targeting/statsData.jsp">
        <tiles:putAttribute name="data" value="${null}"/>
    </tiles:insertTemplate>
</tr>
</tbody>
</c:if>