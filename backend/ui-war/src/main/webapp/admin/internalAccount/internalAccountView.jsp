<%@ page contentType="text/html" %>
<%@ page pageEncoding="UTF-8" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="ad" uri="/ad/serverUI" %>
<%@ taglib prefix="display" uri="http://displaytag.sf.net/el" %>
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
    <ui:pageHeadingByTitle/>
    <c:if test="${allowEdit}">
        <ui:button message="form.edit" href="edit.action?id=${id}" />
    </c:if>
    <c:if test="${ad:isPermitted0('AuctionSettings.view')}">
        <ui:button message="AuctionSettings.title" href="viewAuctionSettings.action?id=${id}" />
    </c:if>
    <c:set var="entityBean" value="${model}"/>
    <%@ include file="../../auditLog/viewLogButton.jspf" %>
</ui:header>

<ui:section titleKey="form.general.properties">
    <ui:fieldGroup>
        
        <ui:field labelKey="account.status">
            <ui:statusButtonGroup
                descriptionKey="${displayStatus.description}"
                entity="${model}" restrictionEntity="Account"
                activatePage="activate.action" inactivatePage="inactivate.action"
                deletePage="delete.action" undeletePage="undelete.action"
                />
        </ui:field>
        
        <%@ include file="/account/accountDetailsView.jsp" %>
        
        <s:if test="advContact != null">
            <ui:field labelKey="account.advertiserContact">
                <ui:text text="${advContact.nameWithStatusSuffix}"/>
            </ui:field>
        </s:if>
        
        <s:if test="pubContact != null">
            <ui:field labelKey="account.publisherContact">
                <ui:text text="${pubContact.nameWithStatusSuffix}"/>
            </ui:field>
        </s:if>
        
        <s:if test="ispContact != null">
            <ui:field labelKey="account.ispContact">
                <ui:text text="${ispContact.nameWithStatusSuffix}"/>
            </ui:field>
        </s:if>
        
        <s:if test="cmpContact != null">
            <ui:field labelKey="account.cmpContact">
                <ui:text text="${cmpContact.nameWithStatusSuffix}"/>
            </ui:field>
        </s:if>

        <s:if test="!notes.empty">
            <ui:simpleField labelKey="account.notes" value="${notes}"/>
        </s:if>

    </ui:fieldGroup>
</ui:section>

<ui:section>
    <span class="groupOfLinks">
        <a href="/admin/Notices/main.action?accountId=${id}"><fmt:message key="admin.notices"/></a>
        <a href="/admin/TermsOfUse/main.action?accountId=${id}"><fmt:message key="admin.termsOfUse"/></a>
    </span>
</ui:section>

<%@ include file="/account/usersSection.jspf" %>
