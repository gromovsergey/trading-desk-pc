<%@page import="com.foros.model.security.ObjectType"%>
<%@page import="com.foros.model.account.Account"%>
<%@page import="com.foros.model.channel.Channel"%>

<%@ taglib uri="/ad/serverUI" prefix="ad" %>

<c:if test="${model != null && objectType != null}">
    <c:set var="isTypeAssignableFromAccount" value='<%=Account.class.isAssignableFrom(((ObjectType)request.getAttribute("objectType")).getObjectClass())%>'/>
    <c:set var="isTypeAssignableFromChannel" value='<%=Channel.class.isAssignableFrom(((ObjectType)request.getAttribute("objectType")).getObjectClass())%>'/>

    <c:if test="${objectType == 'BirtReport'}">
        <c:set var="activeMenuItemKey" scope="request" value="global.menu.reports"/>
    </c:if>
    <c:if test="${objectType == 'Colocation'}">
        <c:set var="activeMenuItemKey" scope="request" value="global.submenu.colocations"/>
    </c:if>
    <c:if test="${objectType == 'Site'}">
        <c:set var="activeMenuItemKey" scope="request" value="global.menu.sites"/>
    </c:if>
    <c:if test="${objectType == 'IspAccount'}">
        <c:set var="activeMenuItemKey" scope="request" value="global.submenu.account"/>
    </c:if>
    <c:if test="${objectType == 'PublisherAccount'}">
        <c:set var="activeMenuItemKey" scope="request" value="global.submenu.account"/>
    </c:if>
    <c:if test="${objectType == 'CmpAccount'}">
        <c:set var="activeMenuItemKey" scope="request" value="global.submenu.account"/>
    </c:if>
    <c:if test="${objectType == 'AgencyAccount'}">
        <c:set var="activeMenuItemKey" scope="request" value="global.submenu.account"/>
    </c:if>
    <c:if test="${objectType == 'AdvertiserAccount' or objectType == 'CampaignCredit'}">
        <ad:sessionContext var="contexts"/>
        <c:set var="activeMenuItemKey" scope="request" value="${contexts.advertiserContext.agencyAdvertiserSet ? 'global.submenu.advertiser' : 'global.submenu.account'}"/>
    </c:if>
    <c:if test="${objectType == 'Opportunity'}">
        <c:set var="activeMenuItemKey" scope="request" value="global.menu.opportunities"/>
    </c:if>
    <c:if test="${objectType == 'User'}">
        <c:set var="activeMenuItemKey" scope="request" value="${contextName != 'global.menu.admin' ? 'global.submenu.account':'global.menu.admin'}"/>
    </c:if>
    <c:if test="${objectType == 'Campaign'}">
        <c:set var="activeMenuItemKey" scope="request" value="global.menu.campaigns"/>
    </c:if>
    <c:if test="${objectType == 'CampaignCreativeGroup'}">
        <c:set var="activeMenuItemKey" scope="request" value="global.menu.campaigns"/>
    </c:if>
    <c:if test="${objectType == 'Creative'}">
        <c:choose>
            <c:when test="${isTextAd}">
                <c:set var="activeMenuItemKey" scope="request" value="global.menu.campaigns"/>
            </c:when>
            <c:otherwise>
                <c:set var="activeMenuItemKey" scope="request" value="global.submenu.creatives"/>
            </c:otherwise>
        </c:choose>
    </c:if>
    <c:if test="${objectType == 'CurrencyExchange'}">
        <c:set var="activeMenuItemKey" scope="request" value="global.menu.admin"/>
    </c:if>
    <c:if test="${isTypeAssignableFromChannel}">
        <c:if test="${param['contextName'] == 'global.menu.advertisers'}">
            <c:set var="activeMenuItemKey" scope="request" value="global.menu.channels"/>
        </c:if>
        <c:if test="${param['contextName'] == 'global.menu.cmps'}">
            <c:set var="activeMenuItemKey" scope="request" value="global.menu.channels"/>
        </c:if>
        <c:if test="${empty param['contextName']}">
            <c:set var="activeMenuItemKey" scope="request" value="global.menu.admin"/>
        </c:if>
    </c:if>
    <c:if test="${objectType == 'Action'}">
        <c:set var="activeMenuItemKey" scope="request" value="global.submenu.actions"/>
    </c:if>
    <c:if test="${objectType == 'Tag'}">
        <c:set var="activeMenuItemKey" scope="request" value="global.menu.sites"/>
    </c:if>
    <c:if test="${objectType == 'WDTag'}">
        <c:set var="activeMenuItemKey" scope="request" value="global.menu.sites"/>
    </c:if>
</c:if>
