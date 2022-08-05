<%@page contentType="text/html" %>
<%@page pageEncoding="UTF-8" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib uri="/struts-tags" prefix="s" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ui" %>

<s:actionerror/>

<s:if test="channelLists != null">
    <c:set var="canViewAccount" value="${ad:isPermitted('Account.view', 'Internal')}"/>

    <ui:pages pageSize="${pageSize}"
              total="${total}"
              selectedNumber="${page}"
              visiblePagesCount="10"
              handler="goToPage"
              displayHeader="true"/>

    <display:table name="channelLists" class="dataView" id="channel">
        <display:setProperty name="basic.msg.empty_list">
            <div class="wrapper">
                <fmt:message key="nothing.found.to.display"/>
            </div>
        </display:setProperty>

        <c:if test="${!hideAccountColumn}">
            <display:column titleKey="channel.search.account">
                <ui:displayStatus displayStatus="${channel.accountDisplayStatus}">
                    <c:choose>
                        <c:when test="${canViewAccount}">
                            <a href="/admin/internal/account/view.action?id=${channel.accountId}">
                                <c:out value="${channel.accountName}"/>
                            </a>
                        </c:when>
                        <c:otherwise>
                            <c:out value="${channel.accountName}"/>
                        </c:otherwise>
                    </c:choose>
                </ui:displayStatus>
            </display:column>
        </c:if>

        <display:column titleKey="channel.search.channelList">
            <ui:displayStatus displayStatus="${channel.displayStatus}">
                <a href="/admin/DiscoverChannelList/view.action?id=${channel.id}"><c:out value="${channel.name}"/></a>
            </ui:displayStatus>
        </display:column>

        <c:if test="${!hideCountryColumn}">
            <display:column titleKey="channel.search.country" class="fixed">
                <ad:resolveGlobal resource="country" id="${channel.country}"/>
            </display:column>
        </c:if>

    </display:table>

    <ui:pages pageSize="${pageSize}"
              total="${total}"
              selectedNumber="${page}"
              visiblePagesCount="10"
              handler="goToPage"
              displayHeader="false"/>

</s:if>
