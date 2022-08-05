<%@ page contentType="text/html" %>
<%@ page pageEncoding="UTF-8" %>

<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ui" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:set var="channel" value="${model}"/>
<c:set var="isDevice" value="${channel.namespace == 'DEVICE'}"/>
<c:set var="isDiscover" value="${channel.namespace == 'DISCOVER'}"/>
<c:set var="isKeyword" value="${channel.namespace == 'KEYWORD'}"/>
<c:set var="isBehavioral" value="${channel.namespace == 'ADVERTISING'}"/>
<c:set var='isInternalUser' value='${ad:isInternal()}' />
<c:set var='channelLastUsed' value='${channelLastUsed}' />

<jsp:useBean id="channelStatistic" type="com.foros.session.channel.ChannelStatsTO" scope="request"/>

<ui:header styleClass="level2 withTip">
    <h2><fmt:message key="channel.statistics"/></h2>
    <ui:hint><fmt:message key="channel.stats.datesInGMT"/></ui:hint>
</ui:header>

<c:if test="${not empty channelStatistic.activity}">
    <display:table name="channelStatistic.activity" class="dataView" id="row">
        <jsp:useBean id="row" type="com.foros.session.channel.ChannelActivityTO"/>
        <display:setProperty name="basic.msg.empty_list">
            <div class="wrapper">
                <fmt:message key="channel.stats.weekMessage"/>
            </div>
        </display:setProperty>
        <display:column titleKey="channel.stats.date">
            <b><fmt:formatDate value="${row.statsDate}" dateStyle="short"/></b>
        </display:column>
        <c:if test="${channelStatistic.triggersStatPresent}">
            <display:column titleKey="channel.stats.pageHits" class="number">
                <fmt:formatNumber value="${row.pageKeywords}" groupingUsed="true"/>
                (<fmt:formatNumber value="${row.pageKeywordsPercent}" groupingUsed="true" maxFractionDigits="2"/>%)
            </display:column>
            <display:column titleKey="channel.stats.searchHits" class="number">
                <fmt:formatNumber value="${row.searchKeywords}" groupingUsed="true"/>
                (<fmt:formatNumber value="${row.searchKeywordsPercent}" groupingUsed="true" maxFractionDigits="2"/>%)
            </display:column>
            <c:if test="${!isKeyword}">
                <display:column titleKey="channel.stats.urlHits" class="number">
                    <fmt:formatNumber value="${row.urls}" groupingUsed="true"/>
                    (<fmt:formatNumber value="${row.urlsPercent}" groupingUsed="true" maxFractionDigits="2"/>%)
                </display:column>
            </c:if>
            <c:if test="${isBehavioral}">
                <display:column titleKey="channel.stats.urlKeywordHits" class="number">
                    <fmt:formatNumber value="${row.urlKeywords}" groupingUsed="true"/>
                    (<fmt:formatNumber value="${row.urlKeywordsPercent}" groupingUsed="true" maxFractionDigits="2"/>%)
                </display:column>
            </c:if>
            <display:column titleKey="channel.stats.totalHits" class="number">
                <fmt:formatNumber value="${row.totalHits}" groupingUsed="true"/>
            </display:column>
        </c:if>
        <display:column titleKey="channel.stats.totalUniques" class="number">
            <fmt:formatNumber value="${row.totalUniques}" groupingUsed="true"/>
        </display:column>
        <display:column titleKey="channel.stats.activeDailyUniques" class="number">
            <fmt:formatNumber value="${row.activeDailyUniques}" groupingUsed="true"/>
        </display:column>
        <c:if test="${!isDiscover && !isDevice}">
            <display:column titleKey="channel.stats.impressions" class="number">
                <fmt:formatNumber value="${row.impressions}" groupingUsed="true"/>
            </display:column>
            <display:column titleKey="channel.stats.clicks" class="number">
                <fmt:formatNumber value="${row.clicks}" groupingUsed="true"/>
            </display:column>
            <display:column titleKey="channel.stats.ctr" class="number">
                <fmt:formatNumber value="${row.ctr}" groupingUsed="false" minFractionDigits="2" maxFractionDigits="2"/>%
            </display:column>
            <c:if test="${ad:isInternal()}">
                <display:column titleKey="channel.stats.ecpm" class="number">
                    ${ad:formatAndConvertIntoCurrency(row.ecpm, currencyExchangeRate)}
                </display:column>
                <display:column titleKey="channel.stats.value" class="number">
                    ${ad:formatAndConvertIntoCurrency(row.value, currencyExchangeRate)}
                </display:column>
            </c:if>
        </c:if>
    </display:table>
</c:if>

