<%@ page contentType="text/html"%>
<%@ page pageEncoding="UTF-8"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="ad" uri="/ad/serverUI"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:set value="#attr.isCreatePage?'create':'update'" var="saveActionName"/>

<s:form action="%{#request.moduleName}/%{#saveActionName}" id="actionSave">
    <s:hidden name="id" />
    <s:hidden name="version" />

    <ui:pageHeadingByTitle />
    
    <ui:errorsBlock>
        <s:fielderror><s:param value="'version'"/></s:fielderror>
    </ui:errorsBlock>

    <ui:section titleKey="form.main">
        <ui:fieldGroup>

            <%@ include file="accountDetailsEdit.jsp"%>

            <s:if test="isInternal()">
                <s:set var="testFlagDisabled" value="true" />
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
                <ui:field id="householdTr" labelKey="account.household" labelForId="household" errors="household">
                        <s:checkbox name="household" id="household" />
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

            <ui:field labelKey="account.notes" labelForId="notes" errors="notes">
                <s:textarea name="notes" id="notes" cssClass="middleLengthText" />
            </ui:field>

        </ui:fieldGroup>
    </ui:section>

    <div class="wrapper">
        <ui:button message="form.save" type="submit" />
        <s:if test="id == null">
            <ui:button message="form.cancel" type="button" onclick="location='main.action';" />
        </s:if>
        <s:else>
            <ui:button message="form.cancel" type="button" onclick="location='view.action?id=${id}';" />
        </s:else>
    </div>
</s:form>
