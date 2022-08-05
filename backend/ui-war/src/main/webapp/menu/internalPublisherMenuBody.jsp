<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>

<ad:sessionContext var="contexts"/>
<c:if test="${contexts.publisherContext.set}">
  <c:set var="publisherContext" value="${contexts.publisherContext}"/>
  <c:set var="accountId" value="${publisherContext.accountId}"/>
  <tr id="accountContextMenu">
    <td class="rootCell">
    <script type="text/javascript">
        $('body').addClass('doubleLevelMenu');
    </script>

    <div class="contextZone">
        <ad:accountDisplayStatus accountId="${accountId}"/>
        <fmt:message key="publisherAccount.account"/>:
        <strong><ad:accountName accountId="${accountId}"/></strong>

        <div class="operation">
            <fmt:message key="account.timeZone"/>:&nbsp;
            <strong><c:out value="${ad:resolveGlobal('timezone', publisherContext.account.timezone.key, true)}" /></strong>
        </div>

        <a href="/admin/publisher/account/switch.action" class="operation"><fmt:message key="switchContext.change"/></a>
    </div>

    <div id="secondLevelHeader">
        <div class="categoriesZone">
            <table class="categories">
                <tr>

                    <!-- Publishers Account Sub Menu -->
                    <c:if test="${ad:isPermitted('Account.view', 'Publisher')}">
                        <ui:headerMenuItem menuKey="global.submenu.account" action="/view.action?id=${accountId}" module="/admin/publisher/account" activeMenu="${activeMenuItemKey}"/>
                    </c:if>
    
                    <!-- Sites Sub Menu -->
                    <c:if test="${ad:isPermitted0('PublisherEntity.view')}">
                        <ui:headerMenuItem
                                menuKey="global.menu.sites"
                                labelKey="global.submenu.sites"
                                action="/main.action${ad:accountParam('?accountId',accountId)}"
                                module="/admin/site"
                                activeMenu="${activeMenuItemKey}"/>
                    </c:if>
    
                    <!-- Reports Sub Menu -->
                    <%@include file="/report/reportPermissions.jsp"%>
                    <c:if test="${publisherReportsAvailable}">
                        <ui:headerMenuItem labelKey="global.submenu.reports" menuKey="global.menu.reports" action="/main.action${ad:accountParam('?account.id',accountId)}" module="/admin/publisher/report" activeMenu="${activeMenuItemKey}"/>
                    </c:if>
                </tr>
            </table>
        </div>
    </div>
    </td>
</tr>
</c:if>
