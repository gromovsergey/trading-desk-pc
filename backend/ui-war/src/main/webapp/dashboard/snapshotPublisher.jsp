<%@page contentType="text/html" %>
<%@page pageEncoding="UTF-8" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib uri="/struts-tags" prefix="s"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ui" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<tiles:importAttribute ignore="true" scope="request" name="contextModule"/>


<script type="text/javascript">

    function showHide(j, showCreditedImps, currBtn){
        var dataBlock = $('#data_' + j);
        var currBtn = $(currBtn);
        var isHidden = dataBlock.data('isHidden');
        isHidden = isHidden != undefined ? isHidden : true;

        var activity = $('#showZeroStat').prop('checked');
        $('#withActivityOnly').val(!activity);
        
        if (isHidden){
            $('#data_' + j)
            .html('<h3 class="level1">${ad:formatMessage("form.loading.resources")}</h3>');
            $.ajax({
                url: "${_context}/dashboard/tagStats.action",
                data: prepareData(j, showCreditedImps),
                success: function(data){
                    $('#data_' + j).html(data);
                    dataBlock.toggle();
                    currBtn.html('<fmt:message key="hide.tags"/>');
                    dataBlock.data('isHidden', !isHidden);
                },
                error: function(data){
                    window.location.reload();
                }
            });
        } else {
            dataBlock.toggle();
            currBtn.html('<fmt:message key="show.tags"/>');
            dataBlock.data('isHidden', !isHidden);
        }
    }

    function prepareData(j, showCreditedImps){
        return $("#dateRange_begin, #dateRange_end, #showZeroStat").serialize() + "&siteId=" + j +
               (showCreditedImps ? "&showCreditedImps=true" : "");
    }

    function loadPublisherDashboard() {
        var activity = $('#showZeroStat').prop('checked');
        $('#withActivityOnly').val(!activity);
        $('#publisherDashboardDiv')
            .html('<h3 class="level1">${ad:formatMessage("form.loading.resources")}</h3>')
            .load("${_context}/dashboard/siteStats.action", $('#mainForm').serializeArray());
    }
</script>

<ad:requestContext var="publisherContext"/>
<c:set var="showTable" value="${!showZeroStat or not empty result}"/>
<c:set var="accountBean" value="${publisherContext.account}"/>

<jsp:include page="/admin/notices/noticesSnapshot.jsp" />

<c:if test="${ad:isPermitted('PublisherEntity.create', accountBean)}">
    <c:set var="siteCreate" value="true"/>
</c:if>

<c:if test="${ad:isPermitted('PublisherEntity.view', accountBean)}">
    <c:set var="siteView" value="true"/>
</c:if>
<c:if test="${ad:isPermitted('PublisherEntity.upload', accountBean.id)}">
    <c:set var="canUpload" value="true"/>
</c:if>


<ui:header>
    <ui:pageHeadingByTitle/>
    <c:choose>
        <c:when test="${ad:isInternal()}">
             <c:if test="${siteCreate}">
                <ui:button message="form.createNew" href="${contextModule}/site/new.action${ad:accountParam('?account.id', publisherContext.accountId)}" />
              </c:if>
        </c:when>
        <c:otherwise>
            <c:if test="${ad:isPermitted0('PublisherEntity.create')}">
                  <ui:button message="form.createNew" href="new.action" />
              </c:if>
        </c:otherwise>
    </c:choose>
            
    <c:if test="${siteView && showTable}">
        <ui:button message="site.list.download" href="${contextModule}/site/export.action${ad:accountParam('?publisherId', publisherContext.accountId)}" />
    </c:if>
    <c:if test="${canUpload}">
        <ui:button message="form.upload.csv" href="${contextModule}/site/selectForUpload.action${ad:accountParam('?publisherId', publisherContext.accountId)}" />
    </c:if>
    <c:if test="${siteView && showTable}">
        <ui:button message="site.downloadAllTags" href="${contextModule}/site/downloadTagsAccount.action${ad:accountParam('?publisherId', publisherContext.accountId)}" target="_blank"/>
    </c:if>
</ui:header>

<c:if test="${!showTable}">
  <div class="wrapper">
    <span class="infos"><fmt:message key="dashboard.publisher.empty"/></span>
  </div>
</c:if>

<form id="mainForm">
<c:if test="${showTable}">
    
    <input type="hidden" name="publisherId" id="publisherId" value="${publisherContext.accountId}"/>
    <table class="dataViewSection">
    <tr class="controlsZone">
        <td>
            <table class="grouping">
                <tr>
                    <td class="withButtons">
                        &nbsp;
                    </td>
                    <td class="filterZone">
                        <s:set var="fastChangeId"><s:property value="%{#request.fastChangeId}" escapeHtml="true"/></s:set>
                        <c:set var="accountId" value="${publisherContext.accountId}"/>
                        <ui:daterange options="Y T WTD MTD QTD YTD LW LM LQ LY" fastChangeId="${fastChangeId}"
                                      onChange="loadPublisherDashboard();" timeZoneAccountId="${accountId}" fromDateFieldName="dateRange.begin" toDateFieldName="dateRange.end"/>
                    </td>
                </tr>
            </table>
        </td>
    </tr>
     <tr class="bodyZone">
        <td>
            <div class="logicalBlock" id="publisherDashboardDiv">
               <%@ include file="siteStats.jsp" %>
            </div>
            <label class="withInput">
                <s:checkbox id="showZeroStat" onclick="loadPublisherDashboard();"  name="showZeroStat" /><fmt:message key="report.display.site.noactivity"/>
                <input type="hidden" id="withActivityOnly" name="withActivityOnly" value="${!showZeroStat}" >
            </label>
        </td>
    </tr>
</table>
</c:if>
</form>
