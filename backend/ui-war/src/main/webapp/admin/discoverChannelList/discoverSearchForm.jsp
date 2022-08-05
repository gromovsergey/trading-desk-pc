<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib uri="/struts-tags" prefix="s" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ui" %>

<script type="text/javascript">
    $(function() {
        $('#searchForm').pagingAssist({
            action:     '/admin/DiscoverChannelList/search.action',
            message:    '${ad:formatMessage("channel.search.loading")}',
            result:     $('#result')
        });
    });
</script>

<s:form id="searchForm">
    <s:actionerror/>

    <ui:section titleKey="form.search">
        <ui:fieldGroup>

            <ui:field labelKey="channel.search.name" labelForId="name" errors="name">
                <s:textfield name="name" id="name" cssClass="middleLengthText" maxlength="100"/>
            </ui:field>

            <ui:field labelKey="channel.search.account" labelForId="accountId" errors="accountId">
                <s:select list="accounts" listKey="id" listValue="name"
                             name="accountId" id="account.id" cssClass="middleLengthText"
                             headerKey="" headerValue="%{getText('form.all')}"/>
            </ui:field>

            <ui:field labelKey="channel.search.country" labelForId="countryCode" errors="countryCode">
                <s:select list="countries"
                             name="countryCode" id="countryCode" cssClass="middleLengthText"
                             headerKey="" headerValue="%{getText('form.all')}"
                             listKey="id" listValue="getText('global.country.' + id + '.name')"/>
            </ui:field>

            <ui:field labelKey="channel.language" labelForId="language" errors="language">
                <s:select list="availableLanguages" listKey="code" listValue="name"
                             name="language" id="language" cssClass="middleLengthText"
                             headerKey="" headerValue="%{getText('form.all')}"/>
            </ui:field>

            <ui:field labelKey="channel.search.status" labelForId="statusId" errors="status">
                <s:select list="statuses" listKey="name" listValue="%{getText(description)}"
                             name="status" id="statusId" cssClass="middleLengthText" value="%{status != null? status: 'ALL_BUT_DELETED'}"/>
            </ui:field>

            <ui:field cssClass="withButton">
                <ui:button message="form.search" onclick="$('#discoverChannels').val('true');" type="submit"/>
            </ui:field>

    </ui:fieldGroup>
    </ui:section>
</s:form>

<div id="result" class="logicalBlock">
</div>