<%@ page contentType="text/html" %>
<%@ page pageEncoding="UTF-8" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<ui:pageHeadingByTitle/>

<s:form id="pubAdvertisingForm" action="run" method="post" target="_blank">
    <ui:section titleKey="form.filter">
        <ui:fieldGroup>
            <input type="hidden" id="accountId" name="accountId" value=${accountId}>

            <ui:field labelKey="report.input.field.dateRange">
                <ui:daterange
                        fromDateFieldName="dateRange.begin"
                        toDateFieldName="dateRange.end"
                        options="Y T WTD MTD QTD YTD LW LM LQ LY R"
                        currentPos="1"
                        maxDate="+1d"
                        fastChangeId="Y"/>
            </ui:field>

            <ui:field labelKey="report.outputFormat">
                <div class="nomargin">
                    <label class="narrowSet">
                        <input type="radio" name="format" value="" checked="checked"/>
                        <fmt:message key="report.outputFormat.HTML"/>
                    </label>
                    <label class="narrowSet">
                        <input type="radio" name="format" value="EXCEL">
                        <fmt:message key="report.outputFormat.Excel"/>
                    </label>
                    <label class="narrowSet">
                        <input type="radio" name="format" value="CSV">
                        <fmt:message key="report.outputFormat.CSV"/>
                    </label>
                </div>
            </ui:field>

            <ui:field cssClass="withButton">
                <ui:button message="report.button.runReport"/>
            </ui:field>

        </ui:fieldGroup>
    </ui:section>
</s:form>
