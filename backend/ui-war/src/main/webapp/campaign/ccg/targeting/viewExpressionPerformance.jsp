<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="/struts-tags" prefix="s" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ui" %>

<script type="text/javascript">
     function loadData() {
         var params = $('#expressionPerformanceForm').serializeArray();
         $('#expressionPerformanceDiv')
             .html('<h3 class="level1">${ad:formatMessage("form.loading.resources")}</h3>')
             .load('${_context}/campaign/group/expressionPerformanceStats.action?id=${id}', params);
     };
</script>

<ui:pageHeadingByTitle/>

<ui:section>
    <ui:fieldGroup>
        <ui:field labelKey="channel.target">
            <table class="fieldAndAccessories">
                <tr>
                    <td class="withField" id="channelTargetName">
                        <c:set var="canViewChannel" value="${ad:isPermitted('AdvertisingChannel.view', channel)}"/>
                        <c:if test="${canViewChannel}">
                            <a href="${_context}/channel/view.action?id=${channel.id}">
                                <ui:nameWithStatus entityStatus="${channel.status}" entityName="${channel.name}"/>
                            </a>
                        </c:if>
                        <c:if test="${not canViewChannel}">
                            <ui:nameWithStatus entityStatus="${channel.status}" entityName="${channel.name}"/>
                        </c:if>
                    </td>
                    <td>
                        <c:if test="${ad:isPermitted('CreativeGroup.updateChannelTarget', model)}">
                            <ui:button message="form.edit" href="../target/edit.action?id=${id}" />
                        </c:if>
                    </td>
                </tr>
            </table>
        </ui:field>
    </ui:fieldGroup>
</ui:section>

<form id="expressionPerformanceForm">
    <s:hidden name="sortColumn"/>

    <table class="dataViewSection">
        <tr class="controlsZone">
            <td>
                <table class="grouping">
                    <tr>
                        <td class="withButtons">&nbsp;</td>
                        <td class="filterZone">
                            <ui:daterange idSuffix="EP" options="TOT Y T WTD MTD LW LM" fastChangeId="TOT" onChange="loadData();" timeZoneAccountId="${accountId}"/>
                        </td>
                    </tr>
                </table>
            </td>
        </tr>
        <tr class="bodyZone">
            <td>
                <div class="logicalBlock" id="expressionPerformanceDiv">
                    <%@ include file="/campaign/ccg/targeting/expressionPerformanceStats.jsp" %>
                </div>
            </td>
        </tr>
    </table>
</form>

<div class="wrapper">
    <ui:button message="form.close" onclick="location='../view.action?id=${id}';" type="button"/>
</div>
