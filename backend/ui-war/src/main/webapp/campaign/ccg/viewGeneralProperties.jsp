<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ui" %>

<%@ include file="/regularchecks/logChecksPopupJS.jsp" %>
<script type="text/javascript">
    function openDeliverySchedule() {
        var url = "${_context}/campaign/group/viewSchedule.action?id=${id}";
        url = url + "&runningKey=deliverySchedule.ccg.running&notRunningKey=deliverySchedule.campaign.not.running&availableKey=deliverySchedule.available.periods&conflictedKey=deliverySchedule.conflicted";
        var options = "resizable=no,menubar=no,status=no,toolbar=no,scrollbars=no,location=0,dialog=no,height=350,width=950,left=100,top=100,titlebar=no";
        window.open(url, "printpop", options);
    }
    
    $(function(){
        var iWidth  = $('#wrapper').parent('.field').width();
        $('#wrapper').css({zoom:1,width:iWidth+'px'});
    });
    $(document).ready(function() {
        $('#logChecksButton').click(function(event) {
            popupLogChecks('group', ${id}, event);
        });

        if (window.location.hash == '#error:version'){
            $('<span></span>').addClass('errors').text('<fmt:message key="errors.version"/>').appendTo('#popupVersionError');
            $("#popupVersionError").addClass('wrapper');
            window.location.hash = '';
        }
    });
