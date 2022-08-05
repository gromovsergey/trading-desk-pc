<%@ page contentType="text/html"%>
<%@ page pageEncoding="UTF-8"%>
<%@ page
    import="com.foros.session.reporting.conversionPixels.ConversionPixelsReportParameters"%>

<%@ taglib prefix="ui" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="/ad/serverUI" prefix="ad"%>
<ad:requestContext var="advertiserContext" />
<c:set var="isAdvertiser"
    value="${advertiserContext.set && advertiserContext.advertiserSet}" />

<script type="text/javascript">
    $().ready(function() {
        showTreeFilter();
    });
</script>

<ui:pageHeadingByTitle />

<s:form id="conversionPixelsForm" action="run" method="post"
    target="_blank">
    <%@include file="../enableDoubleSubmit.jsp"%>
    <input type="hidden" id="accountId" name="accountId" value=${accountId}>
    <ui:section titleKey="report.input.field.reportPeriod">
        <ui:fieldGroup>
        
            <ui:field labelKey="report.input.field.dateRange">
                <ui:daterange
                        timeZoneAccountId="${advertiserContext.accountId}"
                        fromDateFieldName="dateRange.begin"
                        toDateFieldName="dateRange.end"
                        options="Y T WTD MTD QTD YTD LW LM LQ LY R"
                        fastChangeId="Y"
                        currentPos="1"
                        maxDate="+1d"
                        validateRange="true" />
            </ui:field>

            <ui:field labelKey="report.input.field.unitOfTime">
                <s:select
                    list="#{'false':getText('report.output.field.summary'), 'true':getText('report.output.field.date')}"
                    name="showResultsByDay" id="showResultsByDay"
                    class="middleLengthText" />
            </ui:field>
        </ui:fieldGroup>
    </ui:section>


    <ui:section titleKey="report.input.field.advancedSettings">
        <ui:fieldGroup>
            <ui:field id="treeFilterField">
                <%@ include file="treeFilter.jsp"%>
            </ui:field>
        </ui:fieldGroup>
    </ui:section>

    <ui:fieldGroup>
        <ui:field cssClass="withButton">
            <ui:button id="submitButton"
                message="report.button.runReport" />
        </ui:field>
    </ui:fieldGroup>



</s:form>
