<%@ page contentType="text/html" %>
<%@ page pageEncoding="UTF-8" %>


<%@ taglib uri="/struts-tags" prefix="s" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ui" %>

<jsp:useBean id="requestContexts" type="com.foros.util.context.RequestContexts" scope="request"/>
<jsp:useBean id="model" type="com.foros.model.channel.ExpressionChannel" scope="request"/>
<jsp:useBean id="existingAccount" type="com.foros.model.account.Account" scope="request"/>

<script type="text/javascript">
    var ACCOUNT_NAME_DELIMITER = '|';

    function insertText(text){
        var cdml = $("#cdml");
        UI.Text.insertAtCaret(cdml[0], text);
        cdml.focus();
        UI.Text.setCaretToEnd(cdml[0]);
    }

    function insertParenthesis(){
        var container = $("#cdml")[0];
        container.value += '() ';
        UI.Text.setCaretToEnd(container);
    }

    <c:set var='isInternalUser' value='${ad:isInternal()}' />

    function insertChannel(channel){
        var chName = $('#currentAccountIdElement').val() != channel.account.id ? (channel.account.name + ACCOUNT_NAME_DELIMITER + channel.name) : channel.name;
        insertText("[" + chName + "]");
    }

    function updateFields(){
        $('#channelExpressionEditorSave_searchCriteria_activeTab').val($('a[class="tab active"]').attr('id'))
        $('#channelExpressionEditorSave_searchCriteria_content').val($('#searchContent').val())
        $('#channelExpressionEditorSave_searchCriteria_searchName').val($('#searchName').val())
        $('#channelExpressionEditorSave_searchCriteria_searchMyChannels').val($('#searchMyChannels').attr('checked') == 'checked')
        $('#channelExpressionEditorSave_searchCriteria_searchPublicChannels').val($('#searchPublicChannels').attr('checked') == 'checked')
        return true;
    }

    function  updateTabs(){
        $("#countryLabel").html($("#countryCodeForSearch option:selected").text());
        
        $('#searchFormId_account_id').val($('#accountIdForSearch').val());
        $('#searchFormId_country_countryCode').val($('#countryCodeForSearch').val());
        
        var activeTab = $('a[class="tab active"]').attr('id')
        $('#accountChannelsResultsDiv').html('<fmt:message key="form.select.wait"/>');
        <s:if test="usedAvailable">
           $('#usedChannelsPlaceholder').html('<fmt:message key="form.select.wait"/>');
        </s:if>
        
        $('#searchChannelsResultsDiv').hide();
        if (activeTab != 'searchChannels'){
            $('#' + activeTab + 'Placeholder').ajaxPanel().load();
        }
        
        <s:if test="usedAvailable">
            $('#usedChannelsPlaceholder').ajaxPanel().loadOnShow();
        </s:if>
    }

    $().ready(function(){
        var tabsInTabContainer = $('#tabContainer .tabs:eq(0) > .tab');
        tabsInTabContainer.click(function(){
            UI.Text.setCaretToEnd($("#cdml")[0]);
        });

        $('#phrase, #searchName').unbind('keypress').keypress(function(e){
            if(e.which == 13){
                doSearch();
                return false;
            }
        });
        
        $("#accountIdForSearch").change(function() {
            UI.Data.get('accountCountry', {accountId: $(this).val()}, function(data) {
                $("#countryCodeForSearch").val($("countryCode", data).text());
                updateTabs();
            });
        });
        
        $("#countryCodeForSearch").change(function() {
            updateTabs();
        });
    });
</script>

<ui:pageHeadingByTitle/>

<ui:errorsBlock>
    <s:fielderror><s:param value="'version'"/></s:fielderror>
</ui:errorsBlock>

<fmt:message var="countryHintMessage" key="channel.marketplace.country.selected.hint"/>

<c:set var="accountIdForSearchTab" value="${account.id}"/>
<c:set var="needUpdateAccountId" value="true"/>
<%@include file="searchTabs.jsp"%>

<s:form action="%{#attr.isCreatePage ? 'ExpressionChannel/create' : 'ExpressionChannel/update'}" id="channelExpressionEditorSave">
<s:if test="!#attr.isCreatePage">
    <s:hidden name="id"/>
</s:if>
<s:hidden name="version"/>
<s:hidden name="type"/>
<s:hidden name="searchCriteria.activeTab"/>
<s:hidden name="searchCriteria.content"/>
<s:hidden name="searchCriteria.searchName"/>
<s:hidden name="searchCriteria.searchMyChannels"/>
<s:hidden name="searchCriteria.searchPublicChannels"/>

