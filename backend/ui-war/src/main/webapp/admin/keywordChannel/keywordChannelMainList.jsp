<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="display" uri="http://displaytag.sf.net/el"%>
<%@ taglib prefix="ad" uri="/ad/serverUI"%>

<s:if test="hasActionErrors()">
    <div style="margin-top: 5px; margin-bottom: 5px">
        <s:actionerror/>
    </div>
</s:if>

<s:if test="not result.isEmpty">
    <div class="wrapper">
        <s:url var="downloadUrl" action="admin/KeywordChannel/download" includeParams="all"/>
        <c:if test="${total gt maxExportResultSize}">
            <c:set var="onclickText">
                return confirm('<fmt:message key="channel.export.tooMany.confirm"><fmt:param value="${maxExportResultSize}"/></fmt:message>');
            </c:set>
        </c:if>
        <ui:button message="channel.exportToCSV" href="${downloadUrl}&format=CSV" onclick="${onclickText}"/>
        <ui:button message="channel.exportToTAB" href="${downloadUrl}&format=TAB" onclick="${onclickText}"/>
    </div>
</s:if>

<ui:pages pageSize="${pageSize}"
      total="${total}"
      selectedNumber="${page}"
      visiblePagesCount="10"
      handler="goToPage"
      displayHeader="true"/>

<c:set var="canViewAccount" value="${ad:isPermitted('Account.view', 'Internal')}"/>
<s:fielderror>
    <s:param value="'search'"/>
</s:fielderror>

<s:if test="hasActionMessages()">
    <div class="wrapper">
        <s:iterator value="actionMessages">
            <span class="infos"><s:actionmessage/></span>
        </s:iterator>
    </div>
</s:if>

<s:set var="channels" value="result" scope="request" />
<display:table name="channels" class="dataView" id="rowEntity">
    <display:setProperty name="basic.msg.empty_list">
        <div class="wrapper"><fmt:message key="nothing.found.to.display" /></div>
    </display:setProperty>
    <display:column  titleKey="channel.search.account" >
        <ui:displayStatus displayStatus="${rowEntity.account.displayStatus}">
            <c:choose>
                <c:when test="${canViewAccount}">
                    <a href="/admin/internal/account/view.action?id=${rowEntity.account.id}">
                        <c:out value="${rowEntity.account.name}"/>
                    </a>
                </c:when>
                <c:otherwise>
                    <c:out value="${rowEntity.account.name}"/>
                </c:otherwise>
            </c:choose>
        </ui:displayStatus>
    </display:column>
    <display:column titleKey="channel.search.channel">
        <ui:displayStatus displayStatus="${rowEntity.displayStatus}">
            <a href="/admin/KeywordChannel/view.action?id=${rowEntity.id}"><c:out value="${ad:shortString(rowEntity.name, 80)}"/></a>
        </ui:displayStatus>
    </display:column>
    <display:column titleKey="channel.search.keywordType">
        <fmt:message key="enums.KeywordTriggerType.${rowEntity.triggerType.name}"/>
    </display:column>
    <display:column titleKey="channel.search.country">
        <s:set var="countryId" value="%{#attr.rowEntity.countryCode}"/>
        <s:text name="global.country.%{#countryId}.name"/>
    </display:column>
</display:table>

<ui:pages pageSize="${pageSize}"
      total="${total}"
      selectedNumber="${page}"
      visiblePagesCount="10"
      handler="goToPage"
      displayHeader="false"/>
