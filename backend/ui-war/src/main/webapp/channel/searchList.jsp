<%@ page import="com.foros.model.channel.Channel"%>
<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display"%>
<%@ taglib uri="/ad/serverUI" prefix="ad"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ui"%>
<%@ taglib uri="/struts-tags" prefix="s"%>

<script type="text/javascript">
    function submitChannelsExportForm(format, channelType) {
        $('.errors').remove();
        $(document).off('submit.preventDoubleSubmit');
        $('#channelTypeHidden').val(channelType);
        $('#mainForm').attr("action", "export.action?format=" + format).attr("method","post").submit();
    }

    function toggleAllChannels(header) {
            $('[name=selectedExpressionChannelIds]').prop({checked : header.checked});
            $('[name=selectedBehavioralChannelIds]').prop({checked : header.checked});
    }
    
    function checkChannels(){
        if ($('[name=selectedBehavioralChannelIds]:checked').length == 0
                && $('[name=selectedExpressionChannelIds]:checked').length == 0){
            alert('${ad:formatMessage("channel.export.nothingSelected")}');
            return false;
        }

        if ($('[name=selectedBehavioralChannelIds]:checked').length == 0){
            $('#B_CSV').hide();
            $('#B_TAB').hide();
            $('#B_XLSX').hide();
        } else {
            $('#B_CSV').show();
            $('#B_TAB').show();
            $('#B_XLSX').show();
        }
        
        if ($('[name=selectedExpressionChannelIds]:checked').length == 0){
            $('#E_CSV').hide();
            $('#E_TAB').hide();
            $('#E_XLSX').hide();
        } else {
            $('#E_CSV').show();
            $('#E_TAB').show();
            $('#E_XLSX').show();
        }
        return true;
    }
