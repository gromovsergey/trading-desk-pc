<%@ page import="com.foros.security.AuthenticationType" %>
<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/struts-tags" prefix="s" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ui" %>

<c:set var="authTypePSWD" value="<%=AuthenticationType.PSWD%>"/>

<ui:header>
    <ui:pageHeadingByTitle/>
    <c:if test="${ad:isPermitted('User.updateMyPreferences', model)}">
        <ui:button message="form.edit" href="edit.action" />
    </c:if>
    <c:if test="${ad:isPermitted('User.changePassword', model)}">
        <ui:button message="user.changePassword" href="changePassword.action" />
    </c:if>
</ui:header>

<ui:section titleKey="form.main">
    <ui:fieldGroup>

        <c:choose>
            <c:when test="${authType == authTypePSWD}">
                <fmt:message var="authTypeString" key="user.password"/>
            </c:when>
            <c:otherwise>
                 <fmt:message var="authTypeString" key="user.LDAP"/>
            </c:otherwise>
        </c:choose>
        <ui:simpleField labelKey="user.authorization" value="${authTypeString}"/>


        <ui:field labelKey="user.email">
            <ui:text text="${email}"/>
            <c:if test="${not empty param['mailFailure']}">
                <span class="errors"><fmt:message key="user.mail.sent.failure"/></span>
            </c:if>
        </ui:field>

        <c:if test="${not empty firstName}">
            <ui:simpleField labelKey="user.firstName" value="${firstName}"/>
        </c:if>

        <c:if test="${not empty lastName}">
            <ui:simpleField labelKey="user.lastName" value="${lastName}"/>
        </c:if>

        <c:if test="${not empty jobTitle}">
            <ui:simpleField labelKey="user.jobTitle" value="${jobTitle}"/>
        </c:if>

        <c:choose>
            <c:when test="${not empty language}">
                <fmt:message var="languageString" key="enums.Language.${language}"/>
            </c:when>
            <c:otherwise>
                <fmt:message var="languageString" key="enums.Language.EN"/>
            </c:otherwise>
        </c:choose>

        <ui:simpleField labelKey="user.language" value="${languageString}"/>

        <c:if test="${not empty role.name}">
            <ui:field labelKey="user.role">
                <ui:userRoleLink roleField="${role}"/>
            </ui:field>
        </c:if>

        <fmt:message var="statusString" key="enums.Status.${status}"/>
        <ui:simpleField labelKey="user.status" value="${statusString}"/>

        <c:if test="${ad:isInternal()}">
            <c:if test="${not empty dn}">
                <ui:simpleField labelKey="user.LDAPUser" value="${dn}"/>
            </c:if>
        </c:if>

        <c:if test="${not empty phone}">
            <ui:simpleField labelKey="user.phone" value="${phone}"/>
        </c:if>

        <c:if test="${ad:isInternal()}">
            <s:if test="deletedObjectsVisible">
                <fmt:message key="user.deleted.objects.show" var="showDeletedObjectsMsg"/>
            </s:if>
            <s:else>
                <fmt:message key="user.deleted.objects.hide" var="showDeletedObjectsMsg"/>
            </s:else>
            <ui:simpleField labelKey="user.deleted.objects" value="${showDeletedObjectsMsg}"/>
        </c:if>

    </ui:fieldGroup>
</ui:section>

<%@ include file="../account/rsCredentials.jsp" %>
