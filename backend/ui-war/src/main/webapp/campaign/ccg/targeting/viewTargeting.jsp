<%@page contentType="text/html" %>
<%@page pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ui" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<ui:section titleKey="ccg.targeting">
    <table class="dataView">
        <thead>
        <tr>
            <th>
                <fmt:message key="ccg.targeting.targetType"/>
            </th>
            <th>
                <fmt:message key="ccg.targeting.target"/>
            </th>
            <th>
                <span class="textWithHint">
                    <fmt:message key="ccg.targeting.dailyUniques"/>
                    <ui:hint>
                        <fmt:message key="ccg.targeting.dailyUniques.tip"/>
                    </ui:hint>
                </span>
            </th>
            <th>
                <fmt:message key="ccg.targeting.monthlyUniques"/>
            </th>
        </tr>
        </thead>

        <%@include file="viewChannels.jsp"%>
        <%@include file="viewCampaignExcludedChannels.jsp"%>
        <%@include file="viewGeotarget.jsp"%>
        <%@include file="viewDeviceTargeting.jsp"%>
        <%@include file="viewOptInStatusTargeting.jsp"%>
        <%@include file="viewISPColocationTargeting.jsp"%>
        <%@include file="viewCCGSites.jsp"%>
        <%@include file="viewUserSampleGroups.jsp"%>

        <tbody id="ccgTotalTargeting">
        <tr class="total">
            <td class="totalText">
                <ad:wrap>
                    <fmt:message key="ccg.targeting.total"/>
                    <ui:hint>
                        <fmt:message key="ccg.targeting.total.tip"/>
                    </ui:hint>
                </ad:wrap>
            </td>
            <td>&nbsp;</td>
            <s:set var="data" value="targetingStats.total"/>
            <fmt:message var="noData" key="ccg.targeting.total.noData"/>
            <td class="number">${empty data.dailyUsers ? noData : ad:formatNumber(data.dailyUsers)}</td>
            <td class="number">&nbsp;</td>
        </tr>
        </tbody>
    </table>
</ui:section>
