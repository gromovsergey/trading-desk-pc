<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>

<ad:requestContext var="advertiserContext"/>
<c:set var="accountId" value="${advertiserContext.accountId}"/>
<%@ include file="ccg/viewImports.jsp"%>
<%@ include file="ccg/viewHeader.jsp"%>
<s:if test="isNoFreqCapsWarning()">
    <ui:section cssClass="message warning">
        <fmt:message key="ccg.nofrequencycaps"/>
    </ui:section>
</s:if>
<%@ include file="ccg/viewGeneralProperties.jsp"%>

<c:set var="reportPathPrefix" value="${_context}"/>
<s:url var="reportUrl" value="%{#attr.reportPathPrefix}/reporting/displayAdvertising/options.action">
    <s:param name="ccgIds" value="id"/>
</s:url>

<ui:chart type="ccgChart" id="${id}" total="${totalImpressions}" msgKey="chart.message.nodata.CG"
          reportLink="${reportUrl}" selected="${xSelect},${y1Select},${y2Select}"/>

<form id="ccgForm">
    <s:hidden name="id"/>
</form>

<script type="text/javascript">
    new UI.AjaxLoader().switchOff();
    
    function textCWLoadCallback(){
        var jqCZone = $('#creativesDiv .dataViewSection .controlsZone > td:eq(0)'); 
        $('#creativesForm .paginationHeader').clone().prependTo(jqCZone);
        jqCZone.find('.pagingButton').each(function(){
            $(this).attr({'href':'#'+$(this).data('page')+'s'+(sessionStorage.getItem('paLastSession') || 1)});
        });
    }

    $(function() {
        var params = $('#ccgForm').serializeArray();

        $('#targeting_preloader').show();
        $('#targetingDiv')
        .load('${_context}/campaign/group/targetingWrapper.action', params);
        
        sessionStorage.clear();
        $('#creatives_preloader').show();
        $('#creativesDiv').load('${_context}/campaign/group/creativesStatsWrapper.action', params, function(){
            textCWLoadCallback();
        });
        
        <c:if test="${not empty actions}">
        $('#conversions_preloader').show();
        $('#conversionsDiv').load('${_context}/campaign/group/conversionsWrapper.action', params);
        </c:if>

        
    });
</script>

<div id="targetingDiv">
    <ui:section titleKey="ccg.targeting">
        <img id="targeting_preloader" class="hide" src="/images/wait-animation-small.gif"></div>
    </ui:section>
</div>

<%@ include file="ccg/viewActionTrackingPixel.jsp"%>

<div id="creativesDiv">
    <ui:header styleClass="level2">
        <h2><fmt:message key="creatives"/></h2>
        <img id="creatives_preloader" class="hide" src="/images/wait-animation-small.gif">
    </ui:header>
</div>

<c:if test="${not empty actions}">
    <div id="conversionsDiv">
        <ui:header styleClass="level2">
            <h2><fmt:message key="ccg.conversions"/></h2>
            <img id="conversions_preloader" class="hide" src="/images/wait-animation-small.gif">
        </ui:header>
    </div>  
</c:if>


