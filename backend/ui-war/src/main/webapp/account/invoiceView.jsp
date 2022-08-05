<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<ui:header>   
    <ui:pageHeadingByTitle/>
    
            <c:if test="${ad:isPermitted('Invoice.update', model)}">
                <ui:button message="form.edit" href="invoiceEdit.action?id=${id}" />
            </c:if>
    
            <s:if test="genPrintInvoiceAvailable">
                <ui:button message="account.invoice.genPrintInvoice" href="generatePrintableInvoice.action?id=${id}" target="_blank" />
            </s:if>
</ui:header>

<ui:errorsBlock>
    <s:fielderror><s:param value="'version'"/></s:fielderror>
</ui:errorsBlock>

<ui:section titleKey="form.summary">
    <table class="grouping fieldGroups">
        <tr>
            <td>
                <ui:fieldGroup>
                    <ui:field labelKey="account.status">
                        <c:set var="textVal">
                            <fmt:message key="enums.FinanceStatus.${status}"/>
                        </c:set>
                        <ui:text text="${pageScope.textVal}"/>
                        <c:if test="${ad:isPermitted('Invoice.generate', model)}">
                            <ui:postButton href="generateInvoice.action" entityId="${id}" message="account.invoice.generateInvoice"
                                           onclick="if (!confirm('${ad:formatMessage('account.invoice.confirmGenerate')}')) {return false;}"
                                           />
                        </c:if>
                    </ui:field>

                    <s:if test="status.letter != 'D'">
                        <s:if test="invoicingPeriod.invoicingPeriodStartDate != null && invoicingPeriod.invoicingPeriodEndDate != null">
                            <fmt:formatDate var="localizedInvoicingPeriodStartDate" value="${invoicingPeriod.invoicingPeriodStartDate}"
                                            type="date" dateStyle="short"/>
                            <fmt:formatDate var="localizedInvoicingPeriodEndDate" value="${invoicingPeriod.invoicingPeriodEndDate}"
                                            type="date" dateStyle="short"/>
                            <ui:field labelKey="account.invoice.invoicingPeriod">
                                ${localizedInvoicingPeriodStartDate} - ${localizedInvoicingPeriodEndDate}
                            </ui:field>
                        </s:if>
                    </s:if>

                    <s:if test="invoiceEmailDate != null">
                        <fmt:formatDate var="localizedEmailDate" value="${invoiceEmailDate}"
                                        type="both" timeStyle="short" dateStyle="short"/>
                        <ui:simpleField labelKey="account.invoice.emailedDate" value="${localizedEmailDate}"/>
                    </s:if>

                    <s:if test="dueDate != null">
                        <fmt:formatDate var="localizedDueDate" value="${dueDate}"
                                        type="date" dateStyle="short"/>
                        <ui:simpleField labelKey="account.table.title.dueDate" value="${localizedDueDate}"/>
                    </s:if>


                    <s:if test="closedDate != null && status.letter == 'C'">
                        <fmt:formatDate var="localizedClosedDate" value="${closedDate}"
                                        type="both" timeStyle="short" dateStyle="short"/>
                        <ui:simpleField labelKey="account.invoice.closedDate" value="${localizedClosedDate}"/>
                    </s:if>

                    <s:if test="!advertiserName.empty">
                        <ui:field labelKey="account.invoice.advertiser">
                            <ui:displayStatus displayStatus="${account.displayStatus}">
                                <c:choose>
                                    <c:when test="${ad:isPermitted('Account.view', account)}">
                                        <s:if test="isInternal()">
                                            <c:set var="viewAccountActionPathVar"
                                                   value="/admin/account/view.action?id=${account.id}"/>
                                        </s:if>
                                        <s:elseif test="!account.standalone">
                                            <c:set var="viewAccountActionPathVar"
                                                   value="/advertiser/myAccount/agencyAdvertiserView.action?id=${account.id}"/>
                                        </s:elseif>
                                        <s:else>
                                            <c:set var="viewAccountActionPathVar"
                                                   value="/advertiser/myAccount/myAccountView.action"/>
                                        </s:else>
                                        <a href="${viewAccountActionPathVar}">
                                            <ui:text text="${advertiserName}"/>
                                        </a>
                                    </c:when>
                                    <c:otherwise>
                                        <ui:text text="${advertiserName}"/>
                                    </c:otherwise>
                                </c:choose>
                            </ui:displayStatus>
                        </ui:field>
                    </s:if>

                    <s:if test="account.accountType.perCampaignInvoicingFlag && !campaign.name.empty">
                        <ui:field labelKey="account.campaign">
                            <ui:displayStatus displayStatus="${campaign.displayStatus}">
                                <c:choose>
                                    <c:when test="${ad:isPermitted('AdvertiserEntity.view', campaign)}">
                                            <c:set var="viewCampaignActionPathVar"
                                                   value="${_context}/campaign/view.action" scope="page"/>
                                            <a href="${viewCampaignActionPathVar}?id=${campaign.id}">
                                                <c:out value="${campaign.name}"/>
                                            </a>
                                    </c:when>
                                    <c:otherwise>
                                        <c:out value="${campaign.name}"/>
                                    </c:otherwise>
                                </c:choose>
                            </ui:displayStatus>
                        </ui:field>
                    </s:if>
                </ui:fieldGroup>
            </td>
            <td>
                <ui:fieldGroup>
                    <s:if test="commissionAmount != null && commissionAmount > 0">
                        <ui:field labelKey="account.invoice.netAmount">
                            <ui:text text="${ad:formatCurrency(totalAmountNet, account.currency.currencyCode)}"/>
                        </ui:field>
                    </s:if>

                    <s:if test="!opportunities.empty">
                        <ui:field>
                            <s:iterator value="opportunities" var="opportunity">
                                <div>
                                    <c:choose>
                                        <c:when test="${moduleName == 'admin/campaign' || moduleName == 'advertiser/campaign'}">
                                            <s:set var="ioUrl">${_context}/insertionOrder/view.action?id=${opportunity.opportunityId}&invoiceId=${id}&campaignId=${campaign.id}</s:set>
                                        </c:when>
                                        <c:otherwise>
                                            <s:set var="ioUrl">${_context}/insertionOrder/view.action?id=${opportunity.opportunityId}&invoiceId=${id}</s:set>
                                        </c:otherwise>
                                    </c:choose>
                                    <a href="${ioUrl}">
                                        <fmt:message key="campaignAllocation.ioNumber">
                                            <fmt:param value="${opportunity.ioNumber}"/>
                                        </fmt:message>
                                    </a>

                                    <c:if test="${opportunity.poNumber != null}">
                                        (<fmt:message key="campaignAllocation.poNumber"><fmt:param value="${opportunity.poNumber}"/></fmt:message>)
                                    </c:if>
                                    :
                                    ${ad:formatCurrency(opportunity.amount, account.currency.currencyCode)}
                                    <c:if test="${opportunity.campaignId != null}">
                                        (<a href="${_context}/campaign/view.action?id=${opportunity.campaignId}">
                                            <c:out value="${opportunity.campaignName}"/>
                                        </a>)
                                    </c:if>
                                </div>
                            </s:iterator>
                        </ui:field>
                    </s:if>

                    <s:if test="commissionAmount != null && commissionAmount > 0">
                        <ui:field labelKey="account.invoice.commission">
                            <ui:text text="${ad:formatCurrency(commissionAmount, account.currency.currencyCode)}"/>
                        </ui:field>
                    </s:if>

                    <ui:field labelKey="account.invoice.totalAmountPayable">
                        <ui:text text="${ad:formatCurrency(totalAmountDue, account.currency.currencyCode)}"/>
                    </ui:field>

                    <s:if test="paidAmount != null">
                        <ui:field labelKey="account.invoice.paid">
                            <ui:text text="${ad:formatCurrency(paidAmount, account.currency.currencyCode)}"/>
                        </ui:field>
                    </s:if>

                    <s:if test="deductFromPrepaidAmount != null && deductFromPrepaidAmount != 0">
                        <ui:field labelKey="account.invoice.deductedFromPrepaidAmount">
                            <ui:text text="${ad:formatCurrency(deductFromPrepaidAmount, account.currency.currencyCode)}"/>
                        </ui:field>
                    </s:if>

                    <s:if test="creditSettlement != null && creditSettlement != 0">
                        <ui:field labelKey="account.invoice.creditNoteNegotiatedSettlement">
                            <ui:text text="${ad:formatCurrency(creditSettlement, account.currency.currencyCode)}"/>
                        </ui:field>
                    </s:if>

                    <ui:field labelKey="account.invoice.outstanding">
                        <ui:text text="${ad:formatCurrency(openAmount, account.currency.currencyCode)}"/>
                    </ui:field>

                    <s:if test="isInternal() && publisherAmountNet != null">
                        <ui:field labelKey="account.invoice.publisherNetAmount">
                            <ui:text text="${ad:formatCurrency(publisherAmountNet, account.currency.currencyCode)}"/>
                        </ui:field>
                    </s:if>

                    <ui:field>
                        <span style="font-style: italic;"><fmt:message key="account.invoice.amountsTip"/></span>
                    </ui:field>
                </ui:fieldGroup>
            </td>
        </tr>
    </table>
