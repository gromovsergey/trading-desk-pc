<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<div class="categoriesZone">
    <table class="categories">
        <tr>
            <!-- My Account Menu -->
            <c:if test="${ad:isPermitted('Account.view', 'CMP')}">
                <ui:headerMenuItem menuKey="global.menu.myAccount" action="/myAccountView.action" module="/cmp/myAccount" activeMenu="${activeMenuItemKey}"/>
            </c:if>
            
            <!-- Channel Menu -->                        
            <c:if test="${ad:isPermitted0('AdvertisingChannel.view')}">
                <ui:headerMenuItem menuKey="global.menu.channels" action="/contextMain.action" module="/cmp/channel" activeMenu="${activeMenuItemKey}"/>
            </c:if>

            <!-- Report Menu -->
            <%@include file="/report/reportPermissions.jsp"%>
            <c:if test="${cmpReportsAvailable}">
                <ui:headerMenuItem menuKey="global.menu.reports" action="/main.action" module="/cmp/report" activeMenu="${activeMenuItemKey}"/>
            </c:if>

            <!-- Settings Menu-->
            <ui:headerMenuItem menuKey="global.menu.settings" action="/view.action" module="/cmp/myPreferences" activeMenu="${activeMenuItemKey}"/>
        </tr>
    </table>
</div>
