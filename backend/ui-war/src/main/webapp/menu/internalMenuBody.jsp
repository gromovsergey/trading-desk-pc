<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="ad" uri="/ad/serverUI" %>

<%@include file="../admin/adminPermissions.jsp"%>

<c:set var="activeMenuItemKey" value="${contextName == 'global.menu.admin' ? activeMenuItemKey : contextName}"/>

<div class="categoriesZone">
    <table class="categories">
        <tr>
            <!-- Dashboard Menu -->
            <c:if test="${ad:isPermitted0('AdopsDashboard.run')}">
                <ui:headerMenuItem menuKey="global.menu.dashboard" action="/main.action" module="/admin/AdopsDashboard" activeMenu="${activeMenuItemKey}"/>
            </c:if>

            <!-- Advertisers Menu -->
            <c:if test="${ad:isPermitted('Context.switch', 'Advertiser')}">
                <ui:headerMenuItem menuKey="global.menu.advertisers" action="/main.action" module="/admin/advertiser/account" activeMenu="${activeMenuItemKey}"/>
            </c:if>
    
            <!-- Publishers Menu -->
            <c:if test="${ad:isPermitted('Context.switch', 'Publisher')}">
                <ui:headerMenuItem menuKey="global.menu.publishers" action="/main.action" module="/admin/publisher/account" activeMenu="${activeMenuItemKey}"/>
            </c:if>
    
            <!-- ISPs Menu -->
            <c:if test="${ad:isPermitted('Context.switch', 'ISP')}">
                <ui:headerMenuItem menuKey="global.menu.isps" action="/main.action" module="/admin/isp/account" activeMenu="${activeMenuItemKey}"/>
            </c:if>

            <!-- CMPs Menu -->
            <c:if test="${ad:isPermitted('Context.switch', 'CMP')}">
                <ui:headerMenuItem menuKey="global.menu.cmps" action="/main.action" module="/admin/cmp/account" activeMenu="${activeMenuItemKey}"/>
            </c:if>
            
            <!-- Reports Menu -->
            <ui:headerMenuItem menuKey="global.menu.reports" action="/main.action" module="/admin/report" activeMenu="${activeMenuItemKey}"/>
    
            <!-- Admin Menu -->
            <c:if test="${viewChannels1 or viewChannels2 or viewChannels3 or viewCreativeAndTemplate1 or viewCreativeAndTemplate2 or viewOther1 or viewOther2 or viewOther3}">
                <ui:headerMenuItem menuKey="global.menu.admin" action="/admin.action" module="/admin" activeMenu="${activeMenuItemKey}"/>
            </c:if>

            <!-- Settings Menu -->
            <ui:headerMenuItem menuKey="global.menu.settings" action="/view.action" module="/admin/myPreferences" activeMenu="${activeMenuItemKey}"/>
        </tr>
    </table>
</div>
