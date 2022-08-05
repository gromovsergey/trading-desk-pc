<%@ page contentType="text/html" %>
<%@ page pageEncoding="UTF-8" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ui" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<jsp:useBean id="requestContexts" type="com.foros.util.context.RequestContexts" scope="request"/>
<jsp:useBean id="model" type="com.foros.model.channel.BehavioralChannel" scope="request"/>
<jsp:useBean id="existingAccount" type="com.foros.model.account.Account" scope="request"/>

<script type="text/javascript">
    $().ready(function() {
        $("#account").change(function() {
            UI.Data.get('accountCountry', {accountId: $(this).val()}, function(data) {
                $("#countryCode").val($("countryCode", data).text()).change();
            });
        });
    });
</script>

<s:form action="%{#attr.isCreatePage ? 'BehavioralChannel/create' : 'BehavioralChannel/update'}" id="channelSave">
<s:if test="!#attr.isCreatePage">
    <s:hidden name="id"/>
</s:if>
<s:hidden name="version"/>

<ui:pageHeadingByTitle/>

<ui:errorsBlock>
    <s:fielderror><s:param value="'version'"/></s:fielderror>
    <s:actionerror/>
</ui:errorsBlock>

<ui:section>
    <ui:fieldGroup>
        <c:choose>
            <c:when test="${not requestContexts.set and isCreatePage}">
                <ui:field labelKey="account.internalAccount" labelForId="account" required="true" errors="account.id">
                    <s:select name="account.id" cssClass="middleLengthText" id="account" list="channelOwners" listKey="id" listValue="name"/>
                </ui:field>
            </c:when>
            <c:when test="${not requestContexts.set and existingAccount.role == 'INTERNAL'}">
                <ui:simpleField labelKey="account.internalAccount" value="${existingAccount.name}"/>
                <s:hidden name="account.id"/>
            </c:when>
            <c:otherwise>
                <s:hidden name="account.id"/>
            </c:otherwise>
        </c:choose>

        <ui:field labelKey="channel.name" labelForId="name" required="true" errors="name">
            <s:textfield name="name" id="name" cssClass="middleLengthText" maxlength="100"/>
        </ui:field>

        <ui:field labelKey="channel.description" labelForId="decription" errors="description">
            <s:textarea id="decription" name="description" rows="3" cssClass="middleLengthText1" cssStyle="height: 50px"/>
        </ui:field>

        <c:set var="countryEditable" value="${false}"/>
        <c:if test="${existingAccount.international == true}">
            <ui:field labelKey="channel.country" labelForId="countryCode" id="countryElem" errors="country.countryCode">
                <c:choose>
                    <c:when test="${isCreatePage}">
                        <s:select name="country.countryCode"
                                  id="countryCode"
                                  cssClass="middleLengthText"
                                  list="countries"
                                  listKey="id"
                                  listValue="getText('global.country.' + id + '.name')"
                        />
                        <c:set var="countryEditable" value="${true}"/>
                    </c:when>
                    <c:otherwise>
                        <fmt:message key="global.country.${model.country.countryCode}.name"/>
                    </c:otherwise>
                </c:choose>
            </ui:field>
        </c:if>
        <c:if test="${not countryEditable}">
            <s:hidden name="country.countryCode"/>
        </c:if>

        <%@include file="/channel/channelLanguageField.jsp"%>

        <c:if test="${not isCreatePage}">

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
        </c:if>

    </ui:fieldGroup>
</ui:section>

<ui:triggersEdit/>

<div class="wrapper">
  <ui:button message="form.save" type="submit"/>
  <c:choose>
      <c:when test="${empty model.id and not requestContexts.set}">
          <ui:button message="form.cancel" action="main" type="button"/>
      </c:when>
      <c:when test="${empty model.id}">
          <ui:button message="form.cancel" action="contextMain${ad:accountParam('?accountId', existingAccount.id)}" type="button"/>
      </c:when>
      <c:otherwise>
          <ui:button message="form.cancel" action="view?id=${model.id}" type="button"/>
      </c:otherwise>
  </c:choose>
</div>

</s:form>
