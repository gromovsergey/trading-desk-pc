<%@ page import="com.foros.session.reporting.advertiser.olap.OlapAdvertiserReportParameters" %>
<%@ page contentType="text/html" %>
<%@ page pageEncoding="UTF-8" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="ad" uri="/ad/serverUI" %>

<s:set var="advertiserContext" value="#attr.requestContexts.advertiserContext"/>

<script type="text/javascript">
    function changeReportType() {
        var dataContainer = $('#manageColumns .content:eq(0)');

        $.ajax({
            type : 'POST',
            url : 'changeReportType.action',
            data : $('#advertiserForm').serializeArray(),
            success : function(data) {
                dataContainer.html(data)
            }
        });
    }

    function updateColumns(useRecommended) {
        var dataContainer = $('#manageColumns .content:eq(0)');
        var data = $('#advertiserForm').serializeArray();
        data.push({name: 'useRecommended', value: useRecommended});
        $.ajax({
            type : 'POST',
            url : 'changeDateTime.action',
            data : data,
            success : function(data) {
                dataContainer.html(data)
            }
        });
    }

    $(function(){
        var jqAdvertisers;
        $('[name=reportType]').on('change', changeReportType);
        $('[name=unitOfTime], #dateRange_begin, #dateRange_end').on('change', function(){updateColumns(true)});
        $('#advertiserForm').on('submit', function(e){
            if ( $('#treeRootCheckboxTreeFilter').prop('checked') ) {
                jqAdvertisers = $('input[name="advertiserIds"]').not('disabled').prop('disabled', true);
            }
        });
        $(window).on('focus', function(){
            if (jqAdvertisers && jqAdvertisers.length) {
                jqAdvertisers.prop('disabled', false);
                jqAdvertisers = null;
            }
        })
    });
</script>

<ui:pageHeadingByTitle/>

<s:form id="advertiserForm" action="run" method="post" target="_blank">
    <s:hidden name="switchContext"/>
    <%@include file="../enableDoubleSubmit.jsp"%>
    <ui:section titleKey="report.input.field.reportType">
        <ui:fieldGroup>
            <ui:field>
                <s:radio list="detailLevels"
                         name="reportType"
                         listValue="%{getText('report.input.field.reportType.' + name())}"/>
            </ui:field>
        </ui:fieldGroup>
    </ui:section>
    <ui:section titleKey="report.input.field.reportPeriod">
        <ui:fieldGroup>
            <ui:field labelKey="report.input.field.dateRange">
                <ui:daterange
                        fromDateFieldName="dateRange.begin"
                        toDateFieldName="dateRange.end"
                        options="Y T WTD MTD QTD YTD LW LM LQ LY R"
                        fastChangeId="Y"
                        currentPos="1"
                        maxDate="+1d"
                        validateRange="true"
                        onChange="updateColumns(true);"/>
            </ui:field>

            <c:set var="unitOfTimeValues" value="<%=OlapAdvertiserReportParameters.UnitOfTime.values()%>"/>
            <ui:field labelKey="report.input.field.unitOfTime">
                <s:select list="#attr.unitOfTimeValues"
                          name="unitOfTime"
                          id="unitOfTime"
                          class="middleLengthText"
                          listValue="%{getText(unitName)}"
                          headerKey=""
                          headerValue="%{getText('report.output.field.summary')}"/>
            </ui:field>
        </ui:fieldGroup>
    </ui:section>

    <ui:header styleClass="level2">
        <h2><fmt:message key="report.input.field.advancedSettings"/></h2>
    </ui:header>

    <ui:collapsible id="manageColumns" labelKey="report.input.manageColumns">
        <%@ include file="manageColumns.jsp"%>
    </ui:collapsible>

    <s:include value="olapAdvertiserFilter.jsp"/>

    <ui:button message="report.button.runReport"/>

</s:form>
