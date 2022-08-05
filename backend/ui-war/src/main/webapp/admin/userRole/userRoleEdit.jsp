<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>

<script type="text/javascript">

function roleSelected(){

    if($('#accountRoleId').val() == 'INTERNAL'){
      $('#advertiserFlagElem,#publisherFlagElem,#ispFlagElem,#cmpFlagElem,#accessRestrictionsId').show();
        
    <c:if test="${entity.id != null}">
        <c:if test="${entity.advertiserAccountManager && !advertiserAgencyChangeRoleAllowed}">
          $('#advertiserAccountManagerId').prop({disabled : true});
        </c:if>
        <c:if test="${entity.publisherAccountManager && !publisherChangeRoleAllowed}">
          $('#publisherAccountManagerId').prop({disabled : true});
        </c:if>
        <c:if test="${entity.ISPAccountManager && !ispChangeRoleAllowed}">
          $('#ISPAccountManagerId').prop({disabled : true});
        </c:if>
        <c:if test="${entity.CMPAccountManager && !cmpChangeRoleAllowed}">
           $('#CMPAccountManagerId').prop({disabled : true});
        </c:if>       

    </c:if>

    }else{
        $('#advertiserFlagElem,#publisherFlagElem,#ispFlagElem,#cmpFlagElem,#accessRestrictionsId').hide();
    }
}

function checkInternalAccounts(){
    if (!$('#internalAccessTypeMultipleAccountMULTIPLE_ACCOUNTS').is(':checked')){
        $('#accessAccountsId').hide();
        $('#accessRestrictionsId .fieldName label').removeClass('mandatory');
    } else {
        $('#accessAccountsId').show();
        $('#accessRestrictionsId .fieldName label').addClass('mandatory');
    }
}

$().ready(function(){
    roleSelected();
    checkInternalAccounts();

    $('#roleForm').submit(function(){
        if ($('#accountRoleId').val() == 'INTERNAL') {
            var checkedCheckboxes = $('#advertiserAccountManagerId, #publisherAccountManagerId, #ISPAccountManagerId, #CMPAccountManagerId').filter(':checked');

            if (checkedCheckboxes.length > 0) {
                checkedCheckboxes.prop({disabled : false});
            }
        }
        return true;
        });
    $('[name=internalAccessType]').change(checkInternalAccounts);
});

</script>

<s:form action="admin/UserRole/%{#attr.isCreatePage?'create':'update'}" id="roleForm">
    <s:hidden name="id"/>
    <s:hidden name="version"/>
    <div class="wrapper">
        <s:fielderror><s:param value="'version'"/></s:fielderror>
        <s:actionerror/>
    </div>

    <ui:section>
        <ui:fieldGroup>
            
            <ui:field labelKey="UserRole.name" labelForId="nameId" required="true" errors="name,name_role">
                <s:textfield name="name" cssClass="middleLengthText" maxLength="100" id="nameId"/>
            </ui:field>
            
            <ui:field labelKey="UserRole.accountRole" labelForId="accountRoleId" errors="accountRole">
                <c:if test="${empty id}">
                    <s:select name="accountRole" id="accountRoleId" value="accountRole"
                            list="availableAccountRoles" cssClass="middleLengthText"
                            listValue="getText('enum.accountRole.'+name())" onchange="roleSelected();"/>
                </c:if>
                <c:if test="${not empty id}">
                    <s:set var="textVal" value="getText('enum.accountRole.'+accountRole)"/>
                    <ui:text text="${pageScope.textVal}"/>
                    <s:hidden name="accountRole" id="accountRoleId"/>
                </c:if>
            </ui:field>
            
            <ui:field labelKey="UserRole.accessRestrictions" id="accessRestrictionsId" errors="internalAccessType">
                <table class="grouping">
                    <tr>
                        <td>
                            <s:radio list="#{'USER_ACCOUNT':getText('UserRole.InternalAccessType.USER_ACCOUNT')}" name="internalAccessType" id="internalAccessTypeUserAccount"  />
                        </td>
                        <td style="padding-left:10px;">
                            <s:radio list="#{'MULTIPLE_ACCOUNTS':getText('UserRole.InternalAccessType.MULTIPLE_ACCOUNTS')}" name="internalAccessType" id="internalAccessTypeMultipleAccount" />
                        </td>
                        <td style="padding-left:10px;">
                            <s:radio list="#{'ALL_ACCOUNTS':getText('UserRole.InternalAccessType.ALL_ACCOUNTS')}" name="internalAccessType" id="internalAccessTypeAllAccounts" />
                        </td>
                    </tr>
                    <tr>
                        <td class="withField" colspan="3" >    
                            <table class="fieldAndAccessories" id="accessAccountsId">
                                <tr>
                                    <td class="withField">
                                        <ui:optiontransfer 
                                            name="accessAccountIds"
                                            size="9"
                                            cssClass="middleLengthText"
                                            list="${availableAccountIdList}"
                                            selList="${selectedAccountIdList}"
                                            titleKey="form.available"
                                            selTitleKey="form.selected"
                                            saveSorting="true"
                                        />
                                    </td>
                                    <td class="withError">
                                        <s:fielderror><s:param value="'accessAccountIds'"/></s:fielderror>
                                    </td>
                                </tr>
                            </table>
                        </td>
                    </tr>
                </table>
            </ui:field>
            
            <ui:field labelKey="UserRole.ldapDnGroup" labelForId="ldapDnId" errors="ldapDn">
                <s:textfield name="ldapDn" cssClass="middleLengthText" maxLength="2000" id="ldapDnId"/>
            </ui:field>
            
            <ui:field id="advertiserFlagElem" cssClass="hide" labelKey="UserRole.accountManager" errors="advertiserAccountManager">
                <label class="withInput">
                    <s:checkbox name="advertiserAccountManager" id="advertiserAccountManagerId"/>
                    <s:text name="UserRole.advertiser"/>
                </label>
            </ui:field>
            
            <ui:field id="publisherFlagElem" cssClass="hide" errors="publisherAccountManager">
                <label class="withInput">
                    <s:checkbox name="publisherAccountManager" id="publisherAccountManagerId"/>
                    <s:text name="UserRole.publisher"/>
                </label>
            </ui:field>
            
            <ui:field id="ispFlagElem" cssClass="hide" errors="ISPAccountManager">
                <label class="withInput">
                    <s:checkbox name="ISPAccountManager" id="ISPAccountManagerId"/>
                    <s:text name="UserRole.isp"/>
                </label>
            </ui:field>
            
            <ui:field id="cmpFlagElem" cssClass="hide" errors="CMPAccountManager">
                <label class="withInput">
                    <s:checkbox name="CMPAccountManager" id="CMPAccountManagerId"/>
                    <s:text name="UserRole.cmp"/>
                </label>
            </ui:field>
        </ui:fieldGroup>
    </ui:section>

    <s:include value="/templates/formFooter.jsp"/>
</s:form>