</ui:section>

<ui:header styleClass="level2">
    <h2><fmt:message key="account.invoice.invoiceData"/></h2>
</ui:header>

<% pageContext.setAttribute("newLineChar", "\n"); %>
<c:set var="nonZeroInvoiceCount" value="0"/>
<c:forEach items="${invoiceDatas}" var="invoiceData">
    <c:if test="${invoiceData.quantity != 0}">
        <c:set var="nonZeroInvoiceCount" value="${nonZeroInvoiceCount+1}"/>
    </c:if>
</c:forEach>
<s:form id="InvoiceForm" action="%{#attr.moduleName}/invoiceView">
    <c:choose>
        <c:when test="${nonZeroInvoiceCount == 0}">
            <div class="wrapper">
                <fmt:message key="nothing.found.to.display"/>
            </div>
        </c:when>
        <c:otherwise>
            <table class="dataView" id="invoiceData" >
                <thead>
                <s:if test="!account.accountType.perCampaignInvoicingFlag">
                    <th><fmt:message key="account.campaign"/></th>
                </s:if>
                <th><fmt:message key="account.invoice.campaignCreativeGroup"/></th>
                <th><fmt:message key="account.invoice.description"/></th>
                <th><fmt:message key="account.invoice.unitOfMeasure"/></th>
                <th><fmt:message key="account.invoice.unitPrice"/></th>
                <th><fmt:message key="account.invoice.quantity"/></th>
                <th><fmt:message key="account.invoice.amount"/></th>
                </thead>
                <tbody>
                <c:forEach items="${invoiceDatas}" var="invoiceData" varStatus="indexID">
                <c:choose>
                <c:when test="${invoiceData.quantity == 0}">
                    <s:hidden name="invoiceDataQuantityMap['%{#attr.invoiceData.id}']"
                              id="invoiceData[%{#attr.indexID.index}]"
                              disabled="true"/>
                </c:when>
                <c:otherwise>
                <tr>
                    <s:if test="!account.accountType.perCampaignInvoicingFlag">
                        <td>
                            <ui:displayStatus displayStatus="${invoiceData.ccg.campaign.displayStatus}">
                            <c:choose>
                                <c:when test="${ad:isPermitted('AdvertiserEntity.view', invoiceData.ccg.campaign)}">
                                    <a class="preText" href="${_context}/campaign/view.action?id=${invoiceData.ccg.campaign.id}"><c:out value="${invoiceData.ccg.campaign.name}"/></a>
                                </c:when>
                                <c:otherwise>
                                    <span class="preText"><c:out value="${invoiceData.ccg.campaign.name}"/></span>
                                </c:otherwise>
                            </c:choose>
                            </ui:displayStatus>
                        </td>
                    </s:if>
                    <td class="withField">
                        <ui:displayStatus displayStatus="${invoiceData.ccg.displayStatus}">
                        <c:choose>
                            <c:when test="${ad:isPermitted('AdvertiserEntity.view', invoiceData.ccg)}">
                                <c:choose>
                                    <c:when test="${invoiceData.ccg.ccgType.letter == 'T'}">
                                        <a class="preText" href="${_context}/campaign/group/viewText.action?id=${invoiceData.ccg.id}"><c:out
                                                value="${invoiceData.ccg.name}"/></a>
                                    </c:when>
                                    <c:otherwise>
                                        <a class="preText" href="${_context}/campaign/group/viewDisplay.action?id=${invoiceData.ccg.id}"><c:out
                                                value="${invoiceData.ccg.name}"/></a>
                                    </c:otherwise>
                                </c:choose>
                            </c:when>
                            <c:otherwise>
                                <c:out value="${invoiceData.ccg.name}"/>
                            </c:otherwise>
                        </c:choose>
                        </ui:displayStatus>
                    </td>
                    <td class="withField">
                            ${fn:replace(fn:escapeXml(invoiceData.description), newLineChar, "<br>")}
                    </td>
                    <td class="withField">
                        <fmt:message key="account.invoice.unitOfMeasure.${invoiceData.unitOfMeasure}"/>
                    </td>
                    <c:choose>
                        <c:when test="${invoiceData.unitOfMeasure == 'revenue_share'}">
                            <td class="withField">
                                <fmt:message key="notAvailable"/>
                            </td>
                            <td class="withField">
                                <fmt:message key="notAvailable"/>
                            </td>
                            <td class="withField">
                                <fmt:message key="notAvailable"/>
                            </td>
                        </c:when>
                        <c:otherwise>
                            <td class="currency">
                                    ${ad:formatCurrencyExt(invoiceData.unitPrice, account.currency.currencyCode, 5)}
                            </td>
                            <td class="number">
                                <s:if test="isInternal()">
                                    <table class="fieldAndAccessories">
                                        <tr>
                                            <td class="withField">
                                                <span id="q[${indexID.index}]">${invoiceData.quantity}</span>
                                            </td>
                                            <td class="withField mandatory" style="display:none;"
                                                id="invoiceDataQuantityTD[${indexID.index}]">
                                                    <s:textfield
                                                            name="invoiceDataQuantityMap['%{#attr.invoiceData.id}']"
                                                            id="invoiceData[%{#attr.indexID.index}]"
                                                            disabled="true"/>
                                            </td>
                                            <td class="withError">
                                                <c:set value="invoiceDataQuantityMap['${invoiceData.id}']" var="fieldName"/>
                                                <s:if test="!fieldErrors[#attr.fieldName].empty">
                                                    <span class="errors">
                                                        <fmt:message key="errors.field.integer"/>
                                                    </span>
                                                </s:if>
                                                <s:fielderror>
                                                    <s:param>invoiceData[${invoiceData.id}]</s:param>
                                                </s:fielderror>
                                            </td>
                                        </tr>
                                    </table>
                                </s:if>
                                <s:else>
                                    <fmt:formatNumber value="${invoiceData.quantity}" groupingUsed="true"/>
                                </s:else>
                            </td>
                            <td class="currency">
                                    ${ad:formatCurrency(invoiceData.amount, account.currency.currencyCode)}
                            </td>
                        </c:otherwise>
                    </c:choose>
                </tr>
                </c:otherwise>
                </c:choose>
                </c:forEach>
            </table>
        </c:otherwise>
    </c:choose>
</s:form>