<c:if test="${not empty channelStatistic.serving}">
    <c:set var="serving" value="${channelStatistic.serving}"/>
    <c:set var="captionOfChannelStats">
        <fmt:message key="channel.stats.dataAsOf">
            <fmt:param>
                <fmt:formatDate value="${serving.statsDate}" dateStyle="short" timeZone="GMT"/>
            </fmt:param>
        </fmt:message>
    </c:set>
    <table id="row" class="dataView">
        <thead>
            <tr>
                <th>${captionOfChannelStats}</th>
                <th><fmt:message key="channel.stats.impressions"/> </th>
                <th><fmt:message key="channel.stats.uniques"/></th>
                <c:if test="${ad:isInternal()}">
                    <th><fmt:message key="channel.stats.ecpm"/></th>
                    <th><fmt:message key="channel.stats.value"/></th>
                </c:if>
            </tr>
        </thead>
        <tbody>
        <tr class="odd">
            <td><fmt:message key="channel.stats.opportunities"/></td>
            <td><fmt:formatNumber value="${serving.opportunitiesToServe.imps}" groupingUsed="true"/></td>
            <td><fmt:formatNumber value="${serving.opportunitiesToServe.uniques}" groupingUsed="true"/></td>
            <c:if test="${ad:isInternal()}">
                <td>${ad:formatAndConvertIntoCurrency(serving.opportunitiesToServe.ecpm, currencyExchangeRate)}</td>
                <td>${ad:formatAndConvertIntoCurrency(serving.opportunitiesToServe.value, currencyExchangeRate)}</td>
            </c:if>
        </tr>
        <tr class="odd">
            <td><fmt:message key="channel.stats.servedImps"/></td>
            <td><fmt:formatNumber value="${serving.served.imps}" groupingUsed="true"/></td>
            <td><fmt:formatNumber value="${serving.served.uniques}" groupingUsed="true"/></td>
            <c:if test="${ad:isInternal()}">
                <td>${ad:formatAndConvertIntoCurrency(serving.served.ecpm, currencyExchangeRate)}</td>
                <td>${ad:formatAndConvertIntoCurrency(serving.served.value, currencyExchangeRate)}</td>
            </c:if>
        </tr>
        <tr class="odd">
            <td><fmt:message key="channel.stats.notServedFOROSServed"/></td>
            <td><fmt:formatNumber value="${serving.forosAdServed.imps}" groupingUsed="true"/></td>
            <td><fmt:formatNumber value="${serving.forosAdServed.uniques}" groupingUsed="true"/></td>
            <c:if test="${ad:isInternal()}">
                <td>${ad:formatAndConvertIntoCurrency(serving.forosAdServed.ecpm, currencyExchangeRate)}</td>
                <td>${ad:formatAndConvertIntoCurrency(serving.forosAdServed.value, currencyExchangeRate)}</td>
            </c:if>
        </tr>
        <c:if test="${not empty serving.nonForosAdServed}">
            <tr class="odd">
                <td><fmt:message key="channel.stats.notServedNoFOROSServed"/></td>
                <td><fmt:formatNumber value="${serving.nonForosAdServed.imps}" groupingUsed="true"/></td>
                <td><fmt:formatNumber value="${serving.nonForosAdServed.uniques}" groupingUsed="true"/></td>
                <c:if test="${ad:isInternal()}">
                    <td>${ad:formatAndConvertIntoCurrency(serving.nonForosAdServed.ecpm, currencyExchangeRate)}</td>
                    <td>${ad:formatAndConvertIntoCurrency(serving.nonForosAdServed.value, currencyExchangeRate)}</td>
                </c:if>
            </tr>
        </c:if>
        </tbody>
    </table>
</c:if>

<c:if test="${not empty channelStatistic.activity and empty channelStatistic.serving and !isDiscover and !isDevice}">
    <div class="wrapper">
        <fmt:message key="channel.stats.dayMessage"/>
    </div>
</c:if>

<c:if test="${empty channelStatistic.activity and empty channelStatistic.serving}">
    <div class="wrapper">
        <fmt:message key="channel.stats.weekMessage"/>
    </div>
</c:if>

