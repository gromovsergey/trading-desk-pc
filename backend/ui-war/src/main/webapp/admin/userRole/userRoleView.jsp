<%@page contentType="text/html" %>
<%@page pageEncoding="UTF-8" %>

<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>

<ui:section>
    <ui:fieldGroup>

        <ui:field labelKey="UserRole.accountRole" labelForId="accountRoleId" errors="accountRole">
            <s:set var="textVal" value="getText('enum.accountRole.'+accountRole)"/>
            <ui:text text="${pageScope.textVal}"/>
            <s:hidden name="accountRole" id="accountRoleId"/>
        </ui:field>
        
        <c:if test="${accountRole == 'INTERNAL'}">
            <ui:field labelKey="UserRole.accessRestrictions">
                <c:choose>
                    <c:when test="${internalAccessType != 'MULTIPLE_ACCOUNTS'}">
                        <s:set var="textVal" value="getText('UserRole.InternalAccessType.view.' + internalAccessType)"/>
                        <ui:text text="${pageScope.textVal}"/>
                    </c:when>
                    <c:otherwise>
                        <c:set var="textVal">
                            <ad:commaWriter items="${accessAccounts}" label="name" escape="false"/>
                        </c:set>
                        <ui:text text="${pageScope.textVal}"/>
                    </c:otherwise>
                </c:choose>
            </ui:field>
        </c:if>

        <c:if test="${not empty ldapDn}">
            <ui:field labelKey="UserRole.ldapDnGroup">
                <ui:text text="${ldapDn}"/>
            </ui:field>
        </c:if>

        <c:if test="${not empty accountManagers}">
            <ui:field labelKey="UserRole.accountManager">
                <ui:text text="${accountManagers}"/>
            </ui:field>
        </c:if>

    </ui:fieldGroup>
</ui:section>

<ui:header>
    <h1><s:text name="UserRole.permissions"/></h1>
    <c:if test="${ad:isPermitted0('UserRole.update')}">
        <ui:button message="form.edit" href="Permissions/edit.action?userRole.id=${id}" type="link"/>
    </c:if>
</ui:header>

<c:choose>
    <c:when test="${permissionsSet}">
        <%@include file="policyView.jsp"%>

        <s:set var="currentPolicy" value="predefinedReportsPolicy" />
        <s:if test="#currentPolicy.hasPolicy()">
            <ui:header styleClass="level2">
                <h2><s:text name="UserRole.predefinedReportPermissions" /></h2>
            </ui:header>

            <%@include file="policyTableView.jsp"%>
        </s:if>

        <s:set var="currentPolicy" value="birtReportsPolicy" />
        <s:if test="#currentPolicy.hasPolicy()">
            <ui:header styleClass="level2">
                <h2><s:text name="UserRole.birtReportPermissions" /></h2>
            </ui:header>

            <%@include file="policyTableView.jsp"%>
        </s:if>

        <s:set var="currentPolicy" value="agentReportPolicy" />
        <s:if test="#currentPolicy.hasPolicy()">
            <ui:header styleClass="level2">
                <h2><s:text name="UserRole.agentReportPermissions" /></h2>
            </ui:header>

            <%@include file="policyTableView.jsp"%>
        </s:if>

        <s:set var="currentPolicy" value="audienceResearchPolicy" />
        <s:if test="#currentPolicy.hasPolicy()">
            <ui:header styleClass="level2">
                <h2><s:text name="UserRole.audienceResearchPermissions" /></h2>
            </ui:header>

            <%@include file="policyTableView.jsp"%>
        </s:if>
    </c:when>
    <c:otherwise>
        <div class="wrapper"><fmt:message key="nothing.found.to.display" /></div>
    </c:otherwise>
</c:choose>

