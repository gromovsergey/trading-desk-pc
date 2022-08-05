<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="ad" uri="/ad/serverUI"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<script type="text/javascript">
    $(function(){
        $("#countryCode").on('change', function(e){
                var self = $(this);
                UI.Data.get('countryInfo', {"countryCode": self.val() }, function(data){
                    $("#currencyId").val($("currency", data).text());
                    $("#timeZone").val($("timezone", data).text());
                });
        });

        <s:if test="role.name == 'Agency' || role.name == 'Advertiser'">
            if (isTextAdservingAllowed(${accountType.id})) {
                showHideTextAdservingInternal(true, false);
            } else {
                 showHideTextAdservingInternal(false, false);
            }
        </s:if>

        initDateFields();

    });

    function setCNPJName(countryCode) {
        $('#cnpjTR label:eq(0)').text( (countryCode == 'BR') ? '<fmt:message key="account.cnpj"/>:' : '<fmt:message key="account.companyRegistrationNumber"/>:');
    }

    function isTextAdservingAllowed(id) {
        var showTextAdserving = false;

        <c:forEach items="${accountTypes}" var="accType">
            <c:if test="${accType.allowTextAdvertisingFlag}">
                showTextAdserving = showTextAdserving || (${accType.id} == id);
            </c:if>
        </c:forEach>

        return showTextAdserving;
    }
    
    
    function handleAccountType(id) {
        showHideTextAdserving(id);
        showHideSiteTargetingAccess(id);
        showHideFinancialFields(id);
    }
    
    
    
    function showHideSiteTargetingAccess(id) {
        if (isSiteTargetingFlag(id)) {
            $('#siteTargetingFlag').prop({"disabled": false})
            $('#siteTargetingFlagDiv').show();
        } else {
            $('#siteTargetingFlagDiv').hide();
            $('#siteTargetingFlag').prop({"disabled": true})
        }
        
    }
    
    function isSiteTargetingFlag(id) {
        var showSiteTargetingAccess = false;

        <c:forEach items="${accountTypes}" var="accType">
            <c:if test="${accType.siteTargetingFlag}">
            	showSiteTargetingAccess = showSiteTargetingAccess || (${accType.id} == id);
            </c:if>
        </c:forEach>

        return showSiteTargetingAccess;
    }

    function showHideFinancialFields(id) {
        <s:if test="role.name != 'Agency' && role.name != 'Advertiser'">

        return;

        </s:if>
        <s:else>

        var isAgency = false;
        <s:if test="role.name == 'Agency'">
        isAgency = true;
        </s:if>

        var isFinancialFieldsOnAgencyLevel = isFinancialFieldsOnAgencyLevelFlag(id);

        if (isFinancialFieldsOnAgencyLevel && isAgency || !isFinancialFieldsOnAgencyLevel && !isAgency) {
            $('#commission').prop({"disabled": false});
            $('#commissionField').removeClass('hide');
            $('#budgetLimit').prop({"disabled": false});
            $('#budgetLimitField').removeClass('hide');
        } else {
            $('#commissionField').addClass('hide');
            $('#commission').prop({"disabled": true});
            $('#budgetLimitField').addClass('hide');
            $('#budgetLimit').prop({"disabled": true});
        }

        </s:else>
    }

    function isFinancialFieldsOnAgencyLevelFlag(id) {
        var result = false;

        <c:forEach items="${accountTypes}" var="accType">
            <c:if test="${accType.agencyFinancialFieldsFlag}">
                result = result || (${accType.id} == id);
            </c:if>
        </c:forEach>

        return result;
    }

    

    function showHideTextAdserving(id) {
        showHideTextAdservingInternal(isTextAdservingAllowed(id), true);
    }

    function showHideTextAdservingInternal(showTextAdserving, resetRadioValues) {
        if (showTextAdserving){
            $('#textAdservingTr').show();
            var radios = $(':radio[name=textAdservingMode]');
            if (!radios.is(':checked') || resetRadioValues) {
                <c:set var="defaultValue" value="${role == 'AGENCY' ? 'ONE_TEXT_AD_PER_ADVERTISER' : 'ONE_TEXT_AD'}"/>
                radios.filter("[value='${defaultValue}']").prop({"checked": true});
            }
        } else {
            $('#textAdservingTr').hide();
        }
    }

    function getAccMngrList(){
        var options = null;
        if($("#intAccountId").val() != "") {
            UI.Data.Options.get('accMngrsList', 'accountManagerId', {
                "intAccountId": $('#intAccountId').val(), 
                "roleId": '${role.name}', 
                "includeDeleted": false, 
                "noneValue": null
            }, options);
        }
    }
    
    function handleTnsObjects(){
        if ($('#countryCode').val() == 'RU'){
            $('#tnsAdvertiser').show();
            $('#tnsBrand').show();
        } else {
            $('#tnsAdvertiser').hide();
            $('#tnsAdvertiserId').extAutocomplete('clear');
            $('#tnsBrand').hide();
            $('#tnsBrandId').extAutocomplete('clear');
        }
    }

    function initDateFields() {
        $("#contractDateDisplay").datepicker()
    }

