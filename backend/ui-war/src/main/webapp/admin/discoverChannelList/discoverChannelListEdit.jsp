<%@ page contentType="text/html" %>
<%@ page pageEncoding="UTF-8" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib uri="/struts-tags" prefix="s" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ui" %>

<script type="text/javascript">
    $().ready(function() {
        $("#account").change(function() {
            UI.Data.get('accountCountry', {accountId: $(this).val()}, function(data) {
                $("#countryCode").val($("countryCode", data).text()).change();
            });
        });

        $('#saveForm').submit(function() {
            var channels = getSelectedChannelsToLink();
            for (var i = 0; i < channels.length; i++) {
                var propertyNameBase = 'channelsToLink[' + i + ']';
                var idHidden = $('<input/>').attr({'type': 'hidden', 'name': propertyNameBase + '.id'}).val(channels[i].id);
                $(this).append(idHidden);
                var versionHidden = $('<input/>').attr({'type': 'hidden', 'name': propertyNameBase + '.version.fullTime'}).val(channels[i].version);
                $(this).append(versionHidden);
            }
        });
    });

    function toggleAllChannels(header) {
        $('.selectedChannelsToLink:not(:disabled)').prop({checked : header.checked});
    }

    function getSelectedChannelsToLink() {
        var channels = [];
        $('.selectedChannelsToLink').each(function() {
            if (this.checked) {
                // todo: use data method after jquery update to 1.4.3
                channels.push({id: this.value, version: $(this).attr('data-version')});
            }
        });
        return channels;
    }

</script>

