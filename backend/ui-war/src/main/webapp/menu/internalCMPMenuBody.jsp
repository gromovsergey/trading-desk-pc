<%@ page import="com.foros.security.AccountRole" %>
<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>

<ad:sessionContext var="contexts"/>
<c:if test="${contexts.cmpContext.set}">
  <c:set var="cmpContext" value="${contexts.cmpContext}"/>
  <c:set var="accountId" value="${cmpContext.accountId}"/>
  <tr id="accountContextMenu">
    <td class="rootCell">
    <script type="text/javascript">
        $('body').addClass('doubleLevelMenu');
    </script>

    <div class="contextZone">
        <ad:accountDisplayStatus accountId="${accountId}"/>
        <fmt:message key="cmpAccount.account"/>:
        <strong><ad:accountName accountId="${accountId}"/></strong>

        <div class="operation">
            <fmt:message key="account.timeZone"/>:&nbsp;
            <strong><c:out value="${ad:resolveGlobal('timezone', cmpContext.account.timezone.key, true)}" /></strong>
        </div>

        <a href="/admin/cmp/account/switch.action" class="operation"><fmt:message key="switchContext.change"/></a>
    </div>

    <div id="secondLevelHeader">
        <div class="categoriesZone">
            <table class="categories">
                <tr>

                    <!-- CMPs Account Sub Menu -->
                    <c:if test="${ad:isPermitted('Account.view', 'CMP')}">
                        <ui:headerMenuItem menuKey="global.submenu.account" action="/view.action?id=${accountId}" module="/admin/cmp/account" activeMenu="${activeMenuItemKey}"/>
                    </c:if>

                    <!-- Channels sub Menu -->
                    <c:set var="cmpRole" value="<%=AccountRole.CMP%>"/>
                    <c:if test="${ad:isPermitted('AdvertisingChannel.view', cmpRole)}">
                        <ui:headerMenuItem labelKey="global.submenu.channels"
                                           menuKey="global.menu.channels"
                                           action="/contextMain.action${ad:accountParam('?accountId',accountId)}"
                                           module="/admin/channel"
                                           activeMenu="${activeMenuItemKey}"/>
                    </c:if>

                    <!-- Reports Sub Menu -->
                    <%@include file="/report/reportPermissions.jsp"%>
                    <c:if test="${cmpReportsAvailable}">
                        <ui:headerMenuItem labelKey="global.submenu.reports"
                                           menuKey="global.menu.reports"
                                           action="/main.action${ad:accountParam('?account.id',accountId)}"
                                           module="/admin/cmp/report"
                                           activeMenu="${activeMenuItemKey}"/>
                    </c:if>

                </tr>
            </table>
        </div>
    </div>
    </td>
</tr>
</c:if>
