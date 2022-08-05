<%@ page import="com.foros.model.channel.Channel" %>
<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ui" %>

<script type="text/javascript">
    function submitChannelsExportForm(format, channelType) {
        $('.errors').remove();

        var searchType = $('#channelType').val();
        if (searchType != channelType && searchType != "" || $('#visibility').val() == 'CMP') {
            alert("<fmt:message key="channel.export.emptyList"/>");
            return false;
        }

        if (channelType == "B") {
            $('#channelTypeHidden').val('BEHAVIORAL');
        } else if (channelType == "E") {
            $('#channelTypeHidden').val('EXPRESSION');
        }

        $(document).off('submit.preventDoubleSubmit');
        $('#channelsForm').attr("action", "exportInternal.action?format=" + format).attr("method", "post").submit();
    }
</script>

<c:choose>
<c:when test="${channels != null && not empty channels}">
    <form id="channelsForm">
        <s:hidden name="resubmitRequired" value="true"/>
        <s:hidden name="name"/>
        <s:hidden name="accountId"/>
        <s:hidden name="channelType"/>
        <s:hidden name="channelTypeHidden"/>
        <s:hidden name="phrase"/>
        <s:hidden name="countryCode"/>
        <s:hidden name="visibility"/>
        <s:hidden name="status"/>
        <s:hidden name="testOption"/>

        <table class="dataViewSection">
            <tr class="controlsZone">
                <td>
                    <table class="grouping">
                        <tr>
                            <td class="withButtons">

                            <c:if test="${ad:isPermitted0('AdvertisingChannel.view')}">
                                <span style="margin-right: 10px">
                                    <ui:button id="export" message="channel.export" type="link"/>
                                    <ul class="hide">
                                        <li id="B_CSV"><a href="#" onclick="submitChannelsExportForm('CSV', 'B'); return false;">
                                            <fmt:message key="channel.export.behavioral.CSV"/>
                                        </a></li>
                                        <li id="B_TAB"><a href="#" onclick="submitChannelsExportForm('TAB', 'B'); return false;">
                                            <fmt:message key="channel.export.behavioral.TAB"/>
                                        </a></li>
                                        <li id="B_XLSX"><a href="#" onclick="submitChannelsExportForm('XLSX', 'B'); return false;">
                                            <fmt:message key="channel.export.behavioral.XLSX"/>
                                        </a></li>
                                        <li id="E_CSV"><a href="#" onclick="submitChannelsExportForm('CSV', 'E'); return false;">
                                            <fmt:message key="channel.export.expression.CSV"/>
                                        </a></li>
                                        <li id="E_TAB"><a href="#" onclick="submitChannelsExportForm('TAB', 'E'); return false;">
                                            <fmt:message key="channel.export.expression.TAB"/>
                                        </a></li>
                                        <li id="E_XLSX"><a href="#" onclick="submitChannelsExportForm('XLSX', 'E'); return false;">
                                            <fmt:message key="channel.export.expression.XLSX"/>
                                        </a></li>
                                    </ul>
                                    <script type="text/javascript">
                                        $(function(){
                                            $('#export').menubutton({
                                                beforeclick: function(){
                                                    return true;
                                                }
                                            })
                                        });
                                    </script>
                                </span>
                            </c:if>
                            </td>
                        </tr>
                    </table>
                </td>
            </tr>
            <tr class="bodyZone">
                <td>
                    <ui:pages pageSize="${pageSize}"
                              total="${total}"
                              selectedNumber="${page}"
                              visiblePagesCount="10"
                              handler="goToPage"
                              displayHeader="true"/>

                    <display:table name="channels" class="dataView" id="channel">
                        <display:setProperty name="basic.msg.empty_list" >
                            <div class="wrapper">
                                <fmt:message key="nothing.found.to.display"/>
                            </div>
                        </display:setProperty>

                        <c:set var="behavioralType" value="<%=Channel.CHANNEL_TYPE_BEHAVIORAL%>" scope="page"/>
                        <c:set var="expressionType" value="<%=Channel.CHANNEL_TYPE_EXPRESSION%>" scope="page" />
                        <c:set var="audienceType" value="<%=Channel.CHANNEL_TYPE_AUDIENCE%>" scope="page" />

                        <display:column titleKey="channel.search.account">
                            <ui:accountLink id="${channel.accountId}" role="${channel.accountRole}" name="${channel.accountName}"
                                            displayStatus="${channel.accountDisplayStatus}" testFlag="${channel.accountTestFlag}"/>
                        </display:column>

                        <display:column titleKey="channel.search.channel">
                            <ui:displayStatus displayStatus="${channel.displayStatus}">
                                <a href="/admin/channel/view.action?id=${channel.id}"><c:out value="${channel.name}"/></a>
                            </ui:displayStatus>
                        </display:column>

                        <display:column titleKey="channel.search.country" class="fixed">
                            <ad:resolveGlobal resource="country" id="${channel.country}"/>
                        </display:column>

                        <display:column titleKey="channel.search.type"  style="width:150px;">
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

                        <display:column titleKey="channel.search.visibility"  style="width:150px;">
                            <fmt:message key="channel.visibility.${channel.visibility}"/>
                        </display:column>

                    </display:table>

                <ui:pages pageSize="${pageSize}"
                          total="${total}"
                          selectedNumber="${page}"
                          visiblePagesCount="10"
                          handler="goToPage"
                          displayHeader="true"/>
                </td>
            </tr>
        </table>
    </form>
</c:when>
<c:otherwise>
    <fmt:message key="nothing.found.to.display"/>
</c:otherwise>
</c:choose>
