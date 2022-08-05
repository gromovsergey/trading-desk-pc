<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="ad" uri="/ad/serverUI"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="s" uri="/struts-tags" %>

<c:set var="commissionLabel">
    <fmt:message key="account.agencyCommission"/> (%)
</c:set>
<c:set var="isInternal" value="${ad:isInternal()}"/>
<s:if test="role.name == 'Advertiser'">
    <s:set value="#attr.isCreatePage?'advertiserCreate':'advertiserUpdate'" var="saveActionName"/>
</s:if>
<s:else>
    <s:set value="#attr.isCreatePage?'agencyCreate':'agencyUpdate'" var="saveActionName"/>
</s:else>

<s:form action="%{#request.moduleName}/%{#saveActionName}" id="actionSave">
    <s:hidden name="id"/>
    <s:hidden name="version"/>
    <s:hidden name="financialSettings.version"/>
    <s:if test="hasActionErrors()">
        <div style="margin-top: 5px; margin-bottom: 5px">
            <s:actionerror/>
        </div>
    </s:if>

    <ui:pageHeadingByTitle />
    
    <ui:errorsBlock>
        <s:fielderror><s:param value="'version'"/></s:fielderror>
    </ui:errorsBlock>

    <ui:section titleKey="form.main">

        <ui:fieldGroup>
            <%@ include file="accountDetailsEdit.jsp"%>

            <s:if test="isInternal()">
                <ui:field labelKey="account.international" labelForId="internationalFlag" errors="internationalFlag">
                    <s:if test="id != null && internationalFlag">
                        <s:checkbox name="internationalFlag" id="internationalFlag" disabled="true" />
                    </s:if>
                    <s:else>
                        <s:checkbox name="internationalFlag" id="internationalFlag" />
                    </s:else>
                </ui:field>

                <s:set var="testFlagDisabled" value="true"/>
                <c:if test="${ad:isPermitted('Account.setTestFlag', existingAccount)}">
                    <s:set var="testFlagDisabled" value="false" />
                </c:if>

                <ui:field id="testFlagTr" labelKey="account.testFlag" labelForId="testFlag" errors="testFlag">
                    <s:if test="testFlagDisabled">
                        <s:checkbox name="testFl" id="testFlag" disabled="true" />
                    </s:if>
                    <s:else>
                        <s:checkbox name="testFl" id="testFlag" />
                    </s:else>
                </ui:field>

                <ui:field id="selfServiceTr" labelKey="account.selfService" labelForId="selfServiceFlag" errors="selfServiceFlag">
                    <s:checkbox name="selfServiceFlag" id="selfServiceFlag" />
                    <s:textfield name="selfServiceCommissionPercent" id="selfServiceCommissionPercent" cssClass="smallLengthText1" maxlength="15"/>
                    <s:fielderror fieldName="selfServiceCommissionPercent" />
                </ui:field>

                <ui:field labelKey="account.externalAccess" labelForId="externalAccessFlag" errors="externalAccesslFlag">
                <div id="siteTargetingFlagDiv" class="hide">
                    <label class="withInput">
                        <s:checkbox name="siteTargetingFlag" id="siteTargetingFlag"/>
                        <s:text name="account.siteTargeting"/>
                    </label>
                </div>
                <label class="withInput">
                    <s:checkbox name="pubConversionReportFlag" id="pubConversionReportFlag"/>
                    <s:text name="account.publisherConversionReport"/>
                </label>
                </ui:field>
            </s:if>

            <%@ include file="internalAccountLinkEdit.jspf"%>

            <ui:field id="accountManTr" labelKey="account.accountManager" labelForId="accountManagerId">
                <s:select name="accountManager.id" id="accountManagerId" cssClass="middleLengthText"
                          headerValue="%{getText('form.select.none')}" headerKey=""
                          list="internalUsers"
                          listKey="id" listValue="name" value="accountManager.id" >
                </s:select>
            </ui:field>

            <%-- "hidden" css class should be linked --%>
            <ui:field id="textAdservingTr" cssClass="hidden" labelKey="account.textAdserving" errors="textAdservingMode">
                <s:radio cssClass="withInput" name="textAdservingMode"
                         list="textAdservingModes"
                         listKey="name()" listValue="getText('enums.TextAdservingMode.' + name())" />
            </ui:field>

            <s:if test="role.name == 'Advertiser'">
                <ui:field labelKey="account.businessArea" errors="businessArea">
                    <s:textfield name="businessArea" cssClass="middleLengthText" maxLength="50"/>
                </ui:field>
                <ui:field labelKey="account.specificBusinessArea" errors="specificBusinessArea">
                    <s:textfield name="specificBusinessArea" cssClass="middleLengthText" maxLength="50" />
                </ui:field>
            </s:if>

            <c:if test="${isInternal}">
                <s:if test="role.name == 'Advertiser'">
                    <ui:field id="advertiserBudgetLimitField" labelKey="account.budgetLimit" labelForId="advertiserBudgetLimit" required="true" errors="prepaidAmount">
                        <s:textfield name="financialSettings.data.prepaidAmount" cssClass="middleLengthText" id="advertiserBudgetLimit" maxlength="15"/>
                    </ui:field>
                </s:if>
                <s:if test="role.name == 'Agency'">
                    <ui:field id="budgetLimitField" cssClass="hide" labelKey="account.budgetLimit" labelForId="budgetLimit" required="true" errors="prepaidAmount">
                        <s:textfield name="financialSettings.data.prepaidAmount" cssClass="middleLengthText" id="budgetLimit" maxlength="15" disabled="true"/>
                    </ui:field>
                    <ui:field id="commissionField" cssClass="hide" label="${commissionLabel}" labelForId="commission" errors="commissionPercent,commission" required="true">
                        <s:textfield name="financialSettings.commissionPercent" cssClass="smallLengthText1" id="commission" maxLength="6" disabled="true"/>
                    </ui:field>
                </s:if>
            </c:if>

            <ui:field labelKey="account.notes" labelForId="notes" errors="notes">
                <s:textarea name="notes" id="notes" cssClass="middleLengthText" />
            </ui:field>
            
        </ui:fieldGroup>

    </ui:section>

    <c:if test="${isInternal && role == 'ADVERTISER'}">
        <ui:section titleKey="default.categories.content" tipKey="default.categories.content.hint"
                    errors="contentCategories" cssStyle="min-width:100%;" cssClass="widest">
            <table class="grouping">
                <tr>
                    <td>
                        <ui:optiontransfer id="selectCCId" selId="selectCCId2" name="selectedContentCategories"
                                           list="${availableContentCategories}"
                                           selList="${contentCategories}"
                                           cssClass="smallLengthText2" size="9"
                                           saveSorting="true" escape="true"
                                           titleKey="creative.categories.content.available" selTitleKey="creative.categories.content.selected"/>
                    </td>
                </tr>
            </table>
        </ui:section>
    </c:if>

    <div class="wrapper">
        <ui:button message="form.save" type="submit" />
        <s:if test="id == null">
            <ui:button message="form.cancel" type="button" onclick="location='main.action';" />
        </s:if>
        <s:else>
            <ui:button message="form.cancel" type="button"
                onclick="location='advertiserView.action?id=${id}';" />
        </s:else>
    </div>
</s:form>

<script type="text/javascript">
    function showHideSelfServiceCommission() {
        if ($('#selfServiceFlag').prop('checked')) {
            $('#selfServiceCommissionPercent').show();
        } else {
            $('#selfServiceCommissionPercent').hide();
        }
    }

    $('#selfServiceFlag').on('change', function() {
        showHideSelfServiceCommission();
    });

    showHideSelfServiceCommission();
</script>