<%@ page contentType="text/html" %>
<%@ page pageEncoding="UTF-8" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/struts-tags" prefix="s" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ui" %>

<%@include file="searchTabTips.jsp"%>
<ui:fieldGroup>
<ui:field labelKey="channel.search.name" labelForId="searchName">
    <table class="fieldAndAccessories">
        <tr>
            <td class="withField">
                <s:textfield name="searchCriteria.searchName" id="searchName"  cssClass="middleLengthText1"/>
            </td>
            <td class="withField">
                <label class="withInput withCheckbox"><s:checkbox disabled="%{searchCriteria.disableSearchMyChannels}" id="searchMyChannels" name="searchCriteria.searchMyChannels"/><fmt:message key="channel.myChannels"/></label>
                <c:if test="${searchCriteria.disableSearchMyChannels}">
                    <s:hidden name="searchCriteria.searchMyChannels"/>
                </c:if>
            </td>
            <td class="withField">
                <label class="withInput withCheckbox"><s:checkbox disabled="%{searchCriteria.disableSearchPublicChannels}" id="searchPublicChannels" name="searchCriteria.searchPublicChannels" /><fmt:message key="channel.publicChannels"/></label>
                <c:if test="${searchCriteria.disableSearchPublicChannels}">
                    <s:hidden name="searchCriteria.searchPublicChannels"/>
                </c:if>
            </td>
        </tr>
    </table>
</ui:field>
<ui:field labelKey="channel.search.contents" labelForId="searchContent" errors="searchErrors">
    <table class="fieldAndAccessories">
            <tr>
            <td class="withField">
                <s:textfield name="searchCriteria.content" id="searchContent" cssClass="middleLengthText1"/>
            </td>
            <td class="withTip">
                <ui:hint>
                    <fmt:message key="channel.search.inputKeywordOrURL"/>
                </ui:hint>
            </td>
            <td class="withButton">
                <ui:button id="searchButton" message="form.search" type="submit"/>
            </td>
            </tr>
        </table>
    </ui:field>
</ui:fieldGroup>

<div id="searchWait" style="display:none;">
    <fmt:message key="form.select.wait"/>
</div>

<c:set var="matchedChannelForms" scope="request" value="${channels}"/>
<c:if test="${matchedChannelForms != null}">
<div id="searchChannelsResultsDiv" class="dataViewInnerSection">
    <c:set var="messageKey" value="channel.marketplace.notFound"/>
    <c:set var="accountId" value="${account.id}"/>
    <c:set var="notFoundMessage">
        <%@include file="notFoundMessage.jsp"%>
    </c:set>

<display:table name="matchedChannelForms" class="dataView" id="matchedChannelRow">
    <display:setProperty name="basic.msg.empty_list" value="${notFoundMessage}"/>
    <display:column class="hide" property="accountId" titleKey="channel.authorId" headerClass="hide"/>
    <display:column titleKey="channel.name">
        <ui:displayStatus displayStatus="${matchedChannelRow.displayStatus}">
            <c:if test="${accountId != matchedChannelRow.accountId}">
                <ui:text text="${matchedChannelRow.accountName}" maxLength="50" /> /
            </c:if>
            <a href="${_context}/channel/view.action?id=${matchedChannelRow.id}" target="_blank">
                <ui:text text="${matchedChannelRow.name}" maxLength="50" />
            </a>
        </ui:displayStatus>
    </display:column>
    <display:column title="${impressionTip}" style="text-align:right;">
        <fmt:formatNumber value="${matchedChannelRow.imps}" maxFractionDigits="0"  groupingUsed="true"/>
    </display:column>
    <display:column title="${reuseTip}" style="text-align:right;">
        <c:choose>
            <c:when test="${not empty matchedChannelRow.reuse}">
                <fmt:formatNumber value="${matchedChannelRow.reuse}" maxFractionDigits="0" groupingUsed="true"/>
            </c:when>
            <c:otherwise>
                 <fmt:message key="notAvailable"/>
            </c:otherwise>
        </c:choose>
    </display:column>
    <display:column title="${lastUseTip}" style="text-align:right;">
        <c:choose>
            <c:when test="${not empty matchedChannelRow.lastUse}">
                <fmt:formatDate value="${matchedChannelRow.lastUse}" dateStyle="short" timeZone="GMT"/>
            </c:when>
            <c:otherwise>
                <fmt:message key="notAvailable"/>
            </c:otherwise>
        </c:choose>
    </display:column>
    <display:column title="${populationTip}" style="text-align:right;">
        <fmt:formatNumber value="${matchedChannelRow.userCount}" maxFractionDigits="0" groupingUsed="true"/>
    </display:column>
    <display:column titleKey="channel.table.actions" style="align:right;width:100px;text-align:center">
            <div>
                <table class="grouping" align="center">
                    <tr>
                        <td>
                            <ui:button message="form.${param.channelAction}" onclick="${param.channelAction}Channel({
                                    id: '${ad:escapeJavaScriptInTag(matchedChannelRow.id)}',
                                    name: '${ad:escapeJavaScriptInTag(matchedChannelRow.name)}',
                                    account : {
                                        id: ${ad:escapeJavaScriptInTag(matchedChannelRow.accountId)},
                                        name: '${ad:escapeJavaScriptInTag(matchedChannelRow.accountName)}'
                                    }
                                 })"
                             />
                        </td>
                    </tr>
                </table>
            </div>
    </display:column>
</display:table>
</div>
</c:if>
