<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>

<c:if test="${agencyAdvertiserAccountRequest and activeMenuItemKey =='global.menu.myAccount'}">
    <c:set var="activeMenuItemKey" value="global.menu.advertiser"/>
</c:if>

<ad:sessionContext var="contexts"/>
<c:set var="advertiserContext" value="${contexts.advertiserContext}"/>
<c:set var="accountId" value="${advertiserContext.accountId}"/>

<c:choose>
    <c:when test="${advertiserContext.agencyContext}">
        <c:choose>
            <c:when test="${advertiserContext.agencyAdvertiserSet}">
                <c:set var="advertiserId" value="${advertiserContext.agencyAdvertiserId}"/>
            </c:when>
            <c:otherwise>
                <c:set var="advertiserId" value=""/>
            </c:otherwise>
    </c:choose>
    </c:when>
    <c:otherwise>
        <c:set var="advertiserId" value="${advertiserContext.accountId}"/>
    </c:otherwise>
</c:choose>

<div class="categoriesZone">
    <table class="categories">
        <tr>
            <!-- My Account Menu -->
            <c:if test="${ad:isPermitted('Account.view', 'Advertiser')}">
                <ui:headerMenuItem menuKey="global.menu.myAccount" action="/myAccountView.action" module="/advertiser/myAccount" activeMenu="${activeMenuItemKey}"/>
            </c:if>

            <!-- Agency Advertiser Account Sub Menu -->
            <c:if test="${advertiserContext.agencyContext and ad:isPermitted0('AdvertisingAccount.viewList')}">
                <c:choose>
                    <c:when test="${not empty advertiserId and ad:isPermitted0('AgencyAdvertiserAccount.view')}">
                        <ui:headerMenuItem menuKey="global.menu.advertiser" action="/agencyAdvertiserView.action?id=${advertiserId}" module="/advertiser/myAccount" activeMenu="${activeMenuItemKey}"/>
                    </c:when>
                    <c:otherwise>
                        <ui:headerMenuItem menuKey="global.menu.advertiser" action="/advertisers.action${ad:accountParam('?advertiserId',accountId)}" module="/advertiser/campaign" activeMenu="${activeMenuItemKey}"/>
                    </c:otherwise>
                </c:choose>
            </c:if>

            <c:if test="${not empty advertiserId and ad:isPermitted0('AdvertiserEntity.view')}">
                <!-- Campaign Menu -->
                <ui:headerMenuItem menuKey="global.menu.campaigns" action="/campaigns.action${ad:accountParam('?advertiserId',advertiserId)}" module="/advertiser/campaign" activeMenu="${activeMenuItemKey}"/>
                
                <!-- Creative Menu -->
                <ui:headerMenuItem menuKey="global.menu.creatives" action="/main.action${ad:accountParam('?advertiserId',advertiserId)}" module="/advertiser/creative" activeMenu="${activeMenuItemKey}"/>

            </c:if>

            <!-- Channel Menu -->
            <c:if test="${ad:isPermitted('AdvertisingChannel.view', advertiserContext.account)}">
                <ui:headerMenuItem menuKey="global.menu.channels"
                                   action="/contextMain.action" module="/advertiser/channel"
                                   activeMenu="${activeMenuItemKey}"/>
            </c:if>

            <c:if test="${not empty advertiserId and ad:isPermitted0('AdvertiserEntity.view')}">
                <!-- Action Menu -->
                <ui:headerMenuItem menuKey="global.menu.actions" action="/list.action${ad:accountParam('?advertiserId',advertiserId)}" module="/advertiser/Action" activeMenu="${activeMenuItemKey}"/>
            </c:if>

            <!-- Report Menu -->
            <%@include file="/report/reportPermissions.jsp"%>
            <c:if test="${advertiserReportsAvailable}">
                <ui:headerMenuItem menuKey="global.menu.reports" action="/main.action${ad:accountParam('?account.id',accountId)}" module="/advertiser/report" activeMenu="${activeMenuItemKey}"/>
            </c:if>

            <!-- Settings Menu-->
            <ui:headerMenuItem menuKey="global.menu.settings" action="/view.action" module="/advertiser/myPreferences" activeMenu="${activeMenuItemKey}"/>
        </tr>
    </table>
</div>
