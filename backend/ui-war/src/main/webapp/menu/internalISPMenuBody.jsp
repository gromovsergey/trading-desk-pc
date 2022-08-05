<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>

<ad:sessionContext var="contexts"/>
<c:if test="${contexts.ispContext.set}">
  <c:set var="ispContext" value="${contexts.ispContext}"/>
  <c:set var="accountId" value="${ispContext.accountId}"/>
  <tr id="accountContextMenu">
    <td class="rootCell">
    <script type="text/javascript">
        $('body').addClass('doubleLevelMenu');
    </script>

    <div class="contextZone">
        <ad:accountDisplayStatus accountId="${accountId}"/>
        <fmt:message key="ispAccount.account"/>:
        <strong><ad:accountName accountId="${accountId}"/></strong>

        <div class="operation">
            <fmt:message key="account.timeZone"/>:&nbsp;
            <strong><c:out value="${ad:resolveGlobal('timezone', ispContext.account.timezone.key, true)}" /></strong>
        </div>

        <a href="/admin/isp/account/switch.action" class="operation"><fmt:message key="switchContext.change"/></a>
    </div>

    <div id="secondLevelHeader">
        <div class="categoriesZone">
            <table class="categories">
                <tr>
                    <!-- ISPs Account Sub Menu -->
                    <c:if test="${ad:isPermitted('Account.view', 'ISP')}">
                        <ui:headerMenuItem menuKey="global.submenu.account" action="/view.action?id=${accountId}" module="/admin/isp/account" activeMenu="${activeMenuItemKey}"/>
                    </c:if>
    
                    <!-- Colocations Sub Menu -->
                    <c:if test="${ad:isPermitted('Colocation.view', accountId)}">
                        <ui:headerMenuItem menuKey="global.submenu.colocations" action="/list.action${ad:accountParam('?ispId',accountId)}" module="/admin/colocation" activeMenu="${activeMenuItemKey}"/>
                    </c:if>

                    <!-- Reports Sub Menu -->
                    <ui:headerMenuItem labelKey="global.submenu.reports" menuKey="global.menu.reports" action="/main.action${ad:accountParam('?account.id',accountId)}" module="/admin/isp/report" activeMenu="${activeMenuItemKey}"/>

                </tr>
            </table>
        </div>
    </div>
    </td>
</tr>
</c:if>
