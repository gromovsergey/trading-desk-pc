<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<ui:pageHeadingByTitle/>

<s:form action="%{#attr.isCreatePage?'create':'update'}" id="campaignCreditForm">
    <s:hidden name="id"/>
    <s:hidden name="version"/>
    <s:hidden name="account.id" id="accountId"/>

    <div class="wrapper">
        <s:fielderror><s:param value="'version'"/></s:fielderror>
    </div>

    <ui:section>
        <ui:fieldGroup>
            <ui:field labelKey="CampaignCredit.purpose" labelForId="purpose" required="true" errors="purpose">
                <s:radio name="purpose" id="purpose"
                         list="@com.foros.model.campaign.CampaignCreditPurpose@values()"
                         listKey="name()" listValue="getText('enums.CampaignCreditPurpose.' + name())"/>
            </ui:field>

            <c:set var="amountLabel">
                <fmt:message key="CampaignCredit.amount"/> (${ad:currencySymbol(existingAccount.currency.currencyCode)})
            </c:set>
            <ui:field label="${amountLabel}" labelForId="amount" required="true" errors="amount">
                <s:textfield name="amount" id="amount" cssClass="middleLengthText" maxlength="15"/>
            </ui:field>

            <ui:field labelKey="CampaignCredit.description" labelForId="description" required="true" errors="description">
                <s:textarea name="description" id="description" cssClass="middleLengthText" cssStyle="height: 50px" rows="3"/>
            </ui:field>

            <s:if test="allowSelectAdvertiser">
                <ui:field labelKey="CampaignCredit.advertiser" labelForId="advertiserId" errors="advertiser">
                    <c:choose>
                        <c:when test="${not empty advertisers}">
                            <s:select name="model.advertiser.id" cssClass="middleLengthText" id="advertiserId"
                                      headerValue="%{getText('form.all')}" headerKey=""
                                      list="advertisers" listKey="id" listValue="name"/>
                        </c:when>
                        <c:otherwise>
                            <fmt:message key="form.all"/>
                        </c:otherwise>
                    </c:choose>
                </ui:field>
            </s:if>

        </ui:fieldGroup>
    </ui:section>

    <div class="wrapper">
        <ui:button message="form.save" type="submit" />
        <ui:button message="form.cancel" onclick="location='/admin/advertiser/account/advertiserView.action?id=${account.id}';" type="button"/>
    </div>
</s:form>