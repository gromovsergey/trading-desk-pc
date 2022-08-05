<%@ page import="com.foros.security.AuthenticationType" %>
<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="ad" uri="/ad/serverUI" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>

<script type="text/javascript">
$().ready(function() {
    var roleId = $('#roleId').val();
    showhideMaxCreditLimit(roleId);
    $('#plainAuthType').click(loginControl);
});
function rolesControl(){
    var roleId = $('#roleId').val();
    if(roleId && roleId >= 0) {
        UI.Data.Options.fill('availableDns', 'dn', handleUserRoleDN, {roleId:$('#roleId').val()}, ['form.select.pleaseSelect']);
    }
    showhideMaxCreditLimit(roleId);
}

function showhideMaxCreditLimit(roleId) {
    if (!roleId || roleId == '') {
        return;
    }

    UI.Data.get('FinanceUserCheck',
        {userRoleId: roleId},
        function(data) {
            var result = $('result', data).text();
            if (result == 'true') {
                $('#maxCreditLimitTR').show();
            }
    });
}

function handleUserRoleDN(response, selectId, additionalOptionMessages){
    UI.Data.Options._update(response, selectId, additionalOptionMessages);
    var  ldapAvailable = $('#dn option').length - 1 > 0 ;
    if(!ldapAvailable) {
        $('#ldapAuthType').prop({disabled : true});
        $('#plainAuthType').prop({checked : true})
            .click();
    } else {
        $('#ldapAuthType').prop({disabled : false});
    }
}

function ajaxGetUserData(dn) {
    dn && UI.Data.get('userData', {dn:dn}, updateUserData);
}

function updateUserData(respXML) {
    $.each(['email', 'firstName', 'lastName', 'jobTitle', 'phone'], function(){
        $('#' + this).val($(this + '', respXML).text()); // "this" converted to a String, because of a bug in jQuery (otherwise it does not work)
    });
}

function loginControl(){
    var authType = $('input[type=radio][name=authType]:checked').val();
    if (authType == 'LDAP'){
        $('#dnTableId').show();
    } else {
        $('#dnTableId').hide();
    }
}

</script>

<ui:pageHeadingByTitle/>

<ui:errorsBlock>
    <s:fielderror><s:param value="'version'"/></s:fielderror>
</ui:errorsBlock>

<s:form action="%{#request.moduleName}/%{#attr.isCreatePage?'create':(fieldsChangeAllowed?'update':'updateMaxCreditLimit')}">
    <s:hidden name="id"/>
    <s:hidden name="version"/>
    <s:hidden name="account.id"/>
    <s:hidden name="account.name"/>
    <s:hidden name="account.role"/>

