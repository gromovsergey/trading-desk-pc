<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ui" %>
<c:set var="isCCGUpdatePermitted" value="${ad:isPermitted('AdvertiserEntity.update', model)}"/>
<c:if test="${tgtType.letter == 'C' && ad:isPermitted('AdvertiserEntity.editSiteTargeting', account)}">
    <c:set var="sitesSize" value="${groupSites.size()}"/>

    <c:choose>
        <c:when test="${sitesSize > 5}">
            <tbody id="ccgTargetingSites">
            <tr id="ccgTargetingSitesCol">
                <td style="border-bottom: none;">
                    <ad:wrap>
                    <span>
                        <fmt:message key="ccg.targeting.sites"/>
                        <c:if test="${ad:isPermitted('AdvertiserEntity.editSiteTargeting', account) and isCCGUpdatePermitted}">
                            <ui:button message="form.edit" href="edit${ccgType.pageExtension}.action?id=${id}#ccg_site_targeting"/>
                        </c:if>
                    </span>
                        <c:if test="${campaign.marketplaceType == 'FOROS' || campaign.marketplaceType == 'WG'}">
                            <ui:hint>
                                <fmt:message key="ccg.targeting.sites.wgTip"/>
                            </ui:hint>
                        </c:if>
                    </ad:wrap>
                </td>
                <td id="ccgTargetingSitesExpander" colspan="3">
                    <a id="sitesTogle" href="#">
                        [<span class="hideableSites hide">-</span><span class="hideableSites">+</span>]
                        <fmt:message key="ccg.targeting.sites.quantity">
                            <fmt:param value="${sitesSize}"/>
                        </fmt:message>
                    </a>&nbsp;&nbsp;<img id="sites_preloader" class="hide" src="/images/wait-animation-small.gif">
                    <script type="text/javascript">
                        $(function(){
                            var params = $('#ccgForm').serializeArray();
	                       	var sitesAreLoaded = false;
                            $('#sitesTogle').click(function(){
                                if (!sitesAreLoaded) {
                                    $('#sites_preloader').show();
                                    $('#ccgTargetingSitesContainer')
                                    .load('${_context}/campaign/group/sitesWrapper.action', params, function(){
                                        sitesAreLoaded = true;
                                        $('#sites_preloader').hide();
                                        $('#ccgTargetingSitesContainer, #ccgTargetingSitesExpander .hideableSites').toggle();
                                    });
                                    return false;
                                } else {
                                $('#ccgTargetingSitesContainer, #ccgTargetingSitesExpander .hideableSites').toggle();
                                return false;
                                }
                            });
                        });
                    </script>
                </td>
            </tr>
            </tbody>
            <tbody id="ccgTargetingSitesContainer" class="hide">
            </tbody>
        </c:when>
        <c:otherwise>
            <tbody id="ccgTargetingSites">
            <tr>
                <td <c:if test="${sitesSize > 1}">rowspan="${sitesSize}"</c:if>>
                    <ad:wrap>
                    <span>
                        <fmt:message key="ccg.targeting.sites"/>
                        <c:if test="${ad:isPermitted('AdvertiserEntity.editSiteTargeting', account) and isCCGUpdatePermitted}">
                            <ui:button message="form.edit" href="edit${ccgType.pageExtension}.action?id=${id}#ccg_site_targeting"/>
                        </c:if>
                    </span>
                        <c:if test="${campaign.marketplaceType == 'FOROS' || campaign.marketplaceType == 'WG'}">
                            <ui:hint>
                                <fmt:message key="ccg.targeting.sites.wgTip"/>
                            </ui:hint>
                        </c:if>
                    </ad:wrap>
                </td>
                <c:choose>
                    <c:when test="${sitesSize == 0}">
                        <td class="ccg_target">
                            <fmt:message key="ccg.targeting.sites.all"/>
                        </td>
                        <tiles:insertTemplate template="/campaign/ccg/targeting/statsData.jsp">
                            <tiles:putAttribute name="data" value="${targetingStats.country}"/>
                        </tiles:insertTemplate>
                    </c:when>
                    <c:otherwise>
                        <c:set var="canViewSite" value="${ad:isPermitted0('PublisherEntity.view')}"/>
                        <c:forEach var="site" items="${groupSites}" varStatus="row">
                                <td class="ccg_target">
                                <span class="simpleText">
                                    <c:if test="${canViewSite}"><a href="/admin/site/view.action?id=${site.id}"></c:if>
                                    <c:out value="${site.name}"/>
                                    <c:if test="${canViewSite}"></a></c:if>
                                </span>
                                </td>
                                <c:set var="stats" value="${targetingStats.sites[site.id]}"/>
                                <tiles:insertTemplate template="/campaign/ccg/targeting/statsData.jsp">
                                    <tiles:putAttribute name="data" value="${stats}"/>
                                </tiles:insertTemplate>
                            <c:if test="${!row.last}">
                                </tr><tr>
                            </c:if>
                        </c:forEach>
                    </c:otherwise>
                </c:choose>
            </tr>
            </tbody>
        </c:otherwise>
    </c:choose>
</c:if>
