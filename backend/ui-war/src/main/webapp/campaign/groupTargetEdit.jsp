<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="/struts-tags" prefix="s" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ui" %>

<c:set var="modulePath" scope="page" value="${_context}/channel"/>

<script type="text/javascript">

    function updateSelectedChannel(id, name, url) {
        var channelTargetElem = $('#channelTargetName');
        $('#channelTargetId, #channelId').val(id);
        $('#channelName').val(name);
        $('#channelTargetName').val(name);
        channelTargetElem.html(url ? ('<a href="' + url + '">' + UI.Text.escapeHTML(name) + '</a>') : UI.Text.escapeHTML(name));
    }

    function updateChannelTarget(val) {
        $('#channelTarget, #channelTargetId').val(val);
    }

    function selectChannel(channel) {
        $('#resetButton').show();
        $('#runUntargetedButton').show();
        updateChannelTarget("T");
        updateSelectedChannel(channel.id, channel.name, '${modulePath}/view.action?id=' + channel.id);
    }

    function selectReset() {
        $('#resetButton').hide();
        $('#runUntargetedButton').show();
        updateChannelTarget("N");
        updateSelectedChannel('', '<fmt:message key="channel.notset"/>', null);
    }

    function selectRunUntargeted() {
        $('#resetButton').show();
        $('#runUntargetedButton').hide();
        updateChannelTarget("U");
        updateSelectedChannel('', '<fmt:message key="channel.untargeted"/>', null);
    }
    
    function submitForm(){
    	if ($('#channelTarget').val() != 'N' || 
    			($('#channelTarget').val() == 'N' && confirm('${ad:formatMessage('channel.notset.alert')}'))) {
    		$('#groupActiveTab').val($('a[class="tab active"]').attr('id'));
    	    $('#groupSearchContent').val($('#searchContent').val());
    	    $('#groupSearchName').val($('#searchName').val());
    	    $('#groupSearchMyChannels').val($('#searchMyChannels').attr('checked') == 'checked');
    	    $('#groupSearchPublicChannels').val($('#searchPublicChannels').attr('checked') == 'checked');

    		$('#saveTragetFormId').submit();
    		}
    }

    $().ready(function() {
    <c:if test="${group.channelTarget.letter == 'T'}">
        $('#resetButton, #runUntargetedButton').show();
    </c:if>
    <c:if test="${group.channelTarget.letter == 'U'}">
        $('#resetButton').show();
    </c:if>
    <c:if test="${group.channelTarget.letter == 'N'}">
        $('#runUntargetedButton').show();
    </c:if>
    });

</script>

<ui:pageHeadingByTitle/>

<ui:errorsBlock>
    <s:fielderror><s:param value="'version'"/></s:fielderror>
</ui:errorsBlock>

<ui:section>
    <ui:fieldGroup>
        <ui:field labelKey="channel.target">
            <table class="fieldAndAccessories">
                <tr>
                    <td class="withField" id="channelTargetName">
                            <c:choose>
                                <c:when test="${not empty group.channel}">
                                    <c:set var="canViewChannel" value="${ad:isPermitted('AdvertisingChannel.view', group.channel)}"/>
                                    <c:if test="${canViewChannel}">
                                        <a href="${modulePath}/view.action?id=${group.channel.id}">
                                            <ui:nameWithStatus entityStatus="${group.channel.status}" entityName="${group.channel.name}"/>
                                        </a>
                                    </c:if>
                                    <c:if test="${not canViewChannel}">
                                        <ui:nameWithStatus entityStatus="${group.channel.status}" entityName="${group.channel.name}"/>
                                    </c:if>
                                </c:when>
                                <c:when test="${group.channelTarget.letter == 'N'}">
                                    <ui:text textKey="channel.notset"/>
                                </c:when>
                                <c:otherwise>
                                    <ui:text textKey="channel.untargeted"/>
                                </c:otherwise>
                            </c:choose>
                    </td>
                    <td class="withButton">
                        <ui:button id="runUntargetedButton" subClass="hide" message="channel.untargeted.caption"
                                   onclick="selectRunUntargeted();"/>
                    </td>
                    <td class="withButton">
                        <ui:button id="resetButton" subClass="hide" message="form.reset" onclick="selectReset();"/>
                    </td>
                    <td>
                        <s:fielderror><s:param value="'channel'"/></s:fielderror>
                    </td>
                </tr>
            </table>
        </ui:field>
    </ui:fieldGroup>
</ui:section>

<c:set var="tabChannelMarketplaceCaption">
    <fmt:message key="channel.marketplace"/>
</c:set>

<c:set var="tabChannelsCaption">
    <c:if test="${ad:isInternal()}">
        <fmt:message key="channel.account.channels"/>
    </c:if>
    <c:if test="${!ad:isInternal()}">
        <fmt:message key="channel.my.channels"/>
    </c:if>
</c:set>



<c:set var="rootFormId" value="searchTragetFormId"/>
<fmt:message var="countryHintMessage" key="channel.marketplace.cg.country.selected.hint">
    <fmt:param>
        <c:if test="${group.ccgType =='DISPLAY'}"><fmt:message key="channel.marketplace.dcg"/></c:if>
        <c:if test="${group.ccgType =='TEXT'}"><fmt:message key="channel.marketplace.tcg"/></c:if>
    </fmt:param>
</fmt:message>

<c:set var="accountIdForSearchTab" value="${requestContexts.advertiserContext.accountId}"/>
<c:set var="needUpdateAccountId" value="false"/>
<%@include file="/channel/searchTabs.jsp" %>

<s:form action="%{#attr.moduleName}/target/update" id="saveTragetFormId">
    <s:hidden name="group.id"/>
    <s:hidden name="group.name"/>
    <s:hidden name="group.channel.id" id="channelTargetId"/>
    <s:hidden name="group.channel.name" id="channelName"/>
    <s:hidden name="channelTargetLetter" id="channelTarget"/>
    <s:hidden name="group.country.countryCode" id="countryCodeForSearch"/>
    <s:hidden name="existingAccount.id" value="%{group.account.id}"/>
    <s:hidden name="version"/>
    
    <s:hidden name="searchCriteria.activeTab" id="groupActiveTab"/>
    <s:hidden name="searchCriteria.searchName" id="groupSearchName"/>
    <s:hidden name="searchCriteria.content" id="groupSearchContent"/>
    <s:hidden name="searchCriteria.searchMyChannels" id="groupSearchMyChannels"/>
    <s:hidden name="searchCriteria.searchPublicChannels" id="groupSearchPublicChannels"/>
</s:form>
    <div class="wrapper">
        <ui:button message="form.save" onclick="submitForm();" type="submit"/>
        <c:if test="${group.ccgType =='DISPLAY'}">
            <ui:button message="form.cancel" onclick="location='../viewDisplay.action?id=${group.id}';" type="button"/>
        </c:if>
        <c:if test="${group.ccgType =='TEXT'}">
            <ui:button message="form.cancel" onclick="location='../viewText.action?id=${group.id}';" type="button"/>
        </c:if>
    </div>

