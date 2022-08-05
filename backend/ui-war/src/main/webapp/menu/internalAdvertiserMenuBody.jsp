<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="ad" uri="/ad/serverUI" %>

<c:if test="${agencyAdvertiserAccountRequest and activeMenuItemKey =='global.submenu.account'}">
    <c:set var="activeMenuItemKey" value="global.submenu.advertiser"/>
</c:if>

<ad:sessionContext var="contexts"/>

<c:set var="advertiserContext" value="${contexts.advertiserContext}"/>
<c:if test="${advertiserContext.set}">
    <c:set var="accountId" value="${advertiserContext.accountId}"/>

    <c:choose>
        <c:when test="${advertiserContext.agencyContext}">
            <c:if test="${advertiserContext.agencyAdvertiserSet}">
                <c:set var="advId" value="${advertiserContext.agencyAdvertiserId}"/>
            </c:if>
            <c:set var="accountTypeKey" value="agencyAccount.account"/>
        </c:when>
        <c:otherwise>
            <c:set var="advId" value="${advertiserContext.accountId}"/>
            <c:set var="accountTypeKey" value="advertiserAccount.account"/>
        </c:otherwise>
    </c:choose>

<tr id="accountContextMenu">
    <td class="rootCell">
        <script type="text/javascript">
            $('body').addClass('doubleLevelMenu');
        </script>
        
        <div class="contextZone">
            <ad:accountDisplayStatus accountId="${accountId}"/>
            <fmt:message key="${accountTypeKey}"/>:
            <strong><ad:accountName accountId="${accountId}"/></strong>

            <div class="operation">
                <fmt:message key="account.timeZone"/>:&nbsp;
                <strong><c:out value="${ad:resolveGlobal('timezone', advertiserContext.account.timezone.key, true)}" /></strong>
             </div>

            <%--ToDo: uncomment when needed (OUI-28825)--%>
            <%--<c:if test="${advertiserContext.financialFieldsPresent}">--%>
            <%--<div class="operation">--%>
                <%--<fmt:message key="account.credit"/>:&nbsp;--%>
                <%--<strong><c:out value="${ad:formatCurrency(advertiserContext.creditBalance, advertiserContext.account.currency.currencyCode)}" /></strong>--%>
            <%--</div>--%>
            <%--</c:if>--%>

            <a href="/admin/advertiser/account/switch.action" class="operation"><fmt:message key="switchContext.change"/></a>
        </div>
    
        <div id="secondLevelHeader">
            <div class="categoriesZone">
                <table class="categories">
                    <tr>
                        <c:if test="${ad:isPermitted('Account.view', 'Advertiser')}">
                            <!-- Advertisers Account Sub Menu -->
                            <ui:headerMenuItem menuKey="global.submenu.account" action="/advertiserView.action?id=${accountId}" module="/admin/advertiser/account" activeMenu="${activeMenuItemKey}"/>
                        </c:if>

                        <!-- Agency Advertiser Account Sub Menu -->
                        <c:if test="${advertiserContext.agencyContext and ad:isPermitted0('AdvertisingAccount.viewList')}">
                            <c:choose>
                                <c:when test="${not empty advId and ad:isPermitted0('AgencyAdvertiserAccount.view')}">
                                    <ui:headerMenuItem menuKey="global.submenu.advertiser" action="/agencyAdvertiserView.action?id=${advId}" module="/admin/advertiser/account" activeMenu="${activeMenuItemKey}"/>
                                </c:when>
                                <c:otherwise>
                                    <ui:headerMenuItem menuKey="global.submenu.advertiser" action="/advertisers.action?advertiserId=${accountId}" module="/admin/campaign" activeMenu="${activeMenuItemKey}"/>
                                </c:otherwise>
                            </c:choose>
                        </c:if>

                        <!-- Opportunities sub Menu -->
                        <c:if test="${advId != null && ad:isPermitted0('Opportunity.view')}">
                            <ui:headerMenuItem
                                    labelKey="global.submenu.opportunities"
                                    menuKey="global.menu.opportunities"
                                    action="/main.action${ad:accountParam('?advertiserId',advId)}"
                                    module="/admin/opportunity"
                                    activeMenu="${activeMenuItemKey}"/>
                        </c:if>

                        <c:if test="${advId != null && ad:isPermitted0('AdvertiserEntity.view')}">
                            <!-- Campaigns Sub Menu -->
                            <ui:headerMenuItem
                                    labelKey="global.submenu.campaigns"
                                    menuKey="global.menu.campaigns"
                                    action="/campaigns.action?advertiserId=${advId}"
                                    module="/admin/campaign"
                                    activeMenu="${activeMenuItemKey}"/>

                            <!-- Creatives Sub Menu -->
                            <ui:headerMenuItem menuKey="global.submenu.creatives" action="/main.action?advertiserId=${advId}" module="/admin/creative" activeMenu="${activeMenuItemKey}"/>
                        </c:if>

                        <!-- Channels sub Menu -->
                        <c:if test="${ad:isPermitted('AdvertisingChannel.view', advertiserContext.account)}">
                            <ui:headerMenuItem
                                    labelKey="global.submenu.channels"
                                    menuKey="global.menu.channels"
                                    action="/contextMain.action${ad:accountParam('?accountId',accountId)}"
                                    module="/admin/channel"
                                    activeMenu="${activeMenuItemKey}"/>
                        </c:if>

                        <!-- Actions sub Menu -->
                        <c:if test="${advId != null && ad:isPermitted0('AdvertiserEntity.view')}">
                                <ui:headerMenuItem menuKey="global.submenu.actions" action="/list.action?advertiserId=${advId}" module="/admin/Action" activeMenu="${activeMenuItemKey}"/>
                        </c:if>

                        <c:if test="${ad:isPermitted('Report.run', 'advertiser') || ad:isPermitted('Report.run', 'conversions') || (ad:isPermitted('Report.run', 'textAdvertising') && ad:isPermitted('AdvertiserEntity.accessTextAd', advertiserContext.account))}">
                            <!-- Reports Sub Menu -->
                            <ui:headerMenuItem
                                    labelKey="global.submenu.reports"
                                    menuKey="global.menu.reports"
                                    action="/main.action${ad:accountParam('?account.id',accountId)}"
                                    module="/admin/advertiser/report"
                                    activeMenu="${activeMenuItemKey}"/>
                        </c:if>
                    </tr>
                </table>
            </div>
            <c:if test="${advertiserContext.agencyAdvertiserSet}">
                <div class="contextZone">
                    <ad:accountDisplayStatus accountId="${advertiserContext.agencyAdvertiserId}"/>
                    <fmt:message key="advertiserAccount.account"/>:
                    <strong><ad:accountName accountId="${advertiserContext.agencyAdvertiserId}"/></strong>
                    <a href="/admin/campaign/advertisers.action?advertiserId=${advertiserContext.accountId}" class="operation"><fmt:message key="switchContext.change"/></a>
                </div>
            </c:if>
        </div>
    </td>
</tr>
</c:if>