<ui:section titleKey="form.main">
    <ui:fieldGroup>

        <ui:field labelKey="InternalUser.userRole" labelForId="roleId" required="true" errors="roleId">
            <s:if test="fieldsChangeAllowed && roleChangeAllowed">
                <s:select name="role.id" cssClass="middleLengthText" id="roleId"  onchange="rolesControl();"
                    list="roles"
                    listKey="id" listValue="name"/>
            </s:if>
            <s:else>
                <c:out value="${role.name}"/>
                <s:hidden name="role.id"/>
                <s:hidden name="role.name"/>
                <s:hidden id="roleId" value="%{role.id}"/>
            </s:else>
        </ui:field>

        <c:set var="maxCreditLimitLabel">
            <fmt:message key="InternalUser.maxCreditLimit"/> (${ad:currencySymbol('USD')})
        </c:set>
        <ui:field id="maxCreditLimitTR" cssClass="hide" label="${maxCreditLimitLabel}" labelForId="maxCreditLimit" required="true" tipKey="InternalUser.tipkey.maxCreditLimit" errors="maxCreditLimit">
            <s:textfield name="maxCreditLimit" cssClass="smallLengthText1" maxLength="16" id="maxCreditLimit"/>
        </ui:field>
        <s:if test="(#attr.isCreatePage or existingUser.authType.name=='LDAP' ) and !internalPasswordAuthorizationAllowed">
            <ui:field labelKey="InternalUser.ldapUser" required="true" errors="dn">
                <s:select name="dn" cssClass="middleLengthText" id="dn" onchange="ajaxGetUserData(this.value);"
                                    headerValue="%{getText('form.select.pleaseSelect')}" headerKey=""
                                    list="availableDNs"
                                    listKey="value" listValue="name"
                                    disabled="!fieldsChangeAllowed"/>
            </ui:field>
            <input type="hidden" name="authType" value="LDAP"/>
        </s:if>
        <s:else>
            <s:if test="existingUser.authType.name!='LDAP' and !internalPasswordAuthorizationAllowed">
                <ui:field id="onlyLDAPAllowedHint">
                    <span class="infos"><s:text name="errors.user.onlyLDAPAuthAllowed"/></span>
                </ui:field>
            </s:if>
            <ui:field labelKey="InternalUser.authorization" required="true" cssClass="valignFix">
                <label class="withInput">
                    <s:radio cssClass="withInput" name="authType" list="'LDAP'" listValue="getText('InternalUser.ldap')" id="ldapAuthType" onclick="loginControl(); rolesControl();" disabled="!fieldsChangeAllowed"/>
                </label>
            </ui:field>

            <ui:field id="dnTableId" required="true" errors="dn">
                <s:select name="dn" cssClass="middleLengthText" id="dn" onchange="ajaxGetUserData(this.value);"
                    headerValue="%{getText('form.select.pleaseSelect')}" headerKey=""
                    list="availableDNs"
                    listKey="value" listValue="name"
                    disabled="!fieldsChangeAllowed"/>
            </ui:field>

            <ui:field errors="authType">
                <label class="withInput">
                    <s:radio cssClass="withInput" name="authType" list="'PSWD'" listValue="getText('InternalUser.password')" id="plainAuthType" onclick="loginControl();" disabled="!fieldsChangeAllowed"/>
                </label>
            </ui:field>
        </s:else>
        <ui:field labelKey="InternalUser.email" labelForId="email" required="true" errors="email">
            <input type="${ad:isMobileAgent(pageContext.request) ? 'email' : 'text'}" name="email" maxlength="320" value="${email}" id="email" class="middleLengthText" ${not fieldsChangeAllowed ? 'disabled' : ''}>
        </ui:field>

        <ui:field labelKey="InternalUser.firstName" labelForId="firstName" required="true" errors="firstName">
            <s:textfield name="firstName" cssClass="middleLengthText" maxLength="50" id="firstName" disabled="!fieldsChangeAllowed"/>
        </ui:field>

        <ui:field labelKey="InternalUser.lastName" labelForId="lastName" required="true" errors="lastName">
            <s:textfield name="lastName" cssClass="middleLengthText" maxLength="50" id="lastName" disabled="!fieldsChangeAllowed"/>
        </ui:field>

        <ui:field labelKey="InternalUser.language" labelForId="language" errors="languageIsoCode">
            <s:select name="language" cssClass="middleLengthText" id="language"
                list="@com.foros.model.security.Language@values()"
                listKey="name()" listValue="getText('enums.Language.' + name())"
                disabled="!fieldsChangeAllowed"/>
        </ui:field>

        <ui:field labelKey="InternalUser.jobTitle" labelForId="jobTitle" errors="jobTitle">
            <s:textfield name="jobTitle" cssClass="middleLengthText" maxLength="30" id="jobTitle" disabled="!fieldsChangeAllowed"/>
        </ui:field>

        <ui:field labelKey="InternalUser.phoneNumber" labelForId="phone" required="true" errors="phone">
            <s:textfield name="phone" cssClass="middleLengthText" maxLength="80" id="phone" disabled="!fieldsChangeAllowed"/>
        </ui:field>

    </ui:fieldGroup>
</ui:section>

    <script>
      loginControl();
    </script>

<div class="wrapper">
    <ui:button message="form.save" type="submit" novalidate="true" />
    <s:if test="id != null">
        <ui:button message="form.cancel" onclick="location='view.action?id=${id}';" type="button" />
    </s:if>
    <s:else>
        <ui:button message="form.cancel" onclick="location='/admin/internal/account/view.action?id=${account.id}';" type="button" />
    </s:else>
</div>

</s:form>
