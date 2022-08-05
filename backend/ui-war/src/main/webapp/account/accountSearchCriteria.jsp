<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>

<script type="text/javascript">

$().ready(function(){
    $('#search_name').focus();
});

function changeAccountManager() {
    UI.Data.Options.get('accMngrsList', 'accountManagerId',
        {intAccountId:$('#internalAccountId').val(), roleId:$('#accountRoleName').val(), includeDeleted: true, noneValue: 0},
        ['form.all']);
}

function myIntAccount(){
    $('#internalAccountId option[value=${_principal.accountId}]').prop({selected : true});
    changeAccountManager();
}
function myCountry(){
    $('#countryCode option[value=${myCountry}]').prop({selected : true});
}
</script>

<s:form action="%{#request.moduleName}/search" method="POST">
    <s:hidden name="accountRoleName" id="accountRoleName" />

    <ui:section titleKey="form.search">
            
      <ui:fieldGroup>
      
        <ui:field labelKey="${entityName}.accountName" labelForId="search_name" errors="name">
          <s:textfield name="name" id="search_name" cssClass="middleLengthText" maxLength="100" />
        </ui:field>
        
        <ui:field labelKey="AccountType.entityName" labelForId="search_accountTypeId">
          <s:select name="accountTypeId"  id="search_accountTypeId" list="accountTypes"
            cssClass="middleLengthText" listKey="id" listValue="name" headerKey="" headerValue="%{getText('form.all')}" />
        </ui:field>

          <c:if test="${showAccountManager}">
              <ui:field labelKey="account.internalAccount" labelForId="internalAccountId">
                  <table class="fieldAndAccessories">
                    <tr>
                        <td class="withField">
                            <s:select name="internalAccountId" id="internalAccountId" list="internalAccounts"
                                cssClass="middleLengthText" listKey="id" listValue="name" headerKey=""
                                headerValue="%{getText('form.all')}" onchange="changeAccountManager();"/>
                        </td>
                        <c:if test="${showMyAccount}">
                            <td class="withButton" style="font-weight:bold">
                                <a href="#" onclick="myIntAccount();" ><s:text name="account.myAccountName"/></a>
                            </td>
                        </c:if>
                    </tr>
                  </table>
              </ui:field>
          </c:if>
  
        <ui:field labelKey="account.country" labelForId="country" errors="countryCode">
            <table class="fieldAndAccessories">
                <tr>
                    <td class="withField">
                        <s:select name="countryCode" id="countryCode" list="countries" cssClass="middleLengthText"
                            listKey="id" listValue="getText('global.country.' + id + '.name')" headerKey=""
                            headerValue="%{getText('form.all')}" />
                    </td>
                    <td class="withButton" style="font-weight:bold">
                        <a href="#" onclick="myCountry();"><s:text name="account.myCountry"/></a>
                    </td>
                </tr>
            </table>
        </ui:field>
  
        <c:if test="${showAccountManager}">
          <ui:field labelKey="account.accountManager" labelForId="accountManagerId">
            <s:select name="accountManagerId" id="accountManagerId" list="accountManagers" cssClass="middleLengthText"
              listKey="id" listValue="name" headerKey="" headerValue="%{getText('form.all')}" />
          </ui:field>
        </c:if>
        
        <ui:field labelKey="account.status" labelForId="status" errors="status">
          <s:select name="status"  id="status" list="statuses" cssClass="middleLengthText"
            listKey="name"  listValue="getText('' + description + '')" />
        </ui:field>

        <c:if test="${accountRole != 'CMP'}">
        <ui:field labelKey="account.testOption" labelForId="testOption" errors="testOption">
          <s:select name="testOption"  id="testOption" list="testOptions" cssClass="middleLengthText"
            listKey="name"  listValue="getText('' + description + '')" />
        </ui:field>
        </c:if>  
        
        <%-- Button cell style requires correction --%>
        <ui:field cssClass="withButton">
          <ui:button message="form.search" type="submit" />
        </ui:field>
        
      </ui:fieldGroup>
                      
    </ui:section>
    
</s:form>
