<%@ page contentType="text/html" %>
<%@ page pageEncoding="UTF-8" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ui" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:form action="%{#attr.entityName}/saveEditCmp" id="channelSave">
<s:hidden name="id"/>
<s:hidden name="account.id"/>
<s:hidden name="version"/>
<s:hidden name="channelRate.rateType"/>
<s:hidden name="country.countryCode"/>

<ui:pageHeadingByTitle/>

<jsp:useBean id="existingAccount" type="com.foros.model.account.Account" scope="request"/>

<ui:errorsBlock>
    <s:fielderror><s:param value="'version'"/></s:fielderror>
</ui:errorsBlock>

<ui:section>
    <ui:fieldGroup>
        <ui:field labelKey="channel.name" labelForId="name" required="true" errors="name,errors.duplicate">
            <s:textfield name="name" id="name" cssClass="middleLengthText" maxlength="100"/>
        </ui:field>

        <ui:field labelKey="channel.description" labelForId="decription" errors="description">
            <s:textarea styleId="decription" name="description" rows="3" cssClass="middleLengthText1" cssStyle="height: 50px"/>
        </ui:field>

        <c:set var="channelRateLabel">
            <fmt:message key="channel.rate" /> (${ad:currencySymbol(existingAccount.currency.currencyCode)})
        </c:set>

        <ui:field label="${channelRateLabel}" labelForId="channelRate" required="true" errors="channelRateValue">
            <table class="fieldAndAccessories">
                <tr>
                    <td class="withField">
                        <s:textfield name="channelRateValue" styleId="channelRate" styleClass="smallLengthText1" maxlength="20"/>
                    </td>
                    <td class="withField">
                        <span id="rateTypeLabel"><fmt:message key="enum.RateType.${channelRate.rateType}"/></span>
                    </td>
                </tr>
            </table>
        </ui:field>

        <ui:field labelKey="channel.supersededByChannel"
                  labelForId="supersededByChannel"
                  errors="supersededByChannel"
                  tipKey="channel.supersededByChannel.tooltip.edit">

            <ui:autocomplete
                    id="supersededByChannel"
                    source="/xml/supersededChannelsByAccount.action"
                    requestDataCb="Autocomplete.getChannelData"
                    cssClass="middleLengthText"
                    defaultLabel="${(not empty supersededByChannel) ? ad:appendStatus(supersededByChannel.name, supersededByChannel.status) : ''}"
                    defaultValue="${(not empty supersededByChannel) ? supersededByChannel.id : ''}"
                    >
                <script type="text/javascript">
                    Autocomplete.getChannelData = function (query) {
                        return $.extend(
                                {accountId: ${account.id}},
                                {countryCode: '${country.countryCode}'},
                                {selectedId: ${(not empty supersededByChannel) ? supersededByChannel.id : 'null'}},
                                {selfId: ${id}},
                                {query: query});
                    }
                </script>
            </ui:autocomplete>
        </ui:field>

    </ui:fieldGroup>
</ui:section>

<div class="wrapper">
  <ui:button message="form.save" type="submit"/>
  <ui:button message="form.cancel" action="view?id=${id}" type="button" />
</div>

</s:form>