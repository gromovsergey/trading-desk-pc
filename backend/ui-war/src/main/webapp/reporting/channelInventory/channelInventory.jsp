<%@ page import="java.util.Collections" %>
<%@ page import="static com.foros.session.reporting.channelInventoryForecast.ChannelInventoryForecastReportParameters.ChannelFilter" %>
<%@ page import="static com.foros.session.reporting.channelInventoryForecast.ChannelInventoryForecastReportParameters.DateRangeFilter" %>
<%@ page contentType="text/html" %>
<%@ page pageEncoding="UTF-8" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>

<s:set var="canSelectChannel" value="channelId == null"/>
<s:set var="canSelectAccount" value="isInternal() && accountId == null && channelId == null"/>

<script type="text/javascript">
    function prepareChannels() {
        $('#selected_channelIds_id').empty();

        var channelFilter = $('#channelFilter').val();
        if (channelFilter == 'IDS') {
            $('#channelRow').show();
            var accountId = $('#accountId').val();
            UI.Data.Options.fill(
                    'channelsByAccountAndStatus',
                    'available_channelIds_id, alloptions_channelIds_id',
                    updateChannels, {
                        accountPair: accountId,
                        searchStatus : 'LIVE'
                    }, []
            );
        } else {
            $('#channelRow').hide();
        }
    }

    function updateChannels(response, selectId, additionalOptionMessages) {
        UI.Data.Options._update(response, selectId, additionalOptionMessages);
    }

    $().ready(function() {
        $('#accountId').change(prepareChannels);
        $('#channelFilter').change(prepareChannels);

        <s:if test="canSelectChannel">
            prepareChannels();
        </s:if>

    });

</script>

<ui:pageHeadingByTitle/>

<s:form id="channelInventoryForm" action="run" method="post" target="_blank">
    <s:if test="%{targetCurrencyCode != null}">
        <s:hidden name="targetCurrencyCode"/>
    </s:if>
    <ui:section titleKey="report.input.field.filter">
        <ui:fieldGroup>
            <ui:field labelKey="report.input.field.dateRange" labelForId="dateRange">
                <c:set var="dateRangeValues" value="<%=DateRangeFilter.values()%>"/>
                <s:select id="dateRange"
                          name="dateRange"
                          list="#attr.dateRangeValues"
                          listValue="getText(key)"
                />
            </ui:field>

            <s:if test="canSelectAccount">
                <ui:field labelKey="report.input.field.channelCreatorAccount" labelForId="accountId">
                    <select id="accountId" name="accountId" class="middleLengthText">
                        <c:forEach var="account" items="${accounts}">
                            <c:choose>
                                <c:when test="${account.id == accountId}">
                                    <option value="${account.id}" selected="true"><c:out value="${account.name}"/></option>
                                </c:when>
                                <c:otherwise>
                                    <option value="${account.id}"><c:out value="${account.name}"/></option>
                                </c:otherwise>
                            </c:choose>
                        </c:forEach>
                    </select>
                </ui:field>
            </s:if>
            <s:else>
                <ui:simpleField labelKey="report.input.field.channelCreatorAccount" value="${ad:appendStatus(account.name, account.status)}"/>
                <s:hidden name="accountId" id="accountId"/>
            </s:else>

            <s:if test="canSelectChannel">
                <ui:field labelKey="form.filter" labelForId="channelFilter">
                    <c:set var="channelFilterValues" value="<%=ChannelFilter.values()%>"/>
                    <s:select id="channelFilter"
                              name="channelFilter"
                              list="#attr.channelFilterValues"
                              listValue="getText(key)"
                    />
                </ui:field>

                <ui:field id="channelRow" cssClass="hide" labelKey="report.input.field.channels" required="true">
                    <ui:optiontransfer
                            name="channelIds"
                            size="9"
                            cssClass="wide"
                            listKey="id"
                            listValue="name"
                            list="<%= Collections.emptyList() %>"
                            selList="<%= Collections.emptyList() %>"
                            selListKey="id"
                            selListValue="name"
                            titleKey="report.channels.available"
                            selTitleKey="report.channels.selected"
                            saveSorting="true"  />
                </ui:field>
            </s:if>
            <s:else>
                <ui:simpleField labelKey="report.input.field.channel" value="${channel.name}"/>
                <input type="hidden" name="channelIds" value="${channelId}"/>
            </s:else>

            <ui:field labelKey="report.input.field.creativeSizes" required="true">
                <ui:optiontransfer
                        name="creativeSizeIds"
                        size="9"
                        cssClass="wide"
                        listKey="id"
                        listValue="name"
                        list="${sizes}"
                        selList="<%= Collections.emptyList() %>"
                        selListKey="id"
                        selListValue="name"
                        titleKey="report.sizes.available"
                        selTitleKey="report.sizes.selected"
                        saveSorting="true"
                        />
            </ui:field>

            <ui:field id="detailLevel" labelKey="report.input.field.detailLevel" required="true" errors="detailLevel">
                <label>
                    <input type="radio" name="detailLevel" value="FULL" />
                    <fmt:message key="channel.inventoryForecast.fullDetailLevel"/>
                </label>
                <label>
                    <input type="radio" name="detailLevel" value="PERCENTILE" checked="checked" />
                    <fmt:message key="channel.inventoryForecast.percentileDetailLevel"/>&nbsp;
                    <s:textfield name="percentile" id="detailLevelInput" cssClass="smallLengthText" maxlength="3" value="80" />%
                </label>
            </ui:field>

            <ui:field cssClass="withButton">
                <ui:button id="submitButton" message="channel.inventoryForecast.forecast"/>
            </ui:field>

        </ui:fieldGroup>
    </ui:section>
</s:form>