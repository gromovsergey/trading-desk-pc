<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@ page import="com.foros.security.AuthenticationType" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<c:set var="authTypePSWD" value="<%=AuthenticationType.PSWD.getName()%>"/>
<c:set var="editActionName" value="edit.action" />
<c:set var="sendPasswordActionName" value="sendPassword.action" />
<c:set var="activatePageActionName" value="activate.action" />
<c:set var="inactivatePageActionName" value="inactivate.action" />
<c:set var="deletePageActionName" value="delete.action" />
<c:set var="undeletePageActionName" value="undelete.action" />

<%@include file="passwordResetLinkButton.jsp"%>
<c:choose>
    <c:when test="${contextName == 'global.menu.advertisers'}">
        <c:set var="editActionName" value="advertiserEdit.action" />
        <c:set var="sendPasswordActionName" value="advertiserSendPassword.action" />
        <c:set var="activatePageActionName" value="advertiserActivate.action" />
        <c:set var="inactivatePageActionName" value="advertiserInactivate.action" />
        <c:set var="deletePageActionName" value="advertiserDelete.action" />
        <c:set var="undeletePageActionName" value="advertiserUndelete.action" />
    </c:when>
    <c:when test="${contextName == 'global.menu.publishers'}">
        <c:set var="editActionName" value="publisherEdit.action" />
        <c:set var="sendPasswordActionName" value="publisherSendPassword.action" />
        <c:set var="activatePageActionName" value="publisherActivate.action" />
        <c:set var="inactivatePageActionName" value="publisherInactivate.action" />
        <c:set var="deletePageActionName" value="publisherDelete.action" />
        <c:set var="undeletePageActionName" value="publisherUndelete.action" />
    </c:when>
    <c:when test="${contextName == 'global.menu.isps'}">
        <c:set var="editActionName" value="ispEdit.action" />
        <c:set var="sendPasswordActionName" value="ispSendPassword.action" />
        <c:set var="activatePageActionName" value="ispActivate.action" />
        <c:set var="inactivatePageActionName" value="ispInactivate.action" />
        <c:set var="deletePageActionName" value="ispDelete.action" />
        <c:set var="undeletePageActionName" value="ispUndelete.action" />
    </c:when>
    <c:when test="${contextName == 'global.menu.cmps'}">
        <c:set var="editActionName" value="cmpEdit.action" />
        <c:set var="sendPasswordActionName" value="cmpSendPassword.action" />
        <c:set var="activatePageActionName" value="cmpActivate.action" />
        <c:set var="inactivatePageActionName" value="cmpInactivate.action" />
        <c:set var="deletePageActionName" value="cmpDelete.action" />
        <c:set var="undeletePageActionName" value="cmpUndelete.action" />
    </c:when>
</c:choose>

<s:form>
<ui:header>
    <ui:pageHeadingByTitle />
    <c:if test="${ad:isPermitted('User.update', model)}">
        <ui:button message="form.edit" href="${editActionName}?id=${id}" />
    </c:if>
    <c:if test="${ad:isPermitted('User.resetPassword', model)}">
        <ui:postButton href="${sendPasswordActionName}" entityId="${id}" message="user.resetSendPasswordByEmailphone"/>
        <ui:button id="passwordResetLinkButton" type="link" message="user.passwordResetLink.button"/>
    </c:if>
    <c:set var="entityBean" value="${model}"/>
    <%@ include file="../auditLog/viewLogButton.jspf" %>
</ui:header>

<ui:section titleKey="form.main">
    <ui:fieldGroup>

        <ui:field labelKey="user.email">
            <c:out value="${email}"/>
            <c:if test="${authType == authTypePSWD && not empty param['isPasswordSent']}">
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


        <c:if test="${not empty firstName}">
            <ui:simpleField labelKey="user.firstName" value="${firstName}"/>
        </c:if>

        <c:if test="${not empty lastName}">
            <ui:simpleField labelKey="user.lastName" value="${lastName}"/>
        </c:if>

        <c:choose>
            <c:when test="${not empty language}">
                <fmt:message key="enums.Language.${language}" var="langMessage"/>
            </c:when>
            <c:otherwise>
                <fmt:message key="enums.Language.EN" var="langMessage"/>
            </c:otherwise>
        </c:choose>
        <ui:simpleField labelKey="user.language" value="${langMessage}"/>

        <c:if test="${not empty jobTitle}">
            <ui:simpleField labelKey="user.jobTitle" value="${jobTitle}"/>
        </c:if>

        <c:if test="${not empty role.name}">
            <ui:field labelKey="user.role">
                <ui:userRoleLink roleField="${role}"/>
            </ui:field>
        </c:if>

        <ui:field labelKey="user.status">
            <c:set var="statusDescriptionKey">enums.Status.${status}</c:set>
            <ui:statusButtonGroup
                descriptionKey="${statusDescriptionKey}"
                entity="${model}" restrictionEntity="User"
                activatePage="${activatePageActionName}" inactivatePage="${inactivatePageActionName}"
                deletePage="${deletePageActionName}" undeletePage="${undeletePageActionName}"
                />
            <s:fielderror><s:param value="'version'"/></s:fielderror>
        </ui:field>

        <c:if test="${account.role == 'INTERNAL'}">
            <c:if test="${not empty dn}">
                <ui:simpleField labelKey="user.LDAPUser" value="${dn}"/>
            </c:if>
        </c:if>

        <c:if test="${not empty phone}">
            <ui:simpleField labelKey="user.phone" value="${phone}"/>
        </c:if>

        <c:if test="${account.role == 'AGENCY'}">
            <c:if test="${advLevelAccessFlag}">
                <fmt:message key="yes" var="advLevelAccessMessage"/>
                <ui:simpleField labelKey="user.advertiserLevelAccessControl" value="${advLevelAccessMessage}"/>
            </c:if>
        </c:if>

    </ui:fieldGroup>
</ui:section>

<c:if test="${advLevelAccessFlag and account.role == 'AGENCY'}">
  <c:set var="isAdvertiserEditEnable" value="false"/>
  <c:if test="${ad:isPermitted('User.update', model)}">
     <c:set var="isAdvertiserEditEnable" value="true"/>
  </c:if>

  <c:if test="${not empty userAdvertisers || isAdvertiserEditEnable}">
    <ui:section titleKey="user.advertisers">
        <ui:fieldGroup>
            <c:if test="${not empty userAdvertisers}">
                <c:set var="userAdvertisersString">
                    <ad:commaWriter items="${userAdvertisers}" label="name" escape="false"/>
                </c:set>
                <ui:simpleField value="${userAdvertisersString}"/>
            </c:if>

            <c:if test="${isAdvertiserEditEnable}">
                <ui:field>
                    <ui:button message="form.edit" href="editAdvertisers.action?id=${id}" />
                </ui:field>
            </c:if>
        </ui:fieldGroup>
    </ui:section>
  </c:if>
</c:if>

<c:if test="${account.role == 'PUBLISHER' and siteLevelAccessFlag and not empty userSites}">
    <ui:section titleKey="user.siteLevelAccessControl">
        <ui:fieldGroup>
            <ui:field>
                <fmt:message key="user.siteLevelAccessAllowed"/>:
                <ad:commaWriter items="${userSites}" label="name" escape="false"/>
            </ui:field>
        </ui:fieldGroup>
    </ui:section>
</c:if>

</s:form>
