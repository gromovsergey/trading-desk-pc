<%@ page contentType="text/html" %>
<%@ page pageEncoding="UTF-8" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>

<ad:requestContext var="advertiserContext" />
<c:set var="isAdvertiser"
    value="${advertiserContext.set && !advertiserContext.agencyContext}" />
<ui:pageHeadingByTitle/>
<s:form id="conversionsForm" action="run" method="post" target="_blank">
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
                        validateRange="true"/>
            </ui:field>

            <ui:field labelKey="report.input.field.unitOfTime">
                <s:select
                    list="#{'false':getText('report.output.field.summary'), 'true':getText('report.output.field.date')}"
                    name="showResultsByDay" id="showResultsByDay"
                    class="middleLengthText" />
            </ui:field>
            
            

        </ui:fieldGroup>
    </ui:section>

    <ui:header styleClass="level2">
        <h2><fmt:message key="report.input.field.advancedSettings"/></h2>
    </ui:header>

    <ui:collapsible id="manageColumns" labelKey="report.input.manageColumns">
        <ui:persistent>
            <div class="logicalBlock last" id="reportColumnsDiv">
                <%@include file="reportColumns.jsp"%>
            </div>
        </ui:persistent>

        <ui:fieldGroup>
            <ui:field labelKey="report.input.field.statisticColumns" id="statisticColumnsSection">
                <table class="fieldAndAccessories">
                    <tr>
                        <tiles:insertTemplate template="column.jsp">
                            <tiles:putAttribute name="column" value="impressions"/>
                        </tiles:insertTemplate>
                        <tiles:insertTemplate template="column.jsp">
                            <tiles:putAttribute name="column" value="clicks"/>
                        </tiles:insertTemplate>
                        <tiles:insertTemplate template="column.jsp">
                            <tiles:putAttribute name="column" value="CTR"/>
                        </tiles:insertTemplate>
                        <td class="withField"></td>
                    </tr>
                    <tr>
                        <tiles:insertTemplate template="column.jsp">
                            <tiles:putAttribute name="column" value="post_imp_conv"/>
                        </tiles:insertTemplate>
                        <tiles:insertTemplate template="column.jsp">
                            <tiles:putAttribute name="column" value="post_imp_cr"/>
                        </tiles:insertTemplate>
                        <tiles:insertTemplate template="column.jsp">
                            <tiles:putAttribute name="column" value="post_click_conv"/>
                        </tiles:insertTemplate>
                        <tiles:insertTemplate template="column.jsp">
                            <tiles:putAttribute name="column" value="post_click_cr"/>
                        </tiles:insertTemplate>
                    </tr>
                    <tr>
                        <tiles:insertTemplate template="column.jsp">
                            <tiles:putAttribute name="column" value="cost"/>
                        </tiles:insertTemplate>
                        <tiles:insertTemplate template="column.jsp">
                            <tiles:putAttribute name="column" value="revenue"/>
                        </tiles:insertTemplate>
                        <tiles:insertTemplate template="column.jsp">
                            <tiles:putAttribute name="column" value="roi"/>
                        </tiles:insertTemplate>
                    </tr>
                    <tr>
                        <tiles:insertTemplate template="column.jsp">
                            <tiles:putAttribute name="column" value="ttcImpressions"/>
                        </tiles:insertTemplate>
                        <tiles:insertTemplate template="column.jsp">
                            <tiles:putAttribute name="column" value="ttcClicks"/>
                        </tiles:insertTemplate>
                    </tr>
                    <tr>
                        <tiles:insertTemplate template="column.jsp">
                            <tiles:putAttribute name="column" value="postImp1"/>
                        </tiles:insertTemplate>
                        <tiles:insertTemplate template="column.jsp">
                            <tiles:putAttribute name="column" value="postImp2_7"/>
                        </tiles:insertTemplate>
                        <tiles:insertTemplate template="column.jsp">
                            <tiles:putAttribute name="column" value="postImp8_30"/>
                        </tiles:insertTemplate>
                        <td class="withField"></td>
                    </tr>
                    <tr>
                        <tiles:insertTemplate template="column.jsp">
                            <tiles:putAttribute name="column" value="postClick1"/>
                        </tiles:insertTemplate>
                        <tiles:insertTemplate template="column.jsp">
                            <tiles:putAttribute name="column" value="postClick2_7"/>
                        </tiles:insertTemplate>
                        <tiles:insertTemplate template="column.jsp">
                            <tiles:putAttribute name="column" value="postClick8_30"/>
                        </tiles:insertTemplate>
                        <td class="withField"></td>
                    </tr>
                </table>
            </ui:field>

            <ui:field/>
            <ui:field/>
            <ui:field labelKey="report.input.field.settingsColumns">
                <table class="fieldAndAccessories">
                    <tr>
                        <tiles:insertTemplate template="column.jsp">
                            <tiles:putAttribute name="column" value="advertiser"/>
                        </tiles:insertTemplate>
                        <tiles:insertTemplate template="column.jsp">
                            <tiles:putAttribute name="column" value="campaign"/>
                        </tiles:insertTemplate>
                        <tiles:insertTemplate template="column.jsp">
                            <tiles:putAttribute name="column" value="creativeGroup"/>
                        </tiles:insertTemplate>
                        <tiles:insertTemplate template="column.jsp">
                            <tiles:putAttribute name="column" value="channel"/>
                        </tiles:insertTemplate>
                        <tiles:insertTemplate template="column.jsp">
                            <tiles:putAttribute name="column" value="creative"/>
                        </tiles:insertTemplate>
                    </tr>
                    <tr>
                        <tiles:insertTemplate template="column.jsp">
                            <tiles:putAttribute name="column" value="conversion"/>
                        </tiles:insertTemplate>
                        <tiles:insertTemplate template="column.jsp">
                            <tiles:putAttribute name="column" value="conversionCategory"/>
                        </tiles:insertTemplate>
                        <tiles:insertTemplate template="column.jsp">
                            <tiles:putAttribute name="column" value="orderID"/>
                        </tiles:insertTemplate>
                        <s:if test="showPublisher">
                            <tiles:insertTemplate template="column.jsp">
                                <tiles:putAttribute name="column" value="publisher"/>
                            </tiles:insertTemplate>
                        </s:if>
                        <td class="withField" colspan="${showPublisher ? 2 : 3}"></td>
                    </tr>
                </table>
            </ui:field>
        </ui:fieldGroup>
    </ui:collapsible>
    <br/>
    
    <ui:section id="filters" titleKey="form.filter">
        <ad:wrap>
                <ui:fieldGroup>
                    <ui:field id="treeFilterFieldByCampaign">
                        <%@ include file="treeFilterByCampaign.jsp"%>
                    </ui:field>
                </ui:fieldGroup>
                <ui:fieldGroup>
                    <ui:field id="treeFilterFieldByConversion">
                        <%@ include file="treeFilterByConversion.jsp"%>
                    </ui:field>
                </ui:fieldGroup>
        </ad:wrap>
    </ui:section>
    
    <ui:fieldGroup>
        <ui:field cssClass="withButton">
            <ui:button id="submitButton"
                message="report.button.runReport" />
        </ui:field>
    </ui:fieldGroup>
</s:form>
<script type="text/javascript">

    $().ready(function() {
        $('#filters table[class=fieldAndAccessories] td[class=withField]').attr('style','vertical-align:top')
        $('#conversionsForm').on('click', 'input[id^="report_output_field"]', function(){
            if ($(this).prop('checked')) {
                $('#column_'+$(this).attr('id')).show();
            } else {
                $('#column_'+$(this).attr('id')).hide();
            }
        });
        
        $('#conversionsForm').on('change', '#showResultsByDay', function(){
            if ($(this).val() == 'true') {
                $('#column_report_output_field_date').show();
            } else {
                $('#column_report_output_field_date').hide();
            }
        });
        
    });
   </script>


