<%@ page contentType="text/html" %>
<%@ page pageEncoding="UTF-8" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="ad" uri="/ad/serverUI" %>
<%@ taglib prefix="display" uri="http://displaytag.sf.net/el" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<c:if test="${ad:isPermitted('AgencyAdvertiserAccount.update', model)}">
    <s:set var="allowEdit" value="true"/>
</c:if>
<c:if test="${ad:isInternal()}">
    <s:set var="isInternal" value="true"/>
</c:if>

<ui:header>
    <ui:pageHeadingByTitle />
    <c:if test="${allowEdit}">
        <ui:button message="form.edit" href="agencyAdvertiserEdit.action?id=${id}" />
    </c:if>
    <s:set var="entityBean" value="model"/>
    <%@ include file="../auditLog/viewLogButton.jspf" %>
</ui:header>

<ui:section titleKey="form.general.properties">
    <ui:fieldGroup>

        <ui:field labelKey="account.status">
            <ui:statusButtonGroup
                descriptionKey="${displayStatus.description}"
                entity="${model}" restrictionEntity="AgencyAdvertiserAccount"
                activatePage="agencyAdvertiserActivate.action" inactivatePage="agencyAdvertiserInactivate.action"
                deletePage="agencyAdvertiserDelete.action" undeletePage="agencyAdvertiserUndelete.action" />
        </ui:field>

        <ui:simpleField labelKey="account.legalName" value="${legalName}"/>

        <s:if test="!companyRegistrationNumber.empty">
            <s:set var="cnpjKey" value="'account.companyRegistrationNumber'"/>
            <s:if test="country.countryCode == 'BR'">
                <s:set var="cnpjKey" value="'account.cnpj'"/>
            </s:if>
            <ui:simpleField labelKey="${cnpjKey}" value="${companyRegistrationNumber}"/>
        </s:if>

        <s:if test="!contactName.empty">
          <ui:simpleField labelKey="account.contactName" value="${contactName}"/>
        </s:if>

        <s:if test="!businessArea.empty">
          <ui:simpleField labelKey="account.businessArea" value="${businessArea}"/>
        </s:if>

        <s:if test="!specificBusinessArea.empty">
          <ui:simpleField labelKey="account.specificBusinessArea" value="${specificBusinessArea}"/>
        </s:if>

        <s:if test="isInternal">
            <s:if test="budgetLimitPresent">
                <ui:simpleField labelKey="account.budgetLimit" value="${financialSettings.data.prepaidAmount}"/>
            </s:if>
            <s:if test="commissionPresent">
                <ui:field labelKey="account.commission">
                    <fmt:formatNumber var="commission" value="${financialSettings.commission * 100}" maxFractionDigits="2"/>
                    <c:set var="textVal">${commission}%</c:set>
                    <ui:text text="${pageScope.textVal}"/>
                </ui:field>
            </s:if>
        </s:if>

        <s:if test="!notes.empty">
            <ui:simpleField labelKey="account.notes" value="${notes}"/>
        </s:if>
        
        <s:if test="tnsAdvertiser != null">
            <ui:simpleField labelKey="account.tnsAdvertiserIdName"
                value="${tnsAdvertiser.id} - ${tnsAdvertiser.name}"/>
        </s:if>

        <s:if test="tnsBrand != null">
            <ui:simpleField labelKey="account.tnsBrandIdName"
                    value="${tnsBrand.id} - ${tnsBrand.name}"/>
        </s:if>

        <c:if test="${not empty contentCategories}">
            <c:set var="contentCategoriesString">
                <ad:commaWriter items="${contentCategories}" label="name" escape="false"/>
            </c:set>
            <ui:simpleField labelKey="default.categories.content" value="${contentCategoriesString}"/>
        </c:if>

    </ui:fieldGroup>
</ui:section>

<s:set var="isAgencyAdvertiser" value="true"/>

<%--ToDo: uncomment when needed (OUI-28825)--%>
<%--<c:if test="${ad:isInternal()}">--%>
    <%--<%@ include file="advFinanceTable.jsp" %>--%>
    <%--<%@ include file="invoicesSection.jspf"%>--%>
<%--</c:if>--%>
