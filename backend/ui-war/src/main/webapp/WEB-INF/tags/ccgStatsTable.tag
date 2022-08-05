<%@ tag import="com.foros.model.campaign.CampaignCreativeGroup" %>
<%@ tag language="java" body-content="empty" description="Displays Campaign Creative Groups statistics" %>
<%@ attribute name="groups" required="true" type="java.util.Collection" %>
<%@ attribute name="showUniqueUsers" required="true" type="java.lang.Boolean" %>
<%@ attribute name="showPostImpConv" required="true" type="java.lang.Boolean" %>
<%@ attribute name="showPostClickConv" required="true" type="java.lang.Boolean" %>
<%@ attribute name="ccgType" required="true" %>
<%@ attribute name="bulkChangeAvailable" %>
<%@ attribute name="showChannelTarget" required="true" type="java.lang.Boolean" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>

<script type="text/javascript" src="/thirdparty/jquery.tablesorter.min.js"></script>

<script type="text/javascript">
    function toggleAllGroups(header) {
        $('[name=selectedGroups]:enabled').prop({checked : header.checked}).trigger('change');
    }

    function getSelectedIds() {
        var ccgIds = [];
        $('[name=selectedGroups]').each(function () {
            if (this.checked) {
                ccgIds.push(this.value);
            }
        });
        return ccgIds;
    }
</script>

<c:if test="${empty pageScope.bulkChangeAvailable}">
    <c:set var="bulkChangeAvailable" value="false"/>
</c:if>

