<%@ page contentType="text/html" %>
<%@ page pageEncoding="UTF-8" %>
<%@ page import="com.foros.model.channel.Channel" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib uri="/struts-tags" prefix="s" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ui" %>

<c:set var="channelType" value="<%=Channel.CHANNEL_TYPE_DISCOVER_CHANNEL_LIST%>"/>
<c:set var="entityType" value="8"/>
<c:set var="entityBean" value="${model}"/>
<%@ include file="/channel/discoverChannelPopupToLinkJS.jsp" %>
<script type="text/javascript">

    function toggleAllChannels(header) {
        $('[name=selectedChannels]').prop({checked : header.checked})
    }
    function getDeclineReason() {
        var declinationReason = '';
        do {
            declinationReason = prompt("<fmt:message key="decline.reason"/>", "");
            if (declinationReason == null || declinationReason.length == 0) return;
            if (declinationReason.length > 500) {
                alert("<fmt:message key="decline.too.long"/>");
            }
        } while (declinationReason.length > 500);
        return declinationReason;
    }

    function getSelectedChannels() {
        var channels = [];
        $('[name=selectedChannels]').each(function() {
            if (this.checked) {
                // todo: use data method after jquery update to 1.4.3
                channels.push({id: this.value, version: $(this).attr('data-version')});
            }
        });
        return channels;
    }

    function checkChannelBatchActionAndProceed(action, url) {
        var channels = getSelectedChannels();
        if (channels.length == 0) {
            return;
        }
        var channelIds = $.map(channels, function(channel){
            return channel.id;
        });
        UI.Data.post('DiscoverChannelBatchStatusActionCheck', {action: action, ids: channelIds}, function(data) {
            var result = $('result', data).text();
            var confirmed;
            if (result == 'true') {
                if (action == 'DELETE') {
                    confirmed = confirm('${ad:formatMessage('confirmDelete')}');
                } else {
                    confirmed = (action == 'DECLINE') || (channelIds.length == 1) || confirm('${ad:formatMessage('confirmBulkChange')}');
                }
            } else {
                confirmed = confirm('${ad:formatMessage('channel.batchAction.incomplete.confirm')}');
            }
            if (confirmed) {
                if (action == 'DECLINE') {
                    var declinationReason = getDeclineReason();
                    if (declinationReason == null) return;
                    $('#declinationReason').val(declinationReason);
                }
                $('#channelsForm').attr('action', url).submit();
            }
        });
    }

    $(document).ready(function() {
        $('#linkDifferentChannelButton').click(function(event) {
            var channels = getSelectedChannels();
            if (channels.length == 0) {
                //TODO add some alert
                return;
            }
            var channelListId = ${model.id};

            popupLinkToDiscoverChannelList(channelListId, channels, true, event);
        });
        if (window.location.hash == '#error:version'){
            $('<span></span>').addClass('errors').text('<fmt:message key="errors.version"/>').appendTo('#popupVersionError');
            $("#popupVersionError").addClass('wrapper');
            window.location.hash = '';
        }
    });

</script>

<c:if test="${not empty language}">
    <c:set var="channelLanguage"><ad:resolveGlobal resource="language" id="${model.language}"/></c:set>
</c:if>
<c:if test="${empty language}">
    <c:set var="channelLanguage"><fmt:message key="form.select.notSpecified"/></c:set>
</c:if>

<ui:header>
    <ui:pageHeadingByTitle/>
    <table class="grouping groupOfButtons">
        <tr>
            <td>
                <c:if test="${ad:isPermitted('DiscoverChannel.update', model)}">
                    <ui:button message="form.edit" href="/admin/DiscoverChannelList/edit.action?id=${model.id}"/>
                </c:if>
            <td>
                <%@ include file="/auditLog/viewLogButton.jspf" %>
            </td>
        </tr>
    </table>
</ui:header>

<ui:errorsBlock>
    <s:fielderror fieldName="baseKeyword"/>
    <s:fielderror/>
</ui:errorsBlock>
<div id="popupVersionError">
</div>