</script>
<div id="popupVersionError">
</div>
<ui:section titleKey="form.general.properties">
    <table class="grouping fieldGroups">
        <tr>
            <td>
                <ui:fieldGroup>
                    <ui:field labelKey="ccg.status">
                        <ui:statusButtonGroup
                            descriptionKey="${displayStatus.description}"
                            entity="${model}"
                            restrictionEntity="AdvertiserEntity"
                            activatePage="activate${pageExt}.action"
                            inactivatePage="inactivate${pageExt}.action"
                            deletePage="delete${pageExt}.action"
                            undeletePage="undelete${pageExt}.action"

                        >
                            <ui:qaStatusButons entity="${model}" restrictionEntity="AdvertiserEntity"
                                    approvePage="approve${pageExt}.action" declinePage="decline${pageExt}.action" />
                        </ui:statusButtonGroup>
                    </ui:field>

                    <c:if test="${ad:isPermitted('AdvertiserEntity.viewCCGCheck', model)}">
                        <ui:field labelKey="checks.checkStatus">
                            ${checkStatusCaption}
                            <c:if test="${ad:isPermitted('AdvertiserEntity.updateCCGCheck', model)}">
                                <ui:button id="logChecksButton" message="checks.logChecks" type="link" />
                            </c:if>
                        </ui:field>
                    </c:if>

                    <ui:field labelKey="creative.campaignCreative.dates">
                        <fmt:formatDate value="${calculatedStartDate}" var="formattedDateStart"
                                        type="both" timeStyle="short" dateStyle="short"
                                        timeZone="${account.timezone.key}"/>
                        <c:out value="${formattedDateStart}"/>
                        &ndash;
                        <c:set var="textVal">
                            <s:if test="calculatedEndDate != null">
                                <fmt:formatDate value="${calculatedEndDate}" var="formattedDateEnd"
                                    type="both" timeStyle="short" dateStyle="short"
                                    timeZone="${account.timezone.key}"/>
                                <c:out value="${formattedDateEnd}"/>
                            </s:if>
                            <s:else>
                                <fmt:message key="ccg.delivery.notSet"/>
                            </s:else>
                        </c:set>
                        <ui:text subClass="date" text="${pageScope.textVal}"/>
                    </ui:field>

                    <ui:frequencyCapView/>

                    <s:if test="sequentialAdservingFlag">
                        <fmt:message key="campaign.creative.sequentialAdserving.enabled" var="sequentialAdservingMessage"/>
                        <ui:simpleField labelKey="campaign.creative.sequentialAdserving" value="${sequentialAdservingMessage}"/>
                        
                        <c:set var="rotationCriteriaMessage">
                            <fmt:formatNumber value="${rotationCriteria}" groupingUsed="true" maxFractionDigits="0"/> <fmt:message key="campaign.creative.rotationCriteria.impressions"/>
                        </c:set>
                        <ui:simpleField labelKey="campaign.creative.rotationCriteria" value="${rotationCriteriaMessage}"/>
                    </s:if>
                    <s:if test="%{(isInternal() || ccgRate.rateType.name == 'CPM') && optimizeCreativeWeightFlag}">
                        <fmt:message key="campaign.creative.optimization.enabled" var="optimizationMessage"/>
                        <ui:simpleField labelKey="campaign.creative.optimization" value="${optimizationMessage}"/>
                    </s:if>
                    <s:if test="model.deliveryScheduleFlag or !campaignScheduleSet.schedules.empty">
                        <ui:field labelKey="deliverySchedule.label">
                            <ui:button message="view.deliverySchedule"
                                       subClass="${viewDeliverySchedule == true ? 'withWarning' : ''}"
                                       onclick="openDeliverySchedule();"/>
                        </ui:field>
                    </s:if>
                </ui:fieldGroup>
            </td>
            <td>
                <ui:fieldGroup>
                    <c:set var="currencyCode" value="${account.currency.currencyCode}"/>
                    <ui:field labelKey="ccg.ccgRate">
                        <c:set var="textVal" value="${ad:formatCurrency(ccgRate.rate, currencyCode)} ${ccgRate.rateType}"/>
                        <ui:text text="${pageScope.textVal}"/>
                    </ui:field>
                    <ui:field labelKey="ccg.bidStrategy">
                        <c:if test="${bidStrategy.name() == 'MAXIMISE_REACH'}">
                            <fmt:message key="ccg.bidStrategy.maximiseReach"/>
                        </c:if>
                        <c:if test="${bidStrategy.name() == 'MINIMUM_CTR_GOAL'}">
                            <fmt:message key="ccg.bidStrategy.minimumCtrOf">
                                <fmt:param><fmt:formatNumber value="${minCtrGoal}" groupingUsed="true" maxFractionDigits="3"/></fmt:param>
                            </fmt:message>
                        </c:if>
                    </ui:field>
                        <s:if test="budget != null">
                            <ui:simpleField labelKey="ccg.total.budget" value="${ad:formatCurrency(budget, currencyCode)}"/>
                        </s:if>
                        <s:else>
                            <fmt:message var="budgetNotSetLabel" key="ccg.notSet"/>
                            <ui:simpleField labelKey="ccg.total.budget" value="${budgetNotSetLabel}"/>
                        </s:else>
                    <c:if test="${ad:isInternal()}">
                        <ui:simpleField labelKey="ccg.spent.budget" value="${ad:formatCurrency(ccgStats.spentBudget, currencyCode)}"/>
                        <c:if test="${pageExt ne 'Text' or not empty budget}">
                            <ui:simpleField labelKey="ccg.available.budget" value="${ad:formatCurrency(budget - ccgStats.spentBudget, currencyCode)}"/>
                        </c:if>
                    </c:if>

                    <s:set var="formattedDailyBudget">${ad:formatCurrency(calculatedDailyBudget, currencyCode)}</s:set>
                    <s:include value="deliveryPacingView.jsp">
                        <s:param name="deliveryPacingLetter" value="deliveryPacing.letter"/>
                        <s:param name="formattedDailyBudget" value="formattedDailyBudget"/>
                        <s:param name="type" value="ccgType.letter"/>
                        <s:param name="includedInCampaign" value="%{false}"/>
                    </s:include>

                </ui:fieldGroup>
            </td>
            <td>
                <ui:fieldGroup>
                    <fmt:formatNumber var="imps" value="${ccgStats.imps}" groupingUsed="true" maxFractionDigits="0"/>
                    <ui:simpleField labelKey="ccg.totalImpressions" value="${imps}"/>
                    <ui:field labelKey="ccg.totalClicks">
                        <c:set var="totalClicks"><fmt:formatNumber value="${ccgStats.clicks}" groupingUsed="true" maxFractionDigits="0"/>
                            <c:if test="${ccgStats.ctr > 0}">
                                (<fmt:formatNumber value="${ccgStats.ctr}" groupingUsed="false" maxFractionDigits="2"/>%)
                            </c:if>
                        </c:set>
                        <ui:text text="${pageScope.totalClicks}"/>
                    </ui:field>
                    <c:if test="${ccgStats.showPostImpConv}">
                        <ui:field labelKey="ccg.totalPostImpConv">
                            <c:set var="totalPostImpConv">
                                <fmt:formatNumber value="${ccgStats.postImpConv}" groupingUsed="true" maxFractionDigits="0"/>
                                <c:if test="${ccgStats.postImpConvCr > 0}">
                                    (<fmt:formatNumber value="${ccgStats.postImpConvCr}" groupingUsed="false" maxFractionDigits="2"/>%)
                                </c:if>
                            </c:set>
                            <c:choose>
                                <c:when test="${ad:isPermitted('Report.run', 'conversions')}">
                                    <c:set var="reporting" value="${_context}/reporting"/>
                                    <ui:button messageText="${pageScope.totalPostImpConv}" href="${reporting}/conversions/options.action?groupIds=${id}" />
                                </c:when>
                                <c:otherwise>
                                    <ui:text text="${pageScope.totalPostImpConv}"/>
                                </c:otherwise>
                            </c:choose>
                        </ui:field>
                    </c:if>
                    <c:if test="${ccgStats.showPostClickConv}">
                        <ui:field labelKey="ccg.totalPostClickConv">
                            <c:set var="totalPostClickConv">
                                <fmt:formatNumber value="${ccgStats.postClickConv}" groupingUsed="true" maxFractionDigits="0"/>
                                <c:if test="${ccgStats.postClickConvCr > 0}">
                                    (<fmt:formatNumber value="${ccgStats.postClickConvCr}" groupingUsed="false" maxFractionDigits="2"/>%)
                                </c:if>
                            </c:set>
                            <c:choose>
                                <c:when test="${ad:isPermitted('Report.run', 'conversions')}">
                                    <c:set var="reporting" value="${_context}/reporting"/>
                                    <ui:button messageText="${pageScope.totalPostClickConv}" href="${reporting}/conversions/options.action?groupIds=${id}" />
                                </c:when>
                                <c:otherwise>
                                    <ui:text text="${pageScope.totalPostClickConv}"/>
                                </c:otherwise>
                            </c:choose>
                        </ui:field>
                    </c:if>
                    <s:if test="isInternal() && ccgRate.rateType.name == 'CPC'">
                        <ui:field labelKey="ccg.auctionCtr">
                            <s:if test="inRandomMode">
                                <fmt:message key="ccg.auctionCtr.random"/>
                            </s:if>
                            <s:else>
                                <fmt:formatNumber var="auctionCtrFormatted" value="${ccgStats.auctionCtr}"  groupingUsed="true" minFractionDigits="4" maxFractionDigits="4"/>
                                <ui:text text="${auctionCtrFormatted}"/>%
                            </s:else>
                        </ui:field>
                    </s:if>
                    <s:if test="isInternal() && ccgRate.rateType.name != 'CPM'">
                        <ui:field labelKey="ccg.auctionEcpm">
                            <table class="fieldAndAccessories">
                                <tr>
                                    <td>
                                        <s:if test="inRandomMode">
                                            <fmt:message key="ccg.auctionEcpm.random"/>
                                        </s:if>
                                        <s:else>
                                            ${ad:formatCurrencyExt(ccgStats.auctionEcpm, currencyCode, auctionEcpmPrecision)}
                                        </s:else>
                                    </td>
                                    <c:set var="canResetCtrDate" value="#{ad:isPermitted('AdvertiserEntity.resetCtr', model)}"/>
                                    <c:if test="${not inRandomMode and canResetCtrDate}">
                                        <td>
                                            <script type="text/javascript">
                                                function resetCtr() {
                                                    if (!confirm('${ad:formatMessage('ccg.confirmResetCtr')}')) {
                                                        return false;
                                                    }
                                                    $('#resetForm').submit();
                                                    return true;
                                                }
                                            </script>
                                            <s:if test="ccgType.letter == 'T'">
                                                <s:form action="%{#attr.moduleName}/resetTextCtr" id="resetForm" styleClass="hide">
                                                    <s:hidden name="id"/>
                                                    <s:hidden name="version"/>
                                                </s:form>
                                            </s:if>
                                            <s:else>
                                                <s:form action="%{#attr.moduleName}/resetDisplayCtr" id="resetForm" styleClass="hide">
                                                    <s:hidden name="id"/>
                                                    <s:hidden name="version"/>
                                                </s:form>
                                            </s:else>
                                            <ui:button message="form.reset" type="link" onclick="resetCtr();"/>
                                            <td class="withTip">
                                                <ui:hint>
                                                    <fmt:message key="ccg.resetCtr.hint"/>
                                                </ui:hint>
                                            </td>
                                        </td>
                                    </c:if>
                                </tr>
                            </table>
                        </ui:field>
                    </s:if>
                    <fmt:formatNumber var="totalUniqueUsers" value="${ccgStats.totalUniqueUsers}" groupingUsed="true" maxFractionDigits="0"/>
                    <ui:simpleField labelKey="ccg.totalUniqueUsers" value="${totalUniqueUsers}"/>
                    <s:if test="isInternal()">
                        <c:set var="showWaterfallLink" value="${ad:isPermitted('Report.Waterfall.run', model)}"/>
                        <s:if test="ccgStats.auctionsLost != 0">
                            <ui:field labelKey="ccg.auctionsLost">
                                <c:if test="${showWaterfallLink}"><a href="${_context}/reporting/waterfall/run.action?ccgId=${id}"></c:if>
                                    <c:out value="${ccgStats.auctionsLost}"/>
                                <c:if test="${showWaterfallLink}"></a></c:if>
                            </ui:field>
                        </s:if>
                        <s:if test="ccgStats.selectionFailures != 0">
                            <ui:field labelKey="ccg.selectionFailures">
                                <c:if test="${showWaterfallLink}"><a href="${_context}/reporting/waterfall/run.action?ccgId=${id}"></c:if>
                                    <c:out value="${ccgStats.selectionFailures}"/>
                                <c:if test="${showWaterfallLink}"></a></c:if>
                            </ui:field>
                        </s:if>
                    </s:if>
                </ui:fieldGroup>
            </td>
        </tr>
    </table>
</ui:section>
