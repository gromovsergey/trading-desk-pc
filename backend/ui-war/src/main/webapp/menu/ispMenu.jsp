<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>

<div class="categoriesZone">
    <table class="categories">
        <tr>
            <!-- My Account Menu -->
             <c:if test="${ad:isPermitted('Account.view', 'ISP')}">
                <ui:headerMenuItem menuKey="global.menu.myAccount" action="/myAccountView.action" module="/isp/myAccount" activeMenu="${activeMenuItemKey}"/>
            </c:if>
            
            <!-- Report Menu -->
            <%@include file="/report/reportPermissions.jsp"%>
            <c:if test="${ispReportsAvailable}">
                <ui:headerMenuItem menuKey="global.menu.reports" action="/main.action" module="/isp/report" activeMenu="${activeMenuItemKey}"/>
            </c:if>
            
            <!-- Settings Menu -->
            <ui:headerMenuItem menuKey="global.menu.settings" action="/view.action" module="/isp/myPreferences" activeMenu="${activeMenuItemKey}"/>
        </tr>
    </table>
</div>
