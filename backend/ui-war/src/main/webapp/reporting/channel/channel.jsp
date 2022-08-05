<%@ page contentType="text/html" %>
<%@ page pageEncoding="UTF-8" %>
<%@page import="java.util.Collections"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/struts-tags" prefix="s"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ui"%>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>

<script type="text/javascript">

    var channelReportUniquesMetric = ['report.output.field.totalUniques', 'report.output.field.activeDailyUniques'];
    var channelReportAdservingMetric = [
        'report.output.field.opporServeImps.long'
        ,'report.output.field.opporServeUniques.long'
        <c:if test="${ad:isInternal()}">
            ,'report.output.field.opporServeECPM.long'
            ,'report.output.field.opporServeValue.long'
        </c:if>
        ,'report.output.field.servedImps'
        ,'report.output.field.servedClicks'
        ,'report.output.field.servedCTR'
        ,'report.output.field.servedUniques'
        <c:if test="${ad:isInternal()}">
            ,'report.output.field.servedECPM'
            ,'report.output.field.servedValue'
        </c:if>
        ,'report.output.field.notServedFOROSImps.long'
        ,'report.output.field.notServedFOROSUniques.long'
        <c:if test="${ad:isInternal()}">
            ,'report.output.field.notServedFOROSECPM.long'
            ,'report.output.field.notServedFOROSValue.long'
        </c:if>
        ,'report.output.field.notServedNoFOROSImps.long'
        ,'report.output.field.notServedNoFOROSUniques.long'
        <c:if test="${ad:isInternal()}">
            ,'report.output.field.notServedNoFOROSECPM.long'
            ,'report.output.field.notServedNoFOROSValue.long'
        </c:if>
    ];
    var channelReportTriggersMetric = ['report.output.field.matchedUrl', 'report.output.field.searchKeywords.long', 'report.output.field.matchedKeywords', 'report.output.field.matchedUrlKeywords', 'report.output.field.totalMatch'];

    function getChannelId(value){
        if (value) {
            var pos = value.indexOf("_");
            var channelId = value.substr(0, pos);
            return channelId;
        } else {
            return null;
        }
    }

    function reloadAvailableMetric(channelId){
        UI.Data.get('reportAvailableColumns', { channelId : channelId }, parseAvailableColumn, null, {waitHolder: $('#columnsOT')});
    }

    function checkPresets(){
        var ava_metric_list = $('#AvailableMetric')[0];
        var sel_metric_list = $('#SelectedMetric')[0];
        var tmp = $.makeArray(ava_metric_list.options).concat($.makeArray(sel_metric_list.options));
        var all_metric = $.map(tmp, function(n){ return n.value; });

        var uniquesEnabled = channelReportUniquesMetric.filter(function(n) { return all_metric.indexOf(n) != -1 }).length != 0;
        var adservingEnabled = channelReportAdservingMetric.filter(function(n) { return all_metric.indexOf(n) != -1 }).length != 0;
        var triggerEnabled = channelReportTriggersMetric.filter(function(n) { return all_metric.indexOf(n) != -1 }).length != 0;

        $('#uniquesDetailLevel').removeAttr('disabled');
        $('#adServingDetailLevel').removeAttr('disabled');
        $('#triggerDetailLevel').removeAttr('disabled');

        if (!uniquesEnabled){
            if ($('#uniquesDetailLevel').prop('checked')){
                checkFirstAvailable(uniquesEnabled, adservingEnabled, triggerEnabled);
            }
            $('#uniquesDetailLevel').attr('disabled', 'disabled');
        }

        if (!adservingEnabled){
            if ($('#adServingDetailLevel').prop('checked')){
                checkFirstAvailable(uniquesEnabled, adservingEnabled, triggerEnabled);
            }
            $('#adServingDetailLevel').attr('disabled', 'disabled');
        }

        if (!triggerEnabled){
            if ($('#triggerDetailLevel').prop('checked')){
                checkFirstAvailable(uniquesEnabled, adservingEnabled, triggerEnabled);
            }
            $('#triggerDetailLevel').attr('disabled', 'disabled');
        }

        presetChannelReportColumns($('[name=reportName]:checked').val());
    }

    function checkFirstAvailable(uniquesEnabled, adservingEnabled, triggerEnabled){
        if (uniquesEnabled) {
            $('#uniquesDetailLevel').prop({checked : true});
            return;
        }
        if (adservingEnabled) {
            $('#adServingDetailLevel').prop({checked : true});
            return;
        }
        if (triggerEnabled) {
            $('#triggerDetailLevel').prop({checked : true});
            return;
        }
        $('#customDetailLevel').prop({checked : true});
    }

    $().ready(function(){
        $("input[name=reportName]").click(function(){
            if ("channel.custom" != this.value){
                checkPresets();
            }
        });

        $('#accountId').change(function(){
            $('#channelPair').extAutocomplete('clear');
            $('#channelId').val('');
            removeAvailableColumns();
            checkPresets();
            showColumns();
        });
        $('#channelForm').submit(function() {
            <s:if test="%{canSelectChannel()}">
                $('#channelId').val(getChannelId($('#channelPair').getComboVal()));
            </s:if>

            var frm = $('#channelForm')[0];
            if(!frm.metricCols.length) {
                alert($.localize("report.invalid.metrics.columns"));
                return false;
            }

            $('option', frm.metricCols).each(function(){
                this.selected = true;
            });

            return true;
        });

        reloadAvailableMetric($('#channelId').val());
    });

    function presetChannelReportColumns(detail) {
        var availableMetric = [];
        var selectedMetric = [];

        if (detail == 'channel.uniques') {
            selectedMetric = selectedMetric.concat(channelReportUniquesMetric);
            availableMetric = availableMetric.concat(channelReportAdservingMetric).concat(channelReportTriggersMetric);
        }
        else if (detail == 'channel.triggers') {
            selectedMetric = selectedMetric.concat(channelReportTriggersMetric);
            availableMetric = availableMetric.concat(channelReportUniquesMetric).concat(channelReportAdservingMetric);
        }
        else if (detail == 'channel.adserving') {
            selectedMetric = selectedMetric.concat(channelReportAdservingMetric);
            availableMetric = availableMetric.concat(channelReportUniquesMetric).concat(channelReportTriggersMetric);
        }
        else if (detail == 'channel.custom') {
            return;
        }

        moveMetric('SelectedMetric', 'AvailableMetric', availableMetric);
        moveMetric('AvailableMetric', 'SelectedMetric', selectedMetric);

        showColumns();
        $('#submitButton').prop({disabled : false});
    }

    function moveMetric(idFrom, idTo, metricIds) {
        $('#' + idFrom).find('option').each(function(){
            if ( metricIds.indexOf($(this).attr('value')) != -1 ) {
                $(this).prop('selected', true);
            }
        });

        UI.Optiontransfer.moveSelectedOptions(document.getElementById(idFrom),
                document.getElementById(idTo),
                document.getElementById('alloptions_metricCols_id'),
                true,
                false,
                function(){},
                []);
    }

    function showColumns(){
        var sel_output_list = $('#SelectedOutput')[0];
        var sel_metric_list = $('#SelectedMetric')[0];
        var tmp = $.makeArray(sel_output_list.options).concat($.makeArray(sel_metric_list.options));
        var arr = $.map(tmp, function(n){ return n.value.replace(".long", ""); });

        UI.Data.get('reportColumns', {selectId:'', reportColumns:arr}, parseRespColumn, null, {waitHolder: $('#columnsOT')});
    }

    function parseRespColumn(respXML){ // creates preview-table cols, based on 'reportColumns' tag in respXML
        var reportColumns = $('reportColumns', respXML);
        if (!reportColumns.length) return;

        var mytable = $('#nameColumn')[0];
        mytable.deleteTHead();
        var newTHeadRow = $(mytable.createTHead().insertRow(0));

        reportColumns.children().each(function(){
            addCellToRow(newTHeadRow, $(this).attr('value'));
        });
    }

    function parseAvailableColumn(respXML){
        removeAvailableColumns();

        var reportColumns = $('reportColumns', respXML);
        if (reportColumns.length > 0) {
            var targetSel = document.getElementById('AvailableMetric');
            var targetSelAll = document.getElementById('alloptions_metricCols_id');
            reportColumns.children().each(function(){
                targetSel.options[targetSel.options.length] = new Option($(this).attr('value'), $(this).attr('key'), false, false);
                targetSelAll.options[targetSelAll.options.length] = new Option($(this).attr('value'), $(this).attr('key'), false, false);
            });
        }

        $('#uniquesDetailLevel').prop({checked : true});
        checkPresets();
    }

    function removeAvailableColumns() {
        $('#AvailableMetric > option').remove();
        $('#SelectedMetric > option').remove();
        $('#alloptions_metricCols_id > option').remove();
    }

    function addCellToRow(row, text){
        $('<th>').appendTo(row)
                .attr({id : 'cell' + text.replace(/\s/g, '')})
                .html(text);
    }

