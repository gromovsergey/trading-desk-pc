<%@ page contentType="text/html" %>
<%@ page pageEncoding="UTF-8" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/struts-tags" prefix="s" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ui" %>

<script type="text/javascript">
    function doSearch() {
        <c:if test="${needUpdateAccountId}">
            $('#searchFormByNameId_account_id').val($('#accountIdForSearch').val());
        </c:if>
        
        $('#searchFormByNameId_country_countryCode').val($('#countryCodeForSearch').val());
        $('#searchButton').prop({disabled : true});
        $('#searchChannelsResultsDiv').hide();
        $('#searchWait').show();
        $.ajax({
            'url':      'searchChannels.action',
            'type':     'POST',
            'data':     $('#searchFormByNameId').serialize(),
            'success':  function(data) {
                var jqData  = $(data), 
                iDelay      = 10,
                iCnt        = 50;
            
                function showTr(){
                    $('#matchedChannelRow tbody tr:hidden:lt('+iCnt+')').show();
                    if ($('#matchedChannelRow tbody tr:hidden').length) {
                        setTimeout(showTr, iDelay);
                    }
                }
                
                jqData.find('#matchedChannelRow tbody tr:gt('+iCnt+')').addClass('hide');
                $('#searchChannelsPlaceholder').html('').append(jqData);
                setTimeout(showTr, iDelay);
            }
        });
    }

    $(function(){
        $('#searchFormByNameId').on('submit', function(e){
            e.preventDefault();
        });
        $('#searchChannelsPlaceholder').on('click', '#searchButton', doSearch)
        .delegate('keypress', '#searchName, #searchContent', function(e){ 
            if (e.keyCode == 13) {
                doSearch();
                e.preventDefault();
            }
        });
    });
</script>

<c:set var="tabChannelMarketplaceCaption">
    <fmt:message key="channel.searchCaption"/>
</c:set>

<c:set var="tabUsedChannelsCaption">
    <fmt:message key="channel.usedChannels"/>
</c:set>

<c:set var="countryInfo">
    <fmt:message key="channel.marketplace.country.selected">
        <fmt:param>
            <span id="countryLabel">
                <ad:resolveGlobal resource="country" id="${country.countryCode}"/>
            </span>
        </fmt:param>
    </fmt:message>
</c:set>

<form id="searchFormId" >
    <s:hidden id="searchFormId_account_id" value="%{#attr.accountIdForSearchTab}" name="account.id"/>
    <s:hidden id="searchFormId_country_countryCode" name="country.countryCode"/>
</form>

<ui:tabGroup id="tabContainer" additionalInfo="${countryInfo}" additionalInfoHint="${countryHintMessage}" activeTab="${searchCriteria.activeTab}" activeTabProperty="activeTab">
<form id="searchFormByNameId" >
    <s:hidden id="searchFormByNameId_account_id" name="account.id" value="%{#attr.accountIdForSearchTab}"/>
    <s:hidden id="searchFormByNameId_country_countryCode" name="country.countryCode"/>
    <ui:tab title="${tabChannelMarketplaceCaption}" hintKey="channel.marketplace.title" id="searchChannels">
        <div id="searchChannelsPlaceholder">
            <%@include file="searchChannelsTab.jsp"%>
        </div>
    </ui:tab>
</form>

<s:if test="usedAvailable">
    <ui:tab title="${tabUsedChannelsCaption}" hintKey="channel.usedChannels.title" id="usedChannels">
        <ui:ajax id="usedChannelsPlaceholder"
                 url="searchUsedChannels.action"
                 form="searchFormId"
                 loadOnShow="true">
            <fmt:message key="form.select.wait"/>
        </ui:ajax>
    </ui:tab>
</s:if>
</ui:tabGroup>
