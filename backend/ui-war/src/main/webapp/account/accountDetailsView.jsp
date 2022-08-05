<%@ taglib prefix="ad" uri="/ad/serverUI" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:if test="!legalName.empty">
    <ui:simpleField labelKey="account.legalName" value="${legalName}"/>
</s:if>
<s:if test="(role.name == 'Agency' || role.name == 'Advertiser') && contractNumber != null && !contractNumber.empty">
    <s:if test="contractDate != null">
        <fmt:formatDate value="${contractDate}"
                        type="date"
                        dateStyle="short"
                        timeZone="${account.timezone.key}"
                        var="formattedContractDate"/>
        <ui:simpleField labelKey="account.contractNumber" value="${contractNumber} ${formattedContractDate}"/>
    </s:if>
    <s:if test="contractDate == null">
        <ui:simpleField labelKey="account.contractNumber" value="${contractNumber}"/>
    </s:if>
</s:if>
<s:if test="!companyRegistrationNumber.empty">
    <s:set var="cnpjKey" value="'account.companyRegistrationNumber'"/>
    <s:if test="country.countryCode == 'BR'">
        <s:set var="cnpjKey" value="'account.cnpj'"/>
    </s:if>
    <ui:simpleField labelKey="${cnpjKey}" value="${companyRegistrationNumber}"/>
</s:if>

<s:if test="!country.countryCode.empty">
    <ui:field labelKey="account.country">
        <ui:countryLink countryCode="${country.countryCode}"/>
    </ui:field>
</s:if>

<s:if test="!currency.currencyCode.empty">
    <ui:simpleField labelKey="account.currency"
            value="${ad:resolveGlobal('currency', currency.currencyCode, false)}"/>
</s:if>

<s:if test="tnsAdvertiser != null">
    <ui:simpleField labelKey="account.tnsAdvertiserIdName"
            value="${tnsAdvertiser.id} - ${tnsAdvertiser.name}"/>
</s:if>

<s:if test="tnsBrand != null">
    <ui:simpleField labelKey="account.tnsBrandIdName"
            value="${tnsBrand.id} - ${tnsBrand.name}"/>
</s:if>

<fmt:message key="enum.accountRole.${role}" var="accountRoleMessage"/>
<ui:simpleField labelKey="account.role" value="${accountRoleMessage}"/>

<s:if test="!accountType.name.empty">
	<c:choose>
		<c:when test="${ad:isPermitted0('AccountType.view')}">
			<ui:field labelKey="account.accountTypeId">
				<a href="${_context}/AccountType/view.action?id=${accountType.id}"><c:out value="${accountType.name}"/></a>
			</ui:field>
		</c:when>
		<c:otherwise>
			<ui:simpleField labelKey="account.accountTypeId" value="${accountType.name}" />
		</c:otherwise>
	</c:choose>
</s:if>

<s:if test="!timezone.key.empty">
    <ui:simpleField labelKey="account.timeZone"
            value="${ad:resolveGlobal('timezone', timezone.key, true)}"/>
</s:if>