</script>

<c:choose>
    <c:when
        test="${empty id or ad:isPermitted('Account.updateBillingContactDetails', existingAccount)}">
        <ui:field labelKey="account.name" labelForId="name"
            required="true" errors="name">
            <s:textfield name="name" id="name"
                cssClass="middleLengthText" maxLength="100" />
        </ui:field>
        <ui:field labelKey="account.legalName" labelForId="legalName"
            required="true" errors="legalName">
            <s:textfield name="legalName" id="legalName"
                cssClass="middleLengthText" maxLength="200" />
        </ui:field>
    </c:when>
    <c:otherwise>
        <ui:simpleField labelKey="account.name" value="${name}" />
        <ui:simpleField labelKey="account.legalName"
            value="${legalName}" />
    </c:otherwise>
</c:choose>

<s:if test="role.name == 'Agency' || role.name == 'Advertiser'">
    <ui:field labelKey="account.contractNumber" labelForId="contractNumber" errors="contractNumber">
        <s:textfield name="contractNumber" id="contractNumber" cssClass="middleLengthText" maxLength="200" />
    </ui:field>

    <ui:field labelKey="account.contractDate" labelForId="contractDate" errors="contractDate">
        <s:hidden name="contractDate" id="contractDateHidden" />
        <s:textfield size="11" id="contractDateDisplay" name="selectedContractDate.datePart" />
    </ui:field>
</s:if>

<s:set var="cnpjKey" value="'account.companyRegistrationNumber'" />
<s:if test="existingAccount.country.countryCode == 'BR'">
    <s:set var="cnpjKey" value="'account.cnpj'" />
</s:if>
<ui:field id="cnpjTR" labelKey="${cnpjKey}"
    labelForId="companyRegistrationNumber"
    errors="companyRegistrationNumber">
    <s:textfield name="companyRegistrationNumber"
        id="companyRegistrationNumber" cssClass="middleLengthText"
        maxLength="50" />
</ui:field>

<s:if test="id == null">
    <ui:field labelKey="account.country" labelForId="countryCode"
        required="true" errors="country">
        <s:select name="country.countryCode" id="countryCode"
            cssClass="middleLengthText"
            headerValue="%{getText('form.select.pleaseSelect')}"
            headerKey="" list="countries" value="country.countryCode"
            listKey="id"
            listValue="getText('global.country.' + id + '.name')"
            onchange="handleTnsObjects(); setCNPJName(this.value);">
        </s:select>
    </ui:field>
</s:if>
<s:else>
    <ui:simpleField labelKey="account.country"
        value="${ad:resolveGlobal('country', existingAccount.country.countryCode, false)}" />
        <s:hidden name="country.countryCode" id="countryCode"/>
</s:else>


<s:if test="id == null">
    <ui:field labelKey="account.currency" labelForId="currencyId"
        errors="currency">
        <s:select name="currency.id" id="currencyId"
            cssClass="middleLengthText" list="currencies"
            value="currency.id" listKey="id"
            listValue="getText('global.currency.' + name + '.name')">
        </s:select>
    </ui:field>
</s:if>
<s:else>
    <ui:simpleField labelKey="account.currency"
            value="${ad:resolveGlobal('currency', existingAccount.currency.currencyCode, false)}"/>
</s:else>


<s:if test="(isInternal() || isSelfService()) && role.name == 'Advertiser'">
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

<ui:field labelKey="account.role">
    <c:set var="textVal">
        <fmt:message key="enum.accountRole.${role}" />
    </c:set>
    <ui:text text="${pageScope.textVal}" />
</ui:field>

<s:if test="isInternal()">
    <ui:field labelKey="account.accountTypeId"
        labelForId="accountTypeId" required="true" errors="accountType">
        <s:select name="accountType.id" id="accountTypeId"
            cssClass="middleLengthText"
            headerValue="%{getText('form.select.pleaseSelect')}"
            headerKey="" list="accountTypes" listKey="id"
            listValue="name" value="accountType.id"
            onchange="handleAccountType(this.value);">
        </s:select>
    </ui:field>
</s:if>

<s:if test="id == null">
    <ui:field labelKey="account.timeZone" labelForId="timeZone"
        required="true" errors="timezone">
        <s:select name="timezone.id" id="timeZone"
            cssClass="middleLengthText"
            headerValue="%{getText('form.select.pleaseSelect')}"
            headerKey="" list="timeZones" value="timezone.id"
            listKey="id"
            listValue="getText('global.timezone.' + name + '.name')">
        </s:select>
    </ui:field>
</s:if>
<s:else>
    <ui:simpleField labelKey="account.timeZone" value="${ad:resolveGlobal('timezone', existingAccount.timezone.key, true)}"/>
</s:else>

<script type="text/javascript">
    $(function(){
        handleTnsObjects();
        if (${id != null}) {
            showHideSiteTargetingAccess(${id != null ? accountType.id : ''});
        }
        if (${accountType.id != null}) {
            showHideFinancialFields(${accountType.id});
        }
    });
</script>      
