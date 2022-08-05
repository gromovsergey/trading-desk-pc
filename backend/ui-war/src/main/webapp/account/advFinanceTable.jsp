<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="ad" uri="/ad/serverUI" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<c:set var="isMediaHandlingFeePresent" value="${ad:isInternal()}"/>
<c:set var="isEditAllowed" value="${ad:isPermitted('AdvertisingAccount.updateFinance', model)}"/>

<c:if test="${financialFieldsPresent || isMediaHandlingFeePresent || isEditAllowed}">
<ui:section titleKey="account.finance">
    <table class="grouping fieldGroups">
        <tr>
            <td>
                <ui:fieldGroup>
                    <s:if test="financialFieldsPresent">
                        <s:if test="!financialSettings.taxNumber.empty and country.vatEnabled">
                            <ui:simpleField labelKey="account.VATNumber" value="${financialSettings.taxNumber}"/>
                        </s:if>

                        <fmt:message key="account.paymentType.${financialSettings.paymentType}" var="paymentTypeMessage"/>
                        <ui:simpleField labelKey="account.paymentType" value="${paymentTypeMessage}"/>

                        <s:if test="accountType.perCampaignInvoicingFlag">
                            <fmt:message key="account.InvoiceGenerationType.${financialSettings.invoiceGenerationType}" var="invoiceGenerationMessage"/>
                            <ui:simpleField labelKey="account.displayCampaignInvoiceGeneration" value="${invoiceGenerationMessage}"/>
                        </s:if>

                        <s:if test="financialSettings.billingFrequency != null && financialSettings.billingFrequencyOffset != null">
                            <fmt:message var="billingFrequencyMessage" key="account.billingFrequencyMessage">
                                <fmt:param>
                                    <fmt:message key="enums.BillingFrequency.${financialSettings.billingFrequency}"/>
                                </fmt:param>
                                <fmt:param>
                                    ${financialSettings.billingFrequencyOffset}
                                </fmt:param>
                                <fmt:param>
                                    <fmt:formatDate value="${billingTime}" type="time" timeStyle="short" timeZone="${account.timezone.key}"/>
                                </fmt:param>
                                <fmt:param>
                                    <ad:resolveGlobal resource="timezone" id="${account.timezone.key}" prepare="true" />
                                </fmt:param>
                            </fmt:message>
                            <ui:simpleField labelKey="account.billingFrequency" value="${billingFrequencyMessage}"/>
                        </s:if>

                        <s:if test="financialSettings.paymentTerms != null">
                            <fmt:message key="account.paymentTermsWithDays" var="paymentTermsMessage">
                                <fmt:param>${financialSettings.paymentTerms}</fmt:param>
                            </fmt:message>
                            <ui:simpleField labelKey="account.paymentTerm" value="${paymentTermsMessage}"/>
                        </s:if>

                        <%-- Moved to agencyAdvertiserView.jsp and advertiserAccountView.jsp--%>
                        <%--<s:if test="agencyFlag || !standalone">--%>
                            <%--<s:if test="financialSettings.commission != null">--%>
                                <%--<ui:field labelKey="account.commission">--%>
                                    <%--<fmt:formatNumber var="commission" value="${financialSettings.commission * 100}" maxFractionDigits="2"/>--%>
                                    <%--<c:set var="textVal">${commission}%</c:set>--%>
                                    <%--<ui:text text="${pageScope.textVal}"/>--%>
                                <%--</ui:field>--%>
                            <%--</s:if>--%>
                        <%--</s:if>--%>
                    </s:if>

                    <c:if test="${isMediaHandlingFeePresent}">
                        <s:if test="financialSettings.mediaHandlingFee != null">
                            <ui:field labelKey="account.mediaHandlingFee">
                                <fmt:formatNumber var="mediaHandlingFee" value="${financialSettings.mediaHandlingFee * 100}" maxFractionDigits="3"/>
                                <c:set var="textVal">${mediaHandlingFee}%</c:set>
                                <ui:text text="${pageScope.textVal}"/>
                            </ui:field>
                        </s:if>
                    </c:if>

                    <c:if test="${isEditAllowed}">
                        <c:set var="editPage" value="${!standalone ? 'agencyAdvertiserEditFinance.action' : 'advertiserEditFinance.action'}" />
                        <c:set var="accountParam">${ad:accountParam("?accountId", id)}</c:set>
                        <ui:field cssClass="withButton">
                            <ui:button message="form.edit" href="${editPage}${accountParam}" />
                        </ui:field>
                    </c:if>
                </ui:fieldGroup>
            </td>
            <s:if test="financialFieldsPresent">
                <td>
                    <ui:fieldGroup>
                        <ui:field labelKey="account.balance" tipKey="account.balance.tip">
                            ${ad:formatCurrency(creditBalance, currency.currencyCode)}
                        </ui:field>

                        <s:if test="financialSettings.data.prepaidAmount != null && financialSettings.data.prepaidAmount > 0">
                            <ui:simpleField labelKey="account.prepaidAmount"
                                value="${ad:formatCurrency(financialSettings.data.prepaidAmount, currency.currencyCode)}"/>
                        </s:if>

                        <s:if test="financialSettings.creditLimit != null && financialSettings.paymentType.name() == 'POSTPAY'">
                            <ui:simpleField labelKey="account.creditLimit" value="${ad:formatCurrency(financialSettings.creditLimit,
                                currency.currencyCode)}"/>
                        </s:if>

                        <s:if test="financialSettings.data.invoicedOutstanding != null">
                            <ui:field labelKey="account.invoicedOutstanding" tipKey="account.invoicedOutstanding.tip">
                                ${ad:formatCurrency(financialSettings.data.invoicedOutstanding, currency.currencyCode)}
                            </ui:field>
                        </s:if>

                        <s:if test="financialSettings.data.notInvoiced != null">
                            <ui:field labelKey="account.notInvoiced" tipKey="account.notInvoiced.tip">
                                ${ad:formatCurrency(financialSettings.data.notInvoiced, currency.currencyCode)}
                            </ui:field>
                        </s:if>

                        <s:if test="financialSettings.minInvoice != null">
                            <ui:field labelKey="account.minimalInvoice" tipKey="account.minimalInvoice.tip">
                                ${ad:formatCurrency(financialSettings.minInvoice, currency.currencyCode)}
                            </ui:field>
                        </s:if>

                        <s:if test="financialSettings.data.totalPaid != null">
                            <ui:field labelKey="account.totalPaid" tipKey="account.totalPaid.tip">
                                ${ad:formatCurrency(financialSettings.data.totalPaid, currency.currencyCode)}
                            </ui:field>
                        </s:if>

                        <fmt:message key="account.amountsNetOfTax.tip" var="amountsTip"/>

                        <s:if test="agencyFlag || !standalone">
                            <s:if test="accountType.invoiceCommissionFlag">
                                <fmt:message key="account.amountsIncludeCommission.tip" var="amountsTip"/>
                            </s:if>
                            <s:else>
                                <fmt:message key="account.amountsExcludeCommission.tip" var="amountsTip"/>
                            </s:else>
                        </s:if>
                        <ui:field>
                            <span style="font-style: italic;">${amountsTip}</span>
                        </ui:field>
                    </ui:fieldGroup>
                </td>
            </s:if>
        </tr>
    </table>
</ui:section>
</c:if>
