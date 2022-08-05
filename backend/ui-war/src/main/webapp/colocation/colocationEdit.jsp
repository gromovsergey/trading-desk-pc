
<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ui" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<ad:requestContext var="ispContext"/>

<s:form action="admin/colocation/%{#attr.isCreatePage?'create':'update'}" id="colocationEdit">
    <s:hidden name="id"/>
    <s:hidden name="version"/>
    <s:hidden name="account.id"/>

    <ui:pageHeadingByTitle/>

    <div class="wrapper">
        <s:fielderror><s:param value="'version'"/></s:fielderror>
        <s:actionerror/>
    </div>

    <ui:section>
        <ui:fieldGroup>

            <ui:field labelKey="colocation.name" labelForId="name" required="true" errors="name">
                <s:textfield name="name" cssClass="middleLengthText" maxlength="100"/>
            </ui:field>

            <ui:field labelKey="colocation.revenueShare" labelForId="colocationRate.revenueShareInPercent" errors="colocationRate.revenueShareInPercent">
                <s:hidden name="colocationRate.id"/>
                <s:textfield name="colocationRate.revenueShareInPercent" id="colocationRate" cssClass="middleLengthText" maxlength="6"/>%
            </ui:field>

            <ui:field labelKey="colocation.nonOptedInUserServing" cssClass="valignFix">
                <div class="nomargin">
                    <label for="nonOptedInUserServingEnabled" class="withInput">
                        <s:radio list="#{'ALL':getText('colocation.nonOptedInUserServing.all')}" name="optOutServing" id="nonOptedInUserServingEnabled"/>
                    </label>
                    <label for="nonOptedInUserServingEnabledNonOptOut" class="withInput">
                        <s:radio list="#{'NON_OPTOUT':getText('colocation.nonOptedInUserServing.nonOptOut')}" name="optOutServing" id="nonOptedInUserServingEnabledNonOptOut" />
                    </label>
                    <label for="nonOptedInUserServingOptInOnly" class="withInput">
                        <s:radio list="#{'OPTIN_ONLY':getText('colocation.nonOptedInUserServing.optInOnly')}" name="optOutServing" id="nonOptedInUserServingOptInOnly" />
                    </label>
                    <label for="nonOptedInUserServingDisabled" class="withInput">
                        <s:radio list="#{'NONE':getText('colocation.nonOptedInUserServing.none')}" name="optOutServing" id="nonOptedInUserServingDisabled" />
                    </label>
                </div>
            </ui:field>

        </ui:fieldGroup>
    </ui:section>

    <div class="wrapper">
        <ui:button message="form.save" type="submit"/>
        <c:choose>
            <c:when test="${isCreatePage}">
                <ui:button message="form.cancel" href="list.action${ad:accountParam('?ispId', ispContext.accountId)}" type="button"/>
            </c:when>
            <c:otherwise>
                <ui:button message="form.cancel" href="view.action?id=${id}" type="button"/>
            </c:otherwise>
        </c:choose>
    </div>
</s:form>
