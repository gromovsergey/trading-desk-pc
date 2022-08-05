<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="ad" uri="/ad/serverUI" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ui" %>

<s:set var="channelsSize" value="geoChannels.size()"/>
<tbody id="ccgTargetingGeolocations">
<tr>
    <td rowspan="${channelsSize > 1 ? channelsSize : 1}">
        <fmt:message key="ccg.targeting.geolocations"/>
        <c:if test="${ad:isPermitted('CreativeGroup.updateGeoTarget', model)}">
            <ui:button message="form.edit" href="geoTarget/edit.action?id=${id}" />
        </c:if>
    </td>
    <c:set var="canViewGeo" value="${ad:isPermitted0('GeoChannel.view')}"/>
    <s:iterator var="entry" value="geoChannels" status="it">
<s:if test="#it.index > 0"><tr></s:if>
    <td class="ccg_target">
        <c:set var="stateChannel" value="${entry.stateChannel}"/>
        <c:set var="countryChannel" value="${entry.countryChannel}"/>

        <c:if test="${not empty countryChannel}">
                <span class="simpleText">
                    <c:if test="${canViewGeo}"><a href="/admin/GeoChannel/view.action?id=${countryChannel.id}"></c:if>
                    <c:out value="${countryChannel.name}"/>
                    <c:if test="${canViewGeo}"></a></c:if>
                </span> /
        </c:if>

        <c:if test="${not empty stateChannel}">
                <span class="simpleText">
                    <c:if test="${canViewGeo}"><a href="/admin/GeoChannel/view.action?id=${stateChannel.id}"></c:if>
                    <c:out value="${stateChannel.name}"/>
                    <c:if test="${canViewGeo}"></a></c:if>
                </span> /
        </c:if>

        <c:choose>
            <c:when test="${entry.geoType != 'ADDRESS'}">
                <span class="simpleText">
                    <c:if test="${canViewGeo}"><a href="/admin/GeoChannel/view.action?id=${entry.id}"></c:if>
                    <c:out value="${entry.name}"/>
                    <c:if test="${canViewGeo}"></a></c:if>
                </span>
            </c:when>
            <c:otherwise>
                <c:out value="${entry.addressText}"/>
            </c:otherwise>
        </c:choose>

    </td>
    <s:set var="stats" value="targetingStats.geolocations[#entry.id]"/>
    <tiles:insertTemplate template="/campaign/ccg/targeting/statsData.jsp">
        <tiles:putAttribute name="data" value="${stats}"/>
    </tiles:insertTemplate>
</tr>
</s:iterator>
</tbody>