<ui:section>
    <ui:fieldGroup>
        <ui:field labelKey="channel.account">
            <ui:accountLink account="${account}"/>
        </ui:field>

        <ui:field labelKey="channel.country">
            <ui:countryLink countryCode="${model.country.countryCode}"/>
        </ui:field>

        <ui:simpleField labelKey="channel.language" value="${channelLanguage}"/>

        <ui:simpleField labelKey="channel.channelNameMacro" value="${model.channelNameMacro}"/>

        <ui:simpleField labelKey="channel.keywordTriggerMacro" value="${model.keywordTriggerMacro}"/>

        <ui:simpleField labelKey="channel.discoverQueryMacro" value="${model.discoverQuery}"/>

        <ui:simpleField labelKey="channel.discoverAnnotationMacro" value="${model.discoverAnnotation}"/>

        <ui:field labelKey="channel.status" tipKey="channel.statusChangeTip">
            <ui:statusButtonGroup
                descriptionKey="${model.displayStatus.description}"
                entity="${model}" restrictionEntity="DiscoverChannel"
                activatePage="activate.action" inactivatePage="inactivate.action"
                deletePage="delete.action" undeletePage="undelete.action"
                />
        </ui:field>

        <ui:field labelKey="channel.params">
            <c:choose>
                <c:when test="${not empty model.behavParamsList.id}">
                    <a href="${_context}/behavioralParameters/view.action?id=${model.behavParamsList.id}"><c:out value="${model.behavParamsList.name}"/></a>
                </c:when>
                <c:otherwise>
                    <ui:text textKey="form.select.none"/>
                </c:otherwise>
            </c:choose>
        </ui:field>

        <c:if test="${not empty description}">
            <ui:field labelKey="channel.descriptionMacro">
                <s:textarea name="description" cssClass="middleLengthText" cssStyle="height: 50px" rows="3" readonly="true"/>
            </ui:field>
        </c:if>

        <%@include file="../../channel/channelCategoriesView.jsp" %>

    </ui:fieldGroup>
</ui:section>

<table class="dataViewSection">
    <c:if
        test="${account.status.letter != 'D' and model.status.letter != 'D' and not empty childChannels}">
        <tr class="controlsZone">
            <td>
                <table class="grouping">
                    <tr>
                        <td class="withButtons">
                            <c:set var="canUpdate" value="${ad:isPermitted0('DiscoverChannel.update')}"/>
                            <c:if test="${canUpdate and model.status != 'INACTIVE'}">
                                <ui:button message="form.activate"
                                           onclick="checkChannelBatchActionAndProceed('ACTIVATE', 'batchActivate.action')"/>
                                <ui:button message="form.deactivate"
                                           onclick="checkChannelBatchActionAndProceed('INACTIVATE', 'batchInactivate.action')"/>
                            </c:if>
                            <c:if test="${canUpdate}">
                                <ui:button message="form.delete"
                                           onclick="checkChannelBatchActionAndProceed('DELETE', 'batchDelete.action')"/>
                            </c:if>
                            <c:if test="${canUpdate}">
                                <ui:button message="DiscoverChannel.linkDifferent"
                                            href="javascript:;" id="linkDifferentChannelButton"/>
                            </c:if>
                        </td>
                    </tr>
                </table>
            </td>
        </tr>
    </c:if>
    <tr class="bodyZone">
        <td>
            <s:form id="channelsForm">
                <s:hidden name="id"/>
                <s:hidden id="declinationReason" name="declinationReason" value=""/>
                <display:table name="childChannels" class="dataView" id="channel">
                    <display:setProperty name="basic.msg.empty_list">
                        <div class="wrapper">
                            <fmt:message key="nothing.found.to.display"/>
                        </div>
                    </display:setProperty>
                    <display:column title="<input type='checkbox' onclick='toggleAllChannels(this)'>">
                        <input type="checkbox" name="selectedChannels" value="${channel.id}" data-version="${channel.version.time}/${channel.version.nanos}"/>
                    </display:column>
                    <display:column titleKey="channel.channelName" sortProperty="name">
                        <ui:displayStatus displayStatus="${channel.displayStatus}">
                            <a class="value" href="/admin/DiscoverChannel/view.action?id=${channel.id}">
                                <c:out value="${channel.name}"/>
                            </a>
                        </ui:displayStatus>
                    </display:column>
                    <display:column titleKey="channel.pageKeywords.positive" escapeXml="false">
                        <c:set var="keywords"><ad:commaWriter label="original" items="${channel.pageKeywords.positive}" escape="false"/></c:set>
                        <ui:text text="${keywords}" maxLength="30"/>
                    </display:column>
                    <display:column titleKey="channel.searchKeywords.positive" escapeXml="false">
                        <c:set var="keywords"><ad:commaWriter label="original" items="${channel.searchKeywords.positive}" escape="false"/></c:set>
                        <ui:text text="${keywords}" maxLength="30"/>
                    </display:column>
                    <display:column titleKey="channel.status">
                        <c:set var="textVal">
                            <fmt:message key="${channel.displayStatus.description}"/>
                        </c:set>
                        <ui:text text="${pageScope.textVal}"/>
                    </display:column>
                </display:table>
            </s:form>
        </td>
    </tr>
</table>