<c:if test="${not empty channelStatistic.channelOverlap}">
    <display:table name="channelStatistic.channelOverlap" id="overlap" class="dataView">
        <jsp:useBean id="overlap" type="com.foros.session.channel.ChannelOverlapTO"/>
        <display:setProperty name="basic.msg.empty_list">
            <div class="wrapper">
                <fmt:message key="channel.stats.dayMessage"/>
            </div>
        </display:setProperty>
        <display:column titleKey="channel.stats.channelName">
            <a href="/admin/channel/view.action?id=${overlap.channelId}"><c:out value="${overlap.channelName}"/></a>
        </display:column>
        <display:column titleKey="channel.stats.uniques" class="number">
            <fmt:formatNumber value="${overlap.totalUniques}"/>
        </display:column>
        <display:column titleKey="channel.stats.commonUniques" class="number">
            <fmt:formatNumber value="${overlap.commonUniques}"/>
        </display:column>
        <c:if test="${channel.channelType == 'B'}">
            <display:column titleKey="channel.stats.commonTriggers" class="number">
                <c:choose>
                    <c:when test="${not empty overlap.commonTriggers}">
                        <fmt:formatNumber value="${overlap.commonTriggers}"/>
                    </c:when>
                    <c:otherwise>
                        <fmt:message key="notAvailable"/>
                    </c:otherwise>
                </c:choose>
            </display:column>
        </c:if>
        <display:column titleKey="channel.stats.overlapLevel" class="number">
            <fmt:formatNumber value="${overlap.overlapLevel}" maxFractionDigits="0"/><c:if test="${not empty overlap.overlapLevel}">%</c:if>
        </display:column>
    </display:table>
</c:if>

<c:set var='canViewChannelReport' value="${ad:isPermitted('Report.Channel.run', channel)}"/>
<c:set var='canViewTriggersReport' value="${ad:isPermitted('Report.ChannelTriggers.run', channel) && !isKeyword}"/>
<c:set var='canViewSitesReport' value="${ad:isPermitted('Report.ChannelSites.run', channel)}"/>
<c:set var='canViewInventoryForecast' value="${ad:isPermitted('Report.ChannelInventory.run', channel)}"/>
<c:set var='canViewChannelUsage' value="${ad:isPermitted('Report.ChannelUsage.run', channel)}"/>
<c:set var="reportContext">${_context}</c:set>

<c:choose>
    <c:when test="${contextName == 'global.menu.advertisers' and reportContext=='/admin'}">
        <c:set var="reportContext">${_context}/advertiser</c:set>
    </c:when>
    <c:when test="${contextName == 'global.menu.cmps' and reportContext=='/admin'}">
        <c:set var="reportContext">${_context}/cmp</c:set>
    </c:when>
</c:choose>

<c:if test="${canViewChannelReport || canViewTriggersReport || canViewSitesReport || canViewInventoryForecast || canViewChannelUsage}">
    <ui:section>
        <ui:fieldGroup>
            <ui:field labelKey="channel.stats.reports">
                <c:if test="${canViewChannelReport}">
                    <a class="button" href="${reportContext}/reporting/channel/options.action?accountId=${channel.account.id}&channelId=${channel.id}">
                        <fmt:message key="reports.channelReport"/></a>
                </c:if>
                <c:if test="${canViewTriggersReport}">
                    <a class="button" href="${_context}/reporting/channelTriggers/options.action?channelId=${channel.id}">
                        <fmt:message key="reports.channelTriggersReport"/></a>
                </c:if>
                <c:if test="${canViewSitesReport}">
                    <a class="button" href="${_context}/reporting/channelSites/options.action?accountId=${channel.account.id}&channelId=${channel.id}">
                        <fmt:message key="reports.channelSitesReport"/></a>
                </c:if>
                <c:if test="${canViewInventoryForecast}">
                    <a class="button" href="${_context}/reporting/channelInventory/options.action?channelId=${channel.id}">
                        <fmt:message key="channel.channelInventory"/></a>
                </c:if>
                <c:if test="${canViewChannelUsage}">
                    <a class="button" href="${_context}/reporting/channelUsage/options.action?accountId=${channel.account.id}&channelId=${channel.id}">
                        <fmt:message key="reports.channelUsageReport"/></a>
                </c:if>
            </ui:field>
        </ui:fieldGroup>
    </ui:section>
</c:if>

<c:if test="${(channel.visibility == 'PUB' or channel.visibility == 'CMP') and not empty channelStatistic.alsoUsed}">
    <ui:section titleKey="channel.usage.relatedChannels">
        <ui:fieldGroup>
            <c:forEach items="${channelStatistic.alsoUsed}" var="stat">
                <ui:field>
                    <div>
                        <fmt:message key="channel.usage.alsoUsed.message">
                            <fmt:param value="${stat.count}"/>
                            <fmt:param>
                                <a href="${_context}/channel/view.action?id=${stat.channelId}"><c:out value="${stat.channelName}"/></a>
                            </fmt:param>
                        </fmt:message>
                    </div>
                </ui:field>
            </c:forEach>
        </ui:fieldGroup>
    </ui:section>
</c:if>
