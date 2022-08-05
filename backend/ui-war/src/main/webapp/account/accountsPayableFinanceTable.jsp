<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<ui:section titleKey="account.finance">
    <table class="grouping fieldGroups">
        <tr>
            <td>
                <ui:fieldGroup>
                    <s:if test="!financialSettings.taxNumber.empty && account.country.vatEnabled">
                        <ui:simpleField labelKey="account.VATNumber" value="${financialSettings.taxNumber}"/>
                    </s:if>

                    <s:if test="financialSettings.paymentMethod != null">
                        <fmt:message key="enums.PaymentMethod.${financialSettings.paymentMethod}" var="paymentMethodMessage"/>
                        <ui:simpleField labelKey="account.paymentMethod" value="${paymentMethodMessage}"/>
                    </s:if>

                    <s:if test="!financialSettings.bankCountry.countryCode.empty">
                        <ui:field labelKey="account.bankCountry">
                            <ui:countryLink countryCode="${financialSettings.bankCountry.countryCode}"/>
                        </ui:field>
                    </s:if>

                    <s:if test="!financialSettings.bankAccountNumber.empty">
                        <ui:simpleField labelKey="account.bankAccountNumber" value="${financialSettings.bankAccountNumberStripped}"/>
                    </s:if>

                    <s:if test="financialSettings.commission != null">
                        <s:if test="role.name == 'Publisher'">
                            <ui:field labelKey="account.handlingFee">
                                <fmt:formatNumber var="commission" value="${financialSettings.commission * 100}" maxFractionDigits="2"/>
                                <c:set var="textVal">
                                    ${commission}%
                                </c:set>
                                <ui:text text="${pageScope.textVal}"/>
                            </ui:field>
                        </s:if>
                    </s:if>

                    <s:if test="financialSettings.defaultBillToUser != null">
                        <ui:simpleField labelKey="account.accountBillingContact" value="${financialSettings.defaultBillToUser.nameWithStatusSuffix}"/>
                    </s:if>

                    <c:set var="canUpdateFinance" value="${ad:isPermitted('AccountsPayableAccount.updateFinance', model)}"/>
                    <s:if test="#attr.canUpdateFinance">
                        <ui:field cssClass="withButton">
                            <c:set var="accountParam">${ad:accountParam("?accountId", id)}</c:set>
                            <ui:button message="form.edit" href="editFinance.action${accountParam}" />
                        </ui:field>
                    </s:if>

                </ui:fieldGroup>
            </td>
        </tr>
    </table>
</ui:section>
