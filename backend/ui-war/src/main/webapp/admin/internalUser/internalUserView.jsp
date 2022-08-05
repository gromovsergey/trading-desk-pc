<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@page import="com.foros.security.AuthenticationType" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<%@include file="/account/passwordResetLinkButton.jsp"%>
<s:form>
<ui:header>
    <ui:pageHeadingByTitle/>
    <c:if test="${ad:isPermitted('User.update', model) || ad:isPermitted('User.updateMaxCreditLimit', model.role.id)}">
        <ui:button message="form.edit" href="edit.action?id=${id}" />
    </c:if>
    <c:if test="${ad:isPermitted('User.resetPassword', model)}">
        <ui:postButton href="sendPassword.action" entityId="${id}" message="user.resetSendPasswordByEmailphone"/>
        <ui:button id="passwordResetLinkButton" type="link" message="user.passwordResetLink.button"/>
    </c:if>

    <c:set var="entityBean" value="${model}"/>
    <%@ include file="../../auditLog/viewLogButton.jspf" %>
</ui:header>

<ui:section titleKey="form.main">
    <ui:fieldGroup>

        <c:if test="${not empty email}">
            <ui:field labelKey="InternalUser.email">
                <c:out value="${email}"/>
                <c:set var="authTypePSWD" value="<%=AuthenticationType.PSWD.getName()%>"/>
                <c:if test="${authType == authTypePSWD && not empty param['isPasswordSent'] && internalPasswordAuthorizationAllowed}">
                    <c:if test="${param['isPasswordSent'] == 'true'}">
                        <span class="infos"><fmt:message key="user.js.passwordSuccess"><fmt:param value="${email}"/></fmt:message></span>
                    </c:if>
                    <c:if test="${param['isPasswordSent'] == 'false'}">
                        <span class="errors"><fmt:message key="user.js.passwordSuccessGen"><fmt:param value="${email}"/></fmt:message></span>
                    </c:if>
                </c:if>
                <c:if test="${not empty param['mailFailure']}">
                    <span class="errors"><fmt:message key="user.mail.sent.failure"/></span>
                </c:if>
            </ui:field>
        </c:if>

        <c:if test="${not empty role.name}">
            <ui:field labelKey="InternalUser.userRole">
                <ui:userRoleLink roleField="${role}"/>
            </ui:field>
        </c:if>

        <c:if test="${not empty maxCreditLimit && advertisingFinanceUser}">
            <ui:field labelKey="InternalUser.maxCreditLimit">
                <s:if test="limitedBudget">
                    ${ad:formatCurrency(maxCreditLimit, 'USD')}
                </s:if>
                <s:else>
                    <fmt:message key='InternalUser.budget.unlimited'/>
                </s:else>
            </ui:field>
        </c:if>

        <s:if test="authType.name=='LDAP'">
            <fmt:message key="InternalUser.ldap" var="authType_label"/>
        </s:if>
        <s:else>
            <fmt:message key="InternalUser.password" var="authType_label"/>
        </s:else>
        <ui:simpleField labelKey="InternalUser.authorization" value="${authType_label}"/>


        <c:if test="${not empty dn}">
            <ui:field labelKey="InternalUser.ldapUser">
                <ui:text text="${dn}"/>
            </ui:field>
        </c:if>

        <c:if test="${not empty status}">
            <ui:field labelKey="InternalUser.status" errors="version">
                <c:set var="statusDescriptionKey">enums.Status.${status}</c:set>
                <ui:statusButtonGroup
                    descriptionKey="${statusDescriptionKey}"
                    entity="${model}" restrictionEntity="User"
                    activatePage="activate.action" inactivatePage="inactivate.action"
                    deletePage="delete.action" undeletePage="undelete.action"/>
                <s:fielderror><s:param value="'version'"/></s:fielderror>
            </ui:field>
        </c:if>

        <c:if test="${not empty firstName}">
            <ui:simpleField labelKey="InternalUser.firstName" value="${firstName}"/>
        </c:if>

        <c:if test="${not empty lastName}">
            <ui:simpleField labelKey="InternalUser.lastName" value="${lastName}"/>
        </c:if>

        <c:choose>
            <c:when test="${not empty language}">
                <fmt:message key="enums.Language.${language}" var="langMessage"/>
            </c:when>
            <c:otherwise>
                <fmt:message key="enums.Language.EN" var="langMessage"/>
            </c:otherwise>
        </c:choose>
        <ui:simpleField labelKey="InternalUser.language" value="${langMessage}"/>

        <c:if test="${not empty jobTitle}">
            <ui:simpleField labelKey="InternalUser.jobTitle" value="${jobTitle}"/>
        </c:if>

        <c:if test="${not empty phone}">
            <ui:simpleField labelKey="InternalUser.phoneNumber" value="${phone}"/>
        </c:if>

    </ui:fieldGroup>
</ui:section>
</s:form>