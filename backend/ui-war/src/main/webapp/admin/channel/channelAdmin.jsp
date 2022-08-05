<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@ taglib uri="/struts-tags" prefix="s" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ui" %>

<script type="text/javascript">
    $(function() {
        $('#searchForm').pagingAssist({
            action:     '/admin/channel/search.action',
            message:    '${ad:formatMessage("channel.search.loading")}',
            result:     $('#searchResult'),
            onclick:    clearErrors()
        });
    });

    function clearErrors() {
        if ($('#resubmitRequired').val() != 'true') {
            $('.errors').remove();
        }
        return true;
    }

    $(function() {
        if ($('#resubmitRequired').val() == 'true') {
            $('#resubmitRequired').val('false');
            $('#searchForm').submit();
        }
        
        $('head').append('<link rel="icon" href="/images/logo.png" />');
    });
</script>

<ui:header>
    <ui:pageHeadingByTitle />
    <c:set var="canChannelMatchTest" value="${ad:isPermitted0('ChannelMatchTest.run')}"/>
    <c:set var="canCreateChannel" value="${ad:isPermitted('AdvertisingChannel.create', myAccount)}"/>
    <c:if test="${canChannelMatchTest or canCreateChannel}">
        <table class="grouping groupOfButtons">
            <tr>
                <c:if test="${canCreateChannel}">
                    <td>
                        <ui:button message="channel.buttons.createBehavioral" href="BehavioralChannel/new.action?accountId=${myAccount.id}" />
                    </td>
                    <td>
                        <ui:button message="channel.buttons.createExpression" href="ExpressionChannel/new.action?accountId=${myAccount.id}" />
                    </td>
                </c:if>
                <c:if test="${ad:isPermitted0('AdvertisingChannel.upload')}">
                    <td>
                        <ui:button message="channel.upload.bulkUpload" href="uploadInternal/main.action" />
                    </td>
                </c:if>
                <c:if test="${canChannelMatchTest}">
                    <td>
                        <ui:button message="channel.search.test" href="channelMatch/main.action" />
                    </td>
                </c:if>
            </tr>
        </table>
    </c:if>
</ui:header>

<form id="searchForm">
   <s:hidden name="resubmitRequired" id="resubmitRequired"/>

   <ui:section titleKey="form.search">
       <ui:fieldGroup>

           <ui:field labelKey="channel.search.name" labelForId="name" errors="name">
               <s:textfield name="name" id="name" cssClass="middleLengthText" maxlength="100"/>
           </ui:field>

           <ui:field labelKey="channel.search.contents" labelForId="phrase" errors="phrase">
               <s:textfield  name="phrase" id="phrase" cssClass="middleLengthText" maxlength="100"/>
           </ui:field>

           <ui:field labelKey="channel.search.account" labelForId="accountId" errors="accountId">
               <s:select name="accountId" id="accountId" cssClass="middleLengthText"
                   headerValue="%{getText('form.all')}" headerKey=""
                   list="accounts"
                   listKey="id" listValue="name"/>
           </ui:field>

           <ui:field labelKey="channel.search.country" labelForId="countryCode" errors="countryCode">
               <s:select name="countryCode" id="countryCode" cssClass="middleLengthText"
                   headerValue="%{getText('form.all')}" headerKey=""
                   list="countries"
                   listKey="id" listValue="getText('global.country.' + id + '.name')"/>
           </ui:field>

           <ui:field labelKey="channel.search.type" labelForId="channelType" errors="channelType">
               <s:select name="channelType" id="channelType" cssClass="middleLengthText"
                   headerValue="%{getText('form.all')}" headerKey=""
                   list="#{'A':getText('channel.type.audience'), 'B':getText('channel.type.channel'), 'E':getText('channel.type.expression')}"
                   value="%{channelType}"/>
           </ui:field>

           <ui:field labelKey="channel.search.visibility" labelForId="visibility" errors="visibility">
               <s:select name="visibility" id="visibility" cssClass="middleLengthText"
                   headerValue="%{getText('form.all')}" headerKey=""
                   list="#{'PUB':getText('channel.visibility.PUB'),
                           'PRI':getText('channel.visibility.PRI'),
                           'CMP':getText('channel.visibility.CMP')}"/>
           </ui:field>

           <ui:field labelKey="channel.search.status" labelForId="status" errors="status">
               <s:select name="status" id="status" cssClass="middleLengthText"
                   list="statuses"
                   listKey="name" listValue="%{getText(description)}" value="%{status != null? status: 'ALL_BUT_DELETED'}"/>
           </ui:field>

           <ui:field labelKey="account.testOption" labelForId="testOption" errors="testOption">
               <s:select name="testOption"  id="testOption" list="testOptions" cssClass="middleLengthText"
                   listKey="name"  listValue="getText('' + description + '')" />
           </ui:field>

           <ui:field labelKey="CategoryChannel.entityName" labelForId="categoryChannel" errors="categoryChannel">
               <s:select name="categoryChannelId" id="categoryChannel" list="categoryChannels" cssClass="middleLengthText"
                   headerValue="%{getText('form.all')}" headerKey=""
                   listKey="id" listValue="localizedName" />
           </ui:field>

           <ui:field cssClass="withButton">
               <ui:button message="form.search" onclick="clearErrors();" type="submit"/>
           </ui:field>

       </ui:fieldGroup>
   </ui:section>
</form>

<s:actionerror/>

<div id="searchResult" class="logicalBlock"/>