<ui:section>
    <ui:fieldGroup>
        <c:if test="${ad:isInternal()}">
            <c:choose>
                <c:when test="${contextName == 'global.menu.admin' || contextName == 'global.menu.channels'}">
                    <c:choose>
                        <c:when test="${isCreatePage}">
                            <ui:field labelKey="account.internalAccount" labelForId="account" required="true" errors="account.id">
                                <s:select name="account.id" cssClass="middleLengthText" id="accountIdForSearch" list="channelOwners"  listKey="id" listValue="name"/>
                            </ui:field>
                            <c:set var="accountEditable" value="true"/>
                        </c:when>
                        <c:otherwise>
                            <ui:simpleField labelKey="account.internalAccount"
                                value="${account.name}"/>
                        </c:otherwise>
                    </c:choose>
                </c:when>
            </c:choose>
        </c:if>
        <c:if test="${not accountEditable}">
            <s:hidden name="account.id" id="accountIdForSearch"/>
        </c:if>
        
        <ui:field labelKey="channel.name" labelForId="channelNameId" required="true" errors="name">
            <s:textfield name="name" id="name" cssClass="middleLengthText" maxlength="100"/>
        </ui:field>
        
        <ui:field labelKey="channel.description" labelForId="decription" errors="description">
            <s:textarea name="description" id="description" cssClass="middleLengthText" rows="2" style="height: 50px" styleClass="middleLengthText1"/>
        </ui:field>

        <c:choose>
            <c:when test="${existingAccount.international == true}">
                <ui:field labelKey="channel.country" labelForId="countryCodeForSearch" errors="country.countryCode">
                    <c:choose>
                        <c:when test="${isCreatePage}">
                            <s:select name="country.countryCode" id="countryCodeForSearch" cssClass="middleLengthText"
                                list="countries" value="country.countryCode"
                                listKey="id" listValue="getText('global.country.' + id + '.name')"/>
                        </c:when>
                        <c:otherwise>
                            <fmt:message key="global.country.${country.countryCode}.name"/>
                            <s:hidden name="country.countryCode" id="countryCodeForSearch"/>
                        </c:otherwise>
                    </c:choose>
                </ui:field>
            </c:when>
            <c:otherwise>
                <s:hidden name="country.countryCode" id="countryCodeForSearch"/>
            </c:otherwise>
         </c:choose>

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

<ui:section titleKey="channel.expressionEditor" mandatory="true">
    <ui:fieldGroup>
        <ui:field labelKey="channel.expressionEditor.operators">
            <ui:button messageText="OR" onclick="insertText(' OR ');" />
            <ui:button messageText="AND" onclick="insertText(' AND ');" />
            <ui:button messageText="AND&nbsp;NOT" onclick="insertText(' AND_NOT ');" />
            <ui:button messageText="( ... )" onclick="insertParenthesis();" />
        </ui:field>
    </ui:fieldGroup>
    <ui:fieldGroup>

        <ui:field>
            <s:fielderror><s:param value="'expression'"/></s:fielderror>
            <s:textarea id="cdml" styleId="cdml" rows="3" cssClass="bigLengthText"
                name="humanExpression" style="height: 50px"/>
        </ui:field>

        <c:if test="${ad:isPermitted('AdvertisingChannel.create', account)}">
            <ui:field cssClass="withButton" >
                <ui:button message="channel.expressionEditor.createChannel" href="${_context}/channel/BehavioralChannel/new.action${ad:accountParam('?accountId', account.id)}"  target="_blank"/>
            </ui:field>
        </c:if>
    </ui:fieldGroup>
</ui:section>
</s:form>
<div class="wrapper">
    <ui:button message="form.save" id="saveButton" onclick="if (updateFields()){$('#channelExpressionEditorSave').submit();}" type="submit"/>
    <c:choose>
        <c:when test="${standalone == true}">
            <ui:button message="form.cancel" onclick="closeWindow(); return false;" type="button"/>
        </c:when>
        <c:when test="${isCreatePage}">
            <c:choose>
                <c:when test="${not requestContexts.set}">
                    <ui:button message="form.cancel" action="main" type="button"/>
                </c:when>
                <c:otherwise>
                    <ui:button message="form.cancel" action="contextMain${ad:accountParam('?accountId', existingAccount.id)}" type="button"/>
                </c:otherwise>
            </c:choose>
        </c:when>
        <c:otherwise>
            <ui:button message="form.cancel"  action="view?id=${model.id}" type="button"/>
        </c:otherwise>
    </c:choose>
</div>

