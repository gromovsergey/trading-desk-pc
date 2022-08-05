<%@ page contentType="text/html" %>
<%@ page pageEncoding="UTF-8" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<ui:pageHeadingByTitle/>

<s:form id="campaignOverviewForm" action="run" method="post" target="_blank">
    <ui:section titleKey="form.filter">
        <ui:fieldGroup>
            <input type="hidden" id="accountId" name="accountId" value=${accountId}>

            <ui:field labelKey="report.input.field.dateRange">
                <ui:daterange
                        fromDateFieldName="dateRange.begin"
                        toDateFieldName="dateRange.end"
                        options="MTD QTD LM LQ R"
                        currentPos="1"
                        maxDate="+1d"
                        fastChangeId="MTD"/>
            </ui:field>

            <ui:field labelKey="report.input.field.segment">
                <table class="fieldAndAccessories">
                    <tr>
                        <td class="withField"><label class="withInput"> <s:checkbox value="true"
                                    id="segmentByVertical"
                                    name="segmentByVertical" /> <fmt:message
                                    key="report.input.field.segmentByVertical" />
                        </label></td>
                        <td class="withField"><label class="withInput"> <s:checkbox value="true"
                                    id="segmentByProduct"
                                    name="segmentByProduct" /> <fmt:message
                                    key="report.input.field.segmentByProduct" />
                        </label></td>
                    </tr>
                </table>
            </ui:field>


            <ui:field cssClass="withButton">
                <ui:button message="report.button.runReport"/>
            </ui:field>

        </ui:fieldGroup>
    </ui:section>
</s:form>
