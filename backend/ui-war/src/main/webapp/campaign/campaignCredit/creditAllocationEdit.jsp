<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>

<script type="text/javascript">
    function prepareCampaigns() {
        if($('#advertiserId').val() ) {
            UI.Data.Options.get('campaignsForCreditAllocation', 'campaignId', {
                advertiserId: $('#advertiserId').val()
            }, ['form.select.pleaseSelect']);
        } else {
            UI.Data.Options.replaceWith('campaignId', ['form.select.pleaseSelect']);
        }
    }

    function submitForm() {
        $('#campaignCreditAllocationForm').submit();
    }

    $().ready(function() {
        $('#advertiserId').change(prepareCampaigns);
    });
</script>

<ui:pageHeadingByTitle/>

<s:form action="%{#attr.isCreatePage?'create':'update'}" id="campaignCreditAllocationForm">
    <s:hidden name="id"/>
    <s:hidden name="version"/>
    <s:hidden name="campaignCredit.id"/>

    <div class="wrapper">
        <s:fielderror><s:param value="'version'"/></s:fielderror>
    </div>

    <ui:section>
        <ui:fieldGroup>
            <s:if test="allowSelectAdvertiser">
                <s:if test="id == null">
                    <ui:field labelKey="CampaignCreditAllocation.advertiser" labelForId="advertiser" errors="advertiserId" required="true">
                        <s:if test="existingCampaignCredit.advertiser != null && existingCampaignCredit.advertiser.id != null">
                            <s:hidden name="advertiserId" value="%{existingCampaignCredit.advertiser.id}"/>
                            <span class="preText"><c:out value="${existingCampaignCredit.advertiser.name}"/></span>
                        </s:if>
                        <s:else>
                            <s:select cssClass="middleLengthText" id="advertiserId" name="advertiserId"
                                      headerValue="%{getText('form.select.pleaseSelect')}" headerKey=""
                                      list="advertisers" listKey="id" listValue="name"/>
                        </s:else>
                    </ui:field>
                </s:if>
                <s:else>
                    <ui:field labelKey="CampaignCreditAllocation.advertiser">
                        <ui:displayStatus displayStatus="${existingCampaign.account.displayStatus}" testFlag="${existingCampaign.account.testFlag}">
                            <s:hidden name="advertiserId" value="%{existingCampaign.account.id}"/>
                            <span class="preText"><c:out value="${existingCampaign.account.name}"/></span>
                        </ui:displayStatus>
                    </ui:field>
                </s:else>
            </s:if>

            <s:if test="id == null">
                <ui:field labelKey="CampaignCreditAllocation.campaign" labelForId="campaignId" required="true" errors="campaign">
                    <s:select cssClass="middleLengthText" id="campaignId" name="campaign.id"
                              headerValue="%{getText('form.select.pleaseSelect')}" headerKey=""
                              list="campaigns" listKey="id" listValue="name"/>
                </ui:field>
            </s:if>
            <s:else>
                <ui:field labelKey="CampaignCreditAllocation.campaign">
                    <ui:displayStatus displayStatus="${existingCampaign.displayStatus}">
                        <c:out value="${existingCampaign.name}"/>
                    </ui:displayStatus>
                    <s:hidden name="campaign.id"/>
                </ui:field>
            </s:else>

            <c:set var="amountLable">
                <fmt:message key="CampaignCreditAllocation.amount"/> (${ad:currencySymbol(existingCampaignCredit.account.currency.currencyCode)})
            </c:set>
            <ui:field label="${amountLable}" labelForId="amount" required="true" errors="allocatedAmount">
                <s:textfield name="allocatedAmount" id="amount" cssClass="middleLengthText" maxlength="15"/>
            </ui:field>
        </ui:fieldGroup>
    </ui:section>

    <div class="wrapper">
        <ui:button message="form.save" type="button" onclick="submitForm();"/>
        <ui:button message="form.cancel" onclick="location='${_context}/campaignCredit/view.action?id=${campaignCredit.id}';" type="button"/>
    </div>
</s:form>