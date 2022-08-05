<%@ page contentType="text/html" %>
<%@ page pageEncoding="UTF-8" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ui" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:form action="%{#attr.entityName}/saveSubmitCmp" id="channelSave">

<s:hidden name="id"/>
<s:hidden name="account.id"/>
<s:hidden name="version"/>
<s:hidden name="name"/>

<jsp:useBean id="existingAccount" type="com.foros.model.account.Account" scope="request"/>

<ui:pageHeadingByTitle/>

<ui:errorsBlock>
    <s:fielderror><s:param value="'version'"/></s:fielderror>
</ui:errorsBlock>

<ui:section>
    <ui:fieldGroup>
        <c:set var="channelRateLabel">
            <fmt:message key="channel.rate" /> (${ad:currencySymbol(existingAccount.currency.currencyCode)})
        </c:set>

        <ui:field label="${channelRateLabel}" labelForId="channelRate" required="true" errors="channelRateValue">
            <table class="fieldAndAccessories">
                <tr>
                    <td class="withField">
                        <s:textfield name="channelRateValue" id="channelRate" cssClass="smallLengthText1" maxLength="20"/>
                    </td>
                    <td class="withField">
                        <s:select name="channelRate.rateType" cssClass="smallLengthText1" id="rateType" list="rateTypes" listValue="getText('enum.RateType.' + name)"/>
                    </td>
                </tr>
            </table>
        </ui:field>
    </ui:fieldGroup>
</ui:section>

<div class="wrapper">
    <ui:button message="form.save" type="submit"/>
    <ui:button message="form.cancel" action="view?id=${id}" type="button"/>
</div>

</s:form>
