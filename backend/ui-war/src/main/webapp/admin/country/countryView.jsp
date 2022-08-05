<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="ad" uri="/ad/serverUI" %>

<ui:section titleKey="form.main">
    <ui:fieldGroup>
        <ui:simpleField labelKey="Country.countryCode"
              value="${countryCode}"/>
        <ui:simpleField labelKey="Country.name"
              value="${ad:resolveGlobal('country', countryCode, false)}"/>
        <c:if test="${not empty currency and not empty currency.currencyCode}">
            <ui:simpleField labelKey="Country.currency"
                    value="${ad:resolveGlobal('currency', currency.currencyCode, false)}"/>
        </c:if>
        <c:if test="${not empty timezone and not empty timezone.key}">
            <ui:simpleField labelKey="Country.timezone"
                    value="${ad:resolveGlobal('timezone', timezone.key, true)}"/>
            </c:if>
        <c:if test="${not empty language}">
            <ui:field labelKey="Country.language">
                <s:text name="enums.Language.%{language.toUpperCase()}"/>
            </ui:field>
        </c:if>
        <ui:simpleField labelKey="Country.defaultPaymentTerms"
                value="${defaultPaymentTerms}"/>
        <ui:simpleField labelKey="Country.adservingDomain" value="${adservingDomain}"/>
        <ui:simpleField labelKey="Country.discoverDomain" value="${discoverDomain}"/>
        <ui:simpleField labelKey="Country.staticDomain" value="${staticDomain}"/>
        <ui:simpleField labelKey="Country.adTagDomain" value="${adTagDomain}"/>
        <ui:simpleField labelKey="Country.conversionTagDomain" value="${conversionTagDomain}"/>
        <ui:simpleField labelKey="Country.adFooterURL"
                value="${adFooterURL}"/>
        <ui:simpleField labelKey="Country.highChannelThreshold"
                value="${highChannelThreshold}"/>
        <ui:simpleField labelKey="Country.lowChannelThreshold"
                value="${lowChannelThreshold}"/>
        <ui:simpleField labelKey="Country.minUrlTriggerThreshold"
                value="${minUrlTriggerThreshold}"/>
        <ui:field labelKey="Country.maxUrlTriggerShare.label">
            <fmt:formatNumber value="${maxUrlTriggerShareView}" maxFractionDigits="2"/>
        </ui:field>
        <ui:simpleField labelKey="Country.minRequiredTagVisibility.label"
                        value="${minRequiredTagVisibility}"/>
        <c:if test="${not empty sortOrder}">
            <ui:simpleField labelKey="Country.sortOrder"
                    value="${sortOrder}"/>
        </c:if>
        <c:if test="${vatEnabled}">
            <ui:field labelKey="Country.VAT">
                <ui:text textKey="form.enabled"/>
            </ui:field>
            <ui:field labelKey="Country.defaultVATRate">
                <fmt:formatNumber value="${defaultVATRateView}" maxFractionDigits="3"/>
            </ui:field>
        </c:if>
        <c:if test="${vatNumberInputEnabled}">
            <ui:field labelKey="Country.VATNumberInput">
                <ui:text textKey="form.enabled"/>
            </ui:field>
        </c:if>
        <ui:field labelKey="Country.defaultAgencyCommission">
            <fmt:formatNumber value="${defaultAgencyCommissionView}" maxFractionDigits="2"/>
        </ui:field>
        <s:if test="%{invoiceRptFile != null}">
            <ui:field labelKey="Country.invoiceRptFile">
                    <ui:button message="birtReports.download" href="downloadInvoiceRpt.action?id=${countryCode}"/>
            </ui:field>
        </s:if>
    </ui:fieldGroup>
</ui:section>

<ui:section titleKey="account.table.title.address">
    <ui:fieldGroup>
        <ui:field labelKey="country.address.fields">
            <c:set var="textVal">
                <c:set var="addressFieldsCount" value="${fn:length(addressFieldsList)}" />
                <s:iterator value="addressFieldsList" var="field" status="iStatus">
                    <c:if test="${field.enabled }">
                        ${ad:localizeName(field.name)}${(addressFieldsCount-1 != iStatus.index? ', ': '')}
                    </c:if>
                </s:iterator>
            </c:set>
            <ui:text text="${pageScope.textVal}"/>
        </ui:field>
    </ui:fieldGroup>
</ui:section>

<c:if test="${not empty siteCategoryTOList or not empty contentCategoryTOList}">
    <ui:section titleKey="Country.sitecontent.categories">
        <ui:fieldGroup>
            <c:if test="${not empty siteCategoryTOList}">
                <ui:field labelKey="Country.site.category">
                    <c:set var="textVal">
                        <ad:commaWriter items="${siteCategoryTOList}" label="name" escape="false"/>
                    </c:set>
                    <ui:text text="${pageScope.textVal}"/>
                </ui:field>
            </c:if>
            <c:if test="${not empty contentCategoryTOList}">
                <ui:field labelKey="Country.content.category">
                    <c:set var="textVal">
                        <ad:commaWriter items="${contentCategoryTOList}" label="name" escape="false"/>
                    </c:set>
                    <ui:text text="${pageScope.textVal}"/>
                </ui:field>
            </c:if>
        </ui:fieldGroup>
    </ui:section>
</c:if>
