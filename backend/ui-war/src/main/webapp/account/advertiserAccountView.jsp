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
<c:if test="${ad:isInternal()}">
    <s:set var="isInternal" value="true"/>
</c:if>

<ui:header>
<ui:pageHeadingByTitle/>
    <c:if test="${allowEdit}">
        <s:if test="agencyFlag">
            <s:set value="'agencyEdit.action'" var="editActionName"/>
        </s:if>
        <s:else>
            <s:set value="'advertiserEdit.action'" var="editActionName"/>
        </s:else>
        <ui:button message="form.edit" href="${editActionName}?id=${id}" />
    </c:if>
    <s:set var="entityBean" value="model"/>
    <%@ include file="../auditLog/viewLogButton.jspf" %>
</ui:header>

<ui:section titleKey="form.general.properties">
    <ui:fieldGroup>
        
        <ui:field labelKey="account.status">
            <ui:statusButtonGroup
                descriptionKey="${model.displayStatus.description}"
                entity="${model}"
                restrictionEntity="Account" 
                activatePage="advertiserActivate.action"
                inactivatePage="advertiserInactivate.action"
                deletePage="advertiserDelete.action"
                undeletePage="advertiserUndelete.action"
            />
        </ui:field>

        <%@ include file="accountDetailsView.jsp" %>
        
        <s:if test="isInternal">
            <s:if test="international">
                <fmt:message key="yes" var="internationalFlagMessage"/>
            </s:if>
            <s:else>
                <fmt:message key="no" var="internationalFlagMessage"/>
            </s:else>

            <ui:simpleField labelKey="account.international" value="${internationalFlagMessage}"/>
            
            <s:if test="testFlag">
                <fmt:message key="yes" var="testFlagMessage"/>
            </s:if>
            <s:else>
                <fmt:message key="no" var="testFlagMessage"/>
            </s:else>

            <ui:simpleField labelKey="account.testFlag" value="${testFlagMessage}"/>

            <s:if test="selfServiceFlag">
                <c:set var="selfServiceMessage">${selfServiceCommissionPercent}%</c:set>
            </s:if>
            <s:else>
                <fmt:message key="no" var="selfServiceMessage"/>
            </s:else>

            <ui:simpleField labelKey="account.selfService" value="${selfServiceMessage}"/>

            <s:if test="siteTargetingFlag || pubConversionReportFlag">
                <ui:field labelKey="account.externalAccess">
                    <s:if test="siteTargetingFlag && accountType.siteTargetingFlag">
                        <label class="withInput">
                            <s:text name="account.siteTargeting"/>
                        </label>
                    </s:if>
                    <s:if test="pubConversionReportFlag">
                        <label class="withInput">
                            <s:text name="account.publisherConversionReport"/>
                        </label>
                    </s:if>
                </ui:field>
            </s:if>
        </s:if>
        
        <%@ include file="internalAccountLink.jspf" %>
        
        <%@ include file="accountManagerField.jspf"%>

        <s:if test="isInternal">
            <s:if test="commissionPresent">
                <ui:field labelKey="account.commission">
                    <fmt:formatNumber var="commission" value="${financialSettings.commission * 100}" maxFractionDigits="2"/>
                    <c:set var="textVal">${commission}%</c:set>
                    <ui:text text="${pageScope.textVal}"/>
                </ui:field>
            </s:if>
            <s:if test="budgetLimitPresent">
                <ui:simpleField labelKey="account.budgetLimit" value="${financialSettings.data.prepaidAmount}"/>
            </s:if>
        </s:if>

        <s:if test="!notes.empty">
            <ui:field labelKey="account.notes">
                <ui:text subClass="entityName" text="${notes}"/>
            </ui:field>
        </s:if>
        
        <s:if test="accountType.allowTextAdvertisingFlag">
            <fmt:message key="enums.TextAdservingMode.${textAdservingMode}" var="testAdvMessage"/>
            <ui:simpleField labelKey="account.textAdserving" value="${testAdvMessage}"/>
        </s:if>

        <s:if test="role.name == 'Advertiser'">
            <s:if test="!businessArea.empty">
              <ui:simpleField labelKey="account.businessArea" value="${businessArea}"/>
            </s:if>
            <s:if test="!specificBusinessArea.empty">
              <ui:simpleField labelKey="account.specificBusinessArea" value="${specificBusinessArea}"/>
            </s:if>
        </s:if>

        <c:if test="${not empty contentCategories}">
            <c:set var="contentCategoriesString">
                <ad:commaWriter items="${contentCategories}" label="name" escape="false"/>
            </c:set>
            <ui:simpleField labelKey="default.categories.content" value="${contentCategoriesString}"/>
        </c:if>

    </ui:fieldGroup>
</ui:section>

<c:if test="${!agencyAdvertiserAccountRequest}">
<%@ include file="accountTermsView.jsp" %>
</c:if>
<%--ToDo: uncomment when needed (OUI-28825)--%>
<%--<%@ include file="advFinanceTable.jsp" %>--%>
<c:if test="${ad:isPermitted('CampaignCredit.view', model)}">
    <%@ include file="campaignCreditsSection.jsp" %>
</c:if>

<%@ include file="usersSection.jspf" %>

<%--ToDo: uncomment when needed (OUI-28825)--%>
<%--<s:if test="!agencyFlag">--%>
    <%--<%@ include file="invoicesSection.jspf"%>--%>
<%--</s:if>--%>
