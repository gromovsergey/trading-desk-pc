<%@ page contentType="text/html" %>
<%@ page pageEncoding="UTF-8" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="ad" uri="/ad/serverUI" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>

<%-- Statistic columns --%>
<script type="text/javascript">
    $(function() {
        $('#statisticColumnsSection').closest('form')
            .unbind('submit.checkStatColumn')
            .bind('submit.checkStatColumn', function(e){
                if ($('#statisticColumnsSection').find('input[type="checkbox"]:checked').length == 0) {
                    alert('${ad:formatMessage("report.advertising.noColumnsSelected")}');
                    e.preventDefault();
                    return false;
                }
            })
            .end()
            .find('.fieldAndAccessories')
            .eq(0)
            .css({'min-width':'950px'});
    });
</script>
<ui:fieldGroup>
    <ui:field labelKey="report.input.field.statisticColumns" id="statisticColumnsSection">
        <table class="fieldAndAccessories">
            <tr>
                <tiles:insertTemplate template="column.jsp">
                    <tiles:putAttribute name="column" value="impressions"/>
                </tiles:insertTemplate>

                <c:if test="${ad:isInternal()}">
                    <tiles:insertTemplate template="column.jsp">
                        <tiles:putAttribute name="column" value="impressions.hid"/>
                    </tiles:insertTemplate>
                </c:if>

                <tiles:insertTemplate template="column.jsp">
                    <tiles:putAttribute name="column" value="clicks"/>
                </tiles:insertTemplate>

                <c:if test="${ad:isInternal()}">
                    <tiles:insertTemplate template="column.jsp">
                        <tiles:putAttribute name="column" value="clicks.hid"/>
                    </tiles:insertTemplate>
                </c:if>

                <tiles:insertTemplate template="column.jsp">
                    <tiles:putAttribute name="column" value="CTR"/>
                </tiles:insertTemplate>

                <c:if test="${ad:isInternal()}">
                    <tiles:insertTemplate template="column.jsp">
                        <tiles:putAttribute name="column" value="CTR.hid"/>
                    </tiles:insertTemplate>
                </c:if>

                <c:if test="${ad:isInternal()}">
                    <tiles:insertTemplate template="column.jsp">
                        <tiles:putAttribute name="column" value="margin"/>
                    </tiles:insertTemplate>
                </c:if>
            </tr>
            <c:if test="${reportState.available('report.output.field.campaignCreditUsed')}">
                <tr>
                    <tiles:insertTemplate template="column.jsp">
                        <tiles:putAttribute name="column" value="creditedImpressions"/>
                    </tiles:insertTemplate>

                    <tiles:insertTemplate template="column.jsp">
                        <tiles:putAttribute name="column" value="creditedClicks"/>
                    </tiles:insertTemplate>

                    <tiles:insertTemplate template="column.jsp">
                        <tiles:putAttribute name="column" value="creditedActions"/>
                    </tiles:insertTemplate>

                    <c:if test="${ad:isInternal()}">
                        <tiles:insertTemplate template="column.jsp">
                            <tiles:putAttribute name="column" value="campaignCreditUsed"/>
                        </tiles:insertTemplate>

                        <tiles:insertTemplate template="column.jsp">
                            <tiles:putAttribute name="column" value="totalValue"/>
                        </tiles:insertTemplate>
                    </c:if>
                </tr>
            </c:if>
            <tr>
                <tiles:insertTemplate template="column.jsp">
                    <tiles:putAttribute name="column" value="totalUniqueUsers"/>
                </tiles:insertTemplate>

                <tiles:insertTemplate template="column.jsp">
                    <tiles:putAttribute name="column" value="monthlyUniqueUsers"/>
                </tiles:insertTemplate>

                <tiles:insertTemplate template="column.jsp">
                    <tiles:putAttribute name="column" value="dailyUniqueUsers"/>
                </tiles:insertTemplate>

                <tiles:insertTemplate template="column.jsp">
                    <tiles:putAttribute name="column" value="newUniqueUsers"/>
                </tiles:insertTemplate>
                
                <tiles:insertTemplate template="column.jsp">
                    <tiles:putAttribute name="column" value="opportunitiesToServe"/>
                </tiles:insertTemplate>
                
                <tiles:insertTemplate template="column.jsp">
                    <tiles:putAttribute name="column" value="auctionsLost"/>
                </tiles:insertTemplate>
            </tr>
            <tr>
                <c:if test="${ad:isInternal()}">
                    <tiles:insertTemplate template="column.jsp">
                        <tiles:putAttribute name="column" value="totalCost"/>
                    </tiles:insertTemplate>

                    <tiles:insertTemplate template="column.jsp">
                        <tiles:putAttribute name="column" value="eCPU"/>
                    </tiles:insertTemplate>

                    <tiles:insertTemplate template="column.jsp">
                        <tiles:putAttribute name="column" value="eCPM"/>
                    </tiles:insertTemplate>

                    <tiles:insertTemplate template="column.jsp">
                        <tiles:putAttribute name="column" value="wgLicensingCost"/>
                    </tiles:insertTemplate>
                </c:if>
                <c:if test="${reportState.available('report.output.field.selfServiceCost')}">
                    <tiles:insertTemplate template="column.jsp">
                        <tiles:putAttribute name="column" value="selfServiceCost"/>
                    </tiles:insertTemplate>
                </c:if>
                <c:if test="${reportState.available('report.output.field.selfServiceCostNet')}">
                    <tiles:insertTemplate template="column.jsp">
                        <tiles:putAttribute name="column" value="selfServiceCostNet"/>
                    </tiles:insertTemplate>
                </c:if>
                <c:if test="${reportState.available('report.output.field.selfServiceCostGross')}">
                    <tiles:insertTemplate template="column.jsp">
                        <tiles:putAttribute name="column" value="selfServiceCostGross"/>
                    </tiles:insertTemplate>
                </c:if>
            </tr>
        </table>
    </ui:field>
    <%--TODO: fix it it's just gap beetween fields--%>
    <ui:field/>
    <ui:field/>
    <%-- Setting columns --%>
    <ui:field labelKey="report.input.field.settingsColumns">
        <table class="fieldAndAccessories">
            <tr>
                <tiles:insertTemplate template="column.jsp">
                    <tiles:putAttribute name="column" value="inventoryRate"/>
                </tiles:insertTemplate>

                <tiles:insertTemplate template="column.jsp">
                    <tiles:putAttribute name="column" value="country"/>
                </tiles:insertTemplate>

                <td class="withField"/>
            </tr>
            <tr>

                <tiles:insertTemplate template="column.jsp">
                    <tiles:putAttribute name="column" value="channelTarget"/>
                </tiles:insertTemplate>

                <tiles:insertTemplate template="column.jsp">
                    <tiles:putAttribute name="column" value="creativeSize"/>
                </tiles:insertTemplate>

                <tiles:insertTemplate template="column.jsp">
                    <tiles:putAttribute name="column" value="deviceName"/>
                </tiles:insertTemplate>

            </tr>
            <tr>
                <c:set var="hideAdv" value="${requestContexts.advertiserContext.advertiserSet}"/>
                <c:if test="${not hideAdv}">
                    <tiles:insertTemplate template="column.jsp">
                        <tiles:putAttribute name="column" value="adv"/>
                    </tiles:insertTemplate>
                </c:if>

                <tiles:insertTemplate template="column.jsp">
                    <tiles:putAttribute name="column" value="campaign"/>
                </tiles:insertTemplate>

                <tiles:insertTemplate template="column.jsp">
                    <tiles:putAttribute name="column" value="creativeGroup"/>
                </tiles:insertTemplate>

                <c:if test="${hideAdv}">
                    <td class="withField"/>
                </c:if>
                    <td class="withField"/>
            </tr>
        </table>

        <%-- Exists but hidden --%>
        <table style="display: none;">
            <tr>
                <tiles:insertTemplate template="column.jsp">
                    <tiles:putAttribute name="column" value="creative"/>
                </tiles:insertTemplate>
            </tr>
        </table>
    </ui:field>
</ui:fieldGroup>