<c:choose>
    <c:when test="${not empty pageScope.groups}">
        <table class="dataView row_selectable" id="ccgStatsTable">
            <thead>
                <tr>
                    <c:if test="${bulkChangeAvailable}">
                        <th width="1">
                            <input type="checkbox" onclick="toggleAllGroups(this)"/>
                        </th>
                    </c:if>
                    <th class="headerSortDown"><fmt:message key="${pageScope.ccgType != 'Text'?'campaign.creative.group':'campaign.creative.textGroup'}" /></th>
                    <c:if test="${pageScope.showChannelTarget}">
                        <th><fmt:message key="ccg.channelTarget"/></th>
                    </c:if>
                    <th><fmt:message key="campaign.creative.group.impressions" /></th>
                    <th><fmt:message key="campaign.creative.group.clicks" /></th>
                    <th><fmt:message key="campaign.creative.group.ctr" /></th>
                    <c:if test="${pageScope.showPostImpConv}">
                        <th><fmt:message key="campaign.creative.group.postImpConv" /></th>
                        <th><fmt:message key="campaign.creative.group.postImpConvCr" /></th>
                    </c:if>
                    <c:if test="${pageScope.showPostClickConv}">
                        <th><fmt:message key="campaign.creative.group.postClickConv" /></th>
                        <th><fmt:message key="campaign.creative.group.postClickConvCr" /></th>
                    </c:if>
                    <c:if test="${pageScope.showUniqueUsers}">
                        <th><fmt:message key="campaign.creative.group.uniqueUsers" /></th>
                    </c:if>
                    <c:if test="${ad:isInternal()}">
                        <c:if test="${availableCreditUsed}">
                            <th><fmt:message key="campaign.creative.group.creditUsed" /></th>
                        </c:if>
                        <th><fmt:message key="campaign.creative.group.total_cost" /></th>
                        <c:if test="${availableCreditUsed}">
                            <th><fmt:message key="campaign.creative.group.total_value" /></th>
                        </c:if>
                        <th><fmt:message key="campaign.creative.group.ecpm" /></th>
                    </c:if>
                </tr>
            </thead>
            <tbody>
            <c:set var="displayStatusDeleted" value="<%=CampaignCreativeGroup.DELETED%>"/>
            <c:forEach var="group" items="${groups}">
                    <tr>
                        <c:if test="${bulkChangeAvailable}">
                            <td>
                                <input class="row_select_box"
                                       type="checkbox"
                                       name="selectedGroups"
                                       value="${group.id}"
                                       ${group.displayStatus == displayStatusDeleted ? 'disabled=\'disabled\'' : ''}/>
                            </td>
                        </c:if>
                        <c:set var="ds" value="${group.displayStatus.major.letter}"/>
                        <td data-color="${ds == 'N'? 1: ds == 'A'? 2: ds == 'L'? 3: ds == 'I'? 4: 5}">
                            <ui:displayStatus displayStatus="${group.displayStatus}">
                                <a class="preText" href="group/view${pageScope.ccgType}.action?id=${group.id}"><c:out value="${ad:appendStatus(group.name, group.displayStatus)}"/></a>
                            </ui:displayStatus>
                        </td>
                        <c:if test="${pageScope.showChannelTarget}">
                            <c:set var="ds" value="${group.channelTarget != null? group.channelTarget.displayStatus.major.letter: ''}"/>
                            <td data-color="${ds == 'N'? 1: ds == 'A'? 2: ds == 'L'? 3: ds == 'I'? 4: ds == 'D'? 5: 6}">
                                <c:if test="${group.tgtType.letter == 'C'}">
                                    <c:if test="${group.channelTarget != null}">
                                        <c:choose>
                                            <c:when test="${group.targetViewable}">
                                                <ui:displayStatus displayStatus="${group.channelTarget.displayStatus}">
                                                    <a href="${_context}/channel/view.action?id=${group.channelTarget.id}">
                                                        <c:out value="${group.channelTarget.name}"/>
                                                    </a>
                                                </ui:displayStatus>
                                            </c:when>
                                            <c:otherwise>
                                                <ui:displayStatus displayStatus="${group.channelTarget.displayStatus}">
                                                    <c:out value="${group.channelTarget.name}"/>
                                                </ui:displayStatus>
                                            </c:otherwise>
                                        </c:choose>
                                    </c:if>
                                    <c:if test="${group.channelTarget == null && group.target.letter == 'U'}">
                                        <fmt:message key="channel.untargeted"/>
                                    </c:if>
                                    <c:if test="${group.channelTarget == null && group.target.letter == 'N'}">
                                        <fmt:message key="channel.notset"/>
                                    </c:if>
                                </c:if>
                            </td>
                        </c:if>
                        <td class="number">
                            <fmt:formatNumber value="${group.imps}" groupingUsed="true"/>
                        </td>
                        <td class="number">
                            <fmt:formatNumber value="${group.clicks}" groupingUsed="true"/>
                        </td>
                        <td class="number">
                            <fmt:formatNumber value="${group.ctr}" maxFractionDigits="2"/>%
                        </td>
                        <c:if test="${pageScope.showPostImpConv}">
                            <td class="number">
                                <fmt:formatNumber value="${group.postImpConv}" groupingUsed="true"/>
                            </td>
                            <td class="number">
                                <fmt:formatNumber value="${group.postImpConvCr}" maxFractionDigits="2"/>%
                            </td>
                        </c:if>
                        <c:if test="${pageScope.showPostClickConv}">
                            <td class="number">
                                <fmt:formatNumber value="${group.postClickConv}" groupingUsed="true"/>
                            </td>
                            <td class="number">
                                <fmt:formatNumber value="${group.postClickConvCr}" maxFractionDigits="2"/>%
                            </td>
                        </c:if>
                        <c:if test="${pageScope.showUniqueUsers}">
                            <td class="number">
                                <fmt:formatNumber value="${group.uniqueUsers}" groupingUsed="true"/>
                            </td>
                        </c:if>
                        <c:if test="${ad:isInternal()}">
                            <c:if test="${availableCreditUsed}">
                                <td class="currency" data-value="${group.creditUsed}">
                                    ${ad:formatCurrency(group.creditUsed, account.currency.currencyCode)}
                                </td>
                            </c:if>
                            <td class="currency" data-value="${group.totalCost}">
                                ${ad:formatCurrency(group.totalCost, account.currency.currencyCode)}
                            </td>
                            <c:if test="${availableCreditUsed}">
                                <td class="currency" data-value="${group.totalValue}">
                                    ${ad:formatCurrency(group.totalValue, account.currency.currencyCode)}
                                </td>
                            </c:if>
                            <td class="currency" data-value="${group.ecpm}">
                                ${ad:formatCurrency(group.ecpm, account.currency.currencyCode)}
                            </td>
                        </c:if>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
        <script type="text/javascript">
            $(function(){
                var nameIndex = "${bulkChangeAvailable? 1: 0}"; 
                $('#ccgStatsTable').addClass('tablesorter').tablesorter({
                    "headers": { 0: { "sorter": nameIndex == 0} },
                    "sortList": [[nameIndex,0]], 
                    "textExtraction": function(node){
                        var jqNode  = $(node),
                        txt         = $.trim(jqNode.text().replace('-','_'));
                        switch (jqNode.attr('class')) {
                            case 'number':
                                return UI.Localization.parseFloat(txt);
                            case 'currency':
                                return jqNode.data('value');
                            default:
                                return jqNode.data('color')?jqNode.data('color')+'_'+txt:txt;
                        }
                    }
                }).find('th')<c:if test="${bulkChangeAvailable}">.not(':first')</c:if>.wrapInner('<div class="icon" />');
            });
        </script>
    </c:when>
    <c:otherwise>
        <span class=""><fmt:message key="nothing.found.to.display" /></span>
    </c:otherwise>
</c:choose>
