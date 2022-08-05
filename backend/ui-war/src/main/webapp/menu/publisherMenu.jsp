<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="ad" uri="/ad/serverUI" %>

<div class="categoriesZone">
    <table class="categories">
        <tr>
            <!-- MyAccount Menu -->
            <c:if test="${ad:isPermitted('Account.view', 'Publisher')}">
                <ui:headerMenuItem menuKey="global.menu.myAccount" action="/myAccountView.action" module="/publisher/myAccount" activeMenu="${activeMenuItemKey}"/>
            </c:if>
            
            <!-- Site Menu -->
            <c:if test="${ad:isPermitted0('PublisherEntity.view')}">
                <ui:headerMenuItem menuKey="global.menu.sites" action="/main.action" module="/publisher/site" activeMenu="${activeMenuItemKey}"/>
            </c:if>
    
            <!-- Report Menu -->
            <%@include file="/report/reportPermissions.jsp"%>
            <c:if test="${publisherReportsAvailable}">
                <ui:headerMenuItem menuKey="global.menu.reports" action="/main.action" module="/publisher/report" activeMenu="${activeMenuItemKey}"/>
            </c:if>
    
            <!-- My Preferences Menu -->
            <ui:headerMenuItem menuKey="global.menu.settings" action="/view.action" module="/publisher/myPreferences" activeMenu="${activeMenuItemKey}"/>
            
        </tr>
    </table>
</div>
