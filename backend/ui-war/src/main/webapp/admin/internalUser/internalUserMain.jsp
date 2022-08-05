<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>


<ui:pageHeadingByTitle/>
<c:set var="canViewUserRole" value="${ad:isPermitted0('UserRole.view')}"/>

<display:table name="users" class="dataView" defaultsort="1" id="user">
    <display:setProperty name="basic.msg.empty_list" >
        <div class="wrapper">
            <fmt:message key="nothing.found.to.display"/>
        </div>
    </display:setProperty>
    <display:column titleKey="InternalUser.email" style="width:30%;" sortProperty="userEmail">
        <ui:displayStatus displayStatus="${user.userDisplayStatus}">
            <a href="<s:url action="%{#attr.moduleName}/view" />?id=${user.userId}"><c:out value="${user.userEmail}"/></a>
        </ui:displayStatus>
    </display:column>
    <display:column titleKey="InternalUser.userRole">
        <c:choose>
            <c:when test="${canViewUserRole}">
                <a href="${_context}/UserRole/view.action?id=${user.userRoleId}"><c:out
                        value="${user.userRoleName}"/></a>
            </c:when>
            <c:otherwise>
                <c:out value="${user.userRoleName}"/>
            </c:otherwise>
        </c:choose>
    </display:column>
    <display:column titleKey="InternalUser.account">
        <ui:displayStatus displayStatus="${user.accountDisplayStatus}">
            <a href="<s:url action="admin/internal/account/view"/>?id=${user.accountId}"><c:out value="${user.accountName}"/></a>
        </ui:displayStatus>
    </display:column>
    <display:column titleKey="InternalUser.country">
        <ad:resolveGlobal resource="country" id="${user.countryCode}"/>
    </display:column>
</display:table>
