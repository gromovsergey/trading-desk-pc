<%@ page import="com.foros.session.reporting.advertiser.olap.OlapAdvertiserReportParameters" %>
<%@ page import="com.foros.session.reporting.advertiser.olap.OlapDetailLevel" %>
<%@ page contentType="text/html" %>
<%@ page pageEncoding="UTF-8" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>

<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="ad" uri="/ad/serverUI" %>

<tiles:importAttribute ignore="true" scope="request" name="isDisplay"/>

<script type="text/javascript">
    function checkSubtotals() {
        var groups = $('#unitOfTime').val() != '' ? 1 : 0;
        <c:forEach var="level" items="${reportState.subtotalLevels}"> {
        var checkList =
                <ad:commaWriter var="column" items="${level}" separator="+ ',' +">
                '#${fn:replace(column.nameKey, '.', '_')}'
                </ad:commaWriter>;
        if ($(checkList).filter(':checked').length > 0) {
            groups++;
        }
        }
        </c:forEach>

        if (groups >= 2) {
            $('#addSubtotals').show();
        } else {
            $('#addSubtotals').hide();
            $('#addSubtotalsfalse').prop({checked:true});
        }
    }

    function checkWalledGardenStatistics() {
    	if ($('#walledGardenFlag').val() == 'true'){
    		$('#fieldSplitWalledGardenStatistics').show();
    	} else {
    		$('#fieldSplitWalledGardenStatistics').hide();
    	}
    }

    function redrawColumns() {
        $.ajax({
            type : 'POST',
            url : 'redrawColumns.action',
            data : $('#advertiserForm').serializeArray(),
            success : function(data) { $('#reportColumnsDiv').html(data) },
            waitHolder : null
        });
    }


    function applyCheckSubtotals() {
        $('[name=columns]').unbind('change').change(function() {
            redrawColumns();
            checkSubtotals();
        });
        $('[name=costAndRates]').unbind('change').change(function() {
            redrawColumns();
        });
        $('[name=splitWalledGardenStatistics]').unbind('change').change(function() {
            redrawColumns();
        });
    }
    
</script>

<ui:persistent>
    <div class="logicalBlock last" id="reportColumnsDiv">
        <%@include file="reportColumns.jsp"%>
    </div>
</ui:persistent>

<div class="logicalBlock">
    <tiles:importAttribute name="columnsPage"/>
    <jsp:include page="${columnsPage}"/>
</div>

<ui:fieldGroup>
    <ui:field labelKey="report.input.field.addSubtotals" id="addSubtotals">
        <s:radio id="addSubtotals"
                 name="addSubtotals"
                 list="#{true : getText('yes'), false : getText('no') }"/>
    </ui:field>
    <c:if test="${not requestContexts.advertiserContext.set}">
        <ui:field labelKey="report.input.field.costAndRatesDisplayAs">
            <c:set var="costAndRatesValues" value="<%=OlapAdvertiserReportParameters.CostAndRates.values()%>"/>
            <s:radio id="costAndRates"
                     name="costAndRates"
                     list="#attr.costAndRatesValues"
                     listValue="getText('report.input.field.costAndRatesDisplayAs.' + name())"/>
        </ui:field>
    </c:if>

    <c:if test="${isDisplay}">
        <ui:field id="fieldSplitWalledGardenStatistics" labelKey="report.input.field.splitWalledGardenStatistics">
            <input type="hidden" id="walledGardenFlag" name="walledGardenFlag" value="${agencyWalledGarden}"/>
            <s:radio id="splitWalledGardenStatistics"
                name="splitWalledGardenStatistics"
                list="#{true : getText('yes'), false : getText('no') }"/>
        </ui:field>
    </c:if>

</ui:fieldGroup>
<script type="text/javascript">
    applyCheckSubtotals();
    checkSubtotals();
    checkWalledGardenStatistics();
    // To disable unique users columns in some cases
    $('#report_output_field_country, #report_output_field_channelTarget, #report_output_field_creativeSize').change(function() {
        if ($("[name='reportType']:checked").val() == 'Campaign') {
            updateColumns(false);
        }
    });
</script>
