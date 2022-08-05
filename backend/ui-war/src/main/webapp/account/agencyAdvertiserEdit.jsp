<%@page contentType="text/html" %>
<%@page pageEncoding="UTF-8" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="ad" uri="/ad/serverUI" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:if test="!isInternal()">
    <s:set var="isMyAccount" value="true"/>
</s:if>
<c:set var="commissionLabel">
    <fmt:message key="account.agencyCommission"/> (%)
</c:set>

<s:form action="%{#request.moduleName}/%{#attr.isCreatePage?'agencyAdvertiserCreate':'agencyAdvertiserUpdate'}">
<s:hidden name="id"/>
<s:hidden name="agency.id"/>
<s:hidden name="version"/>
<s:hidden name="financialSettings.version"/>

<ui:pageHeadingByTitle/>

<ui:errorsBlock>
    <s:fielderror><s:param value="'version'"/></s:fielderror>
</ui:errorsBlock>

<ui:section titleKey="form.main">

  <ui:fieldGroup>
    <c:choose>
        <c:when test="${empty id or ad:isPermitted('Account.updateBillingContactDetails', existingAccount)}">
            <ui:field labelKey="account.name" labelForId="name" required="true" errors="name">
                <s:textfield name="name" id="name" cssClass="middleLengthText" maxLength="100"/>
            </ui:field>
            <ui:field labelKey="account.legalName" labelForId="legalName" required="true" errors="legalName">
                <s:textfield name="legalName" id="legalName" cssClass="middleLengthText" maxLength="200"/>
            </ui:field>
        </c:when>
        <c:otherwise>
            <ui:simpleField labelKey="account.name" value="${name}"/>
            <ui:simpleField labelKey="account.legalName" value="${legalName}"/>
        </c:otherwise>
    </c:choose>

    <c:set var="cnpjKey" value="account.companyRegistrationNumber"/>
    <s:if test="country.countryCode == 'BR'">
        <s:set var="cnpjKey" value="'account.cnpj'"/>
    </s:if>
    <ui:field labelKey="${cnpjKey}" labelForId="companyRegistrationNumber" errors="companyRegistrationNumber">
      <s:textfield name="companyRegistrationNumber" id="companyRegistrationNumber" cssClass="middleLengthText" maxLength="50"/>
    </ui:field>

    <s:if test="country.countryCode == 'RU' && (isInternal() || agency.isSelfService())">
        <ui:field id="tnsAdvertiser" labelKey="account.tnsAdvertiser">
            <ui:autocomplete id="tnsAdvertiserId" 
                source="Autocomplete.tnsAdvertiserId.getAdvertisers"
                defaultValue="${tnsAdvertiser.id}"
                defaultLabel="${tnsAdvertiser.name}"
                selectedNameKey="account.tnsAdvertiser"
                cssClass="middleLengthText" isMultiSelect="false"
                minLength="1" editable="true" maxLength="100"
                addInLowercase="false">
                <script type="text/javascript">
                    Autocomplete.tnsAdvertiserId.getAdvertisers = function(request, response){
                        UI.Data.get('getTnsAdvertiser', {query:request.term}, function(data) {
                            var opts = $.map($('tnsAdvertiser', data), function(el){
                                var advertiserId = $('id', el).text();
                                var advertiserName = $('name', el).text();
                                return new $.custom.extAutocomplete.Option(advertiserName, advertiserId);
                            });
                            response(opts);
                        });
                    };
                    
                </script>
            </ui:autocomplete>
        </ui:field>
    
        <ui:field id="tnsBrand" labelKey="account.tnsBrand">
            <ui:autocomplete id="tnsBrandId" 
                source="Autocomplete.tnsBrandId.getBrands"
                defaultValue="${tnsBrand.id}"
                defaultLabel="${tnsBrand.name}"
                cssClass="middleLengthText" isMultiSelect="false"
                minLength="1" editable="true" maxLength="100"
                addInLowercase="false">
                <script type="text/javascript">
                    Autocomplete.tnsBrandId.getBrands = function(request, response){
                        UI.Data.get('getTnsBrand', {query:request.term}, function(data) {
                            var opts = $.map($('tnsBrand', data), function(el){
                                var advertiserId = $('id', el).text();
                                var advertiserName = $('name', el).text();
                                return new $.custom.extAutocomplete.Option(advertiserName, advertiserId);
                            });
                            response(opts);
                        });
                    };
                    
                </script>
            </ui:autocomplete>
        </ui:field>

    </s:if>

    <ui:field labelKey="account.contactName" errors="contactName">
        <s:textfield name="contactName" cssClass="middleLengthText" maxLength="50"/>
    </ui:field>
    <ui:field labelKey="account.businessArea" errors="businessArea">
        <s:textfield name="businessArea" cssClass="middleLengthText" maxLength="50"/>
    </ui:field>
    <ui:field labelKey="account.specificBusinessArea" errors="specificBusinessArea">
        <s:textfield name="specificBusinessArea" cssClass="middleLengthText" maxLength="50"/>
    </ui:field>

    <s:if test="isInternal()">
        <s:if test="budgetLimitPresent">
            <ui:field labelKey="account.budgetLimit" labelForId="budgetLimit" required="true" errors="prepaidAmount">
                <s:textfield name="financialSettings.data.prepaidAmount" cssClass="middleLengthText" id="budgetLimit" maxlength="15"/>
            </ui:field>
        </s:if>

        <s:if test="commissionPresent">
            <ui:field label="${commissionLabel}" labelForId="commission" errors="commissionPercent,commission" required="true">
                <s:textfield name="financialSettings.commissionPercent" cssClass="smallLengthText1" id="commission" maxLength="6"/>
            </ui:field>
        </s:if>
    </s:if>

    <ui:field labelKey="account.notes" labelForId="notes" errors="notes">
        <s:textarea name="notes" id="notes" cssClass="middleLengthText"/>
    </ui:field>
  </ui:fieldGroup>
  
</ui:section>

<c:if test="${ad:isInternal() && role == 'ADVERTISER'}">
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
    <c:choose>
        <c:when test="${id != null}">
            <ui:button message="form.cancel" onclick="location='agencyAdvertiserView.action?id=${id}';" type="button" />
        </c:when>
        <c:when test="${isMyAccount}">
            <ui:button message="form.cancel" onclick="location='/advertiser/campaign/advertisers.action';" type="button" />
        </c:when>
        <c:otherwise>
            <ui:button message="form.cancel" onclick="location='/admin/campaign/advertisers.action?advertiserId=${agency.id}';" type="button" />
        </c:otherwise>
    </c:choose>
</div>

</s:form>
