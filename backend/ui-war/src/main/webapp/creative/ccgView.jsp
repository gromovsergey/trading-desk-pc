<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ui" %>

<h2><fmt:message key="creative.campaignCreatives"/></h2>

<c:choose>
    <c:when test="${not empty campaignCreativesList}">
        <table class="dataView" id="campaignCreative">
            <thead>
                <tr>
                    <th><fmt:message key="creative.campaignCreative.campaign"/></th>
                    <th><fmt:message key="creative.campaignCreative.creativeGroup"/></th>
                    <c:if test="${creativeType!='Text'}">
                        <th><fmt:message key="creative.campaignCreative"/></th>
                    </c:if>
                    <th><fmt:message key="creative.campaignCreative.dates"/></th>
                    <th><fmt:message key="creative.campaignCreative.status"/></th>
                </tr>
            </thead>
            <tbody>
                <c:forEach items="${campaignCreativesList}" var="campaignCreative">
                    <tr>
                        <c:if test="${campaignCreative.creativeGroup.campaign.id != prevCampaignId}">
                            <td rowspan="<c:out value="${campaignRowspans[campaignCreative.creativeGroup.campaign.id]}"/>">
                                <ui:displayStatus displayStatus="${campaignCreative.creativeGroup.campaign.displayStatus}">
                                    <a href="${_context}/campaign/view.action?id=${campaignCreative.creativeGroup.campaign.id}">
                                            <c:out value="${campaignCreative.creativeGroup.campaign.name}"/></a>
                                </ui:displayStatus>
                            </td>
                        </c:if>
                        <c:if test="${campaignCreative.creativeGroup.id != prevCreativeGroupId}">
                            <c:set var="displayLink" value="viewText.action"/>
                            <c:if test="${campaignCreative.creativeGroup.ccgType.letter=='D'}">
                                <c:set var="displayLink" value="viewDisplay.action"/>
                            </c:if>
                            <td rowspan="<c:out value="${groupRowspans[campaignCreative.creativeGroup.id]}"/>">
                                <ui:displayStatus displayStatus="${campaignCreative.creativeGroup.displayStatus}">
                                    <a href="${_context}/campaign/group/${displayLink}?id=${campaignCreative.creativeGroup.id}">
                                            <c:out value="${campaignCreative.creativeGroup.name}"/></a>
                                </ui:displayStatus>
                            </td>
                        </c:if>
                        <c:if test="${creativeType!='Text'}">
                        <td>
                            <ui:displayStatus displayStatus="${campaignCreative.displayStatus}">
                                <a href="${_context}/campaign/group/creative/view.action?id=${campaignCreative.id}"><c:out value="${name}"/></a>
                            </ui:displayStatus>
                        </td>
                        </c:if>
                        <td>
                            <s:property value="%{getDateInfoForCC(#attr.campaignCreative)}"/>
                        </td>
                        <td>
                            <fmt:message key="${campaignCreative.displayStatus.description}"/>
                        </td>
                    </tr>
                    <c:set var="prevCampaignId" value="${campaignCreative.creativeGroup.campaign.id}"/>
                    <c:set var="prevCreativeGroupId" value="${campaignCreative.creativeGroup.id}"/>
                </c:forEach>
            </tbody>
        </table>
    </c:when>
    <c:otherwise>
        <div class="wrapper">
            <ui:text textKey="nothing.found.to.display"/>
        </div>
    </c:otherwise>
</c:choose>