</script>

<ui:pageHeadingByTitle/>
<s:form  id="channelForm" action="run" method="post" target="_blank">
    <%@include file="../enableDoubleSubmit.jsp"%>
    <input type="hidden" name="reportName" value="channel"/>
    <ui:section titleKey="form.filter">
        <ui:fieldGroup>
            <ui:field labelKey="report.input.field.dateRange" labelForId="fastChangeId">
                <ui:daterange options="Y T WTD MTD QTD LW LM R" fastChangeId="MTD" currentPos="1" maxDate="+1d" validateRange="true" fromDateFieldName="dateRange.begin" toDateFieldName="dateRange.end"/>
            </ui:field>

            <s:if test="%{canSelectAccount()}">
                <ui:field labelKey="report.input.field.objectType.Account" labelForId="accountId">
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
            <s:elseif test="%{getAccountId() != null}">
                <c:set var="_accountResource" value="report.input.field.channelCreatorAccount"/>
                <ui:simpleField labelKey="${_accountResource}" value="${accountName}"/>
                <s:hidden id="accountId" name="accountId"/>
            </s:elseif>
            <s:if test="%{canSelectChannel()}">
                <ui:field labelKey="report.input.field.channel" cssClass="valignFix" labelForId="channelPair">
                            <input type="hidden" name="channelId" id="channelId"/>
                            <ui:autocomplete
                                id="channelPair"
                                source="/xml/channelsByAccountForChannelReport.action"
                                requestDataCb="Autocomplete.channelPair.getChannelData"
                                onSelect="Autocomplete.channelPair.onSelectChannel"
                                cssClass="middleLengthText">
                                    <script type="text/javascript">
                                        Autocomplete.channelPair.getChannelData = function(query){
                                            return $.extend({accountId:$('#accountId').val()}, {query : query});
                                        }
                                        
                                        Autocomplete.channelPair.onSelectChannel = function(event, data){
                                            reloadAvailableMetric(getChannelId(data.item.value));
                                        }
                                    </script>
                            </ui:autocomplete>
                </ui:field>
            </s:if>
            <s:else>
                <input type="hidden" id="channelPair" value="_${channel.channelType}"/>
                <input type="hidden" id="channelId" name="channelId" value="${channel.id}"/>
                <ui:simpleField labelKey="report.input.field.channel" value="${channel.name}"/>
            </s:else>
    
            <ui:field cssClass="valignFix" labelKey="report.input.field.detailLevel">
                <div class="nomargin">
                    <label class="withInput">
                        <input type="radio" id="uniquesDetailLevel" name="reportName" value="channel.uniques"   checked>
                        <fmt:message key="report.input.field.detailLevel.uniques"/>
                    </label>
                    <label class="withInput">
                        <input type="radio" id="adServingDetailLevel" name="reportName" value="channel.adserving" >
                        <fmt:message key="report.input.field.detailLevel.adserving"/>
                    </label>
                    <label class="withInput">
                        <input type="radio" id="triggerDetailLevel" name="reportName" value="channel.triggers" >
                        <fmt:message key="report.input.field.detailLevel.triggers"/>
                    </label>
                    <label class="withInput">
                        <input type="radio" id="customDetailLevel" name="reportName" value="channel.custom" >
                        <fmt:message key="report.input.field.detailLevel.custom"/>
                    </label>
                </div>
            </ui:field>

            <ui:field id="columnsOT">
                <ui:optiontransfer
                    name="metricCols"
                    size="9"
                    cssClass="middleLengthText"
                    listKey="id"
                    listValue="name"
                    list="<%= Collections.emptyList() %>"
                    selList="<%= Collections.emptyList() %>"
                    id="AvailableMetric"
                    selId="SelectedMetric"
                    selListKey="id"
                    selListValue="name"
                    titleKey="report.availableColumns"
                    selTitleKey="report.selectedColumns"
                    onchange="$('#customDetailLevel').prop({checked : true}); showColumns();"
                    saveSorting="true"
                />
            </ui:field>

            <select name="outputCols" id="SelectedOutput" style="display:none;" multiple="multiple">
                <option selected="true">report.output.field.date</option>
            </select>
    
            <ui:field cssClass="valignFix" labelKey="report.previewOutputColumns">
                <table class="dataView" id="nameColumn"><thead><tr><th id="cellDate"><fmt:message key="report.input.field.date"/></th></tr></thead></table>
            </ui:field>
    
            <ui:field cssClass="withButton">
                <ui:button id="submitButton" message="report.button.runReport" disabled="true"/>
            </ui:field>
        </ui:fieldGroup>
    </ui:section>
 </s:form>