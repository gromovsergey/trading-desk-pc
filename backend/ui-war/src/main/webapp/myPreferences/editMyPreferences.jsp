<%@ page import="com.foros.security.AuthenticationType" %>
<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@ taglib uri="/struts-tags" prefix="s" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>

<s:form action="%{#request.moduleName}/save">
    <s:hidden name="id"/>
    <s:hidden name="account.id"/>
    <s:hidden name="account.name"/>
    <s:hidden name="version"/>
    <s:hidden name="role.name"/>

    <ui:pageHeadingByTitle/>
    <s:fielderror><s:param value="'error'"/></s:fielderror>

    <c:set var="authTypeLDAP" value="<%=AuthenticationType.LDAP.getName()%>"/>

    <ui:errorsBlock>
        <s:fielderror><s:param value="'version'"/></s:fielderror>
    </ui:errorsBlock>

    <ui:section titleKey="form.main">
        <ui:fieldGroup>

            <c:choose>
              <c:when test="${authType == authTypeLDAP}">
                <fmt:message var="authMessage" key="user.LDAP"/>
              </c:when>
              <c:otherwise>
                <fmt:message var="authMessage" key="user.password"/>
              </c:otherwise>
            </c:choose>
            <s:hidden name="authType"/>

            <ui:simpleField labelKey="user.authorization" value="${authMessage}"/>

            <ui:field labelKey="user.email" labelForId="email" required="true" errors="email">
                <input type="${ad:isMobileAgent(pageContext.request) ? 'email' : 'text'}" name="email" maxlength="320" value="${email}" id="email" class="middleLengthText">
            </ui:field>

            <ui:field labelKey="user.firstName" labelForId="firstName" required="true" errors="firstName">
                <s:textfield name="firstName" cssClass="middleLengthText" maxlength="50" id="firstName"/>
            </ui:field>

            <ui:field labelKey="user.lastName" labelForId="lastName" required="true" errors="lastName">
                <s:textfield name="lastName" cssClass="middleLengthText" maxlength="50" id="lastName"/>
            </ui:field>

            <ui:field labelKey="user.jobTitle" labelForId="jobTitle" errors="jobTitle">
                <s:textfield name="jobTitle" cssClass="middleLengthText" maxlength="30" id="jobTitle"/>
            </ui:field>

            <ui:field labelKey="user.language" labelForId="language" errors="languageIsoCode">
                <s:select name="language" cssClass="middleLengthText" id="language"
                    list="@com.foros.model.security.Language@values()"
                    listKey="name()" listValue="getText('user.language.' + name())"/>
            </ui:field>

            <ui:simpleField labelKey="user.role" value="${role.name}"/>
            <s:hidden name="role.id"/>

            <c:if test="${authType == authTypeLDAP}">
                <ui:simpleField labelKey="user.LDAPUser" value="${dn}"/>
                <s:hidden name="dn"/>
            </c:if>

            <ui:field labelKey="user.phone" labelForId="phone" required="true" errors="phone">
                <s:textfield name="phone" cssClass="middleLengthText" maxlength="80" id="phone"/>
            </ui:field>

            <c:if test="${ad:isInternal()}">
                <ui:field labelKey="user.deleted.objects" labelForId="deletedObjectsVisible">
                    <s:radio cssClass="withInput" name="deletedObjectsVisible" value="deletedObjectsVisible" list="showDeletedObjectsOption" listValue="getText(key)" listKey="value" id="deletedObjectsVisible"/>
                </ui:field>
            </c:if>

        </ui:fieldGroup>
    </ui:section>

    <div class="wrapper">
        <ui:button message="form.save" type="submit" novalidate="true" />
        <ui:button message="form.cancel" onclick="location='view.action';" type="button" />
    </div>
</s:form>