</script>
<s:actionerror/>
<s:if test="channels != null">
    <form id="mainForm">
        <s:hidden name="accountId" />
        <input type="hidden" name="channelTypeHidden" id="channelTypeHidden" >
        <table class="dataViewSection">
            <tr class="controlsZone">
                <td>
                    <table class="grouping">
                        <tr>
                            <td class="withButtons"><c:if test="${ad:isPermitted('AdvertisingChannel.export', account)}">
                                
                                <span style="margin-right: 10px">
                                    <ui:button id="export" message="channel.export" type="link"/>
                                    <ul class="hide">
                                        <li id="B_CSV"><a href="#" onclick="submitChannelsExportForm('CSV', 'BEHAVIORAL'); return false;">
                                            <fmt:message key="channel.export.behavioral.CSV"/>
                                        </a></li>
                                        <li id="B_TAB"><a href="#" onclick="submitChannelsExportForm('TAB', 'BEHAVIORAL'); return false;">
                                            <fmt:message key="channel.export.behavioral.TAB"/>
                                        </a></li>
                                        <li id="B_XLSX"><a href="#" onclick="submitChannelsExportForm('XLSX', 'BEHAVIORAL'); return false;">
                                            <fmt:message key="channel.export.behavioral.XLSX"/>
                                        </a></li>
                                        <li id="E_CSV"><a href="#" onclick="submitChannelsExportForm('CSV', 'EXPRESSION'); return false;">
                                            <fmt:message key="channel.export.expression.CSV"/>
                                        </a></li>
                                        <li id="E_TAB"><a href="#" onclick="submitChannelsExportForm('TAB', 'EXPRESSION'); return false;">
                                            <fmt:message key="channel.export.expression.TAB"/>
                                        </a></li>
                                        <li id="E_XLSX"><a href="#" onclick="submitChannelsExportForm('XLSX', 'EXPRESSION'); return false;">
                                            <fmt:message key="channel.export.expression.XLSX"/>
                                        </a></li>
                                    </ul>
                                    <script type="text/javascript">
                                        $(function(){
                                            $('#export').menubutton({
                                                beforeclick: function(){
                                                    return checkChannels();
                                                }
                                            })
                                        });
                                    </script>
                                    </span>
                                    <c:set var="exportEnabled" value="true" />
                                </c:if> <c:if test="${ad:isPermitted('AdvertisingChannel.upload', account)}">
                                    <ui:button message="channel.upload.bulkUpload"
                                        href="upload/main.action${ad:accountParam('?accountId',account.id)}" />
                                </c:if>
                            </td>
                        </tr>
                    </table>
                </td>
            </tr>
            <tr class="bodyZone">
                <td>
                    <div class="logicalBlock" id="campaignDashboardDiv">
                        <display:table name="channels" class="dataView" id="channel">
                            <display:setProperty name="basic.msg.empty_list">
                                <div class="wrapper">
                                    <fmt:message key="nothing.found.to.display" />
                                </div>
                            </display:setProperty>
                            <c:set var="behavioralType" value="<%=Channel.CHANNEL_TYPE_BEHAVIORAL%>" scope="page" />
                            <c:set var="expressionType" value="<%=Channel.CHANNEL_TYPE_EXPRESSION%>" scope="page" />
                            <c:set var="audienceType" value="<%=Channel.CHANNEL_TYPE_AUDIENCE%>" scope="page" />

                            <c:if test="${ad:isInternal()}">
                                <c:if test="${contextName != 'global.menu.advertisers' && contextName != 'global.menu.cmps'}">
                                    <display:column titleKey="channel.search.account">
                                        <ui:accountLink id="${channel.accountId}" role="${channel.accountRole}" name="${channel.accountName}"
                                            displayStatus="${channel.accountDisplayStatus}" />
                                    </display:column>
                                </c:if>
                            </c:if>

                            <c:if test="${exportEnabled}">
                                <display:column
                                    title="<input type='checkbox' onclick='toggleAllChannels(this)'/>"
                                    style="text-align:center;width:24px;">
                                    <c:choose>
                                        <c:when test="${channel.channelType == 'B'}">
                                            <input type="checkbox" name="selectedBehavioralChannelIds" value="${channel.id}" />
                                        </c:when>
                                        <c:when test="${channel.channelType == 'E'}">
                                            <input type="checkbox" name="selectedExpressionChannelIds" value="${channel.id}" />
                                        </c:when>
                                    </c:choose>
                                </display:column>
                            </c:if>

                            <display:column titleKey="channel.search.channel">
                                <ui:displayStatus displayStatus="${channel.displayStatus}">
                                    <a href="view.action?id=${channel.id}"><c:out value="${channel.name}" /></a>
                                </ui:displayStatus>
                            </display:column>
                            <c:if test="${ad:isInternal()}">
                                <c:if
                                    test="${contextName != 'global.menu.advertisers' && contextName != 'global.menu.cmps'  || account.international}">
                                    <display:column titleKey="channel.search.country" class="fixed">
                                        <ad:resolveGlobal resource="country" id="${channel.country}" />
                                    </display:column>
                                </c:if>
                            </c:if>
                            <display:column titleKey="channel.search.type" style="width:150px;">
                                <c:choose>
                                    <c:when test="${channel.channelType == behavioralType}">
                                        <fmt:message key="channel.type.channel" />
                                    </c:when>
                                    <c:when test="${channel.channelType == expressionType}">
                                        <fmt:message key="channel.type.expression" />
                                    </c:when>
                                    <c:when test="${channel.channelType == audienceType}">
                                        <fmt:message key="channel.type.audience" />
                                    </c:when>
                                    <c:otherwise>
                                        Unknown
                                    </c:otherwise>
                                </c:choose>
                            </display:column>
                            <c:if test="${!ad:isAgency() && !ad:isAdvertiser()}">
                                <c:if test="${contextName != 'global.menu.advertisers'}">
                                    <display:column titleKey="channel.search.visibility" style="width:150px;">
                                        <fmt:message key="channel.visibility.${channel.visibility}" />
                                    </display:column>
                                </c:if>
                            </c:if>
                        </display:table>
                    </div>
                </td>
            </tr>
        </table>
    </form>
</s:if>