<s:form action="admin/DiscoverChannelList/save" id="saveForm">
    <s:hidden name="id"/>
    <s:hidden name="version"/>

    <ui:pageHeadingByTitle/>

    <ui:errorsBlock>
        <s:fielderror fieldName="version"/>
    </ui:errorsBlock>

    <c:if test="${!empty existingChannels}">
        <div class="wrapper">
            <span class="infos">
                <fmt:message key="channel.discoverList.duplicateChannel.warning"/>
            </span>
        </div>
    </c:if>

    <ui:section titleKey="channel.globalListParams" tipKey="channel.keywordMacroTip">
        <ui:fieldGroup>
            <c:choose>
                <c:when test="${empty model.id}">
                    <ui:field labelKey="account.account" labelForId="account" required="true" errors="account.id">
                      <s:select list="channelOwners" listKey="id" listValue="name" value="account.id"
                                   name="accountId" id="account" cssClass="middleLengthText"/>
                    </ui:field>
                </c:when>
                <c:otherwise>
                    <s:hidden name="accountId"/>
                    <s:hidden name="accountName"/>
                    <ui:simpleField labelKey="channel.account" value="${accountName}"/>
                </c:otherwise>
            </c:choose>

            <ui:field labelKey="channel.listName" labelForId="name" required="true" errors="name,errors.duplicate">
                <s:textfield name="name" id="name" cssClass="middleLengthText" maxlength="100"/>
            </ui:field>

            <ui:field id="countryElem" labelKey="channel.country" labelForId="countryCode" errors="country.countryCode">
                 <s:select name="country.countryCode" id="countryCode" cssClass="middleLengthText"
                     list="countries" value="country.countryCode"
                     listKey="id" listValue="getText('global.country.' + id + '.name')"/>
            </ui:field>
            
            <s:if test="savedBefore">
                <s:hidden name="prevCountry" value="%{model.country.countryCode}"/>
            </s:if>   
            <s:hidden name="savedBefore" />
            <s:hidden name="prevKeywordList"/>
            <s:hidden name="prevAccountId" value="%{accountId}" />

            <%@include file="/channel/channelLanguageField.jsp"%>

            <ui:field labelKey="channel.params" labelForId="bparamsList" required="false" errors="behavParamsList.id">
                 <s:select name="behavParamsList.id" id="bparamsList" cssClass="middleLengthText"
                     list="availableBehavioralParameters" 
                     listKey="id" listValue="name"
                     headerKey="" headerValue="%{getText('form.select.none')}"/>
            </ui:field>

            <ui:field labelKey="channel.channelNameMacro" labelForId="channelNameMacro" required="true"
                      errors="channelNameMacro">
                <s:textfield name="channelNameMacro" id="channelNameMacro" cssClass="middleLengthText" maxlength="200"/>
            </ui:field>

            <ui:field labelKey="channel.keywordTriggerMacro" labelForId="keywordTriggerMacro" required="true"
                      errors="keywordTriggerMacro">
                <s:textfield name="keywordTriggerMacro" id="keywordTriggerMacro" cssClass="middleLengthText" maxlength="4000"/>
            </ui:field>

            <ui:field labelKey="channel.discoverQueryMacro" labelForId="disocverQueryMacro" required="true"
                      errors="discoverQuery">
                <s:textfield name="discoverQuery" id="discoverQuery" cssClass="middleLengthText" maxlength="4000"/>
            </ui:field>

            <ui:field labelKey="channel.discoverAnnotationMacro" labelForId="disocverAnnotationMacro" required="true"
                      errors="discoverAnnotation">
                <s:textfield name="discoverAnnotation" id="discoverAnnotation" cssClass="middleLengthText" maxlength="4000"/>
            </ui:field>

            <ui:field labelKey="channel.descriptionMacro" labelForId="descriptionMacro" errors="description">
                <s:textarea name="description" id="descriptionMacro" cssClass="middleLengthText" rows="3"
                               cssStyle="height: 50px"/>
            </ui:field>
        </ui:fieldGroup>
    </ui:section>

    <div class="wrapper">
        <table class="grouping fieldsets">
            <tr>
                <td class="singleFieldset">
                    <ui:section titleKey="channel.keywords" errors="keywords,keywordList">
                        <s:textarea name="keywordList" wrap="off" cssClass="middleLengthText1"/>
                        <div style="width:450px">
                            <fmt:message key="channel.listKeywordsNote"/>
                        </div>
                    </ui:section>
                </td>
                <td class="singleFieldset"></td>
            </tr>
        </table>
    </div>

    <c:forEach items="${channelsVersions}" var="channelVersion" varStatus="status">
        <input type="hidden" name="channelsVersion[${status.index}].id" value="${channelVersion.id}"/>
        <input type="hidden" name="channelsVersion[${status.index}].version.fullTime" value="${channelVersion.version.fullTime}"/>
    </c:forEach>

    <c:if test="${!empty existingChannels}">
        <c:forEach items="${existingChannels}" var="channel" varStatus="status">
            <input type="hidden" name="existingChannels[${status.index}].id" value="${channel.id}"/>
        </c:forEach>
        <ui:section titleKey="channel.duplicate.channels" tipKey="channel.discoverList.duplicateChannel.warning.short" errors="existingChannels">
            <display:table name="existingChannels" class="dataView" id="channel" defaultsort="1">
                <display:setProperty name="basic.msg.empty_list">
                    <div class="wrapper">
                        <fmt:message key="nothing.found.to.display"/>
                    </div>
                </display:setProperty>
                <display:column title="<input type='checkbox' onclick='toggleAllChannels(this)'>">
                    <input type="checkbox" class="selectedChannelsToLink" value="${channel.id}" data-version="${channel.version.fullTime}"
                            <c:if test="${channel.status.letter == 'D'}">disabled="disabled"</c:if>/>
                </display:column>
                <display:column titleKey="channel.channelName" sortProperty="name">
                    <ui:displayStatus displayStatus="${channel.displayStatus}">
                        <a class="value" href="/admin/DiscoverChannel/view.action?id=${channel.id}">
                            <c:out value="${channel.name}"/>
                        </a>
                    </ui:displayStatus>
                </display:column>
                <display:column titleKey="channel.country" sortProperty="name">
                     <ui:countryLink countryCode="${channel.country.countryCode}"/>                    
                </display:column>
                <display:column titleKey="channel.search.channelList">
                    <c:choose>
                        <c:when test="${!empty channel.channelList}">
                            <ui:displayStatus displayStatus="${channel.channelList.displayStatus}">
                                <a href="view.action?id=${channel.channelList.id}" class="value">
                                    <c:out value="${channel.channelList.name}"/>
                                </a>
                            </ui:displayStatus>
                        </c:when>
                        <c:otherwise>
                            <fmt:message key="channel.na"/>
                        </c:otherwise>
                    </c:choose>
                </display:column>
            </display:table>
            <c:if test="${existingContainsURLs}">
                <div class="wrapper">
                    <fmt:message key="channel.linkedDuplicateChannelUrlsNote"/>
                </div>
            </c:if>
        </ui:section>
    </c:if>

    <div class="wrapper">
        <ui:button message="form.save"/>
        <c:choose>
            <c:when test="${empty model.id}">
                <ui:button message="form.cancel" onclick="location='main.action'" type="button"/>
            </c:when>
            <c:otherwise>
                <ui:button message="form.cancel" onclick="location='view.action?id=${id}'"
                           type="button"/>
            </c:otherwise>
        </c:choose>
    </div>
</s:form>
