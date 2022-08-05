<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="ad" uri="/ad/serverUI" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<script type="text/javascript">
    function showHideCmpContactShowPhoneRadio(value) {
        $('#cmpContactShowPhoneTr')[value ? 'show' : 'hide']();
    }
</script>

<s:if test="role.name == 'Advertiser'">
    <s:set value="'myAdvertiserAccountUpdate'" var="updateActionName"/>
</s:if>
<s:elseif test="role.name == 'Agency'">
    <s:set value="'myAgencyAccountUpdate'" var="updateActionName"/>
</s:elseif>
<s:else>
    <s:set value="'myAccountUpdate'" var="updateActionName"/>
</s:else>

<s:form action="%{#attr.moduleName}/%{updateActionName}">

<s:hidden name="version"/>

<ui:pageHeadingByTitle/>

<ui:errorsBlock>
    <s:fielderror><s:param value="'version'"/></s:fielderror>
</ui:errorsBlock>

<ui:section titleKey="form.main">
    <ui:fieldGroup>
        <s:if test="country.countryCode == 'RU' && selfServiceFlag">
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

        <s:set var="cnpjKey" value="'account.companyRegistrationNumber'"/>
        <s:if test="country.countryCode == 'BR'">
            <s:set var="cnpjKey" value="'account.cnpj'"/>
        </s:if>
        <ui:field labelKey="${cnpjKey}" labelForId="companyRegistrationNumber" errors="companyRegistrationNumber">
            <s:textfield name="companyRegistrationNumber" id="companyRegistrationNumber" cssClass="middleLengthText" maxLength="50"/>
        </ui:field>
        <s:if test="role.name == 'CMP'">
            <s:if test="accountUsers">
                <ui:field id="cmpContact" labelKey="account.cmpContactDetails"
                    labelForId="cmpContactId">
                    <s:select name="cmpContact.id" id="cmpContactId" styleId="cmpContactId"
                        styleClass="middleLengthText" list="accountUsers" listKey="id" listValue="fullName"
                            headerKey="" headerValue="%{getText('cmpAccount.dontShow')}" onchange="showHideCmpContactShowPhoneRadio(this.value);">
                    </s:select>
                </ui:field>
    
                <s:if test="not cmpContact">
                    <s:set var="showPhoneCssClass" value="'hide'"/>
                </s:if>
                <ui:field cssClass="${showPhoneCssClass}" id="cmpContactShowPhoneTr">
                    <s:radio cssClass="withInput" name="cmpContactShowPhoneFlag" value="cmpContactShowPhone" list="cmpShowPhoneValues" listValue="getText(key)" listKey="value" />
                </ui:field>
    
            </s:if>
        </s:if>

        <s:if test="textAdservingModes != null">
            <ui:field id="textAdservingTr" labelKey="account.textAdserving" errors="textAdservingMode">
                <s:radio cssClass="withInput" name="textAdservingMode"
                         list="textAdservingModes"
                         listKey="name()" listValue="getText('enums.TextAdservingMode.' + name())" />
            </ui:field>
        </s:if>

        <s:if test="isAdvertiser()">
            <ui:field labelKey="account.businessArea" errors="businessArea">
                <s:textfield name="businessArea" cssClass="middleLengthText" maxLength="50"/>
            </ui:field>
            <ui:field labelKey="account.specificBusinessArea" errors="specificBusinessArea">
                <s:textfield name="specificBusinessArea" cssClass="middleLengthText" maxLength="50"/>
            </ui:field>
        </s:if>
        
    </ui:fieldGroup>
</ui:section>

<div class="wrapper">

    <ui:button message="form.save" type="submit" />
    <ui:button message="form.cancel" onclick="location='myAccountView.action';" type="button" />

</div>

</s:form>
