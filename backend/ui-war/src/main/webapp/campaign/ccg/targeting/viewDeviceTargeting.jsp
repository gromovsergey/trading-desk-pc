<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ui" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>

<s:set var="channelsSize" value="deviceChannels.size()"/>
<c:set var="canViewDeviceChannel" value="${ad:isPermitted0('DeviceChannel.view')}"/>

<tbody id="ccgTargetingDevices">
<tr>
    <td rowspan="${channelsSize > 1 ? channelsSize : 1}">
        <fmt:message key="ccg.targeting.devices"/>
        <c:if test="${ad:isPermitted('CreativeGroup.updateDeviceTargeting', model)}">
            <ui:button message="form.edit" href="deviceTargeting/edit.action?id=${id}" />
        </c:if>
    </td>

    <s:iterator var="entry" value="deviceChannels" status="it">
<s:if test="#it.index > 0"><tr></s:if>
    <td class="ccg_target">
        <s:set var="path" value="deviceChannelPath(#entry)"/>
        <s:iterator var="hierarchyEntry" value="#path" status="itPath">
                <span class="simpleText">
                    <c:if test="${canViewDeviceChannel}"><a href="/admin/DeviceChannel/view.action?id=${hierarchyEntry.id}"></c:if>
                    <c:out value="${hierarchyEntry.name}"/>
                    <c:if test="${canViewDeviceChannel}"></a></c:if>
                </span>
            <s:if test="not #itPath.last">/</s:if>
        </s:iterator>
    </td>
    <s:set var="stats" value="targetingStats.devices[#entry.id]"/>
    <tiles:insertTemplate template="/campaign/ccg/targeting/statsData.jsp">
        <tiles:putAttribute name="data" value="${stats}"/>
    </tiles:insertTemplate>
</tr>
</s:iterator>
<s:if test="#channelsSize == 0">
    <td class="ccg_target">
        <fmt:message key="ccg.targeting.devices.all"/>
    </td>
    <tiles:insertTemplate template="/campaign/ccg/targeting/statsData.jsp">
        <tiles:putAttribute name="data" value="${null}"/>
    </tiles:insertTemplate>
    </tr>
</s:if>
</tbody>
