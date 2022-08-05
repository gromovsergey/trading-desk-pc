<%@ page contentType="text/html" %>
<%@ page pageEncoding="UTF-8" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="ad" uri="/ad/serverUI" %>
<%@ taglib prefix="display" uri="http://displaytag.sf.net/el" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<c:if test="${ad:isPermitted('Account.update', model)}">
    <c:set var="allowEdit" value="true"/>
</c:if>

<ui:errorsBlock>
    <s:fielderror><s:param value="'version'"/></s:fielderror>
</ui:errorsBlock>

<ui:header>
    <ui:pageHeadingByTitle />
    <c:if test="${allowEdit}">
        <ui:button message="form.edit" href="edit.action?id=${id}" />
    </c:if>
    <c:set var="entityBean" value="${model}"/>
    <%@ include file="../auditLog/viewLogButton.jspf" %>
</ui:header>

<ui:section titleKey="form.general.properties">
    <ui:fieldGroup>
        
        <ui:field labelKey="account.status">
            <ui:statusButtonGroup
                descriptionKey="${model.displayStatus.description}"
                entity="${model}"
                restrictionEntity="Account"
                activatePage="activate.action"
                inactivatePage="inactivate.action"
                deletePage="delete.action"
                undeletePage="undelete.action"

            />
        </ui:field>
        
        <%@ include file="accountDetailsView.jsp" %>
        
        <s:if test="isInternal()">
            <s:if test="testFlag">
                <fmt:message key="yes" var="testFlagMessage"/>
            </s:if>
            <s:else>
                <fmt:message key="no" var="testFlagMessage"/>
            </s:else>

            <ui:simpleField labelKey="account.testFlag" value="${testFlagMessage}"/>

            <fmt:message key="${passbackBelowFold ? 'publisher.passbackBelowFold.on' : 'publisher.passbackBelowFold.off'}" var="passbackBelowFoldMessage"/>
            <ui:simpleField labelKey="publisher.passbackBelowFold" value="${passbackBelowFoldMessage}"/>

            <ui:field labelKey="publisher.pubAdvertisingReport">
                <ui:text textKey="publisher.pubAdvertisingReport.${pubAdvertisingReportFlag}"/>
            </ui:field>

            <ui:field labelKey="publisher.referrerReport">
                <ui:text textKey="publisher.referrerReport.${referrerReportFlag}"/>
            </ui:field>

            <fmt:message key="${creativesReapproval ? 'publisher.creativesReapproval.true' : 'publisher.creativesReapproval.false'}" var="creativesReapprovalMessage"/>
            <ui:simpleField labelKey="publisher.creativesReapproval" value="${creativesReapprovalMessage}"/>
        </s:if>
        
        <%@ include file="internalAccountLink.jspf" %>
        
        <%@ include file="accountManagerField.jspf"%>
        
        <s:if test="!notes.empty">
            <ui:simpleField labelKey="account.notes" value="${notes}"/>
        </s:if>

        <s:if test="usePubPixel">
            <fmt:message key="yes" var="usePubPixelMessage"/>
        </s:if>
        <s:else>
            <fmt:message key="no" var="usePubPixelMessage"/>
        </s:else>

        <s:if test="isInternal()">
            <ui:simpleField labelKey="publisher.usesRetargetingPixels" value="${usePubPixelMessage}"/>
        </s:if>
        
    </ui:fieldGroup>
</ui:section>
<%@ include file="accountTermsView.jsp" %>
<%@ include file="accountsPayableFinanceTable.jsp" %>
<%@ include file="usersSection.jspf" %>
